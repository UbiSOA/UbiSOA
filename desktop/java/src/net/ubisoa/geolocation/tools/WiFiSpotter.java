package net.ubisoa.geolocation.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
/*
import org.placelab.core.BeaconMeasurement;
import org.placelab.core.WiFiReading;
import org.placelab.spotter.Spotter;
import org.placelab.spotter.SpotterException;*/

public class WiFiSpotter {
	private String os = System.getProperty("os.name");
	private Timer timer = new Timer();
	private static boolean debug = false;
	private HashMap<String, Integer> spotsRssi, spotsAge;
	private HashMap<String, String> spotsSsid;
	
	public WiFiSpotter(boolean autoStart) {
		if (os.compareTo("Mac OS X") != 0 && os.compareTo("Windows XP") != 0 && os.compareTo("Windows Vista") != 0) {
			String message = "Support for " + os + " isn't implemented yet.";
			System.out.println("Sistema operativo: "+os);
			String version2 = System.getProperty("os.version");
			System.out.println("Version: "+version2);
			System.err.println(message); System.exit(1);
		}
		
		String version = System.getProperty("os.version");
		if (os.compareTo("Windows XP") == 0 && version.substring(0, 3).compareTo("5.1") != 0) {
			String message = "Support for " + os + " version " + version + " isn't implemented yet.";
			System.err.println(message); System.exit(1);
		}
		
		spotsRssi = new HashMap<String, Integer>();
		spotsAge = new HashMap<String, Integer>();
		spotsSsid = new HashMap<String, String>();
		
		if (autoStart) start();
	}
	
	public WiFiSpotter() {
		this(false);
	}
	
	public void start() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				
				for (String key: spotsAge.keySet())
					spotsAge.put(key, spotsAge.get(key) + 1);
				
				// Mac OS X specific code.
				if (os.compareTo("Mac OS X") == 0) {
					try {
						Process p = Runtime.getRuntime().exec("/System/Library/PrivateFrameworks/" +
								"Apple80211.framework/Resources/airport -s");
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
								p.getInputStream()));
						String s = stdInput.readLine();
						
						while ((s = stdInput.readLine()) != null) {
							StringTokenizer tokens = new StringTokenizer(s);
							String ssid = tokens.nextToken("00").trim();
							String bssid = tokens.nextToken("-").trim();
							int rssi = Integer.parseInt(tokens.nextToken(" ").trim());
							
							spotsSsid.put(bssid, ssid);
							spotsRssi.put(bssid, rssi);
							spotsAge.put(bssid, 0);
						}
						
						if (debug) {
							for (String key: spotsRssi.keySet())
								System.out.println(key + "\t" + spotsRssi.get(key) + "\t" +
										spotsAge.get(key) + "\t" + spotsSsid.get(key));
							System.out.println();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (os.compareTo("Windows Vista") == 0) {
					try {
						Process p = Runtime.getRuntime().exec("netsh wlan show networks mode=bssid");
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(
								p.getInputStream()));
						String s = stdInput.readLine();
						
						String bssid = new String();
						String ssid = new String();
						int rssi=0;

						while ((s = stdInput.readLine()) != null) {
							if(s.startsWith("SSID "))
							{
								 ssid = s.substring(s.indexOf("SSID "), s.length());
							}
							if(s.trim().startsWith("BSSID 1"))
							{
								 bssid = s.substring(s.indexOf("BSSID")+26, s.indexOf("BSSID") + 43);
							}
							if(s.trim().startsWith("Signal"))
							{
								 rssi = Integer.parseInt(s.substring(s.indexOf("Signal")+21, s.indexOf("Signal") + 23));
							}
							
							if(ssid.length()> 0 && bssid.length()>0 && rssi>0)
							{
							spotsSsid.put(bssid, ssid);
							spotsRssi.put(bssid, rssi);
							spotsAge.put(bssid, 0);
							System.out.println("SSID: " + ssid);
							System.out.println("BSSID: " + bssid);
							System.out.println("RSSI: " + rssi);
							}
						}
						
						if (debug) {
							for (String key: spotsRssi.keySet())
								System.out.println(key + "\t" + spotsRssi.get(key) + "\t" +
										spotsAge.get(key) + "\t" + spotsSsid.get(key));
							System.out.println();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}/*
				if (os.compareTo("Windows XP") == 0) {
			        Spotter s = new org.placelab.spotter.WiFiSpotter();
			        try {
			            s.open();
			            BeaconMeasurement m = (BeaconMeasurement) s.getMeasurement();
			            if (debug) System.out.println(m.numberOfReadings() + " APs were seen\n");
			            if (m.numberOfReadings() > 0) {
			                if (debug) System.out.println("MAC Address" + "SSID" + "RSSI");
			                // Iterate through the Vector and print the readings
			                for (int i = 0; i < m.numberOfReadings(); i++) {
			                    WiFiReading r = (WiFiReading) m.getReading(i);
			                    //System.out.println(r.getId()+ r.getSsid() + "" + r.getRssi());
								spotsSsid.put(r.getId(), r.getId());
								spotsRssi.put(r.getId(), r.getRssi());
								spotsAge.put(r.getId(), 0);
			                }
			            }
			            s.close();
			        } catch (SpotterException ex) {
			            ex.printStackTrace();
			        }					
				}*/
				
			}
		}, 0, 3000);
	}
	
	public void stop() {
		timer.cancel();
	}
	
	public String getSignalData() {
		if (spotsRssi.size() == 0) return null;
		
		String signalData = "";
		for (String key: spotsRssi.keySet())
			if (spotsAge.get(key) <= 3)
				signalData += key + "=" + spotsRssi.get(key) + ",";
		
		return signalData.substring(0, signalData.length() - 1);
	}
	
	public static void main(String[] args) {
		debug = false;
		try {
			WiFiSpotter s = new WiFiSpotter(true);
			while (s.getSignalData() == null);
			for (int i = 0; i < 1; i++) {
				if (!debug) System.out.println(s.getSignalData());
				Thread.sleep(1000);
			}
			s.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
