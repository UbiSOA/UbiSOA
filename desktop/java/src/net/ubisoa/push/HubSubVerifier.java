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
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class HubSubVerifier extends Thread {
	private HttpClient client;
	private Subscription sub;
	private Logger logger;
	
	public HubSubVerifier(HttpClient client, Subscription sub, Logger logger) {
		super();
		this.client = client;
		this.sub = sub;
		this.logger = logger;
	}

	@Override
	public void run() {
		logger.info("Verificating a pending subscription.\n\tTopic: " +	sub.getTopic() +
			"\n\tCallback: " + sub.getCallback());
		String challenge = UUID.randomUUID().toString();
		String callbackURL = sub.getCallback() + "?hub.mode=subscribe&hub.topic=" + sub.getTopic() +
			"&hub.challenge=" +	challenge + "&hub.verify_token=" + sub.getToken();

		try {
			// Trying to get an echo of the challenge from the callback.
			HttpGet get = new HttpGet(callbackURL);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			String challengeEcho = "";
			if (entity != null) {
				challengeEcho = EntityUtils.toString(entity);
				entity.consumeContent();
			}
			
			// Challenge successful, subscription is verified.
			if (challengeEcho.equals(challenge)) {
				sub.setVerified(true);
				logger.info("Subscription was successfully verified.");
				return;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Subscription wasn't valid.");
	}
}
