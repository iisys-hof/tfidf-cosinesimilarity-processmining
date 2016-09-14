package de.iisys.schub.processMining.activities.tasks;

import de.iisys.schub.processMining.activities.UserRelationController;
import de.iisys.schub.processMining.activities.model.MailTaskCandidate;
import de.iisys.schub.processMining.activities.model.MessageTaskCandidate;
import de.iisys.schub.processMining.activities.model.TaskCandidate;
import de.iisys.schub.processMining.activities.model.artefact.LiferayBlogArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayForumArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayWebContentArtefact;
import de.iisys.schub.processMining.activities.model.artefact.LiferayWikiArtefact;
import de.iisys.schub.processMining.activities.model.artefact.MailArtefact;
import de.iisys.schub.processMining.activities.model.artefact.NuxeoDocArtefact;
import de.iisys.schub.processMining.activities.model.artefact.SocialMessageArtefact;
import de.iisys.schub.processMining.config.Lang;

/**
 * This class creates readable tasks, using a TaskCandidate.
 * To add new tasks, add a method "task_YourNewTask" and put 
 * a additional if-statement to the method createTasks(TaskCandidate)
 * 
 * @author Christian Ochsenkühn
 *
 */
public class TaskController {
	
	private static final String CHAPTER_MILESTONE = "Finish chapter";
	
	/**
	 * Creates one ore more tasks, using the given TaskCandidate.
	 * @param taskCandidate
	 * @return
	 * 		String[0]: task one
	 * 		String[1]: optionally task two
	 */
	public static String[] createTasks(TaskCandidate taskCandidate) {
		String[] tasks = null;
		
		/* 
		 * Debugging:
		for(Date d : taskCandidate.getPublishedDates()) {
			System.out.println(d.toString());
		}
		 */
		
		if(taskCandidate.getArtefact() instanceof SocialMessageArtefact ||
				taskCandidate.getArtefact() instanceof MailArtefact) {
			
			tasks = new String[] { task_TalkToARole(taskCandidate) };
			
		} else if(taskCandidate.hasSimilarChapter()) {
			tasks = new String[2];
			tasks[0] = task_PrepareChapter(taskCandidate);
			tasks[1] = task_IntegrateContentsIntoChapter(taskCandidate);
		} else {
			System.out.println("Has no similar chapter: "+taskCandidate.getType());
			tasks = new String[0];
		}
		
		return tasks;
	}
	
	/**
	 * Creates a task similar to "prepare chapter X in a..." (e.g. in a blog post).
	 * @param tc
	 * @return
	 */
	private static String task_PrepareChapter(TaskCandidate tc) {
		StringBuffer name = new StringBuffer("Prepare chapter "+tc.getSimilarChapteTitle()+" in ");
		
		if(tc.getArtefact() instanceof NuxeoDocArtefact) {
			name.append("a document");
		} else if(tc.getArtefact() instanceof LiferayBlogArtefact) {
//			name.append("a blog article");
			name = new StringBuffer("Sketch your ideas about "+tc.getSimilarChapteTitle()+" in a blog post.");
		} else if(tc.getArtefact() instanceof LiferayForumArtefact) {
//			name.append("a forum thread");
			name = new StringBuffer("Discuss chapter "+tc.getSimilarChapteTitle()+" in a forum thread.");
		} else if(tc.getArtefact() instanceof LiferayWikiArtefact) {
			name.append("a wiki page");
		} else if(tc.getArtefact() instanceof LiferayWebContentArtefact) {
			if( ((LiferayWebContentArtefact)tc.getArtefact()).isMeetingMinutes() ) {
				name = new StringBuffer("Make notes of "+tc.getSimilarChapteTitle()+" in meeting minutes.");
			} else {
				name = new StringBuffer("Make notes of "+tc.getSimilarChapteTitle()+" in a liferay web content.");
			}
		} else {
			name.append(tc.getGenerator());
		} // TODO: usw.
		
/*		String role = tc.getRolesString();
		if(!role.equals(""))
			name.append(" with a "+role);
		*/
		
		/*
		System.out.println("[TaskController.task_PrepareChapter] type: "+tc.getType()+", sim: "+tc.getCosineSimilarity()+
				" with "+tc.getSimilarChapteTitle());		
		System.out.println(desc+"\n");
		*/
		
		return name.toString();
	}
	
