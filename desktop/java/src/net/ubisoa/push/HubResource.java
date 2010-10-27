package net.ubisoa.push;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class HubResource extends BaseResource {
	HubServer server = (HubServer)getApplication();
	
	@Get("html")
	public StringRepresentation getHTML() {
		String html = "<form method=\"POST\" style=\"margin-bottom: 12px\">" +
			"<h2>Publish Topic</h2><input type=\"hidden\" name=\"hub.mode\" value=\"publish\" />" +
			"<input type=\"text\" name=\"hub.url\" placeholder=\"Topic URL\" " +
			"style=\"width: 300px; display: inline-block\" />" +
			"<input type=\"submit\" value=\"Submit\" style=\"margin-left: 8px\" /></form>" +
			"<div id=\"topics\"><h2>Published Topics</h2><table>" +
			"<tr><th>Topic</th><th>Last Fetch</th><th>Subscribers</th><th>Last Ping</th></tr>";
		
		html += server.getHTMLTableData() + "</table></div>";
		HTMLTemplate template = new HTMLTemplate("Hub Server", html);
		template.setSubtitle("An implementation of the " +
			"<a href=\"http://pubsubhubbub.googlecode.com/\">PubSubHubbub</a> prototocol.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Post("form")
	public void handlePost(Representation entity) {
		getLogger().info("New request received.");
		
		Form form = new Form(entity);
		String mode = form.getFirstValue("hub.mode");

		if (mode.equals("subscribe")) {
			String verify = form.getFirstValue("hub.verify");
			if (!verify.equals("async")) {
				setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
				return;
			}
			
			String callback = form.getFirstValue("hub.callback");
			String topic = form.getFirstValue("hub.topic");
			String token = form.getFirstValue("hub.verify_token");
			server.handleSubscriptionRequest(callback, topic, token);
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		else if (mode.equals("publish")) {
			String url = form.getFirstValue("hub.url");
			server.handleNewContentNotification(url);
			setStatus(Status.SUCCESS_NO_CONTENT);
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/");
		}
		else setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

}
