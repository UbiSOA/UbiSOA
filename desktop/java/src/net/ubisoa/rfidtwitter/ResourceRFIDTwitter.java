/*
 * Copyright (c) 2010, Edgardo Avilés-López
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
package net.ubisoa.rfidtwitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.atom.AtomConverter;
import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Text;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author I. Cruz <icruz@ubisoa.net>
 */
public class ResourceRFIDTwitter extends BaseResource {
	String rfid = "0011";
	String user = "";
	Statement stat = null;
	Connection conn = null;
	
	private void init() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
	    conn = DriverManager.getConnection("jdbc:sqlite:test.db");
	    stat = conn.createStatement();
	    stat.executeUpdate("drop table if exists tweet;");
	    stat.executeUpdate("create table tweet (rfid, user_twitter);");
	    PreparedStatement prep = conn.prepareStatement(
	      "insert into tweet values (?, ?);");
	    prep.setString(1, "0011");
	    prep.setString(2, "@joa");
	    prep.addBatch();
	    prep.setString(1, "0012");
	    prep.setString(2, "@ignacio");
	    prep.addBatch();
	    prep.setString(1, "0013");
	    prep.setString(2, "@jose");
	    prep.addBatch();
	    prep.setString(1, "0014");
	    prep.setString(2, "@juan");
	    prep.addBatch();

	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	}
	
	@Get("html")
	public StringRepresentation temperatures() throws Exception {
		String all = "";
		String html = "<form class=\"column\" method=\"POST\">" +
			"<h2>Search user</h2>" +
			"<input type=\"input\" id=\"rfid\" name=\"rfid\" />"+
			"<input type=\"submit\" value=\" Ok\" /></form>" +
			"<div class=\"column\"><ul>";
		
		init();
		
	    user = ((PublisherTestRFID)getApplication()).getUser();
	    ResultSet rs = stat.executeQuery("select * from tweet;");
	   
	    while (rs.next()) {
	    	all += "<li><strong>" + rs.getString("rfid") + "</strong>. " +
	    	rs.getString("user_twitter") + "</li>";
	    }
	    rs.close();
	    conn .close();
		
		html += "<div id='hist'><h2><a href=\"#\">Result</a></h2></div> <div>"+ user +"</div>"+
				"<div id='all'><h2><a href=\"#\">All items</a></h2></div> <div>" + all +"</ul></div>" ;
		HTMLTemplate template = new HTMLTemplate("Publisher Twitter's Users Resource", html);
		template.getScripts().add("js/rfidsearch.js");
		template.setSubtitle("This use the Resource RFID for Twitter's users.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	
	@Get("twitter")
	public StringRepresentation idTwitter() throws ClassNotFoundException, SQLException {
		init();
		    
	    ResultSet rs = stat.executeQuery("select * from tweet where rfid='"+ rfid +"';");
	   
	    while (rs.next()) {
	    	user = rs.getString("user_twitter");
	    }
	    rs.close();
	    conn .close();
	    if (user.equals("")) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
		return new StringRepresentation(user, MediaType.TEXT_PLAIN);
	}
	
	@Get("xml")
	public DomRepresentation itemsXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("IdTwitter"), child, subChild;
			d.appendChild(root);
			
			child = d.createElement("Values");
				
			subChild = d.createElement("Id");
			subChild.appendChild(d.createTextNode(rfid));
			child.appendChild(subChild);
				
			subChild = d.createElement("Usuario");
			subChild.appendChild(d.createTextNode(user));
			child.appendChild(subChild);
				
			root.appendChild(child);

			d.normalizeDocument();
			return new DomRepresentation(MediaType.TEXT_XML, d);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Get("atom")
	public Representation itemsAtom() {
		Feed feed = new Feed();
		Entry entry = new Entry();
		entry.setId("urn:uuid:" + UUID.randomUUID());
		entry.setTitle(new Text(user));
		Content content = new Content();
		content.setInlineContent(new StringRepresentation(
			rfid, MediaType.TEXT_PLAIN));
		entry.setContent(content);
		feed.getEntries().add(entry);
		
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed,
			new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation itemsJSON() {
		String padding = getQuery().getFirstValue("callback");
			try {
			JSONObject json = new JSONObject();
			JSONArray itemsArray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("User", user);
			obj.put("Id", rfid);
			itemsArray.put(obj);
			json.put("IdTwitter", itemsArray);
			String jsonStr = json.toString();
			if (padding != null)
				jsonStr = padding + "(" + jsonStr + ")";
			return new JsonRepresentation(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Post("form")
	public void acceptIdTwitter(Representation entity) throws ClassNotFoundException, SQLException {
		Form form = new Form(entity);
		rfid = form.getFirstValue("rfid");
		StringRepresentation exist = idTwitter();
		if(exist == null){
			user = "No item.";
		}
		((PublisherTestRFID)getApplication()).setUser(user);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
		
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://localhost:8318/?output=html"));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost("http://localhost:8310/");
			post.setEntity(paramsEntity);
			Defaults.getHttpClient().execute(post);
			getLogger().info("The Hubbub ping was sent successfully.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
