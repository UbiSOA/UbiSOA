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

import net.ubisoa.core.Defaults;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * A resource representing the common favicon image for all the services in the project.</p>
 * 
 * <p><strong>Example:</strong> Attaching the resource to a Restlet container.</p>
 * <listing>public BaseRouter(Context context) {
 *	…
 *	attach("/favicon.ico", FaviconResource.class);<br />}</listing> 
 * 
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class FaviconResource extends ServerResource {
	
	/**
	 * Retrieves a {@link FileRepresentation} containing the common favicon image for all
	 * the services in the project.
	 * 
	 * @return The favicon image.
	 */
	@Get
	public FileRepresentation representation() {
		Defaults.setServerInfo(this);
		FileRepresentation representation = new FileRepresentation(
			"img/favicon.ico", MediaType.IMAGE_ICON);
		if (representation.getSize() == 0) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
		return representation;
	}
	
}
