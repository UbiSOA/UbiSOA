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
package net.ubisoa.common;

import java.util.List;
import java.util.Vector;

import net.ubisoa.core.Defaults;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/** 
 * A specialization of the {@link ServerResource} class that uses the <code>output</code> query
 * parameter to handle content negotiation.</p>
 * 
 * <p>Currently, this class only understands the recommended output representation formats of UbiSOA
 * which are <code>text/html</code>, <code>text/xml</code>, <code>application/atom+xml</code>, and
 * <code>application/json</code>. The actual content negotiation is performed by setting the
 * <code>Accept</code> request header to the content-type of the format specified in the
 * <code>output</code> query parameter.</p>
 * 
 * <p><strong>Example:</strong> A resource extending this class.</p>
 * <listing>public class PublisherResource extends BaseResource {
 * 	…<br />}</listing>
 * 
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class BaseResource extends ServerResource {
	
	/**
	 * Initializes this resource by setting the <code>Accept</code> request header to the content-type
	 * specified in the <code>output</code> query parameter. The default representation is
	 * <code>text/html</code>.
	 */
	@Override
	protected void doInit() throws ResourceException {
		
		// Sets the Accept request header, accordingly to the output query parameter.
		List<Preference<MediaType>> accept = new Vector<Preference<MediaType>>();
		Parameter output = getQuery().getFirst("output");
		if (output != null) {
			String outputChoice = output.getValue();
			if (outputChoice.compareTo("html") == 0)
				accept.add(new Preference<MediaType>(MediaType.TEXT_HTML));
			else if (outputChoice.compareTo("xml") == 0)
				accept.add(new Preference<MediaType>(MediaType.TEXT_XML));
			else if (outputChoice.compareTo("atom") == 0)
				accept.add(new Preference<MediaType>(MediaType.APPLICATION_ATOM));
			else if (outputChoice.compareTo("json") == 0)
				accept.add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
			else setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		} else accept.add(new Preference<MediaType>(MediaType.TEXT_HTML));
		
		// Makes the switch only for the GET method.
		if (getMethod() == Method.GET)
			getRequest().getClientInfo().setAcceptedMediaTypes(accept);
		
		// Sets the server Agent of the response.
		Defaults.setServerInfo(this);
	}

}
