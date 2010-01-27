package firstResource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.util.DateUtil;

/**
 * Sample code in Java for using the kooaba REST API
 * Contact: support@kooaba.com
 * 
 * @version 2009-04-27
 * @author  Joachim Fornallaz
 */
public class QuerySample {

	public static String accessKey = "df8d23140eb443505c0661c5b58294ef472baf64";
	public static String secretKey = "054a431c8cd9c3cf819f3bc7aba592cc84c09ff7";
	public static String apiAddress = "http://search.kooaba.com/groups/{group_id}/queries.xml";
	private String sourceFile;

	/**
	 * @param args
	 */
	/*public void Identify(String args) {
		// TODO Auto-generated method stub
		QuerySample sample = new QuerySample("c:/java/lena.jpg");
		
		sample.run();
	}*/

	/**
	 * Calculates the MD5 digest of the request body for POST or PUT methods
	 * @param httpMethod
	 * @return String MD5 digest as a hex string
	 * @throws IOException
	 */
	private static String contentMD5(EntityEnclosingMethod httpMethod) throws IOException {
		ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
		httpMethod.getRequestEntity().writeRequest(requestOutputStream);
		return DigestUtils.md5Hex(requestOutputStream.toByteArray());
	}

	/**
	 * Calculates the KWS signature of a HTTP request (POST or PUT)
	 * @param httpMethod
	 * @return String Signature
	 * @throws IOException
	 */
	public static String kwsSignature(EntityEnclosingMethod httpMethod) throws IOException {
		String method = httpMethod.getName();
		String hexDigest = contentMD5(httpMethod);
		String contentType = HeaderElement.parseElements(httpMethod.getRequestEntity().getContentType())[0].getName();
		String dateValue = httpMethod.getRequestHeader("Date").getValue();
		String requestPath = httpMethod.getPath();
		String signatureInput = new String(method + "\n" + hexDigest + "\n" + contentType + "\n" + dateValue + "\n" + requestPath);
		
		String digestInput = new String(secretKey + "\n\n" + signatureInput);
		byte[] digestBytes = DigestUtils.sha(digestInput);
		byte[] encoded = Base64.encodeBase64(digestBytes);
		return new String(encoded);
	}

	public QuerySample(String imagePath) {
		sourceFile = imagePath;
	}

	public String run() {
		String targetURL = apiAddress.replaceFirst("\\{group_id\\}", "32");
		System.out.println(targetURL);
		try {
			// Prepare content body
			File targetFile = new File(sourceFile);
			FilePart imagePart = new FilePart("query[file]", targetFile, "image/jpeg", null);
			Part[] parts = { imagePart };
			// Prepare the HTTP method
			PostMethod queryPost = new PostMethod(targetURL);
			queryPost.setRequestEntity(new MultipartRequestEntity(parts, queryPost.getParams()));
			queryPost.addRequestHeader(new Header("Date", DateUtil.formatDate(new Date())));
			queryPost.addRequestHeader(new Header("Authorization", "KWS " + accessKey + ":" + kwsSignature(queryPost)));
			// Execute the method
			HttpClient client = new HttpClient();
			int status = client.executeMethod(queryPost);
			// Debugging output
			Header[] headers = queryPost.getRequestHeaders();
			//System.out.println("Headers:");
			//for (int i = 0; i < headers.length; i++) {
				//System.out.print("  " + headers[i].toExternalForm());
			//}
			//System.out.println("Status: " + status);
			//System.out.println(queryPost.getResponseBodyAsString());
			return queryPost.getResponseBodyAsString();
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
