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

import net.ubisoa.common.BaseResource;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author E. Avilés <edgardo@ubisoa.net>
 */
public class CallbackResource extends BaseResource {
	private static String lastCallbackData = "{}";
	
	@Get("json")
	public synchronized StringRepresentation jsonCallback(Representation entity) {
		while (getLastCallbackData() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		StringRepresentation representation = new StringRepresentation(
			lastCallbackData, MediaType.APPLICATION_JSON);
		setLastCallbackData(null);
		return representation;
	}
	
	@Get("html")
	public StringRepresentation htmlCallback(Representation entity) {
		String mode = getQuery().getFirstValue("hub.mode");
		String topic = getQuery().getFirstValue("hub.topic");
		String challenge = getQuery().getFirstValue("hub.challenge");
		String token = getQuery().getFirstValue("hub.verify_token");
		PushInfo pushInfo = ((PushApplication)getApplication()).getPushInfo();
		
		if (mode != null && (mode.compareTo("subscribe") == 0 ||
				mode.compareTo("unsubscribe") == 0)) {
			if (pushInfo.getTopic().compareTo(topic) == 0 &&
				pushInfo.getToken().compareTo(token) == 0)				
				return new StringRepresentation(challenge, MediaType.TEXT_PLAIN);
		}
		
		return new StringRepresentation("Waiting callbacks…", MediaType.TEXT_PLAIN);
	}
	
	@Post("json")
	public void acceptCallback(Representation data) {
		String content = "{}";
		try {
			content = data.getText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setLastCallbackData(content);
		((PushApplication)getApplication()).pushCallback(content);
	}
	
	private synchronized String getLastCallbackData() {
		return lastCallbackData;
	}
	
	private synchronized void setLastCallbackData(String data) {
		lastCallbackData = data;
	}
	
}
