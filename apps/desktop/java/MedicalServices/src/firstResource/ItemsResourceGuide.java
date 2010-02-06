package firstResource;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
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

/**
 * Resource that manages a list of items.
 * 
 */
public class ItemsResourceGuide extends BaseResource3 {
	String linea, linea2;

    public ItemsResourceGuide(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
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
        String itemPortion = form.getFirstValue("location");

        // Check that the item is not already registered.
        if (getItems3().containsKey(itemName)) {
            generateErrorRepresentation(
                    "Item " + itemName + " already exists.", "1", getResponse());
        } else {
            // Register the new item
            getItems3().put(itemName, new ItemGuide(itemName, itemPortion));

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
        // Generate the right representation according to its media type.
    	//////////
    	/*
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
                DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("items");
                d.appendChild(r);
                
                //////
                File archivo = null;
                FileReader fr = null;
                BufferedReader br = null;
                try {
                    // Apertura del fichero y creacion de BufferedReader para poder
                    // hacer una lectura comoda (disponer del metodo readLine()).
                    archivo = new File ("C:\\lecturas.txt");
                    fr = new FileReader (archivo);
                    br = new BufferedReader(fr);

                    linea = br.readLine();
                    linea2= br.readLine();
                    System.out.println(linea);
                    System.out.println(linea2	);
                  
                    Client client = new Client(Protocol.HTTP);
                    Client client2 = new Client(Protocol.HTTP);
                    
                    Reference itemsUri= new Reference(
                    "http://localhost:8186/firstResource/items");
                    Reference itemsUri2= new Reference(
                    "http://localhost:8186/firstResource/items");
                   
                    Item temperatura= new Item("Temperatura", linea);
                    Reference itemUri = createItem(temperatura, client, itemsUri);
                    temperatura.setDescription(linea);
                    
                    Item humedad= new  Item("Humedad", linea2);
                    humedad.setDescription(linea2);
                    Reference itemUri2 = createItem(humedad, client2, itemsUri2);
                 }
                 catch(Exception e){
                    e.printStackTrace();
                    System.out.println("errorr");
                 }finally{
                    try{                    
                       if( null != fr ){   
                          fr.close();     
                       }                  
                    }catch (Exception e2){ 
                       e2.printStackTrace();
                       System.out.println("errorr2");
                    }
                 }

                 ////////
               
                /*
               for (Item item : getItems().values()) {
                    Element eltItem = d.createElement("Sensor");

                    Element eltName = d.createElement("Nombre");
                    eltName.appendChild(d.createTextNode(item.getName()));
                    eltItem.appendChild(eltName);

                    Element eltDescription = d.createElement("Valor");
                    eltDescription.appendChild(d.createTextNode(item.getDescription()));
                    eltItem.appendChild(eltDescription);

                    r.appendChild(eltItem);
                }*/
               /*
              for (int cont=0; cont<1;cont++) {
                   Element eltItem = d.createElement("Variables");

                   Element eltName = d.createElement("Temperatura");
                   eltName.appendChild(d.createTextNode(linea));
                   eltItem.appendChild(eltName);

                   Element eltDescription = d.createElement("Humedad");
                   eltDescription.appendChild(d.createTextNode(linea2));
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
        
*/////////////////////////
    	
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {  
            try {  
                DomRepresentation representation = new DomRepresentation(  
                        MediaType.TEXT_XML);  
                // Generate a DOM document representing the list of  
                // items.  
                Document d = representation.getDocument();  
                Element r = d.createElement("items");  
                d.appendChild(r);  
                for (ItemGuide item : getItems3().values()) {  
                    Element eltItem = d.createElement("item");  
  
                    Element eltName = d.createElement("name");  
                    eltName.appendChild(d.createTextNode(item.getImage()));  
                    eltItem.appendChild(eltName);  
  
                    Element eltDescription = d.createElement("portion");  
                    eltDescription.appendChild(d.createTextNode(item  
                            .getLocation()));  
                    eltItem.appendChild(eltDescription);  
                    
                    
                   /* Element eltQuantity = d.createElement("quantity");                    
                    eltQuantity.appendChild(d.createTextNode(item.getQuantity()));
                    eltItem.appendChild(eltQuantity);*/
  
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
    
    /////////
    public static Reference createItem(ItemIngredient item, Client client,
            Reference itemsUri) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("name", item.getName());
        form.add("description", item.getPortion());
        Representation rep = form.getWebRepresentation();

        // Launch the request
        Response response = client.post(itemsUri, rep);
        if (response.getStatus().isSuccess()) {
            return response.getEntity().getIdentifier();
        }

        return null;
    }
    
    public static void get(Client client, Reference reference)
    throws IOException {
    		Response response = client.get(reference);
    			if (response.getStatus().isSuccess()) {
    				if (response.isEntityAvailable()) {
        response.getEntity().write(System.out);
    				}
    			}
    }
    
    public static boolean updateItem(Item item, Client client, Reference itemUri) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("name", item.getName());
        form.add("description", item.getDescription());
        Representation rep = form.getWebRepresentation();

        // Launch the request
        Response response = client.put(itemUri, rep);
        return response.getStatus().isSuccess();
    }
    public static boolean deleteItem(Client client, Reference itemUri) {
        // Launch the request
        Response response = client.delete(itemUri);
        return response.getStatus().isSuccess();
    }

    ////////

}
