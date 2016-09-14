package de.iisys.schub.processMining.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iisys.schub.processMining.activities.model.MailTaskCandidate;
import de.iisys.schub.processMining.activities.model.MessageTaskCandidate;
import de.iisys.schub.processMining.activities.model.ProcessCycle;
import de.iisys.schub.processMining.activities.model.TaskCandidate;
import de.iisys.schub.processMining.activities.model.artefact.IArtefact;
import de.iisys.schub.processMining.activities.model.artefact.MailArtefact;
import de.iisys.schub.processMining.activities.model.artefact.SocialMessageArtefact;
import de.iisys.schub.processMining.activities.network.ArtefactController;
import de.iisys.schub.processMining.activities.network.CamundaConnector;
import de.iisys.schub.processMining.activities.network.ElasticSearchConnector;
import de.iisys.schub.processMining.activities.network.ShindigConnector;
import de.iisys.schub.processMining.activities.network.ShindigDirectConnector;
import de.iisys.schub.processMining.activities.network.ShindigRESTConnector;
import de.iisys.schub.processMining.activities.tasks.TaskController;
import de.iisys.schub.processMining.cmmn.CMMNController;
import de.iisys.schub.processMining.config.Config;
import de.iisys.schub.processMining.similarity.AlgoController;
import de.iisys.schub.processMining.similarity.model.MinedDocument;
import de.iisys.schub.processMining.similarity.model.MinedMainDocument;

/**
 * This class is the main entry for the process mining, coming from shindig.
 * To start the mining, use the startPipeline() method.
 * 
 * @author Christian Ochsenk�hn
 *
 */
public class ActivityController {
	
	/**
	 * if potential task candidate exist in more than THRESHOLD_PERCENTAGE cycles
	 * then it will be kept as task for the cmmn.
	 */
	private final float THRESHOLD_PERCENTAGE = (float) 0.5;
	
	// generators:
	public static final String GEN_LIFERAY_WIKI = "liferay-wikis";
	public static final String GEN_LIFERAY_BLOG = "liferay-blogs";
	public static final String GEN_LIFERAY_FORUM = "liferay-messageboards";
	public static final String GEN_LIFERAY_WEBCONTENT = "liferay-journal";
	
	// activity objects:
	public static final String TYPE_LIFERAY_BLOG_ENTRY = "liferay-blog-entry";
	public static final String TYPE_LIFERAY_FORUM_ENTRY = "liferay-message-board-entry";
	public static final String TYPE_LIFERAY_FORUM_THREAD = "liferay-message-board-thread";
	public static final String TYPE_LIFERAY_WEB_CONTENT = "liferay-journal-entry";
	public static final String TYPE_LIFERAY_WIKI_PAGE = "liferay-wiki-page";
	public static final String TYPE_NUXEO_DOCUMENT = "Document";
	public static final String TYPE_OX_TASK = "open-xchange-task";
	public static final String TYPE_OX_CALENDAR_ENTRY = "open-xchange-appointment";
	public static final String TYPE_PUBLIC_MESSAGE = "message";
	
