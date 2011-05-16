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
package net.ubisoa.sensing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reading {
	private SensingMessage message;
	private String platform, dateTime;
	private int nid, light, lightVisible, microphone, counter;
	private double voltage, temperature, temperatureInternal, humidity;
	private long timestamp;
	
	public Reading(SensingMessage message) {
		setMessage(message);
	}
	
	public Reading() {
	}

	public SensingMessage getMessage() {
		return message;
	}
	
	private String processPlatform() {
		String s = "Unknown";
		if (message.get_platform() == 0xA) s = "TelosB";
		if (message.get_platform() == 0xB) s = "MicaZ";
		return s;
	}
	
	private double processVoltage() {
		double v = message.get_voltage();
		if (message.get_platform() == 0xA)
			v /= 1000.0;
		if (message.get_platform() == 0xB)
			v = 1.223 * 1024.0 / v;
		return v;
	}
	
	private double processTemperature() {
		double v = message.get_temperature();
		if (message.get_platform() == 0xA)
			v = -39.6 + v * 0.01;
		if (message.get_platform() == 0xB) {
			double rthr = 10000.0 * (1023.0 - v) / v;
			double a = 0.001307050, b = 0.000214381, c = 0.000000093;
			v = 1.0 / (a + b * Math.log(rthr) + c * Math.pow(Math.log(rthr), 3.0)) - 273.15;
		}
		return v;
	}
	
	private double processHumidity() {
		double v = message.get_humidity();
		if (message.get_platform() == 0xA) {
			double humidity = -4 + 0.0405 * v + -2.8 * 1e-6 * v * v;
			v = (temperature - 25.0) * (0.01 + 0.00008 * v) + humidity;
		}
		return v;
	}
	
	private double processTemperatureInternal() {
		double v = message.get_temperature_internal();
		if (message.get_platform() == 0xA)
			v = (v / 4096.0 * 1.5 - 0.986) / 0.00355;
		return v;
	}
	
	public void setMessage(SensingMessage message) {
		this.message = message;
		nid = message.get_nid();
		platform = processPlatform();
		light = message.get_light();
		lightVisible = message.get_light_visible();
		microphone = message.get_microphone();
		counter = message.get_counter();
		voltage = processVoltage();
		temperature = processTemperature();
		temperatureInternal = processTemperatureInternal();
		humidity = processHumidity();
		timestamp = System.currentTimeMillis();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateTime = df.format(new Date(timestamp));
	}
	
	public int getNid() {
		return nid;
	}
	
	public void setNid(int nid) {
		this.nid = nid;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public int getLight() {
		return light;
	}
	
	public void setLight(int light) {
		this.light = light;
	}
	
	public int getLightVisible() {
		return lightVisible;
	}
	
	public void setLightVisible(int lightVisible) {
		this.lightVisible = lightVisible;
	}
	
	public int getMicrophone() {
		return microphone;
	}
	
	public void setMicrophone(int microphone) {
		this.microphone = microphone;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public double getVoltage() {
		return voltage;
	}
	
	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	public double getTemperatureInternal() {
		return temperatureInternal;
	}
	
	public void setTemperatureInternal(float temperatureInternal) {
		this.temperatureInternal = temperatureInternal;
	}
	
	public double getHumidity() {
		return humidity;
	}
	
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public String toStringLine() {
		NumberFormat nf = new DecimalFormat("0.00");
		return dateTime + "\t" + nid + "\t" + platform + "\t" +
			nf.format(voltage) + "\t" + light + "\t" + nf.format(temperature) + "\t" +
			lightVisible + "\t" + nf.format(temperatureInternal) + "\t" + microphone;
	}
	
	public String toString() {
		NumberFormat nf = new DecimalFormat("0.00");
		
		String s = dateTime + "\n" +
			"  Node ID: " + nid + "\n" +
			"  Platform: " + platform + "\n" +
			"  Voltage: " + nf.format(voltage) + "\n" +
			"  Light: " + light + "\n" +
			"  Temperature: " + nf.format(temperature) + "\n";
		
		if (message == null || (message != null && message.get_platform() == 0xA))
			s += "  Visible Light: " + lightVisible + "\n" +
				"  Internal Temperature: " + nf.format(temperatureInternal) + "\n" +
				"  Humidity: " + nf.format(humidity) + "%\n";
		
		if (message != null && message.get_platform() == 0xB)
			s += "  Microphone: " + microphone + "\n";
		
		return s;
	}
}
