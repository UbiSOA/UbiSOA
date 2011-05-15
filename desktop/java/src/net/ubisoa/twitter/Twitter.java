package net.ubisoa.twitter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;


public class Twitter {

 

    private String username;

    private String pass;

    private String response;

 

    public Twitter(String username, String pass) {

        setCredentials(username, pass);

    }

    public void statusesUpdate(String status) {

        try {

        URL url = new URL("http://api.twitter.com/version/statuses/update.format");

            status = URLEncoder.encode(status, "UTF-8");

            String parametros = "status=" + status;

            doTwitterRequest(url, parametros);

        } catch (MalformedURLException ex) {

            Logger.getLogger(Twitter.class.getName()).log(Level.SEVERE, null, ex);
      } catch (UnsupportedEncodingException ex) {

            Logger.getLogger(Twitter.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

 

    /**
057
     * Se encarga de la conexi�n con Twitter.
058
     * Necesita tener precargada los datos de autentificaci�n (constructor), la url y los par�metros
059
     */

    private void doTwitterRequest(URL url, String parametros) {

        response = "";

        try {

            // Creamos una conexi�n

            URLConnection conn;

            conn = url.openConnection();

            conn.setAllowUserInteraction(false);

            conn.setDoOutput(true);

            // Configuramos la autentificaci�n (sencilla basada en HTTP)

            conn.setRequestProperty("Authorization", "Basic " + getBasicCredentials());

 

            // Preparamos la conexi�n con el servidor (vamos a mandar un formulario por post)

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Abrimos el canal de comunicaci�n de env�o

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());

            // Mandamos los par�metros de la acci�n que (los ha tenido que precargar el m�todo correspondiente)

            out.writeBytes(parametros.toString());

            // Nos aseguramos de que todo se env�e

            out.flush();

            // Ya hemos dicho lo que ten�amos que decir, as� que cerramos la conexi�n de envio

            out.close();

 

            // Capturamos la respuesta

            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));

 

            String l = "";

            while ((l = input.readLine()) != null) {

                response += l + "\n";

            }

 
       } catch (IOException ex) {

            Logger.getLogger(Twitter.class.getName()).log(Level.SEVERE, null, ex);

 

        }

 

    }
 

    public void setCredentials(String username, String pass) {
        this.username = username;

        this.pass = pass;

    }

 

    public String getBasicCredentials() {

        byte[] credentialsBytes = (username + ":" + pass).getBytes();

        byte[] encodedBytes = Base64.encodeBase64(credentialsBytes);

        return new String(encodedBytes);

    }

 

    public String getResponse() {

        return response;

    }

}
