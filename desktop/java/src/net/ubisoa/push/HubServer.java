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

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;

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
 * @author E. Avilés <edgardo@ubisoa.net>
 */
public class HubServer extends Application {
	HubData data = HubData.getInstance();

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
		Router router = new BaseRouter(getContext());
		router.attach("/", HubResource.class);
		return router;
	}
	
	public void handleNewContentNotification(final String topic) {
		getLogger().info("New content notification has arrived.\t\tTopic: " + topic);
		getTaskService().execute(new HubPingHandler(data, topic, getTaskService(), getLogger()));
	}

	public void handleSubscriptionRequest(String callback,	String topic, String token) {
		getLogger().info("New subscription request has arrived.\n\tTopic: " + topic +
				"\n\tCallback: " + callback);
		
		// Deleting past subscriptions to the topic/callback key.
		data.removeSubsWithTopicAndCallback(topic, callback);
		
		// Registering the new subscription (pending verification).
		Subscription sub = new Subscription(topic, callback, token, false);
		data.addSubscription(sub);
		
		getLogger().info("The Hubbub subscription was stored but not verified yet.");
	}
	
	public String getHTMLTableData() {
		String html = "";
		for (Topic topic : data.getTopics()) {
			String subs = "";
			for (Subscription sub : data.getSubscriptions())
				if (sub.getTopic().equals(topic.getTopic())) {
					String verified = sub.getVerified()?
						" <span class=\"tag\">(VERIFIED)</span>": "";
					subs += "<li>" + sub.getCallback() + verified + "</li>";
				}
			if (subs.compareTo("<ul>") == 0)
				subs = "No subscriptors.";
			else subs += "</ul>";
			html += "<tr><td>" + topic.getTopic() + "</td><td>" + topic.getLastPing() +
			"</td><td>" + subs + "</td><td>" + topic.getLastFetch() + "</td></tr>";
		}
		if (html.compareTo("") == 0)
			return "<tr><td colspan=\"4\">There are no topics on file.</td></tr>";
		return html;
	}

}
