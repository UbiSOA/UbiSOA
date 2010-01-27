package firstResource;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import jp.sourceforge.qrcode.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.graph.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.ontology.*;
/**
 * Resource that manages a list of items.
 * 
 */
public class UserEnvironment extends BaseResource {

	String user= new String();
	static String personEnvironmentURI    = "http://localhost:2122/ubicomp/users/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables del URI
	static String varTemperature;
	static String varRespiratoryRate;
	static String varBloodPresure ;
	static String varHeartRate;
	
	
    public UserEnvironment(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        user =  request.getAttributes().get("user").toString();  
        personEnvironmentURI =  personEnvironmentURI  +  user  + "/environment";
    }

    /**
     * Handle POST requests: create a new item.
     */
    @Override
    public void acceptRepresentation(Representation entity)
            throws ResourceException {
        // Parse the given representation and retrieve pairs of
        // "name=value" tokens.
        Form form = new Form(entity);
        String itemName = form.getFirstValue("name");
        String itemDescription = form.getFirstValue("description");

        // Check that the item is not already registered.
        if (getItems().containsKey(itemName)) {
            generateErrorRepresentation(
                    "Item " + itemName + " already exists.", "1", getResponse());
        } else {
            // Register the new item
            getItems().put(itemName, new Item(itemName, itemDescription));

            // Set the response's status and entity
            getResponse().setStatus(Status.SUCCESS_CREATED);
            Representation rep = new StringRepresentation("Item created",
                    MediaType.TEXT_PLAIN);
            // Indicates where is located the new resource.
            rep.setIdentifier(getRequest().getResourceRef().getIdentifier()
                    + "/" + itemName);
            getResponse().setEntity(rep);
        }
    }

    /**
     * Returns a listing of all registered items.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
    	// Se crea el modelo vacio
        Model model = ModelFactory.createDefaultModel();
        
    	//Properties
    	Property temperature= model.createProperty(nm, "temperature");
    	Property respiratoryRate= model.createProperty(nm, "respiratoryRate");
    	Property bloodPresure = model.createProperty(nm, "bloodPresure");
    	Property heartRate= model.createProperty(nm, "heartRate");
    	   	
    	
 	
 		//Se crea el recurso
 		Resource environment = model.createResource(personEnvironmentURI);
 		
 		//Se asignan los valores a las variables
 		varTemperature = "28";
 		varRespiratoryRate = "80";
 		varBloodPresure = "100";
 		varHeartRate = "90";
 			
 		//Se agregan las propiedades
 		environment.addProperty(temperature, varTemperature);
 		environment.addProperty(respiratoryRate, varRespiratoryRate);
 		environment.addProperty(bloodPresure, varBloodPresure);
 		environment.addProperty(heartRate,varHeartRate);
 		
 		
 		//Se define el namespace, para no poner j.0: se coloca las iniciales SV
		 model.setNsPrefix( "SV", nm );
 		 
 		 //Se imprime el archivo RDF
 		 model.write(System.out, "RDF/XML-ABBREV");
        
    	
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
            	Document d = representation.getDocument();
                //Element r = d.createElement("items");
                Element nodeUbicomp = d.createElement("ubicomp");
                Element nodeUsers = d.createElement("users");
                Element nodeUser = d.createElement(user);
                d.appendChild(nodeUbicomp);
                
                	Element nodeEnvironment = d.createElement("environment");

                    
                	Element eltTemperature = d.createElement("temperature");
                	eltTemperature.appendChild(d.createTextNode(varTemperature));
                	nodeEnvironment.appendChild(eltTemperature);

                    Element eltRespiratoryRate = d.createElement("respiratoryRate");
                    eltRespiratoryRate.appendChild(d.createTextNode(varRespiratoryRate));
                    nodeEnvironment.appendChild(eltRespiratoryRate);
                    
                    Element eltBloodPresure = d.createElement("bloodPresure");
                    eltBloodPresure.appendChild(d.createTextNode(varBloodPresure));
                    nodeEnvironment.appendChild(eltBloodPresure);
                    
                    Element eltHeartRate = d.createElement("heartRate");
                    eltHeartRate.appendChild(d.createTextNode(varHeartRate));
                    nodeEnvironment.appendChild(eltHeartRate);
                    
                    nodeUser.appendChild(nodeEnvironment);
                    nodeUsers.appendChild(nodeUser);
                    nodeUbicomp.appendChild(nodeUsers);
               
                d.normalizeDocument();

                // Returns the XML representation of this document.
                return representation;
                
                
                       
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Generate an XML representation of an error response.
     * 
     * @param errorMessage
     *            the error message.
     * @param errorCode
     *            the error code.
     */
    private void generateErrorRepresentation(String errorMessage,
            String errorCode, Response response) {
        // This is an error
        response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        // Generate the output representation
        try {
            DomRepresentation representation = new DomRepresentation(
                    MediaType.TEXT_XML);
            // Generate a DOM document representing the list of
            // items.
            Document d = representation.getDocument();

            Element eltError = d.createElement("error");

            Element eltCode = d.createElement("code");
            eltCode.appendChild(d.createTextNode(errorCode));
            eltError.appendChild(eltCode);

            Element eltMessage = d.createElement("message");
            eltMessage.appendChild(d.createTextNode(errorMessage));
            eltError.appendChild(eltMessage);

            response.setEntity(representation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