	/**
	 * Contains all supported activity object's objectTypes
	 */
	public static final Set<String> SUPPORTED_ACTIVITY_OBJECTS = new HashSet<String>();
	static {
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_LIFERAY_BLOG_ENTRY);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_LIFERAY_FORUM_ENTRY);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_LIFERAY_FORUM_THREAD);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_LIFERAY_WEB_CONTENT);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_LIFERAY_WIKI_PAGE);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_NUXEO_DOCUMENT);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_OX_TASK);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_OX_CALENDAR_ENTRY);
		ActivityController.SUPPORTED_ACTIVITY_OBJECTS.add(ActivityController.TYPE_PUBLIC_MESSAGE);
	}
	
	
	private List<ProcessCycle> cycles;
	
	/**
	 * Each ProcessCycle has to contain the following data: 
	 * docId, docType, a list of json activities, a list of userIds and a start- and enddate.
	 * @param processCycles
	 */
	public ActivityController(List<ProcessCycle> processCycles) {
		this.cycles = processCycles;
	}
	
	/**
	 * Starts a pipeline of grabbing artefacts and mails, comparing the artefacts with the 
	 * main document and creating the task candidates for each process cycle.
	 * After that, repeating task candidates (THRESHOLD_PERCENTAGE) are found.
	 * At last tasks and a cmmn file are created and optionally sent to Camunda.
	 */
	public void startPipeline() {
		int i=1;
		for(ProcessCycle pc : cycles) {
			// select only relevant activities (verbs: add, post, send)
			
			// grab artefacts (incl. mainDoc artefact)
			grabArtefacts(pc);
			
			// grab emails via elasticsearch
			grabMails(pc);
			
			// compare artefacts with mainArtefact's chapters
			MinedMainDocument minedMainDoc = compareArtefactsWithMainDoc(pc, i);
			
			// keep only similar artefacts and save them as taskCandidates (in cycle)
			createTaskCandidates(pc, minedMainDoc);
			
			i++;
		}
		
		// count repeating TaskCandidates and
		// put TaskCandidates with count > cycles.size()/2 in repeatingTaskCandidates
		Set<TaskCandidate> repeatingTaskCandidates = sortRepeatingTaskCandidates(cycles);
		
		// create "real" tasks:
		createTasks(repeatingTaskCandidates);
	}

	/**
	 * Prepares the given cycle with artefacts for later parsing (incl. main doc artefact).
	 * @param cycle
	 */
	private void grabArtefacts(ProcessCycle cycle) {
		// main artefact (Nuxeo document):
		cycle.setMainArtefact(ArtefactController.createMainArtefact(cycle.getMainDocumentId()));
		
		// other artefacts (blog, doc, wiki,...):
		IArtefact artefact;
		for(int i=0; i<cycle.getActivitiesSize(); i++) {
			artefact = ArtefactController.createActivityArtefact(cycle.getActivity(i));
			/*
			if(artefact!=null)
				System.out.println("DEBUG| grabArtefacts: artefact id= "+artefact.getId());
			else
				System.out.println("DEBUG| grabArtefacts: artefact is null!");
			*/
			cycle.addActivityArtefact(artefact, i);
		}
	}
	
	/**
	 * Receives the users' mail addresses from shindig. Receives these users' emails from
	 * ElasticSearch and prepares the cycle with MailArtefacts.
	 * @param cycle
	 */
	private void grabMails(ProcessCycle cycle) {
		JSONArray hits;
		MailArtefact artefact;
		
		ShindigConnector shindigCon;
		if(Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT))
		    // disabled
