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
public class Food extends BaseResource {
	String path= new String();
	String user= new String();
	static String personURI    = "http://somewhere/JohnSmith";
	static String personURI2    = "http://somewhere/JohnSmith2";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables de la comida
	static String varSugar;
	static String varFiber;
 	static String varCalcium;
 	static String varVitaminA;
 	static String varVitaminB;
 	static String varProtein;
 	static String varTransFat;
 	static String varSaturedFat;
 	static String varIron;
 	static String varCholesterol;
 	static String varSodium;
 	static String varFatCalories;
 	static String varServingSize;
 	static String varServingsNumber;
 	static String varName;
 	
    public Food(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        /*path= request.getResourceRef().getQuery();
        path=request.getResourceRef().getQuery().substring(request.getResourceRef().getQuery().indexOf('=')+1, request.getResourceRef().getQuery().length());
        System.out.println("Ruta del archivo del QR Code: " + path);*/
        
        user =  request.getAttributes().get("foodcode").toString(); 
        
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
        Property sugar= model.createProperty(nm, "sugar");
    	Property fiber= model.createProperty(nm, "fiber");
    	Property calcium= model.createProperty(nm, "calcium");
    	Property vitaminA= model.createProperty(nm, "vitaminA");
    	Property vitaminB= model.createProperty(nm, "vitaminB");
    	Property protein= model.createProperty(nm, "protein");
    	Property transFat= model.createProperty(nm, "transFat");
    	Property saturedFat= model.createProperty(nm, "saturedFat");
    	Property iron= model.createProperty(nm, "iron");
    	Property sodium= model.createProperty(nm, "sodium");
    	Property cholesterol= model.createProperty(nm, "cholesterol");
    	Property fatCalories= model.createProperty(nm, "fatCalories");
    	Property servingSize= model.createProperty(nm, "servingSize");
    	Property servingsNumber= model.createProperty(nm, "servingsNumber");
    	Property name= model.createProperty(nm, "name");
    	    	
 	
 		//Se crea el recurso
 		Resource johnSmith = model.createResource(personURI);
 		
 		//Se asignan los valores a las variables
 		varSugar = "30";
 		varFiber = "2";
 	 	varCalcium = "3";
 	 	varVitaminA = "4";
 	 	varVitaminB = "5";
 	 	varProtein = "6";
 	 	varTransFat = "7";
 	 	varSaturedFat = "8";
 	 	varIron = "9";
 	 	varCholesterol = "01";
 	 	varSodium = "02";
 	 	varFatCalories = "03";
 	 	varServingSize= "04";
 	 	varServingsNumber= "05";
 	 	varName= "Oreo Cookies";
 		
 		//Se agregan las propiedades
 		johnSmith.addProperty(name, "Oreo Cookies");
 		johnSmith.addProperty(sugar, varSugar);
 		johnSmith.addProperty(fiber, varFiber);
 		johnSmith.addProperty(calcium,varCalcium);
 		johnSmith.addProperty(vitaminA, varVitaminA);
 		johnSmith.addProperty(vitaminB, varVitaminB);
 		johnSmith.addProperty(protein, varProtein);
 		johnSmith.addProperty(transFat,varTransFat);
 		johnSmith.addProperty(saturedFat, varSaturedFat);
 		johnSmith.addProperty(iron, varIron);
 		johnSmith.addProperty(sodium, varSodium);
 		johnSmith.addProperty(cholesterol,varCholesterol);
 		johnSmith.addProperty(fatCalories, varFatCalories);
 		johnSmith.addProperty(servingSize, varServingSize);
 		johnSmith.addProperty(servingsNumber, varServingsNumber);
 				
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
                 
                 	Element nodeFood = d.createElement("food");
                 

                    Element eltSugar = d.createElement("sugar");
                 	eltSugar.appendChild(d.createTextNode(varSugar));
                 	nodeFood.appendChild(eltSugar);
                 	
                 	Element eltName = d.createElement("name");
                 	eltName.appendChild(d.createTextNode(varName));
                 	nodeFood.appendChild(eltName);
                 	
                 	Element eltFiber = d.createElement("fiber");
                 	eltFiber.appendChild(d.createTextNode(varFiber));
                 	nodeFood.appendChild(eltFiber);
                 	
                 	Element eltCalcium = d.createElement("calcium");
                 	eltCalcium.appendChild(d.createTextNode(varCalcium));
                 	nodeFood.appendChild(eltCalcium);
                 	
                 	Element eltVitaminA = d.createElement("vitaminA");
                 	eltVitaminA.appendChild(d.createTextNode(varVitaminA));
                 	nodeFood.appendChild(eltVitaminA);
                 	
                 	Element eltVitaminB = d.createElement("vitaminB");
                 	eltVitaminB.appendChild(d.createTextNode(varVitaminB));
                 	nodeFood.appendChild(eltVitaminB);
                 	
                 	Element eltProtein = d.createElement("protein");
                 	eltProtein.appendChild(d.createTextNode(varProtein));
                 	nodeFood.appendChild(eltProtein);
                 	
                 	Element eltTransFat = d.createElement("transFat");
                 	eltTransFat.appendChild(d.createTextNode(varTransFat));
                 	nodeFood.appendChild(eltTransFat);
                 	
                 	Element eltSaturedFat= d.createElement("saturedFat");
                 	eltSaturedFat.appendChild(d.createTextNode(varSaturedFat));
                 	nodeFood.appendChild(eltSaturedFat);
                 	
                 	Element eltIron= d.createElement("iron");
                 	eltIron.appendChild(d.createTextNode(varIron));
                 	nodeFood.appendChild(eltIron);
                 	
                 	Element eltSodium= d.createElement("sodium");
                 	eltSodium.appendChild(d.createTextNode(varSodium));
                 	nodeFood.appendChild(eltSodium);
                 	
                 	Element eltCholesterol= d.createElement("cholesterol");
                 	eltCholesterol.appendChild(d.createTextNode(varCholesterol));
                 	nodeFood.appendChild(eltCholesterol);
                 	
                 	Element eltFatCalories= d.createElement("fatCalories");
                 	eltFatCalories.appendChild(d.createTextNode(varFatCalories));
                 	nodeFood.appendChild(eltFatCalories);
                 	
                 	Element eltServingSize= d.createElement("servingSize");
                 	eltServingSize.appendChild(d.createTextNode(varServingSize));
                 	nodeFood.appendChild(eltServingSize);
                 	
                 	Element eltServingsNumber= d.createElement("servingsNumber");
                 	eltServingsNumber.appendChild(d.createTextNode(varServingsNumber));
                 	nodeFood.appendChild(eltServingsNumber);
                 	
                                          
                     nodeUbicomp.appendChild(nodeFood);
                                   
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
