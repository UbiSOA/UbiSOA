package firstResource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;
import jp.sourceforge.qrcode.util.ContentConverter;
import jp.sourceforge.qrcode.util.DebugCanvas;
import jp.sourceforge.qrcode.util.DebugCanvasAdapter;

// Smallest example of QRCode Decoder

public class QRCodeDecoderCUIExample {

	String hola= new String();
	public  String FileDecode (String args) {
		if (args.length() < 1) {
			System.err.println("Usage: QRCodeDecoderCUIExample imageFilePath");
			System.exit(1);
    }

		QRCodeDecoder decoder = new QRCodeDecoder();
       hola= processDecode(args, decoder);     	
    	return hola;
	}

 // static boolean processDecode(String filename, QRCodeDecoder decoder) {
	 static String processDecode(String filename, QRCodeDecoder decoder) {
    DebugCanvas canvas = new J2SECanvas();
    decoder.setCanvas(canvas);
		BufferedImage image = null;
		try {
      if (filename.startsWith("http://"))
        image = ImageIO.read(new URL(filename));
      else
			  image = ImageIO.read(new File(filename));
			String decodedString = new String(decoder.decode(new J2SEImage(image)));
		  decodedString = ContentConverter.convert(decodedString);
			return decodedString;
		} catch (IOException e) {
			canvas.println("Error: "+ e.getMessage() + " " + filename);
      //return false;
		} catch (DecodingFailedException dfe) {
			canvas.println("Error: " + dfe.getMessage());
      //return false;
		} catch (Exception e) {
			canvas.println("Error: " + e.getMessage());
      //return false;
    }
    return null;
  }
}

class J2SEImage implements QRCodeImage {
  BufferedImage image;

	public J2SEImage(BufferedImage source) {
    this.image = source;
	}

	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
    return image.getHeight();
	}

	public int getPixel(int x, int y) {
    return image.getRGB(x ,y);

	}
}

class J2SECanvas extends DebugCanvasAdapter {
  public void println(String s) {
    //System.err.println(s);
  }
}
