package firstResource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Resource that manages a list of items.
 * 
 */
public class Filter extends BaseResource {
	

	static String filterURI= "http://localhost:2122/ubicomp/filter/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	String path= new String("");
	
	String profile= new String("");
	String role= new String("");
	String qrcode= new String("");
	String barcode= new String("");
	
	//Variables del perfil
	static String firstName = new String();
	static String lastName = new String();
	static String gender = new String();
	static String sound = new String();
	static String weight = new String();
	static String height = new String();
	static String birthDate = new String();
	static String activityFactor = new String();
	static double calories=0;
	
	//Variables para los ingredientes
	static String ingredientName = new String();
	static String portion = new String ();
	static String nameTemp = new String();
	static int countIngredients = 0;
	
	//Variables para la comida
	static String foodName = new String();
	static String sugar = new String();
	static String fiber = new String();
	static String calcium = new String();
	static String vitaminA = new String();
	static String vitaminB= new String();
	static String protein = new String();
	static String transFat = new String();
	static String saturedFat = new String();
	static String iron = new String();
	static String sodium = new String();
	static String cholesterol = new String();
	static String fatCalories = new String();
	static String servingSize = new String();
	static String servingsNumber = new String();
	
	//Variables para el environment del usuario
	static String temperature =  new String();
	static String respiratoryRate =  new String();
	static String bloodPresure =  new String();
	static String heartRate =  new String();
	
	//Variables para las medicinas
	static String name = new String();
	static String time = new String();
	static String quantity = new String();
	
	//Variables para el rol
	static String rol = new String();
	
	//Variables para la decodificacion del QR
	static String url = new String();
	static boolean flagMedicine = false;
	static boolean flagIngredient = false;
	static boolean flagFood = false;
	static boolean flagProfile = false; 
	
	//Variables que van a servir para lo de las medicinas
	int minuteRange = 30;
	
	//Variables para los arreglos de imagenes
	static private String [] imageArray = new String[9];
	int indexImage = 0; //Indice para llevar el control de las imagenes almacenadas en el arreglo
	
	//Variable para la textura
	static String texture = new String();
	
	//Variables para las texturas
	String textAudio = new String();
	String urlTexture = new String();
	
	//Varibles que definen el contenido del archivo XML resultante y por lo tanto la presentacion de la informacion en AR
	static boolean flagArray =false;
	static boolean flagTexture =false;
	static boolean flagText =false;
	static boolean flagGuide =false;
	static boolean flagAudio =false;
	
    public Filter(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
       
        //Se obtienen los parametros de la consulta
        profile = request.getResourceRef().getQueryAsForm().getFirstValue("profile");
        role= request.getResourceRef().getQueryAsForm().getFirstValue("role");
        qrcode= request.getResourceRef().getQueryAsForm().getFirstValue("qrcode");
        barcode= request.getResourceRef().getQueryAsForm().getFirstValue("barcode");
        
        flagArray =false;
    	flagTexture =false;
    	flagText =false;
    	flagGuide =false;
    	flagAudio =false;
    	
    	
    	
    	countIngredients=0;
                
        if(qrcode != null && role!= null && barcode == null && profile == null)//para el escenario del hospital
        {
        	decodeQrCode(qrcode);
        	if(flagProfile== true)
        	{
        		flagArray = true;
        		readRole(role);
        		if(rol.equals("Nurse"))
        		{
        			filterMedicines();       
        			filterEnvironment();
        		}
        		else if(rol.equals("Physics"))
        		{
        			filterEnvironmentHistory();
        		}   
        		
        	}
        }      
        else if(qrcode != null && profile != null && role == null && barcode == null)//para el escenario de la dieta alimenticia
        {
        	decodeQrCode(qrcode);
        	if(flagFood== true)
        	{     
        			filterProfile();      	
        			filterFood();
        			filterIngredients();   
        		
        	}
        	
        		
        }  
        else if(qrcode != null && profile == null && role == null && barcode == null) //para los escenarios de solo desplegar texto y mostrar info de un medicamento en especifico
        {
        		decodeQrCode(qrcode);
        		if(flagMedicine== true)//cuando se visualiza un medicamento
        		{
        			filterMedicine();
        			flagTexture=true;
        		}
        		else//solo va a presentar texto comun en AR
        		{
        			System.out.println("Este es el texto del QRsote: " +  url);
        			flagText= true;
        		}

        		
        }        
        
    }
    