	/**
	 * Creates a task similar to "Integrate contents from ... into chapter X" (e.g. from a blog post).
	 * @param tc
	 * @return
	 */
	private static String task_IntegrateContentsIntoChapter(TaskCandidate tc) {
		String name = "Integrate contents from "+getArtefactName(tc)+
				" into chapter "+tc.getSimilarChapteTitle()+".";
		return name;
	}
	
	/**
	 * Creates a task similar to "talk to... about chapter X".
	 * @param tc
	 * @return
	 */
	private static String task_TalkToARole(TaskCandidate tc) {
		if(tc instanceof MessageTaskCandidate || tc instanceof MailTaskCandidate) {
			switch( ((MessageTaskCandidate)tc).getUserRelation() ) {
				case UserRelationController.REL_SUBORDINATE_SENDER:
					return Lang.TALK_TO_SUPERIOR.getValue()+
							" "+Lang.ABOUT.getValue()+" "+tc.getSimilarChapteTitle();
				case UserRelationController.REL_SUBORDINATE_RECEIVER:
					return Lang.TALK_TO_SUBORDINATE.getValue()+
							" "+Lang.ABOUT.getValue()+" "+tc.getSimilarChapteTitle();
				case UserRelationController.REL_SAME_OU:
					return Lang.TALK_TO_COLLEAGUE.getValue()+
							" "+Lang.ABOUT.getValue()+" "+tc.getSimilarChapteTitle();
				case UserRelationController.REL_MAYBE_SPECIFIC_OU_OR_JOBPOSITION_RECEIVER:
					return Lang.TALK_TO_DEPARTMENT_EMPLOYEE.getValue()+" "+
							((MessageTaskCandidate)tc).getUserRelationAddition()+
							" "+Lang.ABOUT.getValue()+" "+tc.getSimilarChapteTitle();
				case UserRelationController.REL_MAYBE_SPECIFIC_JOBPOSITION_RECEIVER:
//					System.out.println(((MessageTaskCandidate)tc).getUserRelationAddition());
					return Lang.TALK_TO_ROLE.getValue()+" "+
							((MessageTaskCandidate)tc).getUserRelationAddition()+
							" "+Lang.ABOUT.getValue()+" "+tc.getSimilarChapteTitle();
			}
		}
		return null;
	}
	
	/**
	 * Returns a readable artifact name.
	 * @param tc
	 * @return
	 */
	private static String getArtefactName(TaskCandidate tc) {
		String name = "unknown";
		if(tc.getArtefact() instanceof NuxeoDocArtefact) {
			name = "document";
		} else if(tc.getArtefact() instanceof LiferayBlogArtefact) {
			name  = "blog article";
		} else if(tc.getArtefact() instanceof LiferayForumArtefact) {
			name  = "forum thread";
		} else if(tc.getArtefact() instanceof LiferayWikiArtefact) {
			name  = "wiki page";
		} else if(tc.getArtefact() instanceof LiferayWebContentArtefact) {
			name = "web content";
		} else {
			name  = tc.getGenerator();
		}
		return name;
	}
	
	/**
	 * Creates a proper name for a milestone.
	 * @param chapterName
	 * @return
	 */
	public static String getChapterMilestoneName(String chapterName) {
		return CHAPTER_MILESTONE+" "+chapterName;
	}
	
	/*
	public static String createTask(JSONObject activity, String chapterTitle, String involvedRole) {
		
		String task = null;
		String artefactType = null;
		
		JSONObject object = activity.getJSONObject("object");
		String activityType = object.getString("objectType");
		
		
		switch(activityType) {
		case(TYPE_LIFERAY_BLOG_ENTRY):
			artefactType = "blog entry";
			break;
		case(TYPE_LIFERAY_FORUM_ENTRY):
			artefactType = "message board entry";
			break;
		case(TYPE_LIFERAY_FORUM_THREAD):
			artefactType = "message board thread";
			break;
		case(TYPE_LIFERAY_WEB_CONTENT):
			artefactType = "web content article";
			break;
		case(TYPE_LIFERAY_WIKI_PAGE):
			artefactType = "wiki page";
			break;
		case(TYPE_NUXEO_DOCUMENT):
			break;
		case(TYPE_OX_TASK):
			break;
		case(TYPE_OX_CALENDAR_ENTRY):
			break;
		}
		
		task = "Prepare chapter"+chapterTitle+" in a "+artefactType;
		if(involvedRole!=null)
			task += " together with "+involvedRole;
		
		return task;
	}
	*/
}
