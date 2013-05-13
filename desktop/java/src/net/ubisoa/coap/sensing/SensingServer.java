/*
 * This class is used to get the last values ​​that are 
 * sensed and stored in the database. The data given are: date, node, temperature and humidity.
 *
 * Written by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
 *****************************************************************************/

package src.net.ubisoa.coap.sensing;

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
	Reading reading = new Reading();

	private void databaseConnect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.sqlite");
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
			
		
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"SELECT temp.date, temp.node, temperature , humidity FROM temp,hum WHERE temp.node=hum.node ORDER BY temp.date DESC LIMIT 0,1");
			if (resultSet.next())
			{
				reading.setDateTime(resultSet.getString("date"));
				reading.setNode(resultSet.getString("node"));
				reading.setTemperature(resultSet.getFloat("temperature"));		
				reading.setHumidity(resultSet.getFloat("humidity"));
                              
			}
			resultSet.close();
			connection.close();
		
                        this.getLastReadingClient();

			return reading;
                        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
        
        public void getLastReadingClient() {
		try {
			if (connection == null || connection.isClosed())
				databaseConnect();
					
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
				"SELECT * FROM dataClient ORDER BY date DESC LIMIT 0,1");
			if (resultSet.next())
			{
				reading.setLed(resultSet.getBoolean("opcion"));
                              
			}
			resultSet.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