    public static void decodeQrCode(String path)
    {
    	 try 
    	 {        
    		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    		 DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    		 Document doc = docBuilder.parse ("http://localhost:2122/ubicomp/qrcode/?path="+ path);
    		 
    		// normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("Qrcode");

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList urlList = firstPersonElement.getElementsByTagName("description");
                     Element urlElement = (Element)urlList.item(0);

                     NodeList textFNList = urlElement.getChildNodes();
                     url = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();

                     //-------
                     
                     
                 }//end of if clause
                 
                 if(url.indexOf("medicines")>0)
                 {
                	 flagMedicine = true;
                	 //System.out.println("Medicine");
                 }
                 if(url.indexOf("ingredients") >0)
                 {
                	 flagIngredient = true;
                	 //System.out.println("Ingredients");
                 }
                 if(url.indexOf("food") >0)
                 {
                	 flagFood = true;
                	 //System.out.println("Food");
                 }
                 if(url.indexOf("profile") >0)
                 {
                	 flagProfile = true;
                	 //System.out.println("Food");
                 }


             }//end of for loop with s var
    		 
    	 }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }
    }
    
    public static void readProfile(String url)
    {
    	
        try {        	

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (url + ".xml");

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + 
            doc.getDocumentElement().getNodeName());

            NodeList listOfPersons = doc.getElementsByTagName("profile");
            int totalPersons = listOfPersons.getLength();
            System.out.println("Total no of people : " + totalPersons);

            for(int s=0; s<listOfPersons.getLength() ; s++){

                org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    
                    NodeList firstNameList = firstPersonElement.getElementsByTagName("firstName");
                    Element firstNameElement = (Element)firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    firstName = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                    System.out.println("First Name : " + firstName);

                    //-------
                    
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("lastName");
                    Element lastNameElement = (Element)lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    lastName= (( org.w3c.dom.Node)textLNList.item(0)).getNodeValue().trim();
                    System.out.println("Last Name : " + lastName);

                    //----
                    
                    NodeList genderList = firstPersonElement.getElementsByTagName("gender");
                    Element genderElement = (Element)genderList.item(0);

                    NodeList textGenderList = genderElement.getChildNodes();
                    gender = (( org.w3c.dom.Node)textGenderList.item(0)).getNodeValue().trim();
                    System.out.println("Gender : " + gender);

                    //------
                    
                    NodeList soundList = firstPersonElement.getElementsByTagName("sound");
                    Element soundElement = (Element)soundList.item(0);
                    
                    NodeList textSoundList = soundElement.getChildNodes();
                    sound = (( org.w3c.dom.Node)textSoundList.item(0)).getNodeValue().trim();
                    System.out.println("Sound : " + sound);

                    //------
                    
                    NodeList weightList = firstPersonElement.getElementsByTagName("weight");
                    Element weightElement = (Element)weightList.item(0);
                    
                    NodeList textWeightList = weightElement.getChildNodes();
                    weight =  (( org.w3c.dom.Node)textWeightList.item(0)).getNodeValue().trim();
                    System.out.println("Weight : " + weight);

                    //------
                    
                    NodeList heightList = firstPersonElement.getElementsByTagName("height");
                    Element heightElement = (Element)heightList.item(0);
                    
                    NodeList textHeightList = heightElement.getChildNodes();
                    height = (( org.w3c.dom.Node)textHeightList.item(0)).getNodeValue().trim();
                    System.out.println("Weight : " + height);

                    //------
                    
                    NodeList birthDateList = firstPersonElement.getElementsByTagName("birthDate");
                    Element birthDateElement = (Element)birthDateList.item(0);
                    
                    NodeList textBirthDateList = birthDateElement.getChildNodes();
                    birthDate = (( org.w3c.dom.Node)textBirthDateList.item(0)).getNodeValue().trim();
                    System.out.println("BirthDate : " + birthDate);

                    //------


                }//end of if clause


            }//end of for loop with s var
            
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line " 
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());

            }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();

            }catch (Throwable t) {
            t.printStackTrace ();
            }
            //System.exit (0);
            ////


    }

    public static void readRole(String url)
    {
    	
        try {        	

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (url + ".xml");

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + 
            doc.getDocumentElement().getNodeName());

            NodeList listOfPersons = doc.getElementsByTagName("role");
            int totalPersons = listOfPersons.getLength();
            System.out.println("Total no of people : " + totalPersons);

            for(int s=0; s<1 ; s++){

                org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                    Element firstPersonElement = (Element)firstPersonNode;

                    //-------
                    
                    NodeList roleList = firstPersonElement.getElementsByTagName("role");
                    Element roleElement = (Element)roleList.item(0);

                    NodeList textFNList = roleElement.getChildNodes();
                    rol = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                    System.out.println("Role : " + rol);

                    //-------
                    
                    
                }//end of if clause


            }//end of for loop with s var
            
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line " 
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());

            }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();

            }catch (Throwable t) {
            t.printStackTrace ();
            }
            //System.exit (0);
            ////


    }
    
    public void filterEnvironment()
    {
    	String urlEnvironment = url.replaceFirst("profile", "environment");
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             
             Document doc = docBuilder.parse (urlEnvironment + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("environment");
             //System.out.println("Total no of people : " + totalPersons);
             

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList temperatureList = firstPersonElement.getElementsByTagName("temperature");
                     Element temperatureElement = (Element)temperatureList.item(0);

                     NodeList textTemperatureList = temperatureElement.getChildNodes();
                     temperature = (( org.w3c.dom.Node)textTemperatureList.item(0)).getNodeValue().trim();
                     //System.out.println("Temperature : " + name);

                     //-------
                     
                     NodeList respiratoryRateList = firstPersonElement.getElementsByTagName("respiratoryRate");
                     Element respiratoryRateElement = (Element)respiratoryRateList.item(0);

                     NodeList textRespiratoryRateList = respiratoryRateElement.getChildNodes();
                     respiratoryRate= (( org.w3c.dom.Node)textRespiratoryRateList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);      
                     

                     //----
                     
                     NodeList bloodPresureList = firstPersonElement.getElementsByTagName("bloodPresure");
                     Element bloodPresureElement = (Element)bloodPresureList.item(0);

                     NodeList textBloodPresureList = bloodPresureElement.getChildNodes();
                     bloodPresure = (( org.w3c.dom.Node)textBloodPresureList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                     
                     NodeList heartRateList = firstPersonElement.getElementsByTagName("heartRate");
                     Element heartRateElement = (Element)heartRateList.item(0);

                     NodeList textHeartRateList =heartRateElement.getChildNodes();
                     heartRate = (( org.w3c.dom.Node)textHeartRateList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                      /*System.out.println("Se debe de tomar esta: ");
                    	 System.out.println("Name : " + name);
                    	 System.out.println("Time : " + time);      
                    	 System.out.println("Quantity : " + quantity);*/
                     
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:" + temperature + "&chl=" + temperature + "&chtt=Temperature&chco=0000FF,FF0000";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:" + respiratoryRate + "&chl=" + respiratoryRate + "&chtt=Respiratory%20Rate&chco=0000FF,FF0000";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:" +bloodPresure + "&chl=" + bloodPresure+ "&chtt=Blood%20Presure&chco=0000FF,FF0000";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?chs=225x125&cht=gom&chd=t:" + heartRate + "&chl=" + heartRate + "&chtt=Heart%20Rate&chco=0000FF,FF0000";
                    	 
                    
               

                 }//end of if clause


             }//end of for loop with s var
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             for(int cont=0;  cont < 9; cont++)
             {
            	 if(imageArray[cont]!=null)
            	 {
            		 System.out.println("Image " + (cont+ 1) + " : "+ imageArray[cont]);
            	 }
             }
             ////

    }
    
    public void filterEnvironmentHistory()
    {
    	String urlEnvironment = url.replaceFirst("profile", "environment/history");
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             
             Document doc = docBuilder.parse (urlEnvironment + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("environment");
             //System.out.println("Total no of people : " + totalPersons);
             

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList temperatureList = firstPersonElement.getElementsByTagName("temperature");
                     Element temperatureElement = (Element)temperatureList.item(0);

                     NodeList textTemperatureList = temperatureElement.getChildNodes();
                     temperature = (( org.w3c.dom.Node)textTemperatureList.item(0)).getNodeValue().trim();
                     //System.out.println("Temperature : " + name);

                     //-------
                     
                     NodeList respiratoryRateList = firstPersonElement.getElementsByTagName("respiratoryRate");
                     Element respiratoryRateElement = (Element)respiratoryRateList.item(0);

                     NodeList textRespiratoryRateList = respiratoryRateElement.getChildNodes();
                     respiratoryRate= (( org.w3c.dom.Node)textRespiratoryRateList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);      
                     

                     //----
                     
                     NodeList bloodPresureList = firstPersonElement.getElementsByTagName("bloodPresure");
                     Element bloodPresureElement = (Element)bloodPresureList.item(0);

                     NodeList textBloodPresureList = bloodPresureElement.getChildNodes();
                     bloodPresure = (( org.w3c.dom.Node)textBloodPresureList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                     
                     NodeList heartRateList = firstPersonElement.getElementsByTagName("heartRate");
                     Element heartRateElement = (Element)heartRateList.item(0);

                     NodeList textHeartRateList =heartRateElement.getChildNodes();
                     heartRate = (( org.w3c.dom.Node)textHeartRateList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                      /*System.out.println("Se debe de tomar esta: ");
                    	 System.out.println("Name : " + name);
                    	 System.out.println("Time : " + time);      
                    	 System.out.println("Quantity : " + quantity);*/
                     
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?cht=lc&chd=t:"+ temperature +"&chco=76A4FB&chls=3.0,0.0,0.0&chxt=x,y&chxl=0:|0|1|2|3|4|5|6|7|8|9|10|1:|0|25|50|75|100&chs=250x200&chg=10,10&chtt=Temperature";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?cht=lc&chd=t:"+ respiratoryRate +"&chco=76A4FB&chls=3.0,0.0,0.0&chxt=x,y&chxl=0:|0|1|2|3|4|5|6|7|8|9|10|1:|0|25|50|75|100&chs=250x200&chg=10,10&chtt=Respiratory%20Rate";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?cht=lc&chd=t:"+ bloodPresure +"&chco=76A4FB&chls=3.0,0.0,0.0&chxt=x,y&chxl=0:|0|1|2|3|4|5|6|7|8|9|10|1:|0|25|50|75|100&chs=250x200&chg=10,10&chtt=Blood%20Presure";
                    	 imageArray[indexImage++] = "http://chart.apis.google.com/chart?cht=lc&chd=t:"+ heartRate +"&chco=76A4FB&chls=3.0,0.0,0.0&chxt=x,y&chxl=0:|0|1|2|3|4|5|6|7|8|9|10|1:|0|25|50|75|100&chs=250x200&chg=10,10&chtt=Heart%20Rate";
                    	                   
               

                 }//end of if clause


             }//end of for loop with s var
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             for(int cont=0;  cont < 9; cont++)
             {
            	 if(imageArray[cont]!=null)
            	 {
            		 System.out.println("Image " + (cont+ 1) + " : "+ imageArray[cont]);
            	 }
             }
             ////

    }
    
    private Date When()
    {
    	Calendar calendario = new GregorianCalendar();
        int hour, minutes;
        hour =calendario.get(Calendar.HOUR_OF_DAY);
        minutes = calendario.get(Calendar.MINUTE);
        String now = hour + ":" + minutes;
        
        DateFormat format;
        Date date2= new Date();
        
        try
        {
        	format = new SimpleDateFormat("hh:mm");
        	date2 = format.parse(now);
        }
        catch(Exception e)
        {
        	
        }
    	return date2 ;
    }
    public void filterMedicines()
    {
    	String urlMedicines = url.replaceAll("profile", "medicines");
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             Document doc = docBuilder.parse (urlMedicines + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("medicine");
             //System.out.println("Total no of people : " + totalPersons);           
             
             

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList firstNameList = firstPersonElement.getElementsByTagName("name");
                     Element firstNameElement = (Element)firstNameList.item(0);

                     NodeList textFNList = firstNameElement.getChildNodes();
                     name = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                     //System.out.println("Name : " + name);

                     //-------
                     
                     NodeList lastNameList = firstPersonElement.getElementsByTagName("time");
                     Element lastNameElement = (Element)lastNameList.item(0);

                     NodeList textLNList = lastNameElement.getChildNodes();
                     time= (( org.w3c.dom.Node)textLNList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);      
                     

                     //----
                     
                     NodeList genderList = firstPersonElement.getElementsByTagName("quantity");
                     Element genderElement = (Element)genderList.item(0);

                     NodeList textGenderList = genderElement.getChildNodes();
                     quantity = (( org.w3c.dom.Node)textGenderList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                     
                     DateFormat sdf = new SimpleDateFormat("hh:mm");
                     Date date = sdf.parse(time);
                     //System.out.println((date2.getTime() -date.getTime())/60000);
                     Long diference = ((When().getTime() -date.getTime()))/60000;

                     //Si la diferencia entre la hora actual y la hora de la ingesta
                     //de medicamentos cae dentro del rango establecido de tiempo
                     //se agrega al arreglo de imagenes a desplegar
                     if(diference > (-1*minuteRange) &&  diference< minuteRange)
                     {
                    	 /*System.out.println("Se debe de tomar esta: ");
                    	 System.out.println("Name : " + name);
                    	 System.out.println("Time : " + time);      
                    	 System.out.println("Quantity : " + quantity);*/
                    	 imageArray[indexImage] = "http://chart.apis.google.com/chart?cht=p&chd=t:50,50&chs=200x200&chtt=" + name + "%20|%20Hour%20" + time + "|Take%20"+ quantity;
                    	 indexImage++;
                     }
               

                 }//end of if clause


             }//end of for loop with s var
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             for(int cont=0;  cont < 9; cont++)
             {
            	 if(imageArray[cont]!=null)
            	 {
            		 //System.out.println("Image " + (cont+ 1) + " : "+ imageArray[cont]);
            	 }
             }
             ////

    }
    
    public void filterMedicine()
    {
    	
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             Document doc = docBuilder.parse (url + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("medicine");
             //System.out.println("Total no of people : " + totalPersons);            
             

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList firstNameList = firstPersonElement.getElementsByTagName("name");
                     Element firstNameElement = (Element)firstNameList.item(0);

                     NodeList textFNList = firstNameElement.getChildNodes();
                     name = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                     //System.out.println("Name : " + name);

                     //-------
                     
                     NodeList lastNameList = firstPersonElement.getElementsByTagName("time");
                     Element lastNameElement = (Element)lastNameList.item(0);

                     NodeList textLNList = lastNameElement.getChildNodes();
                     time= (( org.w3c.dom.Node)textLNList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);      
                     

                     //----
                     
                     NodeList genderList = firstPersonElement.getElementsByTagName("quantity");
                     Element genderElement = (Element)genderList.item(0);

                     NodeList textGenderList = genderElement.getChildNodes();
                     quantity = (( org.w3c.dom.Node)textGenderList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------
                     
                     DateFormat sdf = new SimpleDateFormat("hh:mm");
                     Date date = sdf.parse(time);
                     //System.out.println((date2.getTime() -date.getTime())/60000);
                     Long diference = ((When().getTime() -date.getTime()))/60000;

                     //Si la diferencia entre la hora actual y la hora de la ingesta
                     //de medicamentos cae dentro del rango establecido de tiempo
                     //se agrega al arreglo de imagenes a desplegar
                     if(diference > (-1*minuteRange) &&  diference< minuteRange)
                     {
                    	 texture = "http://chart.apis.google.com/chart?cht=p&chd=t:50,50&chs=200x200&chtt=" + name + "%20|%20Hour%20" + time + "|Take%20"+ quantity;                    	 
                     }
                     else
                     {
                    	 texture = "http://chart.apis.google.com/chart?cht=p&chd=t:50,50&chs=200x200&chtt="+ name+"|Take%20At%20"+ time +"&chco=FF0000,FF0000";
                     }
                    	 
               

                 }//end of if clause


             }//end of for loop with s var
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             System.out.println("Textura: " + texture);

    }
    
    
    public int edad(String fecha_nac) {     //fecha_nac debe tener el formato dd/MM/yyyy
    	   
        Date fechaActual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String hoy = formato.format(fechaActual);
        String[] dat1 = fecha_nac.split("/");
        String[] dat2 = hoy.split("/");
        int anos = Integer.parseInt(dat2[2]) - Integer.parseInt(dat1[2]);
        int mes = Integer.parseInt(dat2[1]) - Integer.parseInt(dat1[1]);
        if (mes < 0) {
          anos = anos - 1;
        } else if (mes == 0) {
          int dia = Integer.parseInt(dat2[0]) - Integer.parseInt(dat1[0]);
          if (dia > 0) {
            anos = anos - 1;
          }
        }
        System.out.println("Anos : " + anos);
        return anos;
      }
    
    public void filterProfile()
    {
    	
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             Document doc = docBuilder.parse (profile + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("profile");
             //System.out.println("Total no of people : " + totalPersons);

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList firstNameList = firstPersonElement.getElementsByTagName("firstName");
                     Element firstNameElement = (Element)firstNameList.item(0);

                     NodeList textFNList = firstNameElement.getChildNodes();
                     firstName = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                     //System.out.println("Name : " + name);

                     //-------
                     
                     NodeList lastNameList = firstPersonElement.getElementsByTagName("lastName");
                     Element lastNameElement = (Element)lastNameList.item(0);

                     NodeList textLNList = lastNameElement.getChildNodes();
                     lastName= (( org.w3c.dom.Node)textLNList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);      
                     

                     //----
                     
                     NodeList genderList = firstPersonElement.getElementsByTagName("gender");
                     Element genderElement = (Element)genderList.item(0);

                     NodeList textGenderList = genderElement.getChildNodes();
                     gender = (( org.w3c.dom.Node)textGenderList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------    
                     
                     NodeList soundList = firstPersonElement.getElementsByTagName("sound");
                     Element soundElement = (Element)soundList.item(0);

                     NodeList textSoundList = soundElement.getChildNodes();
                     sound = (( org.w3c.dom.Node)textSoundList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------ 
                     
                     NodeList weightList = firstPersonElement.getElementsByTagName("weight");
                     Element weightElement = (Element)weightList.item(0);

                     NodeList textWeightList = weightElement.getChildNodes();
                     weight = (( org.w3c.dom.Node)textWeightList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------ 
                     
                     NodeList heightList = firstPersonElement.getElementsByTagName("height");
                     Element heightElement = (Element)heightList.item(0);

                     NodeList textHeightList = heightElement.getChildNodes();
                     height = (( org.w3c.dom.Node)textHeightList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList activityFactorList = firstPersonElement.getElementsByTagName("activityFactor");
                     Element activityFactorElement = (Element)activityFactorList.item(0);

                     NodeList textActivityFactorList = activityFactorElement.getChildNodes();
                     activityFactor = (( org.w3c.dom.Node)textActivityFactorList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList birthDateList = firstPersonElement.getElementsByTagName("birthDate");
                     Element birthDateElement = (Element)birthDateList.item(0);

                     NodeList textBirthDateList = birthDateElement.getChildNodes();
                     birthDate = (( org.w3c.dom.Node)textBirthDateList.item(0)).getNodeValue().trim();
                    //System.out.println("Quantity : " + firstName  + lastName + gender + sound + weight + height + birthDate);

                 }//end of if clause


             }//end of for loop with s var

             if (gender.toUpperCase().equals("MALE"))
             {
            	 calories =  (66 + (13.7 * Double.parseDouble(weight)) + (5 * Double.parseDouble(height)) - (6.8 * edad (birthDate))) * Double.parseDouble(activityFactor) ;
            	 System.out.println("Hombre : " + calories);
             }
             else
             {
            	 calories =  (655 + (9.6 * Double.parseDouble(weight)) + (1.8 * Double.parseDouble(height)) - (4.7 * edad (birthDate))) * Double.parseDouble(activityFactor) ;
            	 System.out.println("Mujer : " + calories);
             }
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             

    }
    
    public void filterFood()
    {
    	
    	 try {        	

             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             Document doc = docBuilder.parse (url + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("food");
             //System.out.println("Total no of people : " + totalPersons);

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList nameList = firstPersonElement.getElementsByTagName("name");
                     Element nameElement = (Element)nameList.item(0);

                     NodeList textFNList = nameElement.getChildNodes();
                     foodName = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                     //System.out.println("Name : " + name);

                     //-------
                     
                     NodeList sugarList = firstPersonElement.getElementsByTagName("sugar");
                     Element sugarElement = (Element)sugarList.item(0);

                     NodeList textSugarList = sugarElement.getChildNodes();
                     sugar= (( org.w3c.dom.Node)textSugarList.item(0)).getNodeValue().trim();
                     //System.out.println("Time : " + time);                         

                     //----
                     
                     NodeList fiberList = firstPersonElement.getElementsByTagName("fiber");
                     Element fiberElement = (Element)fiberList.item(0);

                     NodeList textFiberList = fiberElement.getChildNodes();
                     fiber = (( org.w3c.dom.Node)textFiberList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------    
                     
                     NodeList calciumList = firstPersonElement.getElementsByTagName("calcium");
                     Element calciumElement = (Element)calciumList.item(0);

                     NodeList textCalciumList = calciumElement.getChildNodes();
                     calcium = (( org.w3c.dom.Node)textCalciumList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------ 
                     
                     NodeList vitaminAList = firstPersonElement.getElementsByTagName("vitaminA");
                     Element vitaminAElement = (Element)vitaminAList.item(0);

                     NodeList textVitaminAList = vitaminAElement.getChildNodes();
                     vitaminA = (( org.w3c.dom.Node)textVitaminAList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);

                     //------ 
                     
                     NodeList vitaminBList = firstPersonElement.getElementsByTagName("vitaminB");
                     Element vitaminBElement = (Element)vitaminBList.item(0);

                     NodeList textVitaminBList = vitaminBElement.getChildNodes();
                     vitaminB = (( org.w3c.dom.Node)textVitaminBList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList proteinList = firstPersonElement.getElementsByTagName("protein");
                     Element proteinElement = (Element)proteinList.item(0);

                     NodeList textProteinList = proteinElement.getChildNodes();
                     protein = (( org.w3c.dom.Node)textProteinList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList transFatList = firstPersonElement.getElementsByTagName("transFat");
                     Element transFatElement = (Element)transFatList.item(0);

                     NodeList textTransFatList = transFatElement.getChildNodes();
                     transFat = (( org.w3c.dom.Node)textTransFatList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList saturedFatList = firstPersonElement.getElementsByTagName("saturedFat");
                     Element saturedFatElement = (Element)saturedFatList.item(0);

                     NodeList textSaturedFatList = saturedFatElement.getChildNodes();
                     saturedFat = (( org.w3c.dom.Node)textSaturedFatList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList ironList = firstPersonElement.getElementsByTagName("iron");
                     Element ironElement = (Element)ironList.item(0);

                     NodeList textIronList = ironElement.getChildNodes();
                     iron = (( org.w3c.dom.Node)textIronList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList sodiumList = firstPersonElement.getElementsByTagName("sodium");
                     Element sodiumElement = (Element)sodiumList.item(0);

                     NodeList textSodiumList = sodiumElement.getChildNodes();
                     sodium = (( org.w3c.dom.Node)textSodiumList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList cholesterolList = firstPersonElement.getElementsByTagName("cholesterol");
                     Element cholesterolElement = (Element)cholesterolList.item(0);

                     NodeList textCholesterolList = cholesterolElement.getChildNodes();
                     cholesterol = (( org.w3c.dom.Node)textCholesterolList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList fatCaloriesList = firstPersonElement.getElementsByTagName("fatCalories");
                     Element fatCaloriesElement = (Element)fatCaloriesList.item(0);

                     NodeList textFatCaloriesList = fatCaloriesElement.getChildNodes();
                     fatCalories = (( org.w3c.dom.Node)textFatCaloriesList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList servingSizeList = firstPersonElement.getElementsByTagName("servingSize");
                     Element servingSizeElement = (Element)servingSizeList.item(0);

                     NodeList textServingSizeList = servingSizeElement.getChildNodes();
                     servingSize = (( org.w3c.dom.Node)textServingSizeList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + quantity);
                     
                     //------ 
                     
                     NodeList servingsNumberList = firstPersonElement.getElementsByTagName("servingsNumber");
                     Element servingsNumberElement = (Element)servingsNumberList.item(0);

                     NodeList textServingsNumberList = servingsNumberElement.getChildNodes();
                     servingsNumber = (( org.w3c.dom.Node)textServingsNumberList.item(0)).getNodeValue().trim();
                     //System.out.println("Quantity : " + foodName + sugar +  fiber + calcium + vitaminA + vitaminB + protein + transFat + saturedFat + iron + sodium + cholesterol + fatCalories + servingSize + servingsNumber);

                 }//end of if clause


             }//end of for loop with s var
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }

             for(int cont=0;  cont < 9; cont++)
             {
            	 if(imageArray[cont]!=null)
            	 {
            		 //System.out.println("Image " + (cont+ 1) + " : "+ imageArray[cont]);
            	 }
             }
             ////

    }
    
    public void filterIngredients()
    {
    	 try {        	

    		 System.out.println("Este es el perfil "  + profile);
    		 String urlIngredients = new String();
             urlIngredients = profile.replaceAll("profile", "ingredients");
    		 
             DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
             Document doc = docBuilder.parse (urlIngredients + ".xml");

             // normalize text representation
             doc.getDocumentElement ().normalize ();
             //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

             NodeList listOfPersons = doc.getElementsByTagName("ingredient");
             //System.out.println("Total no of people : " + totalPersons);

             for(int s=0; s<listOfPersons.getLength() ; s++){

                 org.w3c.dom.Node firstPersonNode = listOfPersons.item(s);
                 if(firstPersonNode.getNodeType() ==  org.w3c.dom.Node.ELEMENT_NODE){


                     Element firstPersonElement = (Element)firstPersonNode;

                     //-------
                     
                     NodeList nameList = firstPersonElement.getElementsByTagName("name");
                     Element nameElement = (Element)nameList.item(0);

                     NodeList textFNList = nameElement.getChildNodes();
                     ingredientName = (( org.w3c.dom.Node)textFNList.item(0)).getNodeValue().trim();
                     System.out.println("Name : " + ingredientName);

                     //-------
                     
                     NodeList sugarList = firstPersonElement.getElementsByTagName("portion");
                     Element sugarElement = (Element)sugarList.item(0);

                     NodeList textSugarList = sugarElement.getChildNodes();
                     portion = (( org.w3c.dom.Node)textSugarList.item(0)).getNodeValue().trim();
                     System.out.println("Portion : " + portion);                         

                     //----  
                     
                                          
                     //Verifica con los datos del alimento
                     if(ingredientName.toUpperCase().equals("SUGAR"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(sugar) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("FIBER"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(fiber) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("CALCIUM"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(calcium) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("VITAMINA"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(vitaminA) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("VITAMINB"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(vitaminB) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("PROTEIN"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(protein) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("TRANSFAT"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(transFat) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("SATUREDFAT"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(saturedFat) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("IRON"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(iron) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("SODIUM"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(sodium) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("CHOLESTEROL"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(cholesterol) )
                    		 countIngredients++;
                     }
                     if(ingredientName.toUpperCase().equals("FATCALORIES"))
                     {
                    	 if(Integer.parseInt(portion) < Integer.parseInt(fatCalories) )
                    		 countIngredients++;
                     }
                     

                 }//end of if clause


             }//end of for loop with s var
             System.out.println("Ingredientes" + countIngredients);
             if(countIngredients > 3)
             {
            	 if(sound.equals("YES"))
            	 {
            		 textAudio ="NOT RECOMMENDED";
            		 flagAudio = true;
            	 }
            	 else
            	 {
            		 urlTexture = "http://chart.apis.google.com/chart?cht=p&chd=t:50,50&chs=200x200&chtt=NOT%20|RECOMMENDED&chco=FF0000,FF0000&chts=000000,20";
            		 flagTexture = true;
            	 }
             }
             else
             {
            	 if(sound.equals("YES"))
            	 {
            		 textAudio ="RECOMMENDED";
            		 flagAudio = true;
            	 }
            	 else
            	 {
            		 urlTexture = "http://chart.apis.google.com/chart?cht=p&chd=t:50,50&chs=200x200&chtt=RECOMMENDED&chco=00FF00,00FF00&chts=000000,20";
            		 flagTexture = true;
            	 }
             }
             
            	 
             
         }catch (SAXParseException err) {
             System.out.println ("** Parsing error" + ", line " 
                  + err.getLineNumber () + ", uri " + err.getSystemId ());
             System.out.println(" " + err.getMessage ());

             }catch (SAXException e) {
             Exception x = e.getException ();
             ((x == null) ? e : x).printStackTrace ();

             }catch (Throwable t) {
             t.printStackTrace ();
             }
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

    
    private Representation representArray(Variant variant)
    {
    	
    	System.out.println("Este es el contenido" + imageArray[1].toString());
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("ubicomp");

                d.appendChild(r);
                Element eltPresentation = d.createElement("presentation");
                	for (int cont = 0; cont < imageArray.length; cont++) {
                		
                	if(imageArray[cont]!=null)
                   	 {
                			
                		Element eltArray = d.createElement("array");                	 
                		Element eltImage = d.createElement("image");
                		eltImage.appendChild(d.createTextNode(imageArray[cont].toString()));
                		eltArray.appendChild(eltImage);
                		
                		eltPresentation.appendChild(eltArray);
                		r.appendChild(eltPresentation);

                	 }
                
                   
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
    
    private Representation representGuide(Variant variant)
    {
    	
    	System.out.println("Este es el contenido" + imageArray[1].toString());
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("ubicomp");

                d.appendChild(r);
                Element eltPresentation = d.createElement("presentation");
                	for (int cont = 0; cont < imageArray.length; cont++) {
                		
                	if(imageArray[cont]!=null)
                   	 {
                			
                		Element eltGuide = d.createElement("guide");                	 
                		Element eltImage = d.createElement("image");
                		eltImage.appendChild(d.createTextNode(imageArray[cont].toString()));
                		eltGuide.appendChild(eltImage);
                		
                		eltPresentation.appendChild(eltGuide);
                		r.appendChild(eltPresentation);

                	 }
                
                   
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
    
    
    private Representation representText(Variant variant)
    {   	
    	
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("ubicomp");

                d.appendChild(r);
                Element eltPresentation = d.createElement("presentation");
                	
                			
                		Element eltText = d.createElement("text");                	 
                		eltText.appendChild(d.createTextNode(url));                		
                		eltPresentation.appendChild(eltText);
                		r.appendChild(eltPresentation);               
                   
               
                
                d.normalizeDocument();           	

                // Returns the XML representation of this document.
                return representation;                   
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    	 return null;
    }
    
    private Representation representAudio(Variant variant)
    {   	
    	
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("ubicomp");

                d.appendChild(r);
                Element eltPresentation = d.createElement("presentation");
                	
                Element eltAudio = d.createElement("audio");                	 
        		Element eltText = d.createElement("text");
        		eltText.appendChild(d.createTextNode(textAudio));
        		eltAudio.appendChild(eltText);        		
        		eltPresentation.appendChild(eltAudio);
        		r.appendChild(eltPresentation);              
               
                
                d.normalizeDocument();           	

                // Returns the XML representation of this document.
                return representation;                   
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    	 return null;
    }
    
    private Representation representTexture(Variant variant)
    {   	
    	
    	if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
                Document d = representation.getDocument();
                Element r = d.createElement("ubicomp");

                d.appendChild(r);
                Element eltPresentation = d.createElement("presentation");
                	
                Element eltTexture = d.createElement("texture");                	 
        		Element eltImage = d.createElement("image");
        		if(urlTexture.length()> 0)
        		{        		
        			eltImage.appendChild(d.createTextNode(urlTexture));
        		}
        		else if(texture.length()> 0)
        		{        		
        			eltImage.appendChild(d.createTextNode(texture));
        		}
        		eltTexture.appendChild(eltImage);        		
        		eltPresentation.appendChild(eltTexture);
        		r.appendChild(eltPresentation);              
               
                
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
     * Returns a listing of all registered items.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
    	// Se crea el modelo vacio
        Model model = ModelFactory.createDefaultModel();
        
    	//Properties
    	Property result= model.createProperty(nm, "result");


 	
 		//Se crea el recurso
 		Resource filter = model.createResource(filterURI);
 		
 		//Se agregan las propiedades
 	//filter.addProperty(result, varFirstName );

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
                Element nodeFilter = d.createElement("filter");
                d.appendChild(nodeUbicomp);
                                    
                	Element eltFirstName = d.createElement("result");                	
                	eltFirstName.appendChild(d.createTextNode("resultado....."));
                	nodeFilter.appendChild(eltFirstName);

                    nodeUbicomp.appendChild(nodeFilter);
               
                d.normalizeDocument();

                // Returns the XML representation of this document.
                return representation;                         
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;*/
 		 if(flagGuide== true)
 		 {
 			 System.out.println("Guide");
 			 return representGuide(variant);
 		 }
 		 else if(flagText== true)
		 {
 			System.out.println("Text");
			 return representText(variant);
		 }
 		 else if(flagTexture== true)
		 {
 			System.out.println("Texture");
			 return representTexture(variant);
		 }
 		 else if(flagAudio== true)
		 {
 			System.out.println("Audio");
			 return representAudio(variant);
		 }
 		 else if(flagArray== true)
		 {
 			System.out.println("Array");
			 return representArray(variant);
		 }
 		 
 		url = new String();
    	flagMedicine = false;
    	flagIngredient = false;
    	flagFood = false;
    	flagProfile = false; 
 		 
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

