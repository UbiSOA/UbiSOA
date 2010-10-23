package net.ubisoa.geolocation;

import net.ubisoa.discovery.DiscoveryCore;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

public class Server extends Application {
	private final LocationCore locationData = new LocationCore();
	private final static int port = 8311;
	
	public static void main(String[] args) throws Exception {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new Server());
		component.start();
		DiscoveryCore.registerService("Alpha Dev", "geolocation.resolver", port);
	}
	
	@Override
	public synchronized Restlet createRoot() {
		Router router = new Router(getContext());
		router.attach("/", LocationResource.class);
		router.attach("/{signalData}", LocationResource.class);
		router.attach("/{signalData}?{format}", LocationResource.class);
		router.attach("/{signalData}/{platform}", LocationResource.class);
		router.attach("/{signalData}/{platform}?{format}", LocationResource.class);
		return router;
	}
	
	public LocationCore getLocationData() {
		return locationData;
	}
	
}
