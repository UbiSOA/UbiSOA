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
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemResourceGuide extends BaseResource3 {

    /** The sequence of characters that identifies the resource. */
    String itemName;

    /** The underlying Item object. */
    ItemGuide item;

    public ItemResourceGuide(Context context, Request request, Response response) {
        super(context, request, response);

        // Get the "itemName" attribute value taken from the URI template
        // /items/{itemName}.
        this.itemName = (String) getRequest().getAttributes().get("itemName");

        // Get the item directly from the "persistence layer".
        this.item = getItems3().get(itemName);

        if (this.item != null) {
            // Define the supported variant.
            getVariants().add(new Variant(MediaType.TEXT_XML));
            // By default a resource cannot be updated.
            setModifiable(true);
        } else {
            // This resource is not available.
            setAvailable(false);
        }
    }

    /**
     * Handle DELETE requests.
     */
    @Override
    public void removeRepresentations() throws ResourceException {
        if (item != null) {
            // Remove the item from the list.
            getItems3().remove(item.getImage());
        }

        // Tells the client that the request has been successfully fulfilled.
        getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        // Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
                DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the item.
                Document d = representation.getDocument();

                Element eltItem = d.createElement("item");
                d.appendChild(eltItem);
                
                Element eltName = d.createElement("name");
                eltName.appendChild(d.createTextNode(item.getImage()));
                eltItem.appendChild(eltName);

                Element eltDescription = d.createElement("portion");
                eltDescription.appendChild(d.createTextNode(item.getLocation()));
                eltItem.appendChild(eltDescription);

                d.normalizeDocument();

                // Returns the XML representation of this document.
                return representation;
            } catch (IOException e) {
            	
            	System.out.println("errorrrrrrrrrrrrrrrrrrrrrrrrrr");
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Handle PUT requests.
     */
    @Override
    public void storeRepresentation(Representation entity)
            throws ResourceException {

        // The PUT request updates or creates the resource.
        if (item == null) {
            item = new ItemGuide(itemName);
        }

        // Update the description.
        Form form = new Form(entity);
        item.setImage(form.getFirstValue("image"));

        if (getItems3().putIfAbsent(item.getImage(), item) == null) {
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } else {
            getResponse().setStatus(Status.SUCCESS_OK);
        }
    }
}
