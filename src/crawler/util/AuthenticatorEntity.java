package crawler.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

// this class is just used for site: http://ir.nist.gov/tv2009/soundandvision/tv9.sv.test
// Basic Authorization
public class AuthenticatorEntity extends Authenticator {

	protected PasswordAuthentication getPasswordAuthentication() {
		/*
		System.err.println("requestType:" + getRequestorType() +  // "SERVER"
				"\nrequestPrompt:" + getRequestingPrompt() +  // "tv-sv"
				"\nrequestSchem:" + getRequestingScheme());  // "basic"
		*/
		String user = "sv-0030";
		char pass[] = {'O','y','j','j','K','&','m','h',};
		return new PasswordAuthentication(user, pass);
		
	}
	
}
