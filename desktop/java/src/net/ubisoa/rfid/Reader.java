package net.ubisoa.rfid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Reader
{
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final void main(String args[]) throws Exception {
		System.out.println(Phidget.getLibraryVersion());

		RFIDPhidget rfid = new RFIDPhidget();
		rfid.addAttachListener(new AttachListener() {
			public void attached(AttachEvent e) {
				try {
					RFIDPhidget source = (RFIDPhidget)e.getSource();
					source.setAntennaOn(true);
					source.setLEDOn(false);
				}
				catch (PhidgetException ex) {
					ex.printStackTrace();
				}
				System.out.println("Attachment: " + e);
			}
		});
		rfid.addDetachListener(new DetachListener() {
			public void detached(DetachEvent e) {
				System.out.println("Detachment: " + e);
			}
		});
		rfid.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent e) {
				System.out.println("Error: " + e);
			}
		});
		rfid.addTagGainListener(new TagGainListener() {
			public void tagGained(TagGainEvent e)
			{
				try {
					RFIDPhidget source = (RFIDPhidget)e.getSource();
					source.setLEDOn(true);
				} catch (PhidgetException ex) {
					ex.printStackTrace();
				}
				System.out.println("Tag Gained: " + e.getValue());
				postTag(e.getValue(), "gained");
			}
		});
		rfid.addTagLossListener(new TagLossListener() {
			public void tagLost(TagLossEvent e) {
				try {
					RFIDPhidget source = (RFIDPhidget)e.getSource();
					source.setLEDOn(false);
				} catch (PhidgetException ex) {
					ex.printStackTrace();
				}
				System.out.println("Tag Lost: " + e.getValue());
				postTag(e.getValue(), "lost");
			}
		});
		rfid.addOutputChangeListener(new OutputChangeListener() {
			public void outputChanged(OutputChangeEvent e) {
				System.out.println("Output Change: " + e);
			}
		});

		rfid.openAny();
		System.out.println("Waiting for RFID attachment…");
		rfid.waitForAttachment(1000);

		System.out.println("Serial: " + rfid.getSerialNumber());
		System.out.println("Outputs: " + rfid.getOutputCount());
		System.out.println("Outputting events. Input to stop.");
		System.in.read();
		System.out.print("Closing…");
		rfid.close();
		rfid = null;
		System.out.println(" [OK]");
	}

	private static void postTag(String id, String action) {
		try {
			String timestamp = dateFormat.format(new Date());
			String enc = "UTF-8";
			String data = "timestamp=" + URLEncoder.encode(timestamp, enc) +
				"&id=" + URLEncoder.encode(id, enc) + "&action=" + URLEncoder.encode(action, enc);
			URL url = new URL("http://127.0.0.1:8350");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(data);
			writer.flush();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			while (reader.readLine() != null);
			writer.close();
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
