package de.iisys.schub.processMining.activities.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import de.iisys.schub.processMining.config.Config;

/**
 * Connector for all Liferay connections.
 * Use the config.properties to configure the Liferay URL.
 * TODO: Use a debug_username and debug_password with admin rights.
 * 
 * Dependency: To get a wiki page by its page id, you have to use the CamundaACM-portlet (iisys/SCHub) in Liferay.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class LiferayConnector {
	
	private String debug_username = "baerbel";
	private String debug_password = "bitte";
	
	private final String LIFERAY_URL;
	private final String PATH_API = "/api/jsonws";
	
	private final String PATH_BLOGENTRY = "/blogsentry/get-entry/entry-id";
//	private final String PATH_WIKIPAGE = "/wikipage/get-page";
	private final String PATH_MESSAGEBOARD = "/mbmessage/get-message/message-id";
	private final String PATH_JOURNALARTICLE = "/journalarticle/get-latest-article/resource-prim-key";
	private final String PATH_JOURNALTEMPLATE = "/journaltemplate/get-template";
	
	private final String PATH_WIKIPAGE = "/CamundaACM-portlet.caseinstance/get-wiki-page/page-id";
	private final String PATH_WIKIPAGE_RESPRIMKEY = "/CamundaACM-portlet.caseinstance/get-wiki-page-by-resource-prim-key/resource-prim-key";
	
	private HttpsURLConnection connection;
	
	public LiferayConnector() {
		LIFERAY_URL = Config.LIFERAY_URL.getValue();
	}
	
	/**
	 * Returns the blog entry with the given blog entry id.
	 * @param blogEntryId
	 * @return
	 * @throws IOException
	 */
	public JSONObject getBlogEntry(String blogEntryId) throws IOException {
		String jsonString = connect("GET", PATH_BLOGENTRY+"/"+blogEntryId);
		
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the wiki page with the given page id.
	 * Dependency: You have to use the CamundaACM-portlet (iisys/SCHub) in Liferay, to use this method.
	 * @param pageId: page id of the wiki page
	 * @return
	 * @throws IOException
	 */
	public JSONObject getWikiPage(String pageId) throws IOException {
		String jsonString = connect("GET", PATH_WIKIPAGE+"/"+pageId);
		
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the wiki page with the given resource primary key.
	 * Dependency: You have to use the CamundaACM-portlet (iisys/SCHub) in Liferay, to use this method.
	 * @param resourcePrimKey
	 * @return
	 * @throws IOException
	 */
	public JSONObject getWikiPageByResourcePrimKey(String resourcePrimKey) throws IOException {
		String jsonString = connect("GET", PATH_WIKIPAGE_RESPRIMKEY+"/"+resourcePrimKey);
		
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the forum entry (message board message) with the given "message id".
	 * @param messageId
	 * @return
	 * @throws IOException
	 */
	public JSONObject getMessageBoardMessage(String messageId) throws IOException {
		String jsonString = connect("GET", PATH_MESSAGEBOARD+"/"+messageId);
		
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Returns the forum entry (message board message) with the given "message id".
	 * Duplicate of getMessageBoardMessage(String).
	 * @param messageId
	 * @return
	 * @throws IOException
	 */
	public JSONObject getForumEntry(String messageId) throws IOException {
		return getMessageBoardMessage(messageId);
	}
	
	/**
	 * Returns the web content article (former journal article) with the given article primary key.
	 * @param articlePrimeKey
	 * @return
	 * @throws IOException
	 */
	public JSONObject getWebContentArticle(String articlePrimeKey) throws IOException {
		String jsonString = connect("GET", PATH_JOURNALARTICLE+"/"+articlePrimeKey);
		
		try {
			JSONObject json = new JSONObject(jsonString);		
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the web content template (former journal template) with the given templateId and groupId.
	 * You can get these ids from getWebContentArticle(String) method.
	 * @param templateId
	 * @param groupId
	 * @return
	 * @throws IOException
	 */
	public JSONObject getWebContentTemplate(String templateId, String groupId) throws IOException {
		String jsonString = connect("GET", PATH_JOURNALTEMPLATE+"/group-id/"+groupId+"/template-id/"+templateId);
		
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Opens a connection to Liferay, using Basic Authentication with debug username and password.
	 * @param requestMethod: GET, POST, DELETE,...
	 * @param contextPath
	 * @return
	 * @throws IOException
	 */
	private String connect(String requestMethod, String contextPath) throws IOException {
		URL url = new URL(LIFERAY_URL + PATH_API + contextPath);
		String userpass = debug_username+":"+debug_password;
		String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		this.connection = (HttpsURLConnection) url.openConnection();
		this.connection.setRequestMethod(requestMethod);
		this.connection.setDoOutput(true);
		this.connection.setRequestProperty("Authorization", basicAuth);
		
		// read response:			
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				(InputStream)connection.getInputStream(), "UTF-8"));
		
		StringBuffer buf = new StringBuffer();
		String line;
		while((line = in.readLine()) != null) {
			buf.append(line);
		}
		in.close();
		
		return buf.toString();
	}
	
	/**
	 * Closes the Liferay connection.
	 */
	public void close() {
		connection.disconnect();
		connection = null;
	}
}
