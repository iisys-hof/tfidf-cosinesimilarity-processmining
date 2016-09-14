package de.iisys.schub.processMining.activities.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
	
	public static String basicAuthRequestGET(URL url, String username, String password) {
		return HttpUtil.basicAuthRequest("GET", url, username, password, null);
	}
	
	public static String basicAuthRequestPOST(URL url, String username, String password) {
		return HttpUtil.basicAuthRequest("POST", url, username, password, null);
	}
	
	public static String basicAuthSendRequestPOST(URL url, String username, String password, String json) {
		return HttpUtil.basicAuthRequest("POST", url, username, password, json);
	}
	
	public static String sendRequest(String requestMethod, URL url, String json) {
		return HttpUtil.basicAuthRequest(requestMethod, url, null, null, json);
	}
	
	public static String basicAuthRequest(String requestMethod, URL url, String username, String password, String json) {	
		try {
			// authentication
			String basicAuth = null;
			if(username!=null && password!=null) {
				String userpass = username+":"+password;
				basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(
						userpass.getBytes());
			} 
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			if(basicAuth!=null)
				connection.setRequestProperty("Authorization", basicAuth);
			
			// send json
			if(json != null) {
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				connection.setRequestProperty("Content-Length", String.valueOf(json.getBytes().length));
				
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
				out.write(json);
				out.flush();
				out.close();
			}
			
			// read response:			
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					(InputStream)connection.getInputStream(), StandardCharsets.UTF_8));
			
			StringBuffer buf = new StringBuffer();
			String line;
			while((line = in.readLine()) != null) {
				buf.append(line);
			}
			in.close();
			
			return buf.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static FileInputStream basicAuthFileRequest(String requestMethod, URL url, String username, String password, String json) {	
		try {
			// authentication
			String userpass = username+":"+password;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(
					userpass.getBytes());
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("Authorization", basicAuth);
			
			// send json
			if(json != null) {
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Content-Length", String.valueOf(json.getBytes().length));
				
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(json);
				out.flush();
				out.close();
			}
			
			// read response
			FileInputStream fin = null;
			InputStream in = connection.getInputStream();
			if(in instanceof FileInputStream) {
				fin = (FileInputStream) in;
			}
			
			return fin;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String postMultipartFormSubmit(URL url, String cmmn, String deploymentName) {
		String boundary = "28319d96a8c54b529aa9159ad75edef9";
		String twoHyphens = "--";
		String lineEnd = "\r\n";
		
		try {
						
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			
			// send multipart form:
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			
			out.writeBytes(twoHyphens + boundary + lineEnd);
			out.writeBytes("Content-Disposition: form-data; name=\"deployment-name\""+lineEnd);
			out.writeBytes(lineEnd);
			out.writeBytes(deploymentName + lineEnd);

			out.writeBytes(twoHyphens + boundary + lineEnd);
			out.writeBytes("Content-Disposition: form-data; name=\"enable-duplicate-filtering\""+lineEnd);
			out.writeBytes(lineEnd);
			out.writeBytes("true" + lineEnd);
			
			// from v7.4: ( https://docs.camunda.org/manual/latest/reference/rest/ )
			/*
			out.writeBytes(twoHyphens + boundary + lineEnd);
			out.writeBytes("Content-Disposition: form-data; name=\"deployment-source\""+lineEnd);
			out.writeBytes(lineEnd);
			out.writeBytes("case application" + lineEnd); */
			
			out.writeBytes(twoHyphens + boundary + lineEnd);
			out.writeBytes("Content-Disposition: form-data; name=\"data\"; filename=\"activity-mining.cmmn\""+lineEnd);
			out.writeBytes(lineEnd);
			out.writeBytes(cmmn);
			
			out.writeBytes(lineEnd);
			out.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			int responseCode = connection.getResponseCode();
			
			if(responseCode == 200) {
			
				// read response:			
				final BufferedReader in = new BufferedReader(new InputStreamReader(
						(InputStream)connection.getInputStream(), StandardCharsets.UTF_8));
				
				StringBuffer buf = new StringBuffer();
				String line;
				while((line = in.readLine()) != null) {
					buf.append(line);
				}
				in.close();
	
				
				out.flush();
				out.close();
				
				return buf.toString();
			} else {
				return responseCode + ": " +connection.getResponseMessage()+ " while connecting to "+url.toString();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
