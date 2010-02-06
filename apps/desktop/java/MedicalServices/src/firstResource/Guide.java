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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Resource that manages a list of items.
 * 
 */
public class Guide extends BaseResource3 {
	
	static String guideURI    = "http://localhost:2122/ubicomp/presentation/guide";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variable que contendra el valor de las imagenes de la guia
	static String varImage;
	static String varLocation;
	
    public Guide(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        //user =  request.getAttributes().get("user").toString();  
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
        String itemName = form.getFirstValue("image");
        String itemDescription = form.getFirstValue("portion");

        // Check that the item is not already registered.
        if (getItems3().containsKey(itemName)) {
            generateErrorRepresentation(
                    "Item " + itemName + " already exists.", "1", getResponse());
        } else {
            // Register the new item
            getItems3().put(itemName, new ItemGuide(itemName, itemDescription));

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
    	Property image= model.createProperty(nm, "image");
    	
 		//Se crea el recurso
 		Resource guide = model.createResource(guideURI);
 		
 		//Se asigna el valor a la variable
 		varImage = "path de la imagen del menu";
 		varLocation = "http://localhost:2122/ubicomp/location/outdoor/1111";
 		
 		Client client = new Client(Protocol.HTTP);
 		Reference itemsUri= new Reference("http://localhost:2122/ubicomp/items3");
 		for(int cont=0; cont< 5; cont++)
        {
 			ItemGuide item1= new ItemGuide("http://litera.files.wordpress.com/2008/03/590852-zacatecas_cathedral-zacatecas.jpg"+ cont, "http://localhost:2122/ubicomp/location/outdoor/1111");
 			Reference itemUri = createItem(item1, client, itemsUri);
        }
 			
 		//Se agregan las propiedades
 		guide.addProperty(image, varImage);
 		guide.addProperty(image, "path de la imagen 2 del menu ");
 		guide.addProperty(image, "path de la imagen 3 del menu ");

 		
 		
 		//Se define el namespace, para no poner j.0: se coloca las iniciales SV
		 model.setNsPrefix( "SV", nm );
 		 
 		 //Se imprime el archivo RDF
 		 model.write(System.out, "RDF/XML-ABBREV");
        
    	/*
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                
                Element nodeUbicomp = d.createElement("ubicomp");
            	Element nodePresentation= d.createElement("presentation");
            	d.appendChild(nodeUbicomp);
                
            		Element nodeGuide = d.createElement("guide");

                    Element eltImage = d.createElement("image");
                    eltImage.appendChild(d.createTextNode(varImage));
                    
                    Element eltLocation = d.createElement("location");
                    eltLocation.appendChild(d.createTextNode(varLocation));
                    eltImage.appendChild(eltLocation);
                	nodeGuide.appendChild(eltImage);

                    Element eltImage2 = d.createElement("image");
                    eltImage2.appendChild(d.createTextNode(varImage));
                    nodeGuide.appendChild(eltImage2);
                    
                    Element eltImage3 = d.createElement("image");
                    eltImage3.appendChild(d.createTextNode(varImage));
                    nodeGuide.appendChild(eltImage3);

                    nodePresentation.appendChild(nodeGuide);
                    nodeUbicomp.appendChild(nodePresentation);
                
                d.normalizeDocument();
                
                */
 		if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("guide");
                //Element r = d.createElement("Ubicomp");
                d.appendChild(r);
               
                for (ItemGuide item : getItems3().values()) {
                	 Element eltItem = d.createElement("menu");
                    
                	//Element eltItem = d.createElement("Qrcode");

                    Element eltName = d.createElement("image");
                    eltName.appendChild(d.createTextNode(item.getImage()));
                    eltItem.appendChild(eltName);
                    
                    Element eltDescription = d.createElement("location");
                    eltDescription.appendChild(d.createTextNode(item.getLocation()));
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
    
    public static Reference createItem(ItemGuide item, Client client,
            Reference itemsUri) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("image", item.getImage());
        form.add("location", item.getLocation());
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
