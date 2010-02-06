package firstResource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
/**
 * Resource that manages a list of items.
 * 
 */
public class UserMedicines extends BaseResource {
	
	String user= new String();
	static String personMedicinesURI    = "http://localhost:2122/ubicomp/users/";
	static String personMedicinesURI2    = "http://localhost:2122/ubicomp/users2/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables del URI
	static String varName;
	static String varQuantity;
	static String varType;
	static String varTime;
	static String varDescription;
	
    public UserMedicines(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        user =  request.getAttributes().get("user").toString(); 
        personMedicinesURI =  personMedicinesURI  +  user  + "/medicines";
        personMedicinesURI2 =  personMedicinesURI2  +  user  + "/medicines";
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
    	Property name= model.createProperty(nm, "name");
    	Property quantity= model.createProperty(nm, "quantity");
    	Property type = model.createProperty(nm, "type");
    	Property time= model.createProperty(nm, "time");
    	Property description = model.createProperty(nm, "description");
    	
    	
 	
 		//Se crea el recurso
 		Resource ampicilina = model.createResource(personMedicinesURI);
 		Resource desenfriolito= model.createResource(personMedicinesURI2);
 		
 		//Se asigna el valor a las variables
 		varName = "penicilina";
 		varQuantity = "2";
 		varType = "Pills";
 		varTime = "16:45";
 		varDescription = "No se administre en personas con afecciones cardiacas";
 		
 		//Se agregan las propiedades
 		ampicilina.addProperty(name, varName);
 		ampicilina.addProperty(quantity, varQuantity);
 		ampicilina.addProperty(type,varType);
 		ampicilina.addProperty(time, varTime);
 		ampicilina.addProperty(description,varDescription);
 		
 		desenfriolito.addProperty(name, "Desenfriolito");
 		desenfriolito.addProperty(quantity, "2");
 		desenfriolito.addProperty(type, "Tabletas");
 		desenfriolito.addProperty(time, "13:45");
 		desenfriolito.addProperty(description, "No se administre en personas con afecciones cardiacas");

 		//Se define el namespace, para no poner j.0: se coloca las iniciales SV
		 model.setNsPrefix( "SV", nm );
 		 
 		 //Se imprime el archivo RDF
 		 model.write(System.out, "RDF/XML-ABBREV");
		 String outputFilename = "C://java/medicines2.rdf";
		 try
		 {
		 model.write(new  PrintWriter(new FileOutputStream(outputFilename)));
		 }
		 catch(FileNotFoundException e)
		 {
			 
		 }
		 
		 String inputFileName  = "C://java/medicines.xml";
		 Model model2 = ModelFactory.createDefaultModel();

	        InputStream in = FileManager.get().open( inputFileName );
	        if (in == null) {
	            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
	        }
	        
	        
	        /*
	        // read the RDF/XML file
	        model2.read(in, "");
	        Property tim = model2.createProperty("SV:time");
	        Resource vcar = model2.getResource("file:///c:/java/medicines.rdf");
	        
	        ResIterator it= model2.listSubjectsWithProperty(VCARD.N);
	        while (it.hasNext())
	        {
	        	Resource r = it.nextResource();
	        	System.out.println(r);
	        }
	                    
	        // write it to standard out
	        //model2.write(System.out);   
		 */
        
 		 Client client = new Client(Protocol.HTTP);
         
         Reference itemsUri= new Reference("http://localhost:2122/ubicomp/items");
         
         for(int cont=0; cont< 5; cont++)
         {
        	 Item item1= new Item("desenfriol" + cont, "Nothing", "2", "Tableta", "17:36");
        	 Reference itemUri = createItem(item1, client, itemsUri);
         }
 		 
    	/*Item item1= new Item("desenfriol", "Nothing", "2", "Tableta", "13:45");
    	 Reference itemUri = createItem(item1, client, itemsUri);
    	Item item2= new Item("desenfriol2", "algo", "3", "Tableta", "12:00");
    	Reference itemUri2 = createItem(item2, client, itemsUri2);*/
 		 
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	
            	/*
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                //Element r = d.createElement("items");
                Element r = d.createElement("Ubicomp");
                d.appendChild(r);
                //for (Item item : getItems().values()) {
                    //Element eltItem = d.createElement("item");
                	Element eltItem = d.createElement("Qrcode");

                    //Element eltName = d.createElement("name");
                	Element eltName = d.createElement("name");
                	eltName.appendChild(d.createTextNode("QRCode"));
                    //eltName.appendChild(d.createTextNode(item.getName()));
                    eltItem.appendChild(eltName);

                    Element eltDescription = d.createElement("description");
                    eltDescription.appendChild(d.createTextNode(user));
                    //eltDescription.appendChild(d.createTextNode(item.getDescription()));
                    eltItem.appendChild(eltDescription);

                    r.appendChild(eltItem);
                //}
                d.normalizeDocument();
                */
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("medicines");
                //Element r = d.createElement("Ubicomp");
                d.appendChild(r);
               
                for (Item item : getItems().values()) {
                	 Element eltItem = d.createElement("medicine");
                    
                	//Element eltItem = d.createElement("Qrcode");

                    Element eltName = d.createElement("name");
                    eltName.appendChild(d.createTextNode(item.getName()));
                    eltItem.appendChild(eltName);
                    
                    Element eltDescription = d.createElement("description");
                    eltDescription.appendChild(d.createTextNode(item.getDescription()));
                    eltItem.appendChild(eltDescription);
                    
                    Element eltType = d.createElement("type");
                    eltType.appendChild(d.createTextNode(item.getType()));
                    eltItem.appendChild(eltType);
                    
                    Element eltTime = d.createElement("time");
                    eltTime.appendChild(d.createTextNode(item.getTime()));
                    eltItem.appendChild(eltTime);
                    
                    Element eltQuantity = d.createElement("quantity");
                    eltQuantity.appendChild(d.createTextNode(item.getQuantity()));
                    eltItem.appendChild(eltQuantity);


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
    
    
    public static Reference createItem(Item item, Client client,
            Reference itemsUri) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("name", item.getName());
        form.add("description", item.getDescription());
        form.add("type", item.getType());
        form.add("time", item.getTime());
        form.add("quantity", item.getQuantity());
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
