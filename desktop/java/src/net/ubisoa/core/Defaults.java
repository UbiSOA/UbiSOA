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
package net.ubisoa.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.restlet.data.ServerInfo;
import org.restlet.resource.ServerResource;

/**
 * @author E. Avilés <edgardo@ubisoa.net>
 */
public final class Defaults {
	public static final String AGENT = "UbiSOA-Framework/1.0.1";
	public static final Boolean USE_LOCAL_FILES = false;
	public static final String MAX_CONNECTIONS = "100";
	public static final String MAX_THREADS = "20";

	public static void setServerInfo(ServerResource serverResource) {
		ServerInfo serverInfo = serverResource.getServerInfo();
		serverInfo.setAgent(AGENT);
		serverResource.setServerInfo(serverInfo);
	}
	
	public static SimpleDateFormat dateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public static HttpClient getHttpClient() {
		return new DefaultHttpClient();
	}
	
	public static String getDefaultLeaseDateString() {
		long leaseDate = new Date().getTime() + 1000 * 60 * 60 * 2; // Valid for 2 hours.
		return dateFormat().format(new Date(leaseDate));
	}
	
	public static String getDateString() {
		return dateFormat().format(new Date());
	}
}
