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
/**
 * Resource that manages a list of items.
 * 
 */
public class UserProfile extends BaseResource {
	
	static String user;
	static String varWeight;
	static String varHeight;
	static String varSound;
	static String varGender;
	static String varFirstName;
	static String varBirthDate;
	static String varLastName;
	static String varActivityFactor;
	static String personURI= "http://localhost:2122/ubicomp/users/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
    public UserProfile(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        user =  request.getAttributes().get("user").toString();  
        personURI = personURI + user + "/profile";
        
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
    	Property firstName= model.createProperty(nm, "firstName");
    	Property lastName= model.createProperty(nm, "lastName");
    	Property gender = model.createProperty(nm, "gender");
    	Property height= model.createProperty(nm, "height");
    	Property birthdate= model.createProperty(nm, "birthdate");
    	Property weight = model.createProperty(nm, "weight");
    	Property sound = model.createProperty(nm, "sound");
    	Property activityFactor = model.createProperty(nm, "activityFactor");

 	
 		//Se crea el recurso
 		Resource userProfile = model.createResource(personURI);

 		//Se asignan los valores a las variables
 		varWeight     = "72";
 		varHeight     = "166";
 		varSound     = "YES";
 		varGender     = "Male";
 		varFirstName    = "John";
 		varLastName   = "Smith";
 		varBirthDate = "14/06/1984";
 		varActivityFactor = "1.375";
 		
 		//Se agregan las propiedades
 		userProfile.addProperty(firstName, varFirstName );
 		userProfile.addProperty(lastName, varLastName);
 		userProfile.addProperty(gender, varGender);
 		userProfile.addProperty(height, varHeight);
 		userProfile.addProperty(weight, varWeight);
 		userProfile.addProperty(birthdate, varBirthDate);
 		userProfile.addProperty(sound, varSound);
 		userProfile.addProperty(sound, varActivityFactor);

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
                
                	Element nodeProfile = d.createElement("profile");

                    
                	Element eltFirstName = d.createElement("firstName");
                	eltFirstName.appendChild(d.createTextNode(varFirstName));
                	nodeProfile.appendChild(eltFirstName);

                    Element eltLastName = d.createElement("lastName");
                    eltLastName.appendChild(d.createTextNode(varLastName));
                    nodeProfile.appendChild(eltLastName);
                    
                    Element eltGender = d.createElement("gender");
                    eltGender.appendChild(d.createTextNode(varGender));
                    nodeProfile.appendChild(eltGender);
                    
                    Element eltSound = d.createElement("sound");
                    eltSound.appendChild(d.createTextNode(varSound));
                    nodeProfile.appendChild(eltSound);
                    
                    Element eltWeight = d.createElement("weight");
                    eltWeight.appendChild(d.createTextNode(varWeight));
                    nodeProfile.appendChild(eltWeight);
                    
                    Element eltHeight = d.createElement("height");
                    eltHeight.appendChild(d.createTextNode(varHeight));
                    nodeProfile.appendChild(eltHeight);
                    
                    Element eltBirthDate = d.createElement("birthDate");
                    eltBirthDate.appendChild(d.createTextNode(varBirthDate));
                    nodeProfile.appendChild(eltBirthDate);
                    
                    Element eltActivityFactor = d.createElement("activityFactor");
                    eltActivityFactor.appendChild(d.createTextNode(varActivityFactor));
                    nodeProfile.appendChild(eltActivityFactor);
                    
                    nodeUser.appendChild(nodeProfile);
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
