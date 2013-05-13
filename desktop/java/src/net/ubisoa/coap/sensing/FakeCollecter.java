/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*This class allows to simulate the temperature capture 
 * delimited by a group of nodes (MOTES).
 * 
 * Note: The initial goal of this class is to allow 
 * operational testing without the need for physical sensors.
 * 
 * The range of nodes is configurable through variables 
 * (NUMBER_NODES_MAX and NUMBER_NODES_MIN). 
 * The temperature range is also configurable through 
 * variables (LIMIT_NUMBER_MAX and LIMIT_NUMBER_MIN).
 *
 * Written by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
  *****************************************************************************/


package src.net.ubisoa.coap.sensing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import javax.swing.Timer;
import org.ws4d.coap.interfaces.CoapChannel;
import org.ws4d.coap.interfaces.CoapMessage;
import org.ws4d.coap.messages.AbstractCoapMessage.CoapHeaderOptionType;
import org.ws4d.coap.messages.CoapBlockOption;
import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapPacketType;
import src.net.ubisoa.coap.message.UbiSOACoAPMessage;

public class FakeCollecter {

    Random rand = new Random();
    static int currRand,currRand2,currRand3;
    static int NUMBER_NODES_MAX = 13;
    static int NUMBER_NODES_MIN = 10;
    static int RANK_NODES = (NUMBER_NODES_MAX - NUMBER_NODES_MIN);
    static int LIMIT_NUMBER_MAX = 150;
    static int LIMIT_NUMBER_MIN = 100;
    static int RANK_SENSING=(LIMIT_NUMBER_MAX - LIMIT_NUMBER_MIN);
    static String opt="00";
    private Connection connection = null;
    static FakeCollecter fakeObj=new FakeCollecter();
    UbiSOACoAPMessage ubisoaMessage=new UbiSOACoAPMessage();
    
    public FakeCollecter() {
    }

    public void startCollecter() {
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                currRand = rand.nextInt(RANK_SENSING) + LIMIT_NUMBER_MIN;
                currRand2 = rand.nextInt(RANK_SENSING) + LIMIT_NUMBER_MIN;
                currRand3 = rand.nextInt(RANK_NODES) + NUMBER_NODES_MIN;
            }
        };

        Timer timer = new Timer(2000, actionListener);
        timer.start();
    }
    
    public static void main(String args[]) throws InterruptedException, UnsupportedEncodingException {
        fakeObj.startCollecter();
        fakeObj.start();
        while (true) {
            Thread.currentThread().sleep(5000);
            fakeObj.setTemperature(Integer.toString(currRand3), Integer.toString(currRand), Integer.toString(currRand2));
        }
    }

    public void setTemperature(String nodeId, String temp, String humi) throws UnsupportedEncodingException {
        CoapMessage coapMessage=new CoapMessage() {
            byte[] payloadTemp;
            byte[] payloadhumi;

        @Override
        public int getMessageCodeValue() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getMessageID() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setMessageID(int msgID) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public byte[] serialize() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void incRetransCounterAndTimeout() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CoapPacketType getPacketType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public byte[] getPayload() {
            return this.payloadTemp;
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPayload(byte[] payload) {
            this.payloadTemp=payload;
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPayload(char[] payload) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPayload(String payload) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getPayloadLength() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setContentType(CoapMediaType mediaType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CoapMediaType getContentType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public byte[] getToken() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CoapBlockOption getBlock1() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBlock1(CoapBlockOption blockOption) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CoapBlockOption getBlock2() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBlock2(CoapBlockOption blockOption) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getObserveOption() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setObserveOption(int sequenceNumber) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeOption(CoapHeaderOptionType optionType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CoapChannel getChannel() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setChannel(CoapChannel channel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getTimeout() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean maxRetransReached() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isReliable() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isRequest() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isResponse() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
        
        System.out.println("Node ID: " + nodeId );
        System.out.println("Current temperature: " + temp );
        System.out.println("Current humidity: " + humi );
        
        //SAVING DATA
        coapMessage.setPayload((nodeId+opt+temp+humi).getBytes());
        dbStore(coapMessage);    
    }

    public void start() {
        dbConnect();
    }
    
    private void dbConnect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:dat/sensing.sqlite");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dbStore(CoapMessage message) throws UnsupportedEncodingException {
        boolean res = false;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            if (connection == null) {
                dbConnect();
            }

            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS temp ("
                    + "node, temperature, date, action, exp2)");
            
             s.executeUpdate("CREATE TABLE IF NOT EXISTS hum ("
                    + "node, humidity, date, action, exp2)");
             

            String sql = "INSERT INTO temp VALUES (?, ?, ?, ?, ?)";
            PreparedStatement p = connection.prepareStatement(sql);
            

            p.setString(1, ubisoaMessage.getNodeIdM(message.getPayload()));
            p.setString(2, ubisoaMessage.getTemperatureM(message.getPayload()));
            //p.setString(3, ubisoaMessage.getHumidityM(message.getPayload()));
            p.setString(3, timeStamp);
            p.setString(4, "mote");
            p.addBatch();

            connection.setAutoCommit(false);
            p.executeBatch();
            connection.setAutoCommit(true);
            
            sql = "INSERT INTO hum VALUES (?, ?, ?, ?, ?)";
            p = connection.prepareStatement(sql);
            

            p.setString(1, ubisoaMessage.getNodeIdM(message.getPayload()));
            //p.setString(2, ubisoaMessage.getTemperatureM(message.getPayload()));
            p.setString(3, ubisoaMessage.getHumidityM(message.getPayload()));
            p.setString(3, timeStamp);
            p.setString(4, "mote");
            p.addBatch();

            connection.setAutoCommit(false);
            p.executeBatch();
            connection.setAutoCommit(true);
            
            s.close();
            p.close();
            connection.close();
            connection = null;

            res = true;
        } catch (SQLException e) {
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