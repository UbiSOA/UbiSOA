package net.ubisoa.servo;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import com.phidgets.PhidgetException;
import com.phidgets.ServoPhidget;

public class ServoServer extends Application {
	private static ServoPhidget phidget;
	
	private static boolean connectPhidget() {
		try {
			phidget = new ServoPhidget();
			phidget.openAny();
			phidget.waitForAttachment(1000);
			phidget.setEngaged(0, true);
			phidget.setPosition(0, 0.0);
			return true;
		} catch (PhidgetException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		if (!connectPhidget()) {
			System.out.println("Cannot start Phidget.");
			return;
		}
		
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8380);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new ServoServer());
		component.start();
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new BaseRouter(getContext());
		router.attach("/", ServoResource.class);
		return router;
	}
	
	public ServoPhidget getPhidget() {
		return phidget;
	}

}
