/*
 * Copyright (c) 2010, Edgardo Avilés-López <edgardo@ubisoa.net>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * – Redistributions of source code must retain the above copyright notice, this list of
 *   conditions and the following disclaimer.
 * – Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * – Neither the name of the CICESE Research Center nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ubisoa.sensing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;
import net.ubisoa.core.Defaults;

public class Collect implements MessageListener {
	private MoteIF moteIF;
	private Connection connection = null;
	
	public Collect(String source) throws Exception {
		if (source != null)
			moteIF = new MoteIF(BuildSource.makePhoenix(source, PrintStreamMessenger.err));
		else moteIF = new MoteIF(BuildSource.makePhoenix(PrintStreamMessenger.err));
	}
	
	private void addMsgType(Message msg) {
		moteIF.registerListener(msg, this);
	}
	
	private void dbConnect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean dbStore(Reading reading) {
		boolean res = false;
		try {
			if (connection == null) dbConnect();
			
			Statement s = connection.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS readings (" +
				"timestamp, nid, light, temperature, voltage, " +
				"light_visible, temperature_internal, humidity, microphone)");
			
			String sql = "INSERT INTO readings VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement p = connection.prepareStatement(sql);
			p.setString(1, reading.getDateTime());
			p.setInt(2, reading.getNid());
			p.setInt(3, reading.getLight());
			p.setFloat(4, (float)reading.getTemperature());
			p.setFloat(5, (float)reading.getVoltage());
			p.setInt(6, reading.getLightVisible());
			p.setFloat(7, (float)reading.getTemperatureInternal());
			p.setFloat(8, (float)reading.getHumidity());
			p.setInt(9, reading.getMicrophone());
			p.addBatch();
			
			connection.setAutoCommit(false);
			p.executeBatch();
			connection.setAutoCommit(true);
			s.close();
			p.close();
			connection.close();
			connection = null;
			
			res = true;
		}
		catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				connection = null;
			}
		}
		return res;
	}
	
	public void start() {
		dbConnect();
	}

	public void messageReceived(int to, Message message) {
		Reading reading = new Reading((SensingMessage)message);
		boolean res = dbStore(reading);
		System.out.print(reading.toStringLine() + "\t");
		System.out.println(res? "[OK]": "[NOT STORED]");
		pingHub();
	}
	
	private boolean isBusy = false;
	
	private void pingHub() {
		if (isBusy) {
			System.out.println("Hubbub ping cancelled.");
			return;
		}
		isBusy = true;
		HttpClient client = Defaults.getHttpClient();
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://127.0.0.1:8340/?output=json"));
			UrlEncodedFormEntity paramsEntity;
			paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost("http://localhost:8310/");
			post.setEntity(paramsEntity);
			HttpResponse res = client.execute(post);
			HttpEntity resEntity = res.getEntity();
			if (resEntity != null) resEntity.consumeContent();			
			System.out.println("The Hubbub ping was sent successfully.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isBusy = false;
	}

	private static void usage() {
		System.err.println("usage: Collect -comm <source>");
		System.exit(1);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) { usage(); return; }
		else if (args[0].compareTo("-comm") != 0) usage();
		
		Collect collect = new Collect(args[1]);
		collect.addMsgType(new SensingMessage());
		collect.start();
	}
}
