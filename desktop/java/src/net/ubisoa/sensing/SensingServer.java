package net.ubisoa.sensing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class SensingServer extends Application {
	private Connection connection;
	private HttpClient client = Defaults.getHttpClient();
	
	private void databaseConnect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8340);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new SensingServer());
		component.start();
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new BaseRouter(getContext());
		router.attach("/", SensingResource.class);
		return router;
	}
	
	public HttpClient getClient() {
		return client;
	}

	public Reading getLastReading() {
		try {
			if (connection == null || connection.isClosed())
				databaseConnect();
			
			Reading reading = new Reading();
		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"SELECT * FROM readings ORDER BY timestamp DESC LIMIT 0,1");
			if (resultSet.next())
			{
				reading.setDateTime(resultSet.getString("timestamp"));
				reading.setNid(resultSet.getInt("nid"));
				reading.setLight(resultSet.getInt("light"));
				reading.setTemperature(resultSet.getFloat("temperature"));
				reading.setVoltage(resultSet.getFloat("voltage"));
				reading.setLightVisible(resultSet.getInt("light_visible"));
				reading.setTemperatureInternal(resultSet.getFloat("temperature_internal"));
				reading.setHumidity(resultSet.getFloat("humidity"));
				reading.setMicrophone(resultSet.getInt("microphone"));
			}
			resultSet.close();
			connection.close();
		
			return reading;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
