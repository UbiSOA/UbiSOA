package firstResource;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
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
public class UserIngredients extends BaseResource2 {
	
	String user= new String();
	static String personIngredientsURI    = "http://localhost:2122/ubicomp/users/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables del URI
	static String varName;
	static String varPortion;
		
    public UserIngredients(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        user =  request.getAttributes().get("user").toString();  
        personIngredientsURI =  personIngredientsURI  +  user  + "/ingredients";  
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
        if (getItems2().containsKey(itemName)) {
            generateErrorRepresentation(
                    "Item " + itemName + " already exists.", "1", getResponse());
        } else {
            // Register the new item
            getItems2().put(itemName, new ItemIngredient(itemName, itemDescription));

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
    	Property portion = model.createProperty(nm, "portion");
    	Property name= model.createProperty(nm, "name");
    	   	
    	
 	
 		//Se crea el recurso
 		Resource ingredient = model.createResource(personIngredientsURI);

 		//Se asignan los valores a las variables
 		varName = "Sugar";
 		varPortion = "8";
 		
 		//Se agregan las propiedades
 		ingredient.addProperty(portion,varPortion);
 		ingredient.addProperty(name, varName);
 		
 		
 		//Se define el namespace, para no poner j.0: se coloca las iniciales SV
		 model.setNsPrefix( "SV", nm );
 		 
 		 //Se imprime el archivo RDF
 		 model.write(System.out, "RDF/XML-ABBREV");
 		 
 		 Client client = new Client(Protocol.HTTP);
         Client client2 = new Client(Protocol.HTTP);
         
         Reference itemsUri= new Reference("http://localhost:2122/ubicomp/items2");
         Reference itemsUri2= new Reference("http://localhost:2122/ubicomp/items2");
         
         for(int cont=0; cont< 5; cont++)
         {
        	 ItemIngredient item1= new ItemIngredient("Sugar", "25");
        	 Reference itemUri = createItem(item1, client, itemsUri);
         }
        
         //////////////////////////
         /*
    	
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
                
                	Element nodeIngredients = d.createElement("ingredients");

                    
                	Element eltName = d.createElement("name");
                	eltName.appendChild(d.createTextNode(varName));
                	nodeIngredients.appendChild(eltName);

                    Element eltPortion = d.createElement("portion");
                    eltPortion.appendChild(d.createTextNode(varPortion));
                    nodeIngredients.appendChild(eltPortion);
                    
                    Element eltName2 = d.createElement("name");
                	eltName2.appendChild(d.createTextNode(varName));
                	nodeIngredients.appendChild(eltName2);

                    Element eltPortion2 = d.createElement("portion");
                    eltPortion2.appendChild(d.createTextNode(varPortion));
                    nodeIngredients.appendChild(eltPortion2);
                    
                    nodeUser.appendChild(nodeIngredients);
                    nodeUsers.appendChild(nodeUser);
                    nodeUbicomp.appendChild(nodeUsers);
               
                d.normalizeDocument();
                
         

                // Returns the XML representation of this document.
                return representation;
                
                
                       
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
         
         if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
             try {
             	
             	DomRepresentation representation = new DomRepresentation(
                         MediaType.TEXT_XML);
                 // Generate a DOM document representing the list of
                 // items.
                 Document d = representation.getDocument();
                 Element r = d.createElement("ingredients");
                 //Element r = d.createElement("Ubicomp");
                 d.appendChild(r);
                
                 for (ItemIngredient item : getItems2().values()) {
                 	 Element eltItem = d.createElement("ingredient");
                     
                 	//Element eltItem = d.createElement("Qrcode");

                     Element eltName = d.createElement("name");
                     eltName.appendChild(d.createTextNode(item.getName()));
                     eltItem.appendChild(eltName);
                     
                     Element eltDescription = d.createElement("portion");
                     eltDescription.appendChild(d.createTextNode(item.getPortion()));
                     eltItem.appendChild(eltDescription);

                     r.appendChild(eltItem);
                 }
                 
                 d.normalizeDocument();            	

                 // Returns the XML representation of this document.
                 return representation;        
                        
                 
                 
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }


        return null;
    }
    
    
    public static Reference createItem(ItemIngredient item, Client client,
            Reference itemsUri) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("name", item.getName());
        form.add("portion", item.getPortion());
        Representation rep = form.getWebRepresentation();

        // Launch the request
        Response response = client.post(itemsUri, rep);
        if (response.getStatus().isSuccess()) {
            return response.getEntity().getIdentifier();
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
