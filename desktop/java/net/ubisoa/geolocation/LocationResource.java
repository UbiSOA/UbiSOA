package net.ubisoa.geolocation;

import net.ubisoa.geolocation.data.Location;

import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

public class LocationResource extends Resource {
	String platform, signalData, format;
	
	public LocationResource(Context context, Request request, Response response) {
		super(context, request, response);
		this.platform = (String)getRequest().getAttributes().get("platform");
		this.signalData = (String)getRequest().getAttributes().get("signalData");
		this.format = (String)getRequest().getAttributes().get("format");
		
		if (format == null || format.compareTo("json") == 0)
			getVariants().add(new Variant(MediaType.APPLICATION_JSON));
		else getVariants().add(new Variant(MediaType.TEXT_XML));
		
		if (signalData == null) setModifiable(true);
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {		
		if (signalData == null) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Missing data\n", MediaType.TEXT_PLAIN);
		}
		
		System.out.println("ESTIMATION REQUEST!");
		System.out.println(signalData);
		if (platform != null) System.out.println(platform);
		if (format != null) System.out.println(format);
		
		Location estimatedLocation = this.getLocationData().estimate(platform, signalData);
		if (estimatedLocation == null) {
			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return new StringRepresentation("Cannot compute location\n", MediaType.TEXT_PLAIN);
		}
		
		if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
			return new DomRepresentation(MediaType.TEXT_XML, estimatedLocation.toXML());
		}
		
		if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
			JsonRepresentation json = new JsonRepresentation(estimatedLocation.toJSON());
			json.setCharacterSet(CharacterSet.UTF_8);
			return json;
		}
				
		return null;
	}

	@Override
	public void acceptRepresentation(Representation entity) throws ResourceException {
		Form form = new Form(entity);
		String platform = form.getFirstValue("platform");
		String signalData = form.getFirstValue("signalData");
		String latitude = form.getFirstValue("latitude");
		String longitude = form.getFirstValue("longitude");
		
		getLocationData().addSignal(platform, Double.parseDouble(latitude), Double.parseDouble(longitude), signalData);
		
		getResponse().setStatus(Status.SUCCESS_CREATED);
		Representation rep = new StringRepresentation("Signal data stored.", MediaType.TEXT_PLAIN);
		getResponse().setEntity(rep);
	}
	
	protected LocationCore getLocationData() {
		return ((Server) getApplication()).getLocationData();
	}
	
}
