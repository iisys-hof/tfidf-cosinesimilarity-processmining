package de.iisys.schub.processMining;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.iisys.schub.processMining.activities.ActivityController;
import de.iisys.schub.processMining.activities.model.ProcessCycle;
import de.iisys.schub.processMining.activities.network.LiferayConnector;
import de.iisys.schub.processMining.activities.network.ShindigRESTConnector;
import de.iisys.schub.processMining.similarity.AlgoController;

/**
 * Main class to start the cosine similarity calculation
 * of a main document (.docx) with chapters and other documents.
 * 
 * This application was written
 * for the project "Social Collaboration Hub" (www.sc-hub.de)
 * at the Institute of Information Systems (www.iisys.de),
 * which is part of Hof University, Germany.
 * 
 * 
 * LICENSE:
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Christian Ochsenkühn
 */
public class MiningMain {
    
    /**
     * Main method
     * @param args
     * 		args[0]: Version
     * 			0 = Save all cosine similarities between mainDoc and compareDocs to an output file (default)
     * 			1 = Save only similarities between similar docs (cosSim > percentile) to an output file
     * 		args[1]: path to main document (default: mainDoc.docx)
     * 		args[2]: path to folder of documents to compare (default: docs)
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void main(String args[]) throws FileNotFoundException, IOException
    {      
        /**
         * For .jar use:
         */
        int version = 0;
        if(args.length>0 && !args[0].isEmpty())
        	version = Integer.parseInt(args[0]);
    	
    	String mainDocPath = "mainDoc.docx";
        if(args.length>1 && !args[1].isEmpty())
        	mainDocPath = args[1];
    	
        String docsPath = "docs";
        if(args.length>2 && !args[2].isEmpty())
        	docsPath = args[2];
        
        //test:
        
//      test_ProcessMining();
        
//      mainDocPath = "TextMiningTest_Highlights der IFA 2015.docx";
//		String nuxeoDocId = "0639a687-01e5-49dd-910c-7040111d80a2";
//		version = 1;
		
		test_LiferayConnection();		
//		CamundaConnector.testConnection();		
//		ElasticSearchConnector.test();
		
