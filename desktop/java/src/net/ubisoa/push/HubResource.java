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

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class HubResource extends BaseResource {
	// TODO: Add documentation.
	
	HubServer server = (HubServer)getApplication();
	
	@Get("html")
	public StringRepresentation getHTML() {
		String html = "<form method=\"POST\" style=\"margin-bottom: 12px\">" +
			"<h2>Publish Topic</h2><input type=\"hidden\" name=\"hub.mode\" value=\"publish\" />" +
			"<input type=\"text\" name=\"hub.url\" placeholder=\"Topic URL\" " +
			"style=\"width: 300px; display: inline-block\" />" +
			"<input type=\"submit\" value=\"Submit\" style=\"margin-left: 8px\" /></form>" +
			"<div id=\"topics\"><h2>Published Topics</h2><table>" +
			"<tr><th>Topic</th><th>Last Ping</th><th>Subscribers</th></tr>";
		
		html += server.getHTMLTableData() + "</table></div>";
		HTMLTemplate template = new HTMLTemplate("Hub Server", html);
		template.setSubtitle("An implementation of the " +
			"<a href=\"http://pubsubhubbub.googlecode.com/\">PubSubHubbub</a> prototocol.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Post("form")
	public void handlePost(Representation entity) {
		getLogger().info("New request received.");
		
		Form form = new Form(entity);
		String mode = form.getFirstValue("hub.mode");

		if (mode.equals("subscribe")) {
			String verify = form.getFirstValue("hub.verify");
			if (!verify.equals("async")) {
				setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
				return;
			}
			
			String callback = form.getFirstValue("hub.callback");
			String topic = form.getFirstValue("hub.topic");
			String token = form.getFirstValue("hub.verify_token");
			server.handleSubscriptionRequest(callback, topic, token);
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		else if (mode.equals("publish")) {
			String url = form.getFirstValue("hub.url");
			server.handleNewContentNotification(url);
			setStatus(Status.SUCCESS_NO_CONTENT);
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/");
		}
		else setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

}
