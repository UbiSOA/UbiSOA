/*
 * Copyright (c) 2010, Edgardo Avilés-López <edgardo@ubisoa.net>
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
