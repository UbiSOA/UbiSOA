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
 * The modified class starts the CoAP server, which receives a 
 * CoAP messages in order to read the UbiSOAMessage's payload. It 
 * identify the type of data the user want to see and gives a corresponding reply.
 *
 * Code adapted for particular purposes by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
 *****************************************************************************/


package src.net.ubisoa.coap.server;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.net.ubisoa.coap.message.UbiSOACoAPMessage;
import src.net.ubisoa.coap.sensing.FakeCollecter;
import src.net.ubisoa.coap.sensing.Reading;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapMessage;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapServer;
import org.ws4d.coap.interfaces.CoapServerChannel;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapResponseCode;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */
public class BasicCoapServer implements CoapServer {

    private static final int PORT = 5683;
    static int counter = 0;
    private Connection connection;
    UbiSOACoAPMessage ubisoaMessage = new UbiSOACoAPMessage();

    public static void main(String[] args) {
        System.out.println("Start UBISOA-CoAP Server on port " + PORT);
        BasicCoapServer server = new BasicCoapServer();

        CoapChannelManager channelManager = BasicCoapChannelManager.getInstance();
        channelManager.createServerListener(server, PORT);

    }

    @Override
    public CoapServer onAccept(CoapRequest request) {
        System.out.println("Accept connection...");
        return this;
    }

    @Override
    public void onRequest(CoapServerChannel channel, CoapRequest request) {
        try {
            String payloadRequested = "", sensing = "none";

            if (request.getPayload().length != 0) {
                switch (ubisoaMessage.getOptionRequestedM(request.getPayload())) {
                    case "01": {
                        sensing = "Temperature";
                        payloadRequested = getLastReading("temp").getData();
                    }
                    break;
                    case "02": {
                        sensing = "Humidity";
                        payloadRequested = getLastReading("hum").getData();
                    }
                    break;
                    default:
                }
            }

            System.out.println("Received message: " + request.toString() + " URI: " + request.getUriPath() + " SENSING_REQUEST: " + sensing);
            CoapMessage response = channel.createResponse(request, CoapResponseCode.Content_205);
            response.setContentType(CoapMediaType.text_plain);
            response.setPayload(payloadRequested);

            if (request.getObserveOption() != null) {
                System.out.println("Client wants to observe this resource.");
            }

            response.setObserveOption(1);
            channel.sendMessage(response);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BasicCoapServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onSeparateResponseFailed(CoapServerChannel channel) {
        System.out.println("Separate response transmission failed.");

    }

    private void databaseConnect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.sqlite");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UbiSOACoAPMessage getLastReading(String table) {
        try {
            if (connection == null || connection.isClosed()) {
                databaseConnect();
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + table + " ORDER BY date DESC LIMIT 0,1");
            if (resultSet.next()) {
                ubisoaMessage.setNodeId(resultSet.getString("node").getBytes());
                ubisoaMessage.setData(resultSet.getString("temperature").getBytes());
                //   ubisoaMessage.setHumidity(resultSet.getString("humidity").getBytes());
            }
            resultSet.close();
            connection.close();

            return ubisoaMessage;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}