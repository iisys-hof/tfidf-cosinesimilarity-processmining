package de.iisys.schub.processMining.activities.network;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Only when Nuxeo is on a https server:
 * 
 * CAUTION: Use this class only for testing purposes, when this application (in shindig) and nuxeo 
 * are NOT on the same server.
 * BETTER: Install Nuxeo's server's certificates on this application's server.
 * 
 * NEVER use on a productive system.
 *
 */
@SuppressWarnings("deprecation")
public class NuxeoSSLClientWrapper {
	
	
	public static HttpClient wrapClient(HttpClient client) {		
		DefaultHttpClient clientReference = null;
		
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			X509TrustManager trustManager = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string){}
				
				public void checkServerTrusted(X509Certificate[] xcs, String string){}
				
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			context.init(null, new TrustManager[]{trustManager}, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(context);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager connectionManager = client.getConnectionManager();
			SchemeRegistry schemeRegistry = connectionManager.getSchemeRegistry();
			schemeRegistry.register(new Scheme("https", socketFactory, 443));
			clientReference = new DefaultHttpClient(connectionManager, client.getParams());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(KeyManagementException e) {
			e.printStackTrace();
		}
		return clientReference;
	}

}
