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
import java.util.List;
import java.util.Vector;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.push.test.SubscriberResource;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.restlet.Context;

/**
 * TODO: Add support for multiple topic subscriptions.
 * TODO: Implement the synchronized subscription mode.
 * 
 * @author E. Avilés <edgardo@ubisoa.net>
 */
public class PushRouter extends BaseRouter {
	public PushRouter(Context context, Class<SubscriberResource> defaultResource, PushInfo pushInfo) {
		super(context);
		attach("/", defaultResource);
		attach("/callback", CallbackResource.class);
		sendSubscriptionRequest(pushInfo);
	}
	
	public void sendSubscriptionRequest(final PushInfo pushInfo) {
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.callback", pushInfo.getCallback()));
			params.add(new BasicNameValuePair("hub.mode", "subscribe"));
			params.add(new BasicNameValuePair("hub.topic", pushInfo.getTopic()));
			params.add(new BasicNameValuePair("hub.verify", "async"));
			params.add(new BasicNameValuePair("hub.verify_token", pushInfo.getToken()));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost(pushInfo.getHub());
			post.setEntity(paramsEntity);
			Defaults.getHttpClient().execute(post);
			getLogger().info("The Hubbub subscription was sent successfully.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
