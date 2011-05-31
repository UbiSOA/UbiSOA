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
package net.ubisoa.twitter;

import java.io.IOException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.phidgets.PhidgetException;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class TwitterResource extends BaseResource {

	@Get("html")
	public StringRepresentation items() {
		String html = "<form class=\"column\" method=\"POST\">" +
			"<h2>Send Message</h2>" +
			"<input type=\"text\" name=\"username\" placeholder=\"username\" required autofocus />" +
			"<textarea name=\"message\" placeholder=\"Message\" required></textarea>" +
			"<input type=\"submit\" value=\"Post Message\" /></form>";
			
		HTMLTemplate template = new HTMLTemplate("Twitter Publisher", html);
		template.setSubtitle("This is a test for the push protocol.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Post("form")
	public void acceptItem(Representation entity) throws PhidgetException, IOException {
		Form form = new Form(entity);
		String username = form.getFirstValue("username");
		String message = form.getFirstValue("message");
		
		 TwitterFactory factory = new TwitterFactory();
		 try {   
			twitter4j.Twitter twitter = factory.getInstance();
			twitter.setOAuthConsumer("BNrlaSJQAtG89PCIDwNcoQ", "cqS1JaodbqPyrjwQ4SToyeKLvawBBZqWQIrh749oc");
			AccessToken accessToken = loadAccessToken(1);
			twitter.setOAuthAccessToken(accessToken);
			twitter.sendDirectMessage(username, message);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private static AccessToken loadAccessToken(int useId){
		String token = "182856244-UXQzLHvRKwOn6jtMIqsXR71BhNhAQmGvQ57yRJka";
		String tokenSecret =  "IFijlJD5iIT9IumvuMuTHSI0clvd5AERLT6U7Ob74" ;
		return new AccessToken(token, tokenSecret);
	}
}