		/*
        AlgoController algo = new AlgoController();
        if(version==0)
        	algo.pipelineCosineSimilarity(mainDocPath, docsPath);
        else if(version==1)
        	algo.pipelineSimilarDocs(mainDocPath, docsPath);
//        else if(version==2)
//        	algo.pipelineNuxeoSimilarDocs(nuxeoDocId, docsPath);
        */
    }
    
    private static void test_LiferayConnection() {
    	LiferayConnector life = new LiferayConnector();
    	String blogEntryId = "51363";
    	System.out.println("Connecting to Liferay...");
    	JSONObject output;
		try {
//			output = life.getBlogEntry(blogEntryId);
//			output = life.getWikiPage("55605");
//			System.out.println(output.toString());
			
			life.getWebContentTemplate("25412", "20181");
			
			/*
			System.out.println("Title: "+output.getString("title"));
	    	System.out.println("\n\n ############## \n"+"Html-Content: "+output.getString("content"));
	    	System.out.println("\n\n ############## \n"+"Content: "+TextParser.parseHtml(output.getString("content")));
	    	*/
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static void test_ProcessMining() {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    	
    	List<ProcessCycle> cycles = new ArrayList<ProcessCycle>();
    	
    	// cycle 1 -----
    	List<JSONObject> activities = new ArrayList<JSONObject>();
    	
    	JSONObject a1_1 = new JSONObject();
    	a1_1.put("actor", getActor("baerbel","Bärbel Bitte"));
    	a1_1.put("generator", getGenerator("application:liferay-wikis", "Liferay Wikis"));
    	a1_1.put("id", "activityentry:315");
    	a1_1.put("title", "Bärbel Bitte hat die Wikiseite Huawei Watch kostet so viel wie Apple Watch erstellt.");
    	a1_1.put("verb", "post");
    	a1_1.put("published", "2015-11-13T10:37:41.660Z");
    	a1_1.put("object", new JSONObject()
    			.put("id", "liferay-wiki-page:55604")
    			.put("displayName", "")
    			.put("objectType", "liferay-wiki-page")
    			.put("url", "")
    			.put("content", "version 1.0"));
    	activities.add(a1_1);
    	
    	JSONObject a1_2 = new JSONObject();
    	a1_2.put("actor", getActor("anna","Anna Alster"));
    	a1_2.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    	a1_2.put("id", "activityentry:316");
    	a1_2.put("title", "Anna Alster hat den Blogeintrag target erstellt.");
    	a1_2.put("verb", "post");
    	a1_2.put("published", "2015-11-13T11:11:44.359Z");
    	a1_2.put("object", new JSONObject()
    			.put("id", "liferay-blog-entry:55701")
    			.put("objectType", "liferay-blog-entry"));
    	activities.add(a1_2);
    	
    	JSONObject a1_3 = new JSONObject();
    	a1_3.put("actor", getActor("anna","Anna Alster"));
    	a1_3.put("generator", getGenerator("application:liferay-messageboards", "Liferay Message Boards"));
    	a1_3.put("id", "activityentry:317");
    	a1_3.put("title", "Anna Alster hat den Forenthread target erstellt.");
    	a1_3.put("verb", "add");
    	a1_3.put("published", "2015-11-13T11:36:01.817Z");
    	a1_3.put("object", new JSONObject()
    			.put("id", "liferay-message-board-entry:55717")
    			.put("objectType", "liferay-message-board-entry"));
    	activities.add(a1_3);
    	
    	JSONObject a1_31 = new JSONObject();
    	a1_31.put("actor", getActor("anna","Anna Alster"));
    	a1_31.put("generator", getGenerator("nuxeo", "Nuxeo"));
    	a1_31.put("id", "activityentry:335");
    	a1_31.put("title", "Dokument erstellt");
    	a1_31.put("verb", "add");
    	a1_31.put("published", "2015-11-19T13:31:15.518Z");
    	a1_31.put("object", new JSONObject()
    			.put("id", "25ede30f-5c5e-457b-9d84-58ef14bf73e5")
    			.put("displayName", "")
    			.put("objectType", "Document")
    			.put("url", "")
    			.put("content", "type: File\nname: Vernetzt und mobil ins i"));
    	activities.add(a1_31);
    	
    	JSONObject a1_4 = new JSONObject();
    	a1_4.put("actor", getActor("zoltan","Zoltan Zorn"));
    	a1_4.put("generator", getGenerator("application:liferay-journal", "Liferay Journal"));
    	a1_4.put("id", "activityentry:319");
    	a1_4.put("title", "Zoltan Zorn hat den Webcontent-Artikel ... erstellt.");
    	a1_4.put("verb", "add");
    	a1_4.put("published", "2015-11-13T13:04:14.550Z");
    	a1_4.put("object", new JSONObject()
    			.put("id", "liferay-journal-entry:55735")
    			.put("objectType", "liferay-journal-entry"));
    	activities.add(a1_4);
    	
    	// activities which are NOT similar
    	for(int i=0; i<24; i++) {
    		JSONObject temp = new JSONObject();
    		temp.put("actor", getActor("anna","Anna Alster"));
    		temp.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    		temp.put("title", "Anna Alster hat den Blogeintrag target erstellt.");
    		temp.put("verb", "post");
    		temp.put("published", "2015-11-13T11:"+i+":44.359Z");
    		temp.put("object", new JSONObject()
        			.put("id", "liferay-blog-entry:57215")
        			.put("objectType", "liferay-blog-entry"));
        	activities.add(temp);
    	}
    	
    	// Doc: Projektvorschlag - Smarte Uhren vernetzen.docx
    	ProcessCycle cycle1 = new ProcessCycle("a8953f85-d81c-4cdc-b701-9b653d0008e1", "Project Proposal", activities);
    	cycle1.addUserId("baerbel");
    	cycle1.addUserId("anna");
    	try {
			cycle1.setStartDate(df.parse("2015-07-13 12:12"));
			cycle1.setEndDate(df.parse("2015-12-11 13:45"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	cycles.add(cycle1);
    	
    	
    	// cycle 2 -----
    	List<JSONObject> activities2 = new ArrayList<JSONObject>();
    	
    	JSONObject a2_11 = new JSONObject();
    	a2_11.put("actor", getActor("baerbel","Bärbel Bitte"));
    	a2_11.put("generator", getGenerator("application:liferay-wikis", "Liferay Wikis"));
    	a2_11.put("id", "activityentry:333");
    	a2_11.put("title", "Bärbel Bitte hat die Wikiseite Telekom erwartet Ablösung der klassischen SIM-Karte für 2016 erstellt.");
    	a2_11.put("verb", "post");
    	a2_11.put("published", "2015-11-18T14:12:00.421Z");
    	a2_11.put("object", new JSONObject()
    			.put("id", "liferay-wiki-page:56923")
    			.put("objectType", "liferay-wiki-page"));
    	activities2.add(a2_11);
    	
    	JSONObject a2_12 = new JSONObject();
    	a2_12.put("actor", getActor("erika","Erika Ernst"));
    	a2_12.put("generator", getGenerator("application:liferay-messageboards", "Liferay Message Boards"));
    	a2_12.put("id", "activityentry:337");
    	a2_12.put("title", "Erika Ernst hat den Forenthread target erstellt.");
    	a2_12.put("verb", "add");
    	a2_12.put("published", "2015-11-20T10:06:01.624Z");
    	a2_12.put("object", new JSONObject()
    			.put("id", "liferay-message-board-entry:57348")
    			.put("objectType", "liferay-message-board-entry"));
    	activities2.add(a2_12);
    	
    	JSONObject a2_21 = new JSONObject();
    	a2_21.put("actor", getActor("anna","Anna Alster"));
    	a2_21.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    	a2_21.put("id", "activityentry:328");
    	a2_21.put("title", "Anna Alster hat den Blogeintrag target erstellt.");
    	a2_21.put("verb", "post");
    	a2_21.put("published", "2015-11-17T15:01:33.031Z");
    	a2_21.put("object", new JSONObject()
    			.put("id", "liferay-blog-entry:56353")
    			.put("objectType", "liferay-blog-entry"));
    	activities2.add(a2_21);
    	
    	JSONObject a2_22 = new JSONObject();
    	a2_22.put("actor", getActor("anna","Anna Alster"));
    	a2_22.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    	a2_22.put("id", "activityentry:329");
    	a2_22.put("title", "Anna Alster hat den Blogeintrag target erstellt.");
    	a2_22.put("verb", "post");
    	a2_22.put("published", "2015-11-17T15:07:31.825Z");
    	a2_22.put("object", new JSONObject()
    			.put("id", "liferay-blog-entry:56362")
    			.put("objectType", "liferay-blog-entry"));
    	activities2.add(a2_22);
    	
    	JSONObject a2_31 = new JSONObject();
    	a2_31.put("actor", getActor("zoltan","Zoltan Zorn"));
    	a2_31.put("generator", getGenerator("application:liferay-messageboards", "Liferay Message Boards"));
    	a2_31.put("id", "activityentry:330");
    	a2_31.put("title", "Zoltan Zorn hat den Forenthread target erstellt.");
    	a2_31.put("verb", "add");
    	a2_31.put("published", "2015-11-17T15:29:52.109Z");
    	a2_31.put("object", new JSONObject()
    			.put("id", "liferay-message-board-entry:56372")
    			.put("objectType", "liferay-message-board-entry"));
    	activities2.add(a2_31);
    	
    	JSONObject a2_32 = new JSONObject();
    	a2_32.put("actor", getActor("erika","Erika Ernst"));
    	a2_32.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    	a2_32.put("id", "activityentry:332");
    	a2_32.put("title", "Erika Ernst hat den Blogeintrag target erstellt.");
    	a2_32.put("verb", "post");
    	a2_32.put("published", "2015-11-17T15:36:11.247Z");
    	a2_32.put("object", new JSONObject()
    			.put("id", "liferay-blog-entry:56395")
    			.put("objectType", "liferay-blog-entry"));
    	activities2.add(a2_32);
    	
    	JSONObject a2_41 = new JSONObject();
    	a2_41.put("actor", getActor("baerbel","Bärbel Bitte"));
    	a2_41.put("generator", getGenerator("application:liferay-wikis", "Liferay Wikis"));
    	a2_41.put("id", "activityentry:339");
    	a2_41.put("title", "Bärbel Bitte hat die Wikiseite Kein anderes Smartphone lässt sich so leicht reparieren wie das Fairphone 2 erstellt.");
    	a2_41.put("verb", "post");
    	a2_41.put("published", "2015-11-20T10:48:26.810Z");
    	a2_41.put("object", new JSONObject()
    			.put("id", "liferay-wiki-page:57360")
    			.put("objectType", "liferay-wiki-page"));
    	activities2.add(a2_41);
    	
    	JSONObject a2_m1 = new JSONObject();
    	a2_m1.put("actor", getActor("baerbel","Bärbel Bitte"));
    	a2_m1.put("generator", getGenerator("shindig-socialmessaging", "Social Messenger"));
    	a2_m1.put("id", "activityentry:345");
    	a2_m1.put("title", "Fragen zur eSim-Karte: Hallo Zoltan,\ndu kennst ...");
    	a2_m1.put("verb", "send");
    	a2_m1.put("published", "2015-11-26T13:18:14.700Z");
    	a2_m1.put("object", new JSONObject()
    			.put("id", "messages:22")
    			.put("objectType", "message"));
    	a2_m1.put("target", new JSONObject()
    			.put("id", "zoltan")
    			.put("objectType", "person"));
    	activities2.add(a2_m1);
    	
    	JSONObject a2_m2 = ShindigRESTConnector.getActivity("anna", "activityentry:346"); // social message
    	if(a2_m2!=null) activities2.add(a2_m2);
    	else System.out.println("Error loading a2_m2");
    		
    	JSONObject a2_m3 = ShindigRESTConnector.getActivity("erika", "activityentry:347"); // social message
    	if(a2_m3!=null) activities2.add(a2_m3);
    	else System.out.println("Error loading a2_m3");
    	
    	// activities which are NOT similar
    	for(int i=0; i<24; i++) {
    		JSONObject temp = new JSONObject();
    		temp.put("actor", getActor("anna","Anna Alster"));
    		temp.put("generator", getGenerator("application:liferay-blogs", "Liferay Blogs"));
    		temp.put("title", "Anna Alster hat den Blogeintrag target erstellt.");
    		temp.put("verb", "post");
    		temp.put("published", "2015-11-13T11:"+i+":44.359Z");
    		temp.put("object", new JSONObject()
        			.put("id", "liferay-blog-entry:57215")
        			.put("objectType", "liferay-blog-entry"));
        	activities2.add(temp);
    	}
    	
    	ProcessCycle cycle2 = new ProcessCycle("08257161-2e56-4299-80da-d41b77968beb", "Project Proposal", activities2);
    	cycle2.addUserId("baerbel");
    	cycle2.addUserId("anna");
    	try {
			cycle2.setStartDate(df.parse("2015-10-25 12:32"));
			cycle2.setEndDate(df.parse("2015-12-11 13:45"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	cycles.add(cycle2);
    	
    	ActivityController controller = new ActivityController(cycles);
    	controller.startPipeline();
    }
    
    private static JSONObject getActor(String id, String name) {
    	JSONObject actor = new JSONObject();
    	actor.put("id", id);
    	actor.put("displayName", name);
    	actor.put("objectType", "person");
    	return actor;
    }
    
    private static JSONObject getGenerator(String id, String name) {
    	JSONObject gen = new JSONObject();
    	gen.put("id", id);
    	gen.put("displayName", name);
    	gen.put("objectType", "application");
    	return gen;
    }
}