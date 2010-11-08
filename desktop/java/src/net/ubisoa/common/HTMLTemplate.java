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

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class HTMLTemplate {
	// TODO: Add documentation.
	
	private String title, subtitle, content;
	private List<String> stylesheets, scripts;

	public HTMLTemplate() {
		title = "Services";
		subtitle = null;
		content = "";
		String baseURI = !Defaults.USE_LOCAL_FILES? "http://api.ubisoa.net/": "";
		stylesheets = new Vector<String>();
		stylesheets.add("http://yui.yahooapis.com/3.1.1/build/cssreset/reset-min.css");
		stylesheets.add(baseURI + "/css/general.css");
		scripts = new Vector<String>();
		scripts.add("http://s3.amazonaws.com/getsatisfaction.com/javascripts/feedback-v2.js");
		scripts.add(baseURI + "/js/getsatisfaction.js");
		scripts.add("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
	}
	
	public HTMLTemplate(String title, String content) {
		this();
		this.title = title;
		this.content = content;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public void SetContent(String content) {
		this.content = content;
	}
	
	public List<String> getStylesheets() {
		return stylesheets;
	}
	
	public List<String> getScripts() {
		return scripts;
	}
	
	public String getHTML() {
		String html = "<!DOCTYPE html>\n<html lang=\"en\">\n" +
			"<head>\n\t<meta charset=\"UTF-8\" />\n\t" +
			"<title>UbiSOA Framework - " + title + "</title>\n";
		for (String stylesheet : stylesheets)
			html += "\t<link rel=\"stylesheet\" href=\"" + stylesheet + "\" />\n";
		html +=	"</head>\n<body>\n\t<header>\n" +
			"\t\t<a href=\"http://www.ubisoa.net/\"></a>\n" +
			"\t\t<span>Services</span>\n\t</header>\n\t<div id=\"wrapper\">\n" +
			"\t\t<div id=\"content-wrapper\">\n" +
			"\t\t\t<h1>" + title + "</h1>\n" +
			((subtitle != null)?
				"\t\t\t<p class=\"subtitle\">" + subtitle + "</p>\n": "") + 
			"\t\t\t" + content + "\n\t\t</div>\n\t</div>\n";
		for (String script : scripts)
			html += "\n\t<script src=\"" + script + "\"></script>";
		html += "\n</body>\n</html>";
		return html;
	}
	
	@Override
	public String toString() {
		return getHTML();
	}
	
}
