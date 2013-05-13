/*
 * This class structure a package that we have named UbiSOACoAPMessage, 
 * it is embedded in the payload of the CoAP message. Its structure is following:
 * 
 * //PAYLOAD MESSAGE
 * ////////////////////////////////////////////////////
 * ///          ///                ///              ///
 * /// NODE-ID  ///  OPTIONSENSING ///      DATA    ///
 * /// (2 char) ///     (2 char)   ///    (5 char)  ///
 * ///          ///                ///              ///
 * ////////////////////////////////////////////////////
 * 
 * ///OPTIONSENSING
 * ///01=TEMPERATURE
 * ///02=HUMIDITY
 * 
 * Written by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
  *****************************************************************************/


package src.net.ubisoa.coap.message;

import java.io.UnsupportedEncodingException;


public class UbiSOACoAPMessage {
    byte[] nodeId;
    byte[] data;
    byte[] option;
    
    public void UbiSOACoAPMessage(){}

    public String getNodeId() throws UnsupportedEncodingException {
        return new String(nodeId, "UTF-8");
    }

    public void setNodeId(byte[] nodeId) {
        this.nodeId = nodeId;
    }
    
     public String getData() throws UnsupportedEncodingException {
        return new String(data, "UTF-8");
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    

    public byte[] getOption() {
        return option;
    }

    public void setOption(byte[] option) {
        this.option = option;
    }
    
    public String getNodeIdM(byte[] payload) throws UnsupportedEncodingException {
        String salida = new String(payload, "UTF-8");
        return salida.substring(0, 2);
    }

    public String getTemperatureM(byte[] payload) throws UnsupportedEncodingException {
        String salida = new String(payload, "UTF-8");
        return salida.substring(4, 7);
    }

    public String getHumidityM(byte[] payload) throws UnsupportedEncodingException {
        String salida = new String(payload, "UTF-8");
        return salida.substring(7, 10);
    }
    
    public String getOptionRequestedM(byte[] payload) throws UnsupportedEncodingException {
        String salida = new String(payload, "UTF-8");
        return salida.substring(2, 4);
    }
    
}
