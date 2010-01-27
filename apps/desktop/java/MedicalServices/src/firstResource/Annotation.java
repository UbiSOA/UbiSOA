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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Resource that manages a list of items.
 * 
 */
public class Annotation extends BaseResource {
	
	static String annotationCode= new String();
	static String annotationURI    = "http://localhost:2122/ubicomp/annotation/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables del codigo de la anotacion
	static String varPublic;
	static String varURL;
	static String varCaducityDate;
	static String varName;
	static String varDescription;
	
    public Annotation(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        annotationCode =  request.getAttributes().get("annotationCode").toString(); 
        annotationURI= annotationURI+ annotationCode;
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
    	Property caducityDate = model.createProperty(nm, "caducityDate");
    	Property url = model.createProperty(nm, "url");
    	Property publik = model.createProperty(nm, "public");
    	Property description = model.createProperty(nm, "description");
    	Property name= model.createProperty(nm, "name");
    	   	
    	
 	
 		//Se crea el recurso
 		Resource ingredient = model.createResource(annotationURI);
 		
 		//Se obtiene los datos de la anotacion con el codigo XXXXX
 		varPublic = "YES";
 		varURL = "http://www.cicese.mx";
 		varCaducityDate = "12-12-2010";
 		varName = "CICESE";
 		varDescription =  "Annotation about CICESE";
 			
 		//Se agregan las propiedades
 		ingredient.addProperty(url, varURL);
 		ingredient.addProperty(publik, varPublic);
 		ingredient.addProperty(description,varDescription);
 		ingredient.addProperty(caducityDate, varCaducityDate);
 		ingredient.addProperty(name, varName);
 		
 		
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
                d.appendChild(nodeUbicomp);
                
                	Element nodeAnnotation = d.createElement("annotation");

                    
                	Element eltName = d.createElement("name");
                	eltName.appendChild(d.createTextNode(varName));
                	nodeAnnotation.appendChild(eltName);

                    Element eltDescription = d.createElement("description");
                    eltDescription.appendChild(d.createTextNode(varDescription));
                    nodeAnnotation.appendChild(eltDescription);
                    
                    Element eltCaducityDate = d.createElement("caducityDate");
                    eltCaducityDate.appendChild(d.createTextNode(varCaducityDate));
                    nodeAnnotation.appendChild(eltCaducityDate);
                    
                    Element eltPublic = d.createElement("public");
                    eltPublic.appendChild(d.createTextNode(varPublic));
                    nodeAnnotation.appendChild(eltPublic);
                    
                    Element eltURL = d.createElement("url");
                    eltURL.appendChild(d.createTextNode(varURL));
                    nodeAnnotation.appendChild(eltURL);

                    nodeUbicomp.appendChild(nodeAnnotation);
               
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
