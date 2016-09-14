package de.iisys.schub.processMining.activities.network;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import de.iisys.schub.processMining.activities.ActivityController;
import de.iisys.schub.processMining.activities.model.artefact.IArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayBlogArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayForumArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayWebContentArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayWikiArtefact;
import de.iisys.schub.processMining.activities.model.artefact.MailArtefact;
import de.iisys.schub.processMining.activities.model.artefact.NuxeoDocArtefact;
import de.iisys.schub.processMining.activities.model.artefact.SocialMessageArtefact;
import de.iisys.schub.processMining.config.Config;
import de.iisys.schub.processMining.similarity.model.MinedDocument;
import de.iisys.schub.processMining.similarity.model.MinedMainDocument;
import de.iisys.schub.processMining.similarity.parsing.DocxParser;
import de.iisys.schub.processMining.similarity.parsing.TextParser;

/**
 * Controller for all artifact creations and manipulations.
 * 
 * @author Christian Ochsenk�hn
 *
 */
public class ArtefactController {
	
	private final String NO_TITLE = "-";
	
	private LiferayConnector liferayConnector;
	private NuxeoConnector nuxeoConnector;
	
	private NuxeoDocArtefact mainArtefact;
	private List<IArtefact> artefacts;
	
	private final String WEBCONTENT_CONTENT = "content";
	private final String WEBCONTENT_GROUP_ID = "groupId";
	private final String WEBCONTENT_TEMPLATE_ID = "templateId";
	private final String WEBCONTENT_TITLE = "titleCurrentValue";
	
	private final String WEBCONTENTTEMPLATE_NAME = "name";
	private final String WEBCONTENTTEMPLATE_MEETING_MINUTES = "MeetingMinutes";
	
	
	public ArtefactController() {
		artefacts = new ArrayList<IArtefact>();
	}
	
	/**
	 * Adds the main nuxeo document artifact.
	 * @param nuxeoDocArtifact
	 */
	public void addMainArtefact(NuxeoDocArtefact nuxeoDocArtifact) {
		this.mainArtefact = nuxeoDocArtifact;
	}
	
	/**
	 * Adds a "comparte artifact".
	 * @param artefact
	 */
	public void addArtefact(IArtefact artefact) {
		this.artefacts.add(artefact);
	}
	
	/**
	 * Adds a list of "compare artifacts".
	 * Clears the list before adding.
	 * @param artefacts
	 */
	public void addArtefacts(List<IArtefact> artifacts) {
		this.artefacts.clear();
		this.artefacts.addAll(artifacts);
	}
	
