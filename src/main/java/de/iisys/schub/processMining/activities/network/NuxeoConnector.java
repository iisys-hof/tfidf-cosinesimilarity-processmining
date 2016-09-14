package de.iisys.schub.processMining.activities.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import de.iisys.schub.processMining.config.Config;

/**
 * Connector for all Nuxeo connections.
 * Use the config.properties to configure the Nuxeo URL.
 * TODO: Use a debug_username and debug_password with admin rights.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class NuxeoConnector {
	
	private String debug_username = "baerbel";
	private String debug_password = "bitte";
	
	private HttpAutomationClient client;
	private Session session;
	private String lastDocTitle;
	private String lastDocFilePath;
	
	private final String PROP_MAP_FILECONTENT = "file:content";
	private final String IN_PROP_MAP_DATA = "data";
	
	/**
	 * Opens a connection to Nuxeo, using debug username and password.
	 */
	public NuxeoConnector() {
		String NUXEO_URL = Config.NUXEO_URL.getValue();
		System.out.println("\t Connecting with nuxeo... "+NUXEO_URL);
		this.client = new HttpAutomationClient(NUXEO_URL+"/site/automation");
		if(NUXEO_URL.startsWith("https"))
			NuxeoSSLClientWrapper.wrapClient(client.http());
		try {
			this.session = client.getSession(debug_username, debug_password);
			System.out.println("\t Connected!");
		} catch (IOException e) {
			System.out.println("\t Error: Cannot connect to nuxeo!");
		}	
	}
	
	/**
	 * Returns a Nuxeo document with the given Nuxeo doc id.
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Document getDocument(String id) throws Exception {	
		Document doc = null;
		
        Documents docs = (Documents) session.newRequest("Document.Query")
        		.setHeader(Constants.HEADER_NX_SCHEMAS, "*")
				.set("query", "SELECT * FROM Document " + "WHERE ecm:uuid = '" +id+ "'")
//				.set("query", "SELECT * FROM Document " + "WHERE file:filename = 'Dienstreiseantrag.docx'")
        		.execute();

        if(docs.size() == 1)
            doc = docs.iterator().next();
        else if(docs.size() == 0)
            throw new Exception("Document not found.");
        else
            throw new Exception("Too many documents found.");

        this.lastDocTitle = doc.getTitle();
        return doc;
    }
	
	/**
	 * For testing purposes only!
	 * @return
	 * @throws IOException
	 */
	public String getAllDocumentIds() throws IOException {
		Documents docs = (Documents) session.newRequest("Document.Query")
				.setHeader(Constants.HEADER_NX_SCHEMAS, "*")
//				.set("query", "SELECT ecm:uuid FROM Document " + "WHERE ecm:primaryType = 'File'")
				.set("query", "SELECT ecm:uuid, title FROM Document " + "WHERE dc:contributors IN ('anna', 'baerbel')")
				.execute();
		
		StringBuffer buf = new StringBuffer("Ids: ");
		for(Document doc : docs) {
			buf.append("\n"+doc.getId()+": "+doc.getTitle());
		}
		return buf.toString();
	}
	
	/**
	 * Returns the nuxeo document with the given id as File.
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public File getDocumentFile(String id) throws Exception {
		Document doc = this.getDocument(id);
		
		Set<String> keys = doc.getProperties().getKeys();
		for(String s : keys) {
			System.out.println(s);
		}
		
//		PropertyList propList = doc.getProperties().getList("files:files");
		PropertyMap propMap = doc.getProperties().getMap(PROP_MAP_FILECONTENT);
		
		if(propMap==null)
			throw new Exception("No such property list.");
		else {
			String path = propMap.getString(IN_PROP_MAP_DATA);
			FileBlob blob = (FileBlob) this.session.getFile(path);
			return blob.getFile();
		}
		
		/*
		if(propList.size()>0) {
			PropertyMap propMap = propList.getMap(0).getMap("file");
		
			// get the data URL
			String path = propMap.getString("data");
			FileBlob blob = (FileBlob) this.session.getFile(path);
			return blob.getFile();
		} else {
			throw new Exception("No files in map!");
		}
		*/
	}
	
	/**
	 * Returns the nuxeo document with the given id as InputStream.
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public InputStream getDocumentInputStream(String id) throws Exception {
		Document doc = this.getDocument(id);
		
		PropertyMap propMap = doc.getProperties().getMap(PROP_MAP_FILECONTENT);
		
		if(propMap==null)
			throw new Exception("No such property: "+PROP_MAP_FILECONTENT);
		else {
			String filePath = propMap.getString(IN_PROP_MAP_DATA);
			this.lastDocFilePath = filePath;
			FileBlob blob = (FileBlob) this.session.getFile(filePath);
			return blob.getStream();
		}
	}
	
	/**
	 * Closes the Nuxeo session and client.
	 */
	public void close() {
		session.close();
		client.shutdown();
	}
	
	/**
	 * Returns the title of the lastly received document.
	 * @return
	 */
	public String getLastDocTitle() {
		if(this.lastDocTitle!=null)
			return this.lastDocTitle;
		else
			return null;
	}
	
	/**
	 * Returns the path of the lastly received document.
	 * @return
	 */
	public String getLastDocFilePath() {
		if(this.lastDocFilePath!=null)
			return this.lastDocFilePath;
		else
			return null;
	}
}
