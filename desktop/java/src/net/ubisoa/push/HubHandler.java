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

import net.ubisoa.core.Defaults;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class HubHandler extends Thread {
	private HubServer hubApp;
	
	public HubHandler(HubServer hubApp) {
		this.hubApp = hubApp;
	}
	
	@Override
	public void run() {
		for (;;) {
			try {
				synchronized (this) {
					// Looking for subscriptions to verify.
					hubApp.getLogger().info("Looking for subscriptions to verify…");
					for (Subscription sub : hubApp.getSubscriptions())
						if (!sub.getVerified())
							new HubSubVerifier(
								hubApp.getDefaultClient(), sub, hubApp.getLogger()).start();
					
					// Looking for notifications to send.
					hubApp.getLogger().info("Looking for notifications to send…");
					while (hubApp.getNotificationsQueue().peek() != null) {
						Topic topic = hubApp.getNotificationsQueue().poll();
						
						// Updating the topic's statistics.
						int topicIndex = -1;
						for (Topic topicObj : hubApp.getTopics())
							if (topicObj.getTopic().equals(topic.getTopic()))
								topicIndex = hubApp.getTopics().indexOf(topicObj);
						if (topicIndex != -1) {
							topic = hubApp.getTopics().get(topicIndex);
							topic.setLastPing(Defaults.getDateString());
						}
						
						// Sending notifications to each subscriber.						
						for (Subscription sub : hubApp.getSubscriptions())
							if (sub.getTopic().equals(topic.getTopic()) && sub.getVerified())
								new HubNotifier(
									hubApp.getDefaultClient(), sub, hubApp.getLogger()).start();
					}
				
					// Waiting until a thread notification arrives.
					hubApp.getLogger().info("Waiting…");
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