//			shindigCon = new ShindigDirectConnector();
		    shindigCon = null;
		else
			shindigCon = new ShindigRESTConnector();
		
		Map<String,String> userIdsMails = shindigCon.getUserEMailAddress(cycle.getUserIds());
		
		hits = ElasticSearchConnector.getEmails(userIdsMails.keySet(), cycle.getStartDate(), cycle.getEndDate());
		
		if(hits==null)
			return;
		else
			System.out.println("PROCESSMINING: ElasticSearch Email Hits: "+hits.length());	
					
		for(int i=0; i<hits.length(); i++) {			
			String senderMail = ElasticSearchConnector.getSenderMailFromHit(hits.getJSONObject(i));			
			String senderId = userIdsMails.get(senderMail);
			
			int inCyclePos = cycle.addActivity(hits.getJSONObject(i));
			artefact = ArtefactController.createMailArtefact(hits.getJSONObject(i));
			
			artefact.setSenderId(senderId);
			String receiverMail = ElasticSearchConnector.getReceiverMailFromHit(hits.getJSONObject(i), 0);
			if(userIdsMails.containsKey(receiverMail))
				artefact.setReceiverId(userIdsMails.get(receiverMail));
			
			cycle.addActivityArtefact(artefact, inCyclePos);
		}		
	}
	
	/**
	 * Compares the artifacts with the main document and its chapters, using AlgoController.
	 * This is where the real text mining happens.
	 * Prepares the MinedMainDocument with the mining results.
	 * 
	 * @param cycle
	 * @param cycleNr: position of the cycle in the cycles' list
	 * @return
	 */
	private MinedMainDocument compareArtefactsWithMainDoc(ProcessCycle cycle, int cycleNr) {
		System.out.println("PROCESSMINING: compare artefacts with main doc:");
		ArtefactController artefactCon = new ArtefactController();
		artefactCon.addMainArtefact(cycle.getMainArtefact());
		artefactCon.addArtefacts(cycle.getAllArtefacts());
		
		System.out.println("DEBUG| compareArtefactsWithMainDoc: allArtefacts size: "+cycle.getAllArtefacts().size());
//		System.out.println("DEBUG| compareArtefactsWithMainDoc 01: "+cycle.getAllArtefacts().get(0));
		
		AlgoController algo = new AlgoController();
		MinedMainDocument minedMainDoc = algo.pipelineNuxeoSimilarArtefacts(artefactCon, cycleNr);
		return minedMainDoc;
	}
	
	/**
	 * Creates potential task candidates, using the mining results, 
	 * and adds them to the ProcessCycle.
	 * @param pc
	 * @param mainDoc
	 */
	private void createTaskCandidates(ProcessCycle pc, MinedMainDocument mainDoc) {
		List<TaskCandidate> tcToAdd;
		TaskCandidate temp; TaskCandidate temp2;
		JSONObject json; JSONObject json2;
		IArtefact artefact;
		List<TaskCandidate> tempCands = new ArrayList<TaskCandidate>();
		List<JSONObject> tempActivities = new ArrayList<JSONObject>();
		
		// TODO: test:
		System.out.println("\n\n"+"PROCESSMINING: createTaskCandidates()"+"\n"+
				"\tactivities: "+pc.getActivitiesSize()+"\n"+
				"\tartefacts: "+pc.getAllArtefacts().size()+"\n"+
				"\tmain doc possible similars: "+mainDoc.getSimilarDocs().size());
		
		for(int i=0; i<pc.getActivitiesSize(); i++) {
			temp = null; temp2 = null; json = null; json2 = null;
			json = pc.getActivity(i);
			json2 = new JSONObject(pc.getActivity(i).toString());
			artefact = pc.getArtefact(i);
			
//			if(artefact instanceof MailArtefact) System.out.println("####### mail artefact found.");
			
//			for(int j=mainDoc.getChapters().size()-1; j>=0; j--) {
			for(MinedDocument chapter : mainDoc.getChapters()) {
				tcToAdd = new ArrayList<TaskCandidate>();
				if(chapter.getSimilarDoc(i) != null) { // then this artefact is similar to this chapter					
					
					if(artefact instanceof SocialMessageArtefact || artefact instanceof MailArtefact) {
						String[] userRel;
						if(artefact instanceof MailArtefact) {
							temp = new MailTaskCandidate(json, pc.getMainDocumentId());
							temp.setArtefact(artefact);
							userRel = UserRelationController.getUserRelation((MailTaskCandidate)temp);
//							System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> We have a mail artefact!");
						} else { // SocialMessageArtefact
							temp = new MessageTaskCandidate(json, pc.getMainDocumentId());
							userRel = UserRelationController.getUserRelation(temp);
						}
						((MessageTaskCandidate)temp).setUserRelation( Integer.valueOf(userRel[0]) );
						((MessageTaskCandidate)temp).setUserRelationAddition( userRel[1] );
						tcToAdd.add(temp);
						
						if(userRel.length>2) { // then we create a 2nd taskCandidate
							if(artefact instanceof MailArtefact)
								temp2 = new MailTaskCandidate(json2, pc.getMainDocumentId());
							else
								temp2 = new MessageTaskCandidate(json2, pc.getMainDocumentId());
							
							((MessageTaskCandidate)temp2).setUserRelation( UserRelationController.REL_MAYBE_SPECIFIC_JOBPOSITION_RECEIVER );
							((MessageTaskCandidate)temp2).setUserRelationAddition( userRel[2] );
							tcToAdd.add(temp2);
						}
					} else {
						temp = new TaskCandidate(json, pc.getMainDocumentId());
						tcToAdd.add( temp );
					}
					
					for(int t=0; t<tcToAdd.size(); t++) {
						TaskCandidate tcOld = tcToAdd.get(t);
						tcToAdd.get(t).setSimilarChapterTitle(chapter.getName());
						tcToAdd.get(t).setSimilarChapterTerms(chapter.getSimilarDocTerms(i));
						tcToAdd.get(t).setCosineSimilarity(chapter.getSimilarDocsCosine(i));
						tcToAdd.get(t).setArtefact(pc.getArtefact(i));
						
						tcToAdd.set(t, tcOld);
						
						// TODO: reference/value problem while setting!!!
					
					
						int indexOf = tempActivities.indexOf(tcToAdd.get(t).getActivity());					
						if(indexOf != -1) { // if activity already has similar chapter
							// then check whose cosineSimilarity is bigger
							if(tcToAdd.get(t).getCosineSimilarity() > tempCands.get(indexOf).getCosineSimilarity()) {
								// and keep/take the bigger one's taskCandidate
								tempCands.set(indexOf, tcToAdd.get(t));
								tempActivities.set(indexOf, tcToAdd.get(t).getActivity());
							}					
						} else { // else just add the activity (and taskCandidate)
							tempCands.add(tcToAdd.get(t));
							tempActivities.add(tcToAdd.get(t).getActivity());
						}
					}
				}
			}
			
			if(temp==null) { // check for similarity with the main document
				if(mainDoc.getSimilarDoc(i) != null) { // then this artefact is similar to the main document
					temp = new TaskCandidate(pc.getActivity(i), pc.getMainDocumentId());
					temp.setArtefact(pc.getArtefact(i));
					
					tempCands.add(temp);
					tempActivities.add(json);
				}
			}
		}
		
		pc.addAllTaskCandidates(tempCands);
		
		// TODO: Test:
		List<TaskCandidate> cands = pc.getTaskCandidates();
		System.out.println("PROCESSMINING: TaskCandidates: "+cands.size());
	}
	
	/**
	 * Only keeps task candidates which occur in more than THRESHOLD_PERCENTAGE process cycles.
	 * @param cycles
	 * @return
	 * 		Returns set of repeating task candidates.
	 */
	private Set<TaskCandidate> sortRepeatingTaskCandidates(List<ProcessCycle> cycles) {
		Map<TaskCandidate, Integer> tcCombinations = new HashMap<TaskCandidate,Integer>(); // type+chapterTitle, count
		
		HashMap<TaskCandidate, Integer> tempCycleTCs;
		for(ProcessCycle pc : cycles) {
			System.out.println("--- Cycle start:");
			tempCycleTCs = new HashMap<TaskCandidate, Integer>();
			for(TaskCandidate tc : pc.getTaskCandidates()) {
/*
				if(tc.getArtefact() instanceof SocialMessageArtefact) { // if taskCandidate is a message
					System.out.println("taskCandidate is a message");
//					tc.getArtefact().setUserRelation(UserRelationController.getUserRelation(tc));
					
					this.messagesTaskCandidates.add(tc);
					
				} else  */
				if(tempCycleTCs.containsKey(tc)) { // if taskcandidate type already occurred in this cycle
					System.out.println("taskcandidate type already occurred in this cycle: "+tc.getType());
					continue;
					
				} else {
					if(tcCombinations.containsKey(tc)) {
						Integer count = tcCombinations.get(tc);
						count++;
//						tcCombinations.replace(tc, count); // only in Java 1.8
						tcCombinations.remove(tc);
						tcCombinations.put(tc, count);
						System.out.println("count: "+count+", tc: "+tcCombinations.get(tc));
					} else {
						tcCombinations.put(tc, 1);
//						System.out.println("Added tc ("+tc.hashCode()+") to tcCombinations.");
					}
					tempCycleTCs.put(tc, null);
				}
			}
		}
		
		// taskCandidates with "real" artefacts (blog entry, wiki page,...):
		Iterator<Map.Entry<TaskCandidate, Integer>> iter = tcCombinations.entrySet().iterator();
		while(iter.hasNext()) {
			// right way to remove entries from map
			// avoids ConcurrentModificationException
			Map.Entry<TaskCandidate, Integer> entry = iter.next();	
				if(entry.getValue() < cycles.size()*THRESHOLD_PERCENTAGE)
					iter.remove();
		}
		
		return tcCombinations.keySet();
	}
	
	/**
	 * Uses task candidates to create "real" tasks and cmmn with a diagram.
	 * Receives this CMMN xml file from CMMNController and
	 * optionally connects to Camunda to deploy the CMMN.
	 * @param fullTcSet
	 */
	private void createTasks(Set<TaskCandidate> fullTcSet) {
		String name = this.cycles.get(0).getDocType();
		CMMNController cmmn = new CMMNController(name);
		
		TemporalRelController.findTemporalSuccessors(fullTcSet);
		List<Set<TaskCandidate>> tcSetList = TemporalRelController.sortByChapters(fullTcSet);
		
		for(Set<TaskCandidate> tcSet : tcSetList) {
			List<String> entryCriterionIds = new ArrayList<>();
			
			String chapterName = null;
			boolean flagForChapterName = false;
			for(TaskCandidate tc : tcSet) {
				String tasks[] = TaskController.createTasks(tc);
				if(flagForChapterName==false) {
					chapterName = tc.getSimilarChapteTitle();
					flagForChapterName = true;
					
					entryCriterionIds.add(  cmmn.addHumanTaskToGroup(tasks[0], true) );
					for(int i=1; i<tasks.length; i++) {
						entryCriterionIds.add( cmmn.addHumanTaskToGroup(tasks[i], false) );
					}
				} else {
					for(String task : tasks) {
						entryCriterionIds.add( cmmn.addHumanTaskToGroup(task, false) );
					}
				}
			}
			
			cmmn.addMilestone(
					TaskController.getChapterMilestoneName(chapterName), 
					entryCriterionIds.toArray(new String[entryCriterionIds.size()])
			);
		}
		
		AlgoController.saveToOutputFile(cmmn.getCMMNxml(), "activity-mining.xml");
		
		if(Config.SEND_TO_CAMUNDA.getValue().equals("true")) {
			System.out.println("Deploying CMMN in Camunda.");
			CamundaConnector.deployCMMNCaseDefinition(name, cmmn.getCMMNxml());
		}
	}

}
