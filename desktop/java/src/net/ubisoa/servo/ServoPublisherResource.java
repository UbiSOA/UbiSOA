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
import org.restlet.ext.atom.Content;
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

	//Servo servo = new Servo();
	Servo servo = null;
	List<Servo> servos = ((PublisherTest)getApplication()).getServos();
	HttpClient client = ((PublisherTest)getApplication()).getClient();
	
	@Get("html")
	public StringRepresentation items() {
		String html = "<form class=\"column\" method=\"POST\">" +
			"<h2>Post Servo</h2>" +
			"<input type=\"range\" id=\"position\" step=\"1.0\" min=\"0\" max=\"180\" value=\"0\" placeholder=\"Position\" required />" +
			//"<textarea name=\"content\" placeholder=\"Content\" required></textarea>" +
			"<input type=\"submit\" value=\"Post Servo\" /></form>" +
			"</form>" +
			"<div class=\"column\"><h2>Published Servos</h2><ul>";// +
			//"<img src=\"img\\gear1_w.gif\" alt=\"servo\"  />";
		for (Servo serv : servos)
			html += "<li><strong>" + serv.getDeviceName() + "</strong>. " +
			"<label>Device ID: </label>" + serv.getDeviceID() +// "<?php echo '$_POST'; ?>" + 
			"<label> Position: </label>" + serv.getPositionPost() + "</li>";
		if (servos == null)
			html += "<li>No servo posted</li>";
		html += "</ul></div>";
			
		HTMLTemplate template = new HTMLTemplate("Publisher Test for Servo", html);
		template.setSubtitle("This is a test for the push protocol.");
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
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceClass())));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceID");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceID())));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceLabel");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(servo.getDeviceLabel()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceName");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(servo.getDeviceName()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceType");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(servo.getDeviceType()));
				child.appendChild(subChild);
				
				subChild = d.createElement("deviceVersion");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getDeviceVersion())));
				child.appendChild(subChild);
				
				subChild = d.createElement("motorCount");
				//subChild.appendChild(d.createTextNode("X"));
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getMotorCount())));
				child.appendChild(subChild);
				
				/*subChild = d.createElement("position");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPosition(servo.getDeviceID()))));
				child.appendChild(subChild);
				
				subChild = d.createElement("positionMax");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPositionMax(servo.getDeviceID()))));
				child.appendChild(subChild);
				
				subChild = d.createElement("positionMin");
				subChild.appendChild(d.createTextNode(String.valueOf(servo.getPositionMin(servo.getDeviceID()))));
				child.appendChild(subChild);*/
				
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
	
	/*@Get("atom")
	public Representation itemsAtom() {
		Feed feed = new Feed();
		//for (Servo servo : servos) {
			Entry entry = new Entry();
			entry.setId("urn:uuid:" + UUID.randomUUID());
			//entry.setTitle(new Text(servo.getName()));
			entry.setTitle(new Text(servo.getDeviceName()));
			Content content = new Content();
			content.setInlineContent(new Representation(
				String.valueOf(servo.getDeviceClass()), MediaType.TEXT_PLAIN));
			entry.setContent(content);
			feed.getEntries().add(entry);
		//}
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed,
			new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation itemsJSON() {
		try {
			JSONObject json = new JSONObject();
			JSONArray itemsArray = new JSONArray();
			for (Servo servo : servos) {
				JSONObject obj = new JSONObject();
				obj.put("name", servo.getName());
				obj.put("position", servo.getPosition());
				itemsArray.put(obj);
			}
			json.put("servos", itemsArray);
			return new JsonRepresentation(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}*/
	
	@Post("form")
	public void acceptItem(Representation entity) {
		//System.out.println("servopublisher...form");
		Form form = new Form(entity);
		System.out.println("posPost= " + form.getFirstValue("position"));
		//double position = 0.0;
		//if (form.getFirstValue("position") != null)
			double position = Double.valueOf(form.getFirstValue("position"));
		servo = new Servo(position);
		((PublisherTest)getApplication()).getServos().add(servo);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
		
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://127.0.0.1:8315/?output=json"));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost("http://localhost:8315/");
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
