package net.ubisoa.semaphore;

import java.util.Date;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

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

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

public class SemaphoreResource extends BaseResource {
	private static final int COLOR_BLUE = 0;
	private static final int COLOR_GREEN = 1;
	private static final int COLOR_YELLOW = 2;
	private static final int COLOR_RED = 3;
	
	private InterfaceKitPhidget phidget = ((SemaphoreServer)getApplication()).getPhidget();

	private boolean ledIsOn(int ledId) {
		try {
			return phidget.getOutputState(ledId);
		} catch (PhidgetException e) {
			return false;
		}
	}
	
	@Get("html")
	public StringRepresentation semaphore() {
		String html = "<script type=\"text/javascript\">" +
				"function click(sender) { var target = $('#' + $(sender).html().toLowerCase()); target.attr('checked', $(sender).hasClass('on') ? '' : 'checked'); $(sender).toggleClass('on'); $('#form').submit(); }" +
				"</script>" +
			"<style>.led { border: 2px solid black; border-radius: 50px; width: 50px; " +
			"padding: 15px 0; text-align: center; float: left; margin-right: 4px; background: " +
			"-webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, from(#EEE), to(#AAA)); " +
			"color: #555; text-shadow: rgba(255, 255, 255, 0.8) 0 1px 0; cursor: pointer; } .led.on { " +
			"border-color: black; color: white; text-shadow: black 0px -1px 0px; } .led.blue.on " +
			"{ background: -webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, from(#6AC1FF), " +
			"to(#2C63FF)); } .led.green.on { background: -webkit-gradient(radial, 50% 30%, 0, " +
			"50% 0%, 50, from(#adffa5), to(#00D325)); } .led.yellow.on { background: " +
			"-webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, from(#FFF593), to(#FFA90D)); } " +
			".led.red.on { background: -webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, " +
			"from(#ff9d62), to(#FF3000)); }</style>" +
			"<div class=\"led blue " + (ledIsOn(COLOR_BLUE)? "on" : "off") + "\" onclick=\"click(this)\">Blue</div>" +
			"<div class=\"led green " + (ledIsOn(COLOR_GREEN)? "on" : "off") + "\" onclick=\"click(this)\">Green</div>" +
			"<div class=\"led yellow " + (ledIsOn(COLOR_YELLOW)? "on" : "off") + "\" onclick=\"click(this)\">Yellow</div>" +
			"<div class=\"led red " + (ledIsOn(COLOR_RED)? "on" : "off") + "\" onclick=\"click(this)\">Red</div>" +
			"<div style=\"clear: both\"><br /><form id=\"form\" method=\"POST\"><div style=\"display:none\">" +
			"<input type=\"checkbox\" id=\"blue\" name=\"blue\" value=\"on\"" +
			(ledIsOn(COLOR_BLUE)? "checked": "") + "/> Blue<br />" +
			"<input type=\"checkbox\" id=\"green\" name=\"green\" value=\"on\"" +
			(ledIsOn(COLOR_GREEN)? "checked": "") + "/> Green<br />" +
			"<input type=\"checkbox\" id=\"yellow\" name=\"yellow\" value=\"on\"" +
			(ledIsOn(COLOR_YELLOW)? "checked": "") + "/> Yellow<br />" +
			"<input type=\"checkbox\" id=\"red\" name=\"red\" value=\"on\"" +
			(ledIsOn(COLOR_RED)? "checked": "") + "/> Red<br />" +
			"<input type=\"submit\" value=\"Submit\" /></div></form>";
		
		HTMLTemplate template = new HTMLTemplate("Semaphore Server", html);
		template.setSubtitle("This is a semaphore server.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation semaphoreXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = d.createElement("semaphore"), child;
			d.appendChild(root);
			
			child = d.createElement("blue");
			child.appendChild(d.createTextNode((ledIsOn(COLOR_BLUE)? "on" : "off")));
			root.appendChild(child);
			
			child = d.createElement("green");
			child.appendChild(d.createTextNode((ledIsOn(COLOR_GREEN)? "on" : "off")));
			root.appendChild(child);
			
			child = d.createElement("yellow");
			child.appendChild(d.createTextNode((ledIsOn(COLOR_YELLOW)? "on" : "off")));
			root.appendChild(child);
			
			child = d.createElement("red");
			child.appendChild(d.createTextNode((ledIsOn(COLOR_RED)? "on" : "off")));
			root.appendChild(child);
			
			d.normalizeDocument();
			return new DomRepresentation(MediaType.TEXT_XML, d);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Get("atom")
	public Representation semaphoreAtom() {
		String html ="<table>" +
			"<tr><th>Blue</th><td>" + (ledIsOn(COLOR_BLUE)? "on" : "off") + "</td></tr>" +
			"<tr><th>Green</th><td>" + (ledIsOn(COLOR_GREEN)? "on" : "off") + "</td></tr>" +
			"<tr><th>Yellow</th><td>" + (ledIsOn(COLOR_YELLOW)? "on" : "off") + "</td></tr>" +
			"<tr><th>Red</th><td>" + (ledIsOn(COLOR_RED)? "on" : "off") + "</td></tr>" +
			"</table>";
		Feed feed = new Feed();
		Entry entry = new Entry();
		entry.setId("urn:uuid:" + UUID.randomUUID());
		entry.setTitle(new Text("Semaphore Status"));
		Content content = new Content();
		content.setInlineContent(new StringRepresentation(html, MediaType.TEXT_HTML));
		entry.setContent(content);
		feed.getEntries().add(entry);
		
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed, new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation semaphoreJson() {
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			json.put("blue", ledIsOn(COLOR_BLUE)? "on" : "off");
			json.put("green", ledIsOn(COLOR_GREEN)? "on" : "off");
			json.put("yellow", ledIsOn(COLOR_YELLOW)? "on" : "off");
			json.put("red", ledIsOn(COLOR_RED)? "on" : "off");
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
		try {
			Form form = new Form(entity);
		
			String blue = form.getFirstValue("blue");
			boolean blueIsOn = blue != null && blue.equals("on");
				phidget.setOutputState(COLOR_BLUE, blueIsOn);
		
			String green = form.getFirstValue("green");
			boolean greenIsOn = green != null && green.equals("on");
			phidget.setOutputState(COLOR_GREEN, greenIsOn);
			
			String yellow = form.getFirstValue("yellow");
			boolean yellowIsOn = yellow != null && yellow.equals("on");
			phidget.setOutputState(COLOR_YELLOW, yellowIsOn);
			
			String red = form.getFirstValue("red");
			boolean redIsOn = red != null && red.equals("on");
			phidget.setOutputState(COLOR_RED, redIsOn);
			
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/?t=" + (new Date()).getTime());
		} catch (PhidgetException e) {
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
			setLocationRef("/?t=" + (new Date()).getTime());
		}
	}
	
}
