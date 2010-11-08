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
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class FileResource extends ServerResource {
	// TODO: Add documentation.
	
	@Get
	public FileRepresentation representation() {
		MediaType mediaType = MediaType.ALL;
		String type = (String)getRequest().getAttributes().get("type"),
			filename = (String)getRequest().getAttributes().get("filename");
		
		String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
		if (ext.compareTo("ico") == 0)
			mediaType = MediaType.IMAGE_ICON;
		else if (ext.compareTo("png") == 0)
			mediaType = MediaType.IMAGE_PNG;
		else if (ext.compareTo("jpg") == 0)
			mediaType = MediaType.IMAGE_JPEG;
		else if (ext.compareTo("gif") == 0)
			mediaType = MediaType.IMAGE_GIF;
		else if (ext.compareTo("css") == 0)
			mediaType = MediaType.TEXT_CSS;
		else if (ext.compareTo("js") == 0)
			mediaType = MediaType.TEXT_JAVASCRIPT;
				
		Defaults.setServerInfo(this);
		return new FileRepresentation(type + "/" + filename, mediaType);
	}
	
}
