package net.ubisoa.discovery;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

@SuppressWarnings("restriction")
public class DiscoveryCore {
	private static String discoveryVersionNumber = "1.0.1";
	
	public static void registerService(String name, String interfaces, int port) {
		TXTRecord txtRecord = new TXTRecord();
		txtRecord.set("txtvers", discoveryVersionNumber);
		txtRecord.set("implements", "geolocation.resolver");
		try {
			DNSSD.register(0, DNSSD.ALL_INTERFACES, name, "_ubisoa._tcp", null,
					null, port, txtRecord, new RegisterListener() {

				public void serviceRegistered(DNSSDRegistration registration, int flags,
						String serviceName, String regType, String domain) {
					System.out.println("Service is registered.");
				}

				public void operationFailed(DNSSDService service, int errorCode) {
					System.err.println("Registration failed " + errorCode);
				}});
		} catch (DNSSDException e) {
			e.printStackTrace();
		}
	}
	
}
