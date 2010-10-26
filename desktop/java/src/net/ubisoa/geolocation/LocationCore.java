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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.Collections;

import net.ubisoa.geolocation.data.Location;
import net.ubisoa.geolocation.data.SignalComparator;

public class LocationCore  {
	private static final long serialVersionUID = -9072315148588923599L;
	private Connection connection = null;
	
	public LocationCore() {
		try {
			Class.forName("org.sqlite.JDBC");
			openConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	private void openConnection() throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:dat/geolocation.db");
	}

	public void addSignal(String platform, double latitude, double longitude, String signalData) {
		try {
			if (connection == null) openConnection();

			Statement stat = connection.createStatement();
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS signals (platform, latitude, longitude, signalData);");
			
			PreparedStatement prep = connection.prepareStatement("INSERT INTO signals VALUES (?, ?, ?, ?);");
			prep.setString(1, platform);
			prep.setDouble(2, latitude);
			prep.setDouble(3, longitude);
			prep.setString(4, signalData);
			prep.addBatch();

			connection.setAutoCommit(false);
			prep.executeBatch();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<Location> getSignalsByBSSID(String platform, String bssid) {
		Vector<Location> res = new Vector<Location>();
		
		try {
			if (connection == null) openConnection();
			
			Statement stat = connection.createStatement();
			ResultSet rs = stat.executeQuery(
					"SELECT latitude, longitude, rssi FROM signals WHERE platform='" + platform +
					"' AND bssid='" + bssid + "'");
			while (rs.next()) {
				Location l = new Location();
				l.setLatitude(rs.getDouble("latitude"));
				l.setLongitude(rs.getDouble("longitude"));
				l.setAltitude(rs.getDouble("rssi"));
				res.add(l);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public Vector<Location> getSignalsByBSSIDs(String platform, Set<String> bssids) {
		Vector<Location> res = new Vector<Location>();
		try { if (connection == null) openConnection();
		
			String query = "SELECT latitude, longitude, signalData FROM signals WHERE (";
			for (String bssid : bssids) query += "signalData LIKE '%" + bssid + "%' OR ";
			query = query.substring(0, query.length() - 3) +
				((platform != null)? ") AND platform='" + platform + "'": "");
			
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				Location l = new Location();
				l.setLatitude(rs.getDouble("latitude"));
				l.setLongitude(rs.getDouble("longitude"));
				l.setTag(rs.getString("signalData"));
				res.add(l);
			}
		
		} catch (SQLException e) { e.printStackTrace(); }
		return res;
	}
	
	public Location getCentroid(Vector<Location> locations) {
		if (locations.size() == 0) return null;
		double lat, lng, alt; lat = lng = alt = 0.0;
		for (Location l : locations) {
			lat += l.getLatitude();
			lng += l.getLongitude();
			alt += l.getAltitude();
		}
		int n = locations.size();
		return new Location(lat / n, lng / n, alt / n);
	}
	
	public static float evaluateSignal(Location estimation, String signal) {
		HashMap<String, Integer> estimationMap = parseSignal((String)estimation.getTag());
		HashMap<String, Integer> signalMap = parseSignal(signal);
		int compared, total, estimationTotal = estimationMap.keySet().size();
		double diff; diff = compared = total = 0;
		for (String key : signalMap.keySet()) {
			if (estimationMap.keySet().contains(key)) {
				diff += Math.abs(signalMap.get(key) - estimationMap.get(key));
				compared++;
			} total++;
		}
		return (float)(diff / compared) + Math.abs(estimationTotal - compared) * 100;
	}
	
	private Vector<Location> filterEstimations(Vector<Location> possibleLocations, String signal) {
		Vector<Location> filtered = new Vector<Location>();
		float minEval = evaluateSignal(possibleLocations.get(0), signal);
		for (Location location : possibleLocations)
			if (evaluateSignal(location, signal) == minEval)
				filtered.add(location);
			else break;
		return filtered;
	}
	
	public Location estimate(String platform, String signalData) {
		Vector<Location> possibleLocations = getSignalsByBSSIDs(platform, parseSignal(signalData).keySet());
		if (possibleLocations.size() == 0) return null;
		
		Comparator<Location> comparator = new SignalComparator(signalData);
		Collections.sort(possibleLocations, comparator);
		possibleLocations = filterEstimations(possibleLocations, signalData);
		Location.printCollection(possibleLocations, signalData);
		return getCentroid(possibleLocations); 
	}
	
	public static HashMap<String, Integer> parseSignal(String signalData) {
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		String[] signal = signalData.split(",");
		for (String reading : signal) {
			String[] val = reading.split("=");
			res.put(val[0], Integer.parseInt(val[1]));
		}
		return res;
	}

	public Vector<Location> getSignalsTest() {
		Vector<Location> res = new Vector<Location>();
		res.add(estimate("iPhone", "0:1c:58:6d:71:b0=-91,0:24:36:a7:6a:4d=-90,0:e:d7:b0:f7:61=-89," + 
				"0:21:7c:a0:f7:b1=-85,3a:c5:3c:b:ce:1e=-82,0:1c:58:d7:aa:f0=-74,0:9:5b:51:6b:76=-62," + 
				"0:7:e:7d:91:90=-47"));
		return res;
	}
}
