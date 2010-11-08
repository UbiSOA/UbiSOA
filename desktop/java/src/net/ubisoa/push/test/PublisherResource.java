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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class PublisherResource extends BaseResource {
	// TODO: Add documentation.
	
	List<Item> items = ((PublisherTest)getApplication()).getItems();
	HttpClient client = ((PublisherTest)getApplication()).getClient();
	
	@Get("html")
	public StringRepresentation items() {
		String html = "<form class=\"column\" method=\"POST\">" +
			"<h2>Post New Item</h2>" +
			"<input type=\"text\" name=\"title\" placeholder=\"Title\" required autofocus />" +
			"<textarea name=\"content\" placeholder=\"Content\" required></textarea>" +
			"<input type=\"submit\" value=\"Post Item\" /></form>" +
			"<div class=\"column\"><h2>Published Items</h2><ul>";
		for (Item item : items)
			html += "<li><strong>" + item.getTitle() + "</strong>. " +
				item.getContent() + "</li>";
		if (items.size() == 0) html += "<li>No items</li>";
		html += "</ul></div>";
			
		HTMLTemplate template = new HTMLTemplate("Publisher Test", html);
		template.setSubtitle("This is a test for the push protocol.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation itemsXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("items"), child, subChild;
			d.appendChild(root);
			
			for (Item item : items) {
				child = d.createElement("item");
				
				subChild = d.createElement("title");
				subChild.appendChild(d.createTextNode(item.getTitle()));
				child.appendChild(subChild);
				
				subChild = d.createElement("content");
				subChild.appendChild(d.createTextNode(item.getContent()));
				child.appendChild(subChild);
				
				root.appendChild(child);
			}
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
		for (Item item : items) {
			Entry entry = new Entry();
			entry.setId("urn:uuid:" + UUID.randomUUID());
			entry.setTitle(new Text(item.getTitle()));
			Content content = new Content();
			content.setInlineContent(new StringRepresentation(
				item.getContent(), MediaType.TEXT_PLAIN));
			entry.setContent(content);
			feed.getEntries().add(entry);
		}
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed,
			new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation itemsJSON() {
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			JSONArray itemsArray = new JSONArray();
			for (Item item : items) {
				JSONObject obj = new JSONObject();
				obj.put("title", item.getTitle());
				obj.put("content", item.getContent());
				itemsArray.put(obj);
			}
			json.put("items", itemsArray);
			String jsonStr = json.toString();
			if (padding != null)
				jsonStr = padding + "(" + jsonStr + ")";
			return new JsonRepresentation(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Post("form")
	public void acceptItem(Representation entity) {
		Form form = new Form(entity);
		String title = form.getFirstValue("title");
		String content = form.getFirstValue("content");
		Item item = new Item(title, content);
		((PublisherTest)getApplication()).getItems().add(item);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
		
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://127.0.0.1:8311/?output=json"));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost("http://localhost:8310/");
			post.setEntity(paramsEntity);
			HttpResponse res = client.execute(post);
			HttpEntity resEntity = res.getEntity();
			if (resEntity != null) resEntity.consumeContent();			
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
