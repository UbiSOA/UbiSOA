package firstResource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class FirstResourceApplication extends Application {

    /** The list of items is persisted in memory. */
    private final ConcurrentMap<String, Item> items = new ConcurrentHashMap<String, Item>();
    private final ConcurrentMap<String, ItemIngredient> items2 = new ConcurrentHashMap<String, ItemIngredient>();
    private final ConcurrentMap<String, ItemGuide> items3 = new ConcurrentHashMap<String, ItemGuide>();

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createRoot() {
        // Create a router Restlet that defines routes.
        Router router = new Router(getContext());
        
    	//Create the account handler  
		org.restlet.Restlet account = new org.restlet.Restlet() {  
		    @Override  
		    public void handle(Request request, Response response) {  
		        // Print the requested URI path  
		        String message = "Account of user \""  
		                + request.getAttributes().get("user") + "\"";  
		        response.setEntity(message, MediaType.TEXT_PLAIN);  
		    }  
		}; 
		
		// Create the orders handler  
		org.restlet.Restlet orders = new org.restlet.Restlet(getContext()) {  
		    @Override  
		    public void handle(Request request, Response response) {  
		        // Print the user name of the requested orders  
		        String message = "Orders of user \""  
		                + request.getAttributes().get("user") + "\"";  
		        response.setEntity(message, MediaType.TEXT_PLAIN);  
		    }  
		};  
		  
		// Create the order handler  
		org.restlet.Restlet order = new org.restlet.Restlet(getContext()) {  
		    @Override  
		    public void handle(Request request, Response response) {  
		        // Print the user name of the requested orders  
		        /*String message = "Order \""  
		                + request.getAttributes().get("order")  
		                + "\" for user \""  
		                + request.getAttributes().get("user") + "\"";*/  
		        String message = "Order \"" + "joafa" +  "\"";
		        response.setEntity(message, MediaType.TEXT_PLAIN);  
		    }  
		}; 
        
		// Attach the handlers to the root router  
		
		//Obtiene el perfil del usuario
		router.attach("/filter", Filter.class);		
		
		//Obtiene el perfil del usuario
		router.attach("/users/{user}/profile", UserProfile.class);
		
		//Obtiene las medicinas que se le estan suministrando al usuario
		router.attach("/users/{user}/medicines", UserMedicines.class);
		
		//Obtiene las medicina especifica que se le esta suministrando al usuario
		router.attach("/users/{user}/medicines/{medicineCode}", UserMedicine.class);
		
		//Obtiene los ingredientes que no puede ingerir el usuario
		router.attach("/users/{user}/ingredients", UserIngredients.class);
		
		//Obtiene las medicinas que se le estan suministrando al usuario
		router.attach("/users/{user}/ingredients/{ingredientCode}", UserIngredient.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/users/{user}/environment", UserEnvironment.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/users/{user}/environment/history", UserEnvironmentHistory.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/users/{user}/role", UserRole.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation", Presentation.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/text", Text.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/audio", Audio.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/array", Array.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/guide", Guide.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/texture", Texture.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/presentation/virtualobject/arrow", Arrow.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/event/{eventCode}", Event.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/event/click", Click.class);
		
		//Obtiene los valores de los signos vitales del paciente
		router.attach("/event/doubleclick", DoubleClick.class);
		
		//Obtiene los valores de localizacion en exteriores
		router.attach("/location/outdoor/{locationCode}", LocationOutdoor.class);
		
		//Obtiene los valores de localizacion en interiores
		router.attach("/location/indoor/{locationCode}", LocationIndoor.class);
		
		//Obtiene la informacion de la comida que esta visualizando el usuario
		router.attach("/food/{foodcode}",Food.class);
		
		//Obtiene la informacion de la comida que esta visualizando el usuario
		router.attach("/annotation/{annotationCode}",Annotation.class);
				
        //Define la ruta para la descripcion de lo que hace el recurso qrcode
		//Para decodificar el codigo es necesario pasarle el path de la imagen a decoficar
		//Esto es http://localhost:2122/ubicomp/qrcode/?path=path
        router.attach("/qrcode/", QrcodeResource.class);
        
        //Define la ruta para la descripcion de lo que hace el recurso barcode
        //Para decodificar el codigo de barras es necesario pasarle el path de la imagen a decodificar
        //Esto es http://localhost:2122/ubicomp/barcode/?path=path
        router.attach("/barcode/", QrcodeResource.class);
        
        //Define la ruta para la descripcion de lo que hace el recurso barcode
        //Para obtener la informacion anexa a la imagen es necesario pasarle el path de la imagen a decodificar
        //Esto es http://localhost:2122/ubicomp/image/?path=path
        router.attach("/image/", ImageResource.class);
        
        // Defines a route for the resource "item"
        router.attach("/items/{itemName}", ItemResource.class);
        
     // Defines a route for the resource "list of items"
		router.attach("/items", ItemsResource.class);
		
	     // Defines a route for the resource "list of items"
		router.attach("/items2", ItemsResourceIngredient.class);
		
		 // Defines a route for the resource "list of items"
		router.attach("/items3", ItemsResourceGuide.class);


        return router;
    }

    /**
     * Returns the list of registered items.
     * 
     * @return the list of registered items.
     */
    public ConcurrentMap<String, Item> getItems() {
        return items;
    }
    public ConcurrentMap<String, ItemIngredient> getItems2() {
        return items2;
    }
    public ConcurrentMap<String, ItemGuide> getItems3() {
        return items3;
    }
}

