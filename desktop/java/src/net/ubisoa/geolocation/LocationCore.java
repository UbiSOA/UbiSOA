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

}
