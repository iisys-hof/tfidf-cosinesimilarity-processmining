package de.iisys.schub.processMining.activities.network;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import de.iisys.schub.processMining.config.Config;

/**
 * Connector for all Camunda connections.
 * Use the config.properties to configure the Camunda URL.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class CamundaConnector {
	private static String CAMUNDA_URL;
	private static final String API_PATH = "/engine-rest";
	private static final String METHOD_POST_DEPLOYMENT = "/deployment/create";
	
	/**
	 * Sends the given cmmn xml file to Camunda in order to deploy it as a new case definition.
	 * @param name
	 * @param cmmnXml
	 * @return
	 * 		Returns the href link to the newly deployed case. 
	 * 		Returns null if failed.
	 */
	public static String deployCMMNCaseDefinition(String name, String cmmnXml) {
		CAMUNDA_URL = Config.CAMUNDA_URL.getValue();
		
		try {
			URL url = new URL(CAMUNDA_URL+API_PATH+"/engine/default"+METHOD_POST_DEPLOYMENT);
			
			/*
			JSONObject json = new JSONObject();
			json.put("deployment-name", name);
			json.put("enable-duplicate-filtering", false);
			json.put("deploy-changed-only", false);
			json.put("data", cmmnXml);
			
			String response = HttpUtil.sendRequest("POST", url, json.toString()); */
			
			String response = HttpUtil.postMultipartFormSubmit(url, cmmnXml, name);
			
			System.out.println(response);

			return new JSONObject(response).getJSONObject("links").getString("href");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException je) {
			System.out.println("Response is no json object!");
			return null;
		}
	}
	
	/**
	 * For testing purposes only.
	 */
	public static void testConnection() {
		CAMUNDA_URL = Config.CAMUNDA_URL.getValue();
		
		String cmmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
						"<bpmn2:definitions ...>"+
						 "<!-- BPMN 2.0 XML omitted -->"+
						"</bpmn2:definitions>";
		
		try {
			URL url = new URL(CAMUNDA_URL+API_PATH+"/engine/default"+METHOD_POST_DEPLOYMENT);
//			String response = HttpUtil.sendRequest("POST", url, null);
			String response = HttpUtil.postMultipartFormSubmit(url, cmmn, "Test");
			System.out.println(response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
