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
 * A router for Restlet applications that handles the resources and methods needed to implement
 * a hub service subscriber. Contains a method to send subscription requests that will later
 * be verified by the callback resource. After the subscription is verified, the push messages
 * will be received by the callback resource and notified to the Restlet application.</p>
 * 
 * <p>Applications using this router must implement the {@link PushApplication} interface or a
 * {@link RuntimeException} will be thrown when running the constructor.</p>
 * 
 * <p><strong>Example:</strong> Creating a push router for a Restful application where
 * the main resource is handled by the {@link SubscriberResource} class.</p>
 * <listing>public Restlet createInboundRoot() {
 *	return new PushRouter(getContext(), SubscriberResource.class);<br />}</listing>
 * 
 * @see PushApplication
 * @see BaseRouter
 * 
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class PushRouter extends BaseRouter {
	// TODO: Add support for multiple topic subscriptions.
	// TODO: Implement the synchronized subscription mode.
	
	/**
	 * Creates a Restlet router with the specified resource attached to <code>/</code> and the
	 * {@link CallbackResource} attached to <code>/callback</code>.
	 * 
	 * If the Restlet application from where this router is created does not implements the
	 * {@link PushApplication} interface this constructor will throw an {@link RuntimeException}.
	 * 
	 * @param context			Restlet application context.
	 * @param defaultResource	The resource to attach to "/".
	 */
	public PushRouter(Context context, Class<SubscriberResource> defaultResource) {
		super(context);
		if (getApplication() instanceof PushApplication) {
			attach("/", defaultResource);
			attach("/callback", CallbackResource.class);
			sendSubscriptionRequest();
		} else throw new RuntimeException(
			"Restlet application does not implements the PushApplication interface");
	}
	
	/**
	 * Sends a subscription request to a hub server.
	 * 
	 * The topic to subscribe to, hub service URL, verify token, and other parameters are
	 * obtained from the {@link PushInfo} instance retrieved from the
	 * {@link PushApplication#getPushInfo()} method of the Restlet application using this router.
	 */
	public void sendSubscriptionRequest() {
		PushInfo pushInfo = ((PushApplication)getApplication()).getPushInfo();
		
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
