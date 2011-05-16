package net.ubisoa.sensing;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SensingResource extends BaseResource {
	HttpClient client = ((SensingServer)getApplication()).getClient();
	
	@Get("html")
	public StringRepresentation lastReading() {
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		Reading reading = ((SensingServer)getApplication()).getLastReading();		
		String html = "<div><h2>Last Reading</h2>";
		if (reading != null)
			html += "<table>" +
				"<tr><th>Timestamp</th><td>" + reading.getDateTime() + "</td></tr>" +
				"<tr><th>Node ID</th><td>" + reading.getNid() + "</td></tr>" +
				"<tr><th>Light</th><td>" + reading.getLight() + " lx</td></tr>" +
				"<tr><th>Light (Visible)</th><td>" + reading.getLightVisible() + " lx</td></tr>" +
				"<tr><th>Temperature</th><td>" + nf.format(reading.getTemperature()) + "&deg;C</td></tr>" +
				"<tr><th>Temperature (Internal)</th><td>" + nf.format(reading.getTemperatureInternal()) + "&deg;C</td></tr>" +
				"<tr><th>Voltage</th><td>" + nf.format(reading.getVoltage()) + " V</td></tr>" +
				"<tr><th>Humidity</th><td>" + nf.format(reading.getHumidity()) + "%</td></tr>" +
				"<tr><th>Microphone</th><td>" + reading.getMicrophone() + "</td></tr>" +
				"</table>";
		else html += "<p>Cannot read database.</p>";
		html += "</div>";
		HTMLTemplate template = new HTMLTemplate("Sensing Server", html);
		template.setSubtitle("This is a sensing readings server.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation lastReadingXml() {
		Reading reading = ((SensingServer)getApplication()).getLastReading();
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("reading"), child;
			d.appendChild(root);
			
			child = d.createElement("timestamp");
			child.appendChild(d.createTextNode(reading.getDateTime()));
			root.appendChild(child);
			
			child = d.createElement("nodeId");
			child.appendChild(d.createTextNode(Integer.toString(reading.getNid())));
			root.appendChild(child);
			
			child = d.createElement("light");
			child.appendChild(d.createTextNode(Integer.toString(reading.getLight())));
			root.appendChild(child);
			
			child = d.createElement("lightVisible");
			child.appendChild(d.createTextNode(Integer.toString(reading.getLightVisible())));
			root.appendChild(child);
			
			child = d.createElement("temperature");
			child.appendChild(d.createTextNode(Double.toString(reading.getTemperature())));
			root.appendChild(child);
			
			child = d.createElement("temperatureInternal");
			child.appendChild(d.createTextNode(Double.toString(reading.getTemperatureInternal())));
			root.appendChild(child);
			
			child = d.createElement("voltage");
			child.appendChild(d.createTextNode(Double.toString(reading.getVoltage())));
			root.appendChild(child);
			
			child = d.createElement("humidity");
			child.appendChild(d.createTextNode(Double.toString(reading.getHumidity())));
			root.appendChild(child);
			
			child = d.createElement("microphone");
			child.appendChild(d.createTextNode(Integer.toString(reading.getMicrophone())));
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
	public Representation itemsAtom() {
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		Reading reading = ((SensingServer)getApplication()).getLastReading();
		String html = "<table>" +
			"<tr><th>Timestamp</th><td>" + reading.getDateTime() + "</td></tr>" +
			"<tr><th>Node ID</th><td>" + reading.getNid() + "</td></tr>" +
			"<tr><th>Light</th><td>" + reading.getLight() + " lx</td></tr>" +
			"<tr><th>Light (Visible)</th><td>" + reading.getLightVisible() + " lx</td></tr>" +
			"<tr><th>Temperature</th><td>" + nf.format(reading.getTemperature()) + "&deg;C</td></tr>" +
			"<tr><th>Temperature (Internal)</th><td>" + nf.format(reading.getTemperatureInternal()) + "&deg;C</td></tr>" +
			"<tr><th>Voltage</th><td>" + nf.format(reading.getVoltage()) + " V</td></tr>" +
			"<tr><th>Humidity</th><td>" + nf.format(reading.getHumidity()) + "%</td></tr>" +
			"<tr><th>Microphone</th><td>" + reading.getMicrophone() + "</td></tr>" +
			"</table>";
		
		Feed feed = new Feed();
		
			Entry entry = new Entry();
			entry.setId("urn:uuid:" + UUID.randomUUID());
			entry.setTitle(new Text(reading.getDateTime()));
			Content content = new Content();
			content.setInlineContent(new StringRepresentation(html, MediaType.TEXT_HTML));
			entry.setContent(content);
			feed.getEntries().add(entry);
		
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed,
			new Variant(MediaType.APPLICATION_ATOM), this);
	}

	@Get("json")
	public JsonRepresentation lastReadingJson() {
		Reading reading = ((SensingServer)getApplication()).getLastReading();
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			json.put("timestamp", reading.getDateTime());
			json.put("nodeId", reading.getNid());
			json.put("light", reading.getLight());
			json.put("lightVisible", reading.getLightVisible());
			json.put("temperature", reading.getTemperature());
			json.put("temperatureInternal", reading.getTemperatureInternal());
			json.put("voltage", reading.getVoltage());
			json.put("humidity", reading.getHumidity());
			json.put("microphone", reading.getMicrophone());
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
}
