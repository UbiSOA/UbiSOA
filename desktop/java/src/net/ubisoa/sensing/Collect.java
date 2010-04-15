package net.ubisoa.sensing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import net.tinyos.message.*;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;

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
			
			res = true;
		}
		catch (SQLException e) {
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
	}
	
	private static void usage() {
		System.err.println("usage: Collect -comm <source>");
		System.exit(1);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length > 2)
			if (args[0].compareTo("-comm") != 0) usage();
		else usage();
		
		Collect collect = new Collect(args[1]);
		collect.addMsgType(new SensingMessage());
		collect.start();
	}
}
