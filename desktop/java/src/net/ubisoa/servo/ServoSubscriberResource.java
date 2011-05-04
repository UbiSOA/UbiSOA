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
package net.ubisoa.servo;

import java.io.IOException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * @author V. Soto <valeria@ubisoa.net>
 */
public class ServoSubscriberResource extends BaseResource {
	
	@Get
	public StringRepresentation items() {		
		String html = "<h2>Published Servos</h2>", items = "";
		
		try {
			HttpGet get = new HttpGet("http://127.0.0.1:8311/?output=json");
			HttpResponse response = Defaults.getHttpClient().execute(get);
			String content = EntityUtils.toString(response.getEntity());
			
			JSONObject jsonObj = new JSONObject(content);
			JSONArray jsonArray = jsonObj.getJSONArray("servos");
			items = "";
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonItem = jsonArray.getJSONObject(i);
				items = "<li><strong>" + jsonItem.getString("title") + ".</strong> " +
					jsonItem.getString("content") + "</li>" + items;
			}
			html += "<ul>" + items + "</ul>";
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		HTMLTemplate template = new HTMLTemplate("Subscriber Test", html);
		template.setSubtitle("This is a test of the PubSubHubbub server.");
		template.getScripts().add("http://api.ubisoa.net/js/subscriber-test.js");
		template.getStylesheets().add("http://api.ubisoa.net/css/subscriber-test.css");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}	
}
