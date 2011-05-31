package net.ubisoa.servo;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

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

import com.phidgets.PhidgetException;
import com.phidgets.ServoPhidget;

public class ServoResource extends BaseResource {
	private ServoPhidget phidget = ((ServoServer)getApplication()).getPhidget();

	@Get("html")
	public StringRepresentation servoMotor() throws PhidgetException {
		double minimum = phidget.getPositionMin(0);
		double current = phidget.getPosition(0);
		double maximum = phidget.getPositionMax(0);
		
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		
		String html = "<p>Current position: " + numberFormat.format(minimum) + " &lt; <strong>" +
			numberFormat.format(current) + "</strong> &lt; " +
			numberFormat.format(maximum) + "</p><br />" + 
			"<form id=\"form\" method=\"POST\">" +
			"<input type=\"text\" name=\"position\" placeholder=\"" +
			numberFormat.format(current) + "\"/>" +
			"<input type=\"submit\" value=\"Submit\" /></form>";
		
		HTMLTemplate template = new HTMLTemplate("Servo Motor Server", html);
		template.setSubtitle("This is a servo motor server.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation semaphoreXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = d.createElement("servoMotor"), child;
			d.appendChild(root);
			
			child = d.createElement("minimum");
			child.appendChild(d.createTextNode(Double.toString(phidget.getPositionMin(0))));
			root.appendChild(child);
			
			child = d.createElement("current");
			child.appendChild(d.createTextNode(Double.toString(phidget.getPosition(0))));
			root.appendChild(child);
			
			child = d.createElement("maximum");
			child.appendChild(d.createTextNode(Double.toString(phidget.getPositionMax(0))));
			root.appendChild(child);
			
			d.normalizeDocument();
			return new DomRepresentation(MediaType.TEXT_XML, d);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Get("atom")
	public Representation semaphoreAtom() throws PhidgetException {
		String html ="<table>" +
			"<tr><th>Minimum</th><td>" + Double.toString(phidget.getPositionMin(0)) + "</td></tr>" +
			"<tr><th>Current</th><td>" + Double.toString(phidget.getPosition(0)) + "</td></tr>" +
			"<tr><th>Maximum</th><td>" + Double.toString(phidget.getPositionMax(0)) + "</td></tr>" +
			"</table>";
		Feed feed = new Feed();
		Entry entry = new Entry();
		entry.setId("urn:uuid:" + UUID.randomUUID());
		entry.setTitle(new Text("Servo Motor Status"));
		Content content = new Content();
		content.setInlineContent(new StringRepresentation(html, MediaType.TEXT_HTML));
		entry.setContent(content);
		feed.getEntries().add(entry);
		
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed, new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation semaphoreJson() throws PhidgetException {
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			json.put("minimum", Double.toString(phidget.getPositionMin(0)));
			json.put("current", Double.toString(phidget.getPosition(0)));
			json.put("maximum", Double.toString(phidget.getPositionMax(0)));
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
			String valueStr = form.getFirstValue("position");
			double value;
			try {
				value = Double.parseDouble(valueStr);
			} catch (Exception e) {
				value = 0.0;
			}
			phidget.setPosition(0, value);
			
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/?t=" + (new Date()).getTime());
		} catch (PhidgetException e) {
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
			setLocationRef("/?t=" + (new Date()).getTime());
		}
	}
}
