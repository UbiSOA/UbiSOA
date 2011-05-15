package net.ubisoa.rfid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.OutputChangeEvent;
import com.phidgets.event.OutputChangeListener;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;
import com.phidgets.event.TagLossEvent;
import com.phidgets.event.TagLossListener;


public class RFIDReader
{
	
	public static final void main(String args[]) throws Exception {
		RFIDPhidget rfid;

		System.out.println(Phidget.getLibraryVersion());

		rfid = new RFIDPhidget();
		rfid.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae)
			{
				try
				{
					((RFIDPhidget)ae.getSource()).setAntennaOn(true);
					((RFIDPhidget)ae.getSource()).setLEDOn(true);
				}
				catch (PhidgetException ex) { }
				System.out.println("attachment of " + ae);
			}
		});
		rfid.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
			}
		});
		rfid.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("error event for " + ee);
			}
		});
		rfid.addTagGainListener(new TagGainListener()
		{
			public void tagGained(TagGainEvent oe)
			{
				String value = oe.toString().substring(oe.toString().indexOf(":") + 2,oe.toString().length());
				System.out.println("Value in: " + value);
				
				///
				try {
			        // Construct data
					
			        String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode("Value", "UTF-8");
			        data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(value.toString(), "UTF-8");
			       
			     
			        URL url = new URL("http://127.0.0.1:8411");
			        URLConnection conn = url.openConnection();
			        conn.setDoOutput(true);
			        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());		       
			        wr.write(data);
			        wr.flush();
			        System.out.println(data);
			    
			        // Get the response
			        
			        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        int i=0;
			        
			        while ((rd.readLine()) != null) {
			            // Process line...
			        //	System.out.println("linea"+i+""+line);
			        	i++;
			        }
			        
			        wr.close();
			        rd.close();
			    } catch (Exception e) {
			    	 System.out.println("EXCEPCION..");
			    }
				///
				
				
				
			}
		});
		rfid.addTagLossListener(new TagLossListener()
		{
			public void tagLost(TagLossEvent oe)
			{
				String value = oe.toString().substring(oe.toString().indexOf(":") + 2,oe.toString().length());
				System.out.println("Value out: " + value);
			}
		});
		rfid.addOutputChangeListener(new OutputChangeListener()
		{
			public void outputChanged(OutputChangeEvent oe)
			{
				System.out.println(oe);
			}
		});

		rfid.openAny();
		System.out.println("waiting for RFID attachment...");
		rfid.waitForAttachment(1000);

		System.out.println("Serial: " + rfid.getSerialNumber());
		System.out.println("Outputs: " + rfid.getOutputCount());

		System.out.println("Outputting events.  Input to stop.");
		System.in.read();
		System.out.print("closing...");
		rfid.close();
		rfid = null;
		System.out.println(" ok");
	}
}