	/**
	 * Connects to Nuxeo to receive and parse the main document.
	 * Uses a docx parser to find chapters.
	 * @return
	 * 		Returns the parsed main document [with its chapters].
	 * 		Returns null if an exception occurs.
	 */
	public MinedMainDocument parseMainArtifact() {
		try {
			InputStream is = this.getNuxeoInstance().getDocumentInputStream(mainArtefact.getId());
			
			String title = this.getNuxeoInstance().getLastDocTitle();
			if(title==null) title = "Main Document";
			
			DocxParser docx = new DocxParser(is);
			docx.parseDocxAndChapters();
			MinedMainDocument mainDoc = new MinedMainDocument(title, docx.getFullText());
			mainDoc.addChapters(docx.getChapterHeadlines(), docx.getChapterTexts());
			is.close();
			return mainDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Depending on the artifact, connects to nuxeo, liferay or shindig
	 * to parse the artifact and get its title and content. Adds null to the
	 * return-list if an artifact cannot be parsed.
	 * @return
	 */
	public List<MinedDocument> parseCompareArtifacts() {
//		System.out.println("parseCompareArtifacts()");
		List<MinedDocument> compareDocs = new ArrayList<MinedDocument>();
		
		InputStream is;
		DocxParser docx;
		String title = null;
		String text;
		
		for(IArtefact artefact : this.artefacts) {
			text = null;
			
			if(artefact instanceof NuxeoDocArtefact) {	// Nuxeo Document:
				try {
					is = getNuxeoInstance().getDocumentInputStream(artefact.getId());
					
					title = this.getNuxeoInstance().getLastDocTitle();
					if(title==null) title = NO_TITLE;
					
					if(this.getNuxeoInstance().getLastDocFilePath().endsWith(".docx")) {
						docx = new DocxParser(is);
						docx.parseDocxSimple();
						text = docx.getFullText();
					} else if(this.getNuxeoInstance().getLastDocFilePath().endsWith(".doc")) {
						text = DocxParser.parseDocSimple(is);
					}
				} catch (Exception e) {
					System.out.println("Error: Can not read nuxeo doc with id: "+artefact.getId());
					continue;
				}
			} else if(artefact instanceof LiferayBlogArtefact) { // Liferay Blog Entry:
				try {
					JSONObject blogEntry = this.getLiferayInstance().getBlogEntry(artefact.getId());
					title = blogEntry.getString("title");
					text = blogEntry.getString("content");
					text = TextParser.parseHtml(text);
				} catch (Exception e) {
					System.out.println("Error: Can not read Liferay blog entry with id: "+artefact.getId());
					continue;
				}
			} else if(artefact instanceof LiferayForumArtefact) { // Liferay Forum Message
				try {
					JSONObject forumEntry = this.getLiferayInstance().getMessageBoardMessage(artefact.getId());
					title = forumEntry.getString("subject");
					text = forumEntry.getString("body");
				} catch(Exception e) {
					System.out.println("Error: Can not read Liferay forum message with id: "+artefact.getId());
					continue;
				}
			} else if(artefact instanceof LiferayWebContentArtefact) { // Liferay Web Content Article (Journal)
				try {
					JSONObject webContentArticle = this.getLiferayInstance().getWebContentArticle(artefact.getId());
					title = webContentArticle.getString(WEBCONTENT_TITLE);
					text = webContentArticle.getString(WEBCONTENT_CONTENT);
					// check if is "meeting minutes" content:
					JSONObject webContentTemplate = this.getLiferayInstance().getWebContentTemplate(
							webContentArticle.getString(WEBCONTENT_TEMPLATE_ID), webContentArticle.getString(WEBCONTENT_GROUP_ID));
					String templateName = webContentTemplate.getString(WEBCONTENTTEMPLATE_NAME);
					if(templateName.contains(WEBCONTENTTEMPLATE_MEETING_MINUTES)) {
						((LiferayWebContentArtefact)artefact).setIsMeetingMinutes(true);
					}
				} catch(Exception e) {
					System.out.println("Error: Can not read Liferay web content with id: "+artefact.getId());
					continue;
				}
			} else if(artefact instanceof LiferayWikiArtefact) { // Liferay Wiki Page
				try {
					JSONObject wikiPage = this.getLiferayInstance().getWikiPage(artefact.getId());
					title = wikiPage.getString("title");
					text = wikiPage.getString("content");
				} catch(Exception e) {
					System.out.println("Error: Can not read Liferay wiki page with id: "+artefact.getId());
					continue;
				}
			} else if(artefact instanceof SocialMessageArtefact) { // Shindig Message
				ShindigConnector shindigCon;
				if(Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT))
				    // disabled
//					shindigCon = new ShindigDirectConnector();
				    shindigCon = null;
				else
					shindigCon = new ShindigRESTConnector();
				JSONObject message = shindigCon.getOutboxMessage(((SocialMessageArtefact) artefact).getUserId(), artefact.getId());
				if(message!=null) {
					title = message.getString("title");
					text = message.getString("body");
				}
//				System.out.println("MinedDocument is a SocialMessageArtefact");
			} else if(artefact instanceof MailArtefact) { // Email via ElastiSearch
				title = ((MailArtefact)artefact).getSubject();
				text =  ((MailArtefact)artefact).getContent();
			}
			
			if(title==null)
				title = NO_TITLE;
			
			if(text!=null)
				compareDocs.add(new MinedDocument(title, text));
			else
				compareDocs.add(null);
		}
		
		return compareDocs;
	}
	
	
	// Connection instances:
	
	/**
	 * unused
	 */
	public void closeAllConnections() {
		if(this.nuxeoConnector!=null) {
			this.nuxeoConnector.close();
			this.nuxeoConnector = null;
		}
	}
	
	/**
	 * Only creates one NuxeoConnector instance and returns it.
	 * @return
	 */
	public NuxeoConnector getNuxeoInstance() {
		if(this.nuxeoConnector==null) {
			nuxeoConnector = new NuxeoConnector();
		}
		return this.nuxeoConnector;
	}
	
	/**
	 * Only creates one LiferayConnector instance and returns it.
	 * @return
	 */
	public LiferayConnector getLiferayInstance() {
		if(this.liferayConnector==null) {
			this.liferayConnector = new LiferayConnector();
		}
		return this.liferayConnector;
	}
	
	// static:
	
	/**
	 * Creates and returns the (empty) main artifact.
	 * @param mainDocId
	 * @return
	 */
	public static NuxeoDocArtefact createMainArtefact(String mainDocId) {
		return new NuxeoDocArtefact(mainDocId);
	}
	
	/**
	 * Creates and returns an artifact, using the given json activity.
	 * @param activity
	 * @return
	 */
	public static IArtefact createActivityArtefact(JSONObject activity) {
		
//		System.out.println("DEBUG| createActivityArtefact activity: "+activity);
		JSONObject object = activity.getJSONObject(ShindigConnector.ACTIVITY_OBJECT);
		
		switch(object.getString(ShindigConnector.ACTIVITY_OBJECT_OBJECTTYPE)) {
			case(ActivityController.TYPE_LIFERAY_BLOG_ENTRY):
	//			System.out.println("It is a blog entry.");
				return new LiferayBlogArtefact(object.getString("id").split(":")[1]);	// blog entry id
			case(ActivityController.TYPE_LIFERAY_FORUM_ENTRY):
			case(ActivityController.TYPE_LIFERAY_FORUM_THREAD):
	//			System.out.println("It is a forum entry.");
				return new LiferayForumArtefact(object.getString("id").split(":")[1]);	// message board message id
			case(ActivityController.TYPE_LIFERAY_WEB_CONTENT):
	//			System.out.println("It is a web content.");
				return new LiferayWebContentArtefact(object.getString("id").split(":")[1]);	// journal article id
			case(ActivityController.TYPE_LIFERAY_WIKI_PAGE):
	//			System.out.println("It is a wiki page.");
				return new LiferayWikiArtefact(object.getString("id").split(":")[1]); // wiki page id
			case(ActivityController.TYPE_NUXEO_DOCUMENT):
	//			System.out.println("It is a nuxeo document.");
				return new NuxeoDocArtefact(object.getString("id"));	// document version series id
			case(ActivityController.TYPE_OX_TASK):
				break;
			case(ActivityController.TYPE_OX_CALENDAR_ENTRY):
				break;
			case(ActivityController.TYPE_PUBLIC_MESSAGE):
				SocialMessageArtefact a = new SocialMessageArtefact(object.getString("id"));
				a.setUserId(activity.getJSONObject(ShindigConnector.ACTIVITY_ACTOR).getString(ShindigConnector.ID));
				return a;
		}
		
//		System.out.println("DEBUG| createActivityArtefact: return null!!!");
		return null;
	}
	
	/**
	 * Creates and returns a mail artifact, using the given json object.
	 * The json object has to contain a ElasticSearchConnector.SOURCE object.
	 * @param searchMailObject
	 * @return
	 */
	public static MailArtefact createMailArtefact(JSONObject searchMailObject) {
		JSONObject mailSource = searchMailObject.getJSONObject(ElasticSearchConnector.SOURCE);
		
		MailArtefact art = new MailArtefact( String.valueOf(mailSource.getInt(ElasticSearchConnector.ID)) );
		art.setSubject(mailSource.getString(ElasticSearchConnector.SUBJECT));
		art.setContent(mailSource.getString(ElasticSearchConnector.TEXTCONTENT));
		
		return art;
	}
	
}
