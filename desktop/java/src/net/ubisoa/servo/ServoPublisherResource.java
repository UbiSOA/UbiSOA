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
import java.io.UnsupportedEncodingException;
import java.lang.Math;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;
//import net.ubisoa.push.test.Item;
//import net.ubisoa.push.test.PublisherTest;
import net.ubisoa.servo.PublisherTest;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.atom.AtomConverter;
//import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Text;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author V. Soto <valeria@ubisoa.net>
 */
public class ServoPublisherResource extends BaseResource {

	Servo servo = ((PublisherTest)getApplication()).getServo();
	List<Integer> posts = ((PublisherTest)getApplication()).getPosts();
	HttpClient client = ((PublisherTest)getApplication()).getClient();
	double valor = 0;
	long redondeo = 0;
	
	@Get("html")
	public StringRepresentation items() {
		
		try
		{
			if (posts != null) {
				valor = posts.get(posts.size() - 1);
				//redondeo = Math.round(((Math.PI*valor)/180));
				redondeo = (long)((Math.PI*valor)/180);
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		String html = "<form class=\"column\" name=\"fservo\" method=\"POST\">" +
			"<h2>Post Servo</h2>" +
			"<img id=\"servo\" name=\"servo\" src=\"/img/gear1_w.gif\" height=\"50\" width=\"50\" />" +
			"<input type=\"range\" name=\"position\" id=\"position\" min=\"0\" max=\"180\" value=\"" + String.valueOf(valor) + "\" required />" +
			"</form>" +
			"<div class=\"column\"><h2>Published Servos - Position</h2><ul>";
		if (posts != null) {
			html += "<img src=\"http://chart.apis.google.com/chart?chs=350x150&chd=t:1,100&chp=" +
			(int)redondeo + "&cht=p3&chco=dd0000,dd0000,ffffff&chl=" +
			String.valueOf(valor) + "\" alt=\"Ubisoa Servo\" />"; 
			int j = posts.size();
			for (int i = j - 1; i >= 0; i--)
				html += "<li><strong>" + posts.get(i) + "</strong>. ";
		}
		if (posts == null)
			html += "<li>No servo posted</li>";
		html += "</ul></div>";
			
		HTMLTemplate template = new HTMLTemplate("Publisher Test for Servo", html);
		template.setSubtitle("This is a test for the push protocol.");
		template.getScripts().add("/js/servo.js");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation itemsXML() {
		try {
			System.out.println("servopublisher...xml");
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("servos"), child, subChild;
			d.appendChild(root);
			
			//for (Servo servo : servos) {
				child = d.createElement("servo");
				
				subChild = d.createElement("deviceClass");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceClass())));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceID");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceID())));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceLabel");
				subChild.appendChild(d.createTextNode(servo.getDeviceLabel()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceName");
				subChild.appendChild(d.createTextNode(servo.getDeviceName()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceType");
				subChild.appendChild(d.createTextNode(servo.getDeviceType()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceVersion");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceVersion())));
				child.appendChild(subChild);
				
				subChild = d.createElement("motorCount");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getMotorCount())));
				child.appendChild(subChild);
				
				subChild = d.createElement("position");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPositionPost())));
				child.appendChild(subChild);
				
				/*subChild = d.createElement("positionMax");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPositionMax(servo.getDeviceID()))));
				child.appendChild(subChild);
				
				subChild = d.createElement("positionMin");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPositionMin(servo.getDeviceID()))));
				child.appendChild(subChild);
				
				/*subChild = d.createElement("serialNumber");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getSerialNumber())));
				child.appendChild(subChild);
				
				subChild = d.createElement("serialAddress");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(servo.getServerAddress()));
				child.appendChild(subChild);*/
				
				root.appendChild(child);
			//}*/
			d.normalizeDocument();
			return new DomRepresentation(MediaType.TEXT_XML, d);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Get("atom")
	public Representation itemsAtom() {
		Feed feed = new Feed();
		Entry entry = new Entry();
		entry.setId("urn:uuid:" + UUID.randomUUID());
		entry.setTitle(new Text(servo.getDeviceName()));
		entry.setTitle(new Text(String.valueOf(servo.getPositionPost())));
		feed.getEntries().add(entry);
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed,
			new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation itemsJSON() {
		try {
			JSONObject json = new JSONObject();
			JSONArray itemsArray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("name", servo.getDeviceName());
			obj.put("position", servo.getPositionPost());
			itemsArray.put(obj);
			json.put("servos", itemsArray);
			return new JsonRepresentation(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Post("form")
	public void acceptItem(Representation entity) {
		
		Form form = new Form(entity);
		
		try {
			String t = form.getFirstValue("position");
			if (t == "null")
				t = "0";
			servo.position(Double.parseDouble(t));
			if (((PublisherTest)getApplication()).getPosts().size() >= 8)
				((PublisherTest)getApplication()).getPosts().remove(0);
			((PublisherTest)getApplication()).getPosts().add((int)servo.getPositionPost());
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/");
			
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://127.0.0.1:8315/?output=json"));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost("http://localhost:8310/");
			post.setEntity(paramsEntity);
			Defaults.getHttpClient().execute(post);
			getLogger().info("The Hubbub ping was sent successfully.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
