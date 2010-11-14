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
import java.util.Vector;

import javax.imageio.ImageIO;

import net.ubisoa.common.BaseResource;
import net.ubisoa.core.Defaults;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author I. Cruz <icruz@ubisoa.net>
 */
public class ResourceARGenerator extends BaseResource {
	
	private int grados = 50;
	
	@Get("png")
	public FileRepresentation representation()throws Exception {
		
		File img = new File("chartAR.png");
		
		URL url = new URL("http://chart.apis.google.com/chart?cht=gom&chd=t:" + grados + "&chs=250x150&chl=" + grados + "°C" );
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
				"chartAR.png", MediaType.IMAGE_PNG);
		if (representation.getSize() == 0) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
		
		return representation;
	}
	
	@Post("form")
	public void acceptItem(Representation entity) {
		Form form = new Form(entity);
		String grd = form.getFirstValue("grados");
		grados = Integer.parseInt(grd);
		//String content = form.getFirstValue("content");
		//Item item = new Item(title, content);
		//List<Item> items = ((PublisherTest)getApplication()).getItems();
		//items.add(item);
		setStatus(Status.REDIRECTION_PERMANENT);
		setLocationRef("/");
		
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.mode", "publish"));
			params.add(new BasicNameValuePair("hub.url", "http://localhost:8311/?output=png"));
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
