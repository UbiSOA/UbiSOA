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
package net.ubisoa.push.test;

import java.util.UUID;

import net.ubisoa.core.Defaults;
import net.ubisoa.push.PushApplication;
import net.ubisoa.push.PushInfo;
import net.ubisoa.push.PushRouter;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class SubscriberTest extends Application implements PushApplication {
	// TODO: Add documentation.
	
	private final PushInfo pushInfo = new PushInfo(
		"http://127.0.0.1:8310/", "http://127.0.0.1:8311/?output=json",
		"http://127.0.0.1:8312/callback", UUID.randomUUID().toString());
	private HttpClient client = Defaults.getHttpClient();
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8312);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new SubscriberTest());
		component.start();
	}

	@Override
	public Restlet createInboundRoot() {
		return new PushRouter(getContext(), SubscriberResource.class);
	}

	@Override
	public PushInfo getPushInfo() {
		return pushInfo;
	}

	@Override
	public void pushCallback(String data) {
		getLogger().info("Received a push message!\n\t" + data);
	}
	
	public HttpClient getClient() {
		return client;
	}

}
