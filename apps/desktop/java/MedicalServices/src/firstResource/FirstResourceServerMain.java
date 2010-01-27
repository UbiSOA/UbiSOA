package firstResource;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.*;
import org.restlet.Restlet;

public class FirstResourceServerMain {

    public static void main(String[] args) throws Exception {
    	
        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8182.
        component.getServers().add(Protocol.HTTP, 2122);

	
	// Create a new tracing Restlet  
    
	org.restlet.Restlet restlet = new org.restlet.Restlet() {  
    	@Override  
    	public void handle(org.restlet.data.Request request, org.restlet.data.Response response) {  
        // Print the requested URI path  
    		System.out.println( "Holala" + request.getResourceRef().getRemainingPart());
        String message = "Resource URI  : " + request.getResourceRef()  
                + '\n' + "Root URI      : " + request.getRootRef()  
                + '\n' + "Routed part   : "  
                + request.getResourceRef().getBaseRef() + '\n'  
                + "Remaining part: "  
                + request.getResourceRef().getRemainingPart()+ '\n'
                + "Dizque el Query "
                + request.getResourceRef().getQuery();  
        response.setEntity(message, MediaType.TEXT_PLAIN);
       
    }  
}; 

		// Attach the sample application.
       component.getDefaultHost().attach("/ubicomp", new FirstResourceApplication());
		 
        

        // Start the component.
        component.start();
    }

}
