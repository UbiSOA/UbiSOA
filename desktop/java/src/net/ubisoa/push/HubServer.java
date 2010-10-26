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
package net.ubisoa.push;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.FaviconResource;
import net.ubisoa.common.FileResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * TODO: Implement the synchronized subscription mode.
 * TODO: Implement the option to unsubscribe from topics.
 * TODO: Auto-unsubscribe from topics after lease time has passed. 
 * 
 * @author E. Avilés <edgardo@ubisoa.net>
 */
public class HubServer extends BaseResource {
	private Connection conn;
	
	public HubServer() {
		try {
			Class.forName("org.sqlite.JDBC");
			openConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8310);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections",
				Defaults.MAX_TOTAL_CONNECTIONS + "");
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_TOTAL_CONNECTIONS + "");
		component.getDefaultHost().attach("/", HubServer.class);
		component.getDefaultHost().attach("/{type}/{filename}", FileResource.class);
		component.getDefaultHost().attach("/favicon.ico", FaviconResource.class);
		component.start();
	}
	
	private void openConnection() throws SQLException {
		conn = DriverManager.getConnection("jdbc:sqlite:dat/net.ubisoa.push.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate("CREATE TABLE IF NOT EXISTS topics(topic, lastFetch, lastPing)");
		stat.executeUpdate("CREATE TABLE IF NOT EXISTS subscriptions" +
			"(topic, callback, token, verified, lease)");
	}
	
	private void handleNewContentNotification(String topic) throws SQLException {
		getLogger().info("New content notification has arrived.\t\tTopic: " + topic);
		
		// Checking if there are any database records for the topic.
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("SELECT topic FROM topics WHERE topic='" + topic + "'");
		int rowCount = 0; while (rs.next()) rowCount++;
		
		// Adding the topic to the database, if it wasn't registered before.
		if (rowCount == 0) {
			PreparedStatement prst = conn.prepareStatement("INSERT INTO topics(topic) VALUES(?)");
			prst.setString(1, topic);
			prst.execute();
		}
		
		// Updating the last fetched and ping dates.
		PreparedStatement prst = conn.prepareStatement(
			"UPDATE topics SET lastFetch=?, lastPing=? WHERE topic=?");
		prst.setString(1, Defaults.dateFormat().format(new Date()));
		prst.setString(2, Defaults.dateFormat().format(new Date()));
		prst.setString(3, topic);
		prst.execute();
		
		// Fetching the topic's new content.
		String mediaType, content;
		try {
			HttpResponse response = Defaults.getHttpClient().execute(new HttpGet(topic));
			HttpEntity entity = response.getEntity();
			mediaType = entity.getContentType().getValue();
			mediaType = mediaType.substring(0, mediaType.lastIndexOf(';'));
			content = EntityUtils.toString(entity);
			content.toString();
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return;
		}
		if (mediaType == null || content == null) {
			prst = conn.prepareStatement("DELETE FROM topics WHERE topic=?");
			prst.setString(1, topic);
			prst.execute();
			
			prst = conn.prepareStatement("DELETE FROM subscriptions WHERE topic=?");
			prst.setString(1, topic);
			prst.execute();
			
			getLogger().warning("Cannot get the topic's content. The topic was removed.");
			setStatus(Status.SUCCESS_NO_CONTENT);
			return;
		}
		
		// Looping through all the subscribers of this topic.
		rs = stat.executeQuery("SELECT * FROM subscriptions WHERE topic='" + topic + "'");
		while (rs.next()) {
			Boolean verified = rs.getBoolean("verified");
			String callback = rs.getString("callback");
			String token = rs.getString("token");
			if (!verified) if (!verifySubscription(topic, callback, token)) continue;
			
			// Sending new topic's content to subscribed callback URLs.
			try {
				HttpPost post = new HttpPost(callback);
				StringEntity postEntity = new StringEntity(content, "UTF-8");
				post.setEntity(postEntity);
				post.setHeader("Content-Type", mediaType);
				Defaults.getHttpClient().execute(post);
				
				getLogger().info("New topic content was successfully sent to a callback." +
					"\n\tTopic: " + topic + "\n\tCallback: " + callback);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		getLogger().info("Handling of new content notification completed.");
		setStatus(Status.SUCCESS_NO_CONTENT);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
	}

	private void handleSubscriptionRequest(String callback,	String topic, String token)
			throws SQLException {
		getLogger().info("New subscription request has arrived.\n\tTopic: " + topic +
				"\n\tCallback: " + callback);
		
		// Deleting past subscriptions to the topic/callback key.
		PreparedStatement prst = conn.prepareStatement(
			"DELETE FROM subscriptions WHERE topic=? AND callback=?");
		prst.setString(1, topic);
		prst.setString(2, callback);
		prst.execute();
		
		// Registering the new subscription (pending verification).
		prst = conn.prepareStatement("INSERT INTO subscriptions VALUES(?, ?, ?, ?, ?)");
		prst.setString(1, topic);
		prst.setString(2, callback);
		prst.setString(3, token);
		prst.setBoolean(4, false);
		prst.setString(5, Defaults.getDefaultLeaseDateString());
		prst.execute();
		
		getLogger().info("The Hubbub subscription was stored but not verified yet.");
	}
	
	private Boolean verifySubscription(String topic, String callback, String token)
			throws SQLException {
		getLogger().info("Verificating a pending subscription.\n\tTopic: " +
			topic + "\n\tCallback: " + callback);
		String challenge = UUID.randomUUID().toString();
		String callbackURL = callback + "?hub.mode=subscribe&hub.topic=" + topic +
			"&hub.challenge=" +	challenge + "&hub.verify_token=" + token;
		
		try {
			// Trying to get an echo of the challenge from the callback.
			HttpGet get = new HttpGet(callbackURL);
			HttpResponse response = Defaults.getHttpClient().execute(get);
			String challengeEcho = EntityUtils.toString(response.getEntity());
			
			// Challenge successful, subscription is verified.
			if (challengeEcho.compareTo(challenge) == 0) {
				PreparedStatement prst = conn.prepareStatement(
					"UPDATE subscriptions SET verified=1, lease=? WHERE topic=? AND callback=?");
				prst.setString(1, Defaults.getDefaultLeaseDateString());
				prst.setString(2, topic);
				prst.setString(3, callback);
				prst.execute();
				
				getLogger().info("Subscription was successfully verified.");
				return true;
			} else {
				// Cannot verify the subscription, removing it from the database.
				PreparedStatement prst = conn.prepareStatement("DELETE FROM subscriptions " +
					"WHERE topic=? AND callback=?");
				prst.setString(1, topic);
				prst.setString(2, callback);
				prst.execute();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getLogger().info("Subscription wasn't valid.");
		return false;
	}
	
	@Get("html")
	public StringRepresentation getHTML() {
		String html = "<form method=\"POST\" style=\"margin-bottom: 12px\">" +
			"<h2>Publish Topic</h2><input type=\"hidden\" name=\"hub.mode\" value=\"publish\" />" +
			"<input type=\"text\" name=\"hub.url\" placeholder=\"Topic URL\" " +
			"style=\"width: 300px; display: inline-block\" />" +
			"<input type=\"submit\" value=\"Submit\" style=\"margin-left: 8px\" /></form>" +
			"<div id=\"topics\"><h2>Published Topics</h2><table>" +
			"<tr><th>Topic</th><th>Last Fetch</th><th>Subscribers</th><th>Last Ping</th></tr>";
		
		try {
			html += getHTMLTableData() + "</table></div>";
		} catch (SQLException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage(), MediaType.TEXT_HTML);
		}
		
		HTMLTemplate template = new HTMLTemplate("Hub Server", html);
		template.setSubtitle("An implementation of the " +
			"<a href=\"http://pubsubhubbub.googlecode.com/\">PubSubHubbub</a> prototocol.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	private String getHTMLTableData() throws SQLException {
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("SELECT * FROM topics ORDER BY lastPing DESC");
		String html = "";
		while (rs.next()) {
			String topic = rs.getString("topic");
			String subs = "<ul>";
			Statement subST = conn.createStatement();
			ResultSet subRS = subST.executeQuery("SELECT * FROM subscriptions WHERE topic='" +
				topic + "' ORDER BY verified, callback");
			while (subRS.next()) {
				String callback = subRS.getString("callback");
				String verified = subRS.getBoolean("verified")?
					" <span class=\"tag\">(VERIFIED)</span>": "";
				subs += "<li>" + callback + verified + "</li>";
			}
			if (subs.compareTo("<ul>") == 0)
				subs = "No subscriptors.";
			else subs += "</ul>";
			
			html += "<tr><td>" + topic + "</td><td>" +
			rs.getString("lastFetch") + "</td><td>" + subs + "</td><td>" +
			rs.getString("lastPing") + "</td></tr>";
		}
		
		if (html.compareTo("") == 0)
			return "<tr><td colspan=\"4\">There are no topics on file.</td></tr>";
		return html;
	}
	
	@Post("form")
	public void handlePost(Representation entity) {
		Form form = new Form(entity);
		String callback = form.getFirstValue("hub.callback");
		String mode = form.getFirstValue("hub.mode");
		String url = form.getFirstValue("hub.url");
		String topic = form.getFirstValue("hub.topic");
		String verify = form.getFirstValue("hub.verify");
		Boolean synchronizedMode = verify != null && verify.compareTo("sync") == 0; 
		String token = form.getFirstValue("hub.verify_token");
		
		try {
			if (mode.compareTo("subscribe") == 0 && !synchronizedMode)
				handleSubscriptionRequest(callback, topic, token);
			else if (mode.compareTo("publish") == 0)
				handleNewContentNotification(url);
			else setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
		} catch (SQLException e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
