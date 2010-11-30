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
package net.ubisoa.argenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;
import net.ubisoa.push.test.Item;

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
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author I. Cruz <icruz@ubisoa.net>
 */
public class ResourceARGenerator extends BaseResource {
	
	private int grados = 15;
	List<Item> items = ((PublisherTestAR)getApplication()).getItems();
	HttpClient client = ((PublisherTestAR)getApplication()).getClient();
		
	@Get("html")
	public StringRepresentation temperatures() throws Exception {
		if(items.size()!=0){
			grados = Integer.parseInt(items.get(items.size()-1).getContent());
		}
		String html = "<form class=\"column\" method=\"POST\">" +
			"<h2>Post New Temperature</h2>" +
			"<input type=\"range\" id=\"grados\" name=\"grados\" min=\"-20\" max=\"50\" value='"+ grados +"'/>" +
			"<div id='amount'>"+ grados +" °C</div><input type=\"submit\" value=\"Post Temperature\" /></form>" +
			"<img src='img/chartAR.png'/><div class=\"column\"><h2>Last Ten Temperatures</h2> <ul>";
		
		Item item = null;
		for (int i=items.size()-1;i>=0;i--){
			item = items.get(i);
			html += "<li><strong>" + (i+1) + "</strong>. " +
				item.getContent() + "</li>";
		}
		if (items.size() == 0) html += "<li>No items</li>";
		html += "</ul></div>";
		HTMLTemplate template = new HTMLTemplate("Publisher Temperatures", html);
		template.getScripts().add("js/argenerator.js");
		template.setSubtitle("This use the Resource Temperature Augmented Reality.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("png")
	public FileRepresentation chartAR()throws Exception {
		File img = new File("img/chartAR.png");
		if(items.size()!=0){
			Item item = items.get(items.size()-1);
			grados = Integer.parseInt(item.getContent());
		}
		
		URL url = new URL("http://chart.apis.google.com/chart?cht=gom&chd=t:" + grados + "&chs=250x150&chds=-20,50&chl=" + grados + "°C" );
		java.net.URLConnection con = url.openConnection();
		con.connect();		
		java.io.InputStream urlfs = con.getInputStream();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int c;
		while ((c = urlfs.read()) != -1){
			out.write((byte) c);
		}		
		urlfs.close();
		
		ByteArrayInputStream outImg = new ByteArrayInputStream(out.toByteArray());
		BufferedImage bufferedImage = ImageIO.read(outImg);
		ImageIO.write(bufferedImage, "png", img);
		
		FileRepresentation representation = new FileRepresentation(
				"img/chartAR.png", MediaType.IMAGE_PNG);
		if (representation.getSize() == 0) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
		return representation;
	}
	
	@Get("xml")
	public DomRepresentation historyXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("History"), child, subChild;
			d.appendChild(root);
			
			Item item = null;
			for (int i=items.size()-1;i>=0;i--){
				item = items.get(i);
				child = d.createElement("Values");
				
				subChild = d.createElement("id");
				subChild.appendChild(d.createTextNode(String.valueOf(i+1)));
				child.appendChild(subChild);
					
				subChild = d.createElement("temperatura");
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
	
	@Get("json")
	public JsonRepresentation historyJson() {
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			JSONArray itemsArray = new JSONArray();
			Item item = null;
			for (int i=items.size()-1;i>=0;i--){
				item = items.get(i);
				JSONObject obj = new JSONObject();
				obj.put("grados", item.getContent());
				obj.put("id", String.valueOf(i+1));
				itemsArray.put(obj);
				
			}
			json.put("History", itemsArray);
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
	
	@Get("atom")
	public Representation itemsAtom() {
		Feed feed = new Feed();
		Item item = null;
		for (int i=items.size()-1;i>=0;i--){
			item = items.get(i);
			Entry entry = new Entry();
			entry.setId("urn:uuid:" + UUID.randomUUID());
			entry.setTitle(new Text(String.valueOf(i+1)));
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
	
	@Post("form")
	public void acceptChartAR(Representation entity) throws Exception {
		Form form = new Form(entity);	
		
		String grd = form.getFirstValue("grados");
		grados = Integer.parseInt(grd);
		Item item = new Item("", grd);
		((PublisherTestAR)getApplication()).getItems().add(item);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
		chartAR();
		if(items.size()>10)
			((PublisherTestAR)getApplication()).getItems().remove(0);
				
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://localhost:8317/?output=html"));
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
