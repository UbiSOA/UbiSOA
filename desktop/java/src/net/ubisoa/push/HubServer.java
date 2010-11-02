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

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

/**
 * TODO: Implement the synchronized subscription mode.
 * TODO: Implement the option to unsubscribe from topics.
 * TODO: Auto-unsubscribe from topics after lease time has passed. 
 * 
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class HubServer extends Application {
	private Vector<Topic> topics = new Vector<Topic>(20, 5);
	private Vector<Subscription> subscriptions = new Vector<Subscription>(20, 5);
	private BlockingQueue<Topic> notificationsQueue = new LinkedBlockingQueue<Topic>();
	private HubHandler handler;
	private HttpClient client = Defaults.getHttpClient();
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8310);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new HubServer());
		component.start();
	}
	
	@Override
	public Restlet createInboundRoot() {
		handler = new HubHandler(this);
		handler.start();
		
		Router router = new BaseRouter(getContext());
		router.attach("/", HubResource.class);
		return router;
	}
	
	public void handleNewContentNotification(String topic) {
		getLogger().info("New content notification has arrived.\t\tTopic: " + topic);
		notificationsQueue.add(new Topic(topic));
		synchronized (handler) { handler.notify(); }
	}

	public void handleSubscriptionRequest(String callback,	String topic, String token) {
		getLogger().info("New subscription request has arrived.\n\tTopic: " + topic +
				"\n\tCallback: " + callback);
		
		// Deleting past subscriptions to the topic/callback key.
		Vector<Integer> toRemoveSubs = new Vector<Integer>(10, 5);
		for (Subscription sub : subscriptions)
			if (sub.getTopic().equals(topic) && sub.getCallback().equals(callback))
				toRemoveSubs.add(subscriptions.indexOf(sub));
		for (int index : toRemoveSubs) subscriptions.remove(index);
		
		// Registering the new subscription (pending verification).
		subscriptions.add(new Subscription(topic, callback, token, false));
		
		// Registering the topic.
		Boolean isRegistered = false;
		for (Topic topicObj : topics)
			if (topicObj.getTopic().equals(topic))
				isRegistered = true;
		if (!isRegistered) topics.add(new Topic(topic));
		
		synchronized (handler) { handler.notify(); }
		getLogger().info("The Hubbub subscription was stored but not verified yet.");
	}
	
	public Vector<Subscription> getSubscriptions() {
		return subscriptions;
	}
	
	public BlockingQueue<Topic> getNotificationsQueue() {
		return notificationsQueue;
	}
	
	public Vector<Topic> getTopics() {
		return topics;
	}
	
	public HttpClient getDefaultClient() {
		return client;
	}
	
	public String getHTMLTableData() {
		String html = "";
		for (Topic topic : topics) {
			String subs = "";
			for (Subscription sub : subscriptions)
				if (sub.getTopic().equals(topic.getTopic())) {
					String verified = sub.getVerified()?
						" <span class=\"tag\">(VERIFIED)</span>": "";
					subs += "<li>" + sub.getCallback() + verified + "</li>";
				}
			if (subs.compareTo("<ul>") == 0)
				subs = "No subscriptors.";
			else subs += "</ul>";
			
			String topicURL = topic.getTopic(), lastPingStr = topic.getLastPing();
			if (lastPingStr == null) lastPingStr = "(none)";
			
			html += "<tr><td>" + topicURL + "</td><td>" + lastPingStr +
			"</td><td>" + subs + "</td></tr>";
		}
		if (html.compareTo("") == 0)
			return "<tr><td colspan=\"4\">There are no topics on file.</td></tr>";
		return html;
	}

}
