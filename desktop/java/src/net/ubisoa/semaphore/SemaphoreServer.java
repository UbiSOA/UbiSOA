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
package net.ubisoa.semaphore;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class SemaphoreServer extends Application {
	private static InterfaceKitPhidget phidget;

	private static boolean connectPhidget() {
		try {
			phidget = new InterfaceKitPhidget();
			phidget.openAny();
			phidget.waitForAttachment(1000);
			for (int i = 0; i < 4; i++)
				phidget.setOutputState(i, true);
			return true;
		} catch (PhidgetException e) {
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (!connectPhidget()) {
			System.out.println("Cannot start Phidget.");
			return;
		}
		
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8360);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new SemaphoreServer());
		component.start();
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new BaseRouter(getContext());
		router.attach("/", SemaphoreResource.class);
		return router;
	}
	
	public InterfaceKitPhidget getPhidget() {
		return phidget;
	}
}
