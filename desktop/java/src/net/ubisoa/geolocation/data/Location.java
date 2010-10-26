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
package net.ubisoa.geolocation.data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.geolocation.LocationCore;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Location implements Serializable {
	private static final long serialVersionUID = 842479961543605697L;
	protected double latitude;
	protected double longitude;
	protected double altitude;
	protected double accuracy;
	protected double altitudeAccuracy;
	protected double heading;
	protected double speed;
	protected Object tag;
	
	public Location(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public Location(double latitude, double longitude) {
		this(latitude, longitude, 0.0);
	}

	public Location() {
		this(0.0, 0.0);
	}

	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	public double getAltitudeAccuracy() {
		return altitudeAccuracy;
	}
	
	public void setAltitudeAccuracy(double altitudeAccuracy) {
		this.altitudeAccuracy = altitudeAccuracy;
	}
	
	public double getHeading() {
		return heading;
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public Location clone() {
		return new Location(latitude, longitude, altitude);
	}
	
	public Object getTag() {
		return tag;
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	/****************************************************************************************************
	 * Calculates the distances between two location points.
	 * @param from	First location point
	 * @param to	Second location point
	 * @return	A double with the distance between the two points
	 ****************************************************************************************************/
	public static double distance(Location from, Location to) {
		return Math.sqrt(Math.pow(to.getLatitude() - from.getLatitude(), 2) +
				Math.pow(to.getLongitude() - from.getLongitude(), 2));
	}

	public Document toXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element root = d.createElement("geolocation");
			d.appendChild(root);
			
			Element child = d.createElement("latitude");
			child.appendChild(d.createTextNode(latitude + ""));
			root.appendChild(child);
			
			child = d.createElement("longitude");
			child.appendChild(d.createTextNode(longitude + ""));
			root.appendChild(child);
			
			child = d.createElement("altitude");
			child.appendChild(d.createTextNode(altitude + ""));
			root.appendChild(child);
			
			child = d.createElement("accuracy");
			child.appendChild(d.createTextNode(accuracy + ""));
			root.appendChild(child);
			
			child = d.createElement("altitudeAccuracy");
			child.appendChild(d.createTextNode(altitudeAccuracy + ""));
			root.appendChild(child);
			
			child = d.createElement("heading");
			child.appendChild(d.createTextNode(heading + ""));
			root.appendChild(child);
			
			child = d.createElement("speed");
			child.appendChild(d.createTextNode(speed + ""));
			root.appendChild(child);

			d.normalizeDocument();
			return d;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject toJSON() {
		try {
			JSONObject json = new JSONObject();
			json.put("latitude", latitude + "");
			json.put("longitude", longitude + "");
			json.put("altitude", altitude + "");
			json.put("accuracy", accuracy + "");
			json.put("altitudeAccuracy", altitudeAccuracy + "");
			json.put("heading", heading + "");
			json.put("speed", speed + "");
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;		
	}

	@Override
	public String toString() {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		NumberFormat nf = new DecimalFormat("0.0000000", dfs);
		return "(" + nf.format(latitude) + ", " + nf.format(longitude) + ")";
	}
	
	public static void printCollection(Collection<Location> collection, String signal) {
		NumberFormat nf = new DecimalFormat("0.000000");
		NumberFormat nf2 = new DecimalFormat("0.00");
		
		System.out.println("LATITUDE\tLONGITUDE\tEVAL\tTAG");
		for (Location location : collection)
			System.out.println(
					nf.format(location.latitude) + "\t" +
					nf.format(location.longitude) + "\t" +
					nf2.format(LocationCore.evaluateSignal(location, signal)) + "\t" +
					location.getTag());
		System.out.println();
	}
	
}