/* Copyright [2011] [University of Rostock]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/

/*
 * This code shows a modification to the original version written by 
 * Christian Lerche <christian.lerche@uni-rostock.de>. 
 * 
 * This class simulates an actuator to identify if the temperature has exceeded 
 * the limit defined statically performs a predetermined action, 
 * in otherwise it will be waiting. 
 * Data is stored in the database for control has a user actions.
 *
 * Code adapted for particular purposes by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
 *****************************************************************************/

package src.net.ubisoa.coap.client;

import java.awt.SplashScreen;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ws4d.coap.Constants;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapRequestCode;
import src.net.ubisoa.coap.message.UbiSOACoAPMessage;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */
public class BasicCoapClient implements CoapClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = Constants.COAP_DEFAULT_PORT;
    static int counter = 0;
    CoapChannelManager channelManager = null;
    CoapClientChannel clientChannel = null;
    static String CLIENT_ID = "00";
    UbiSOACoAPMessage ubisoaMessage = new UbiSOACoAPMessage();
    private boolean on;
    private Connection connection = null;
    static BasicCoapClient client = new BasicCoapClient();


    
    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
        System.out.println("Start UBISOA-CoAP Client");
        client.channelManager = BasicCoapChannelManager.getInstance();
        client.runTestClient();

        while (true) {
            
            client.runTestClient();
            Thread.currentThread().sleep(4000);
        }
    }

    public void runTestClient() {
        try {
            clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS), PORT);
            CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
//			coapRequest.setContentType(CoapMediaType.text_plain);
//			coapRequest.setToken("ABCD".getBytes());
//			coapRequest.setUriHost("123.123.123.123");
//			coapRequest.setUriPort(5683);
//			coapRequest.setUriPath("/seg1/seg2/seg3");
            coapRequest.setUriPath("/border_sensor");
            coapRequest.setPayload((CLIENT_ID + "01").getBytes());//ID del cliente
//			coapRequest.setUriQuery("a=1&b=2&c=3");
//			coapRequest.setProxyUri("coap://localhost:61616/sv");
            clientChannel.sendMessage(coapRequest);
            //System.out.println("Sent Request:"+coapRequest.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
        System.out.println("Connection Failed");
    }

    @Override
    public void onResponse(CoapClientChannel channel, CoapResponse response) {
        try {
            System.out.println("Received response:" + response.toString());
            System.out.println("Payload: " + new String(response.getPayload(), "UTF-8"));
            String t = new String(response.getPayload(), "UTF-8").replace("\n", "");
            int temp = Integer.valueOf(t);

            //If the temperature is higher than 100 degrees
            if (temp > 100) {
                if (!on) {
                    on = true;
                }
            } else {
                if (on) {
                    on = false;
                }
            }
            
            System.out.println("Led: " + on);
            try {
                this.dbStore();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BasicCoapClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(BasicCoapClient.class.getName()).log(Level.SEVERE, null, ex);
            }


        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BasicCoapClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean getOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
   
   
     private boolean dbStore() throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
        boolean res = false;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
        
        Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.sqlite");
        
        try {

            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS dataClient ("
                    + "node, opcion, date, exp1, exp2, exp3)");

            String sql = "INSERT INTO dataClient VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement p = connection.prepareStatement(sql);
            
            p.setString(1, CLIENT_ID);
            p.setBoolean(2,on);
            System.out.println(on);
            p.setString(3, timeStamp);
            p.executeUpdate();
            
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                connection = null;
            }
        }
        return res;
    }
}