package de.iisys.schub.processMining.activities.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import de.iisys.schub.processMining.activities.model.artefact.IArtefact;
import de.iisys.schub.processMining.activities.network.ShindigConnector;
import de.iisys.schub.processMining.activities.network.ShindigDirectConnector;
import de.iisys.schub.processMining.activities.network.ShindigRESTConnector;
import de.iisys.schub.processMining.config.Config;

public class TaskCandidate {
	
	protected JSONObject activity;
	private IArtefact activityArtefact;
	
	private List<String> roles;
	private TreeSet<Date> published;
	
//	private MinedDocument similarChapter;
	private String similarChapterTitle;
	private String[] similarChapterTerms;
	private float cosineSimilarity;
	
	private List<TaskCandidate> successors;
	
	private String mainDocumentId;
	
	
	public TaskCandidate(JSONObject jsonActivity) {
		this.activity = jsonActivity;
		this.mainDocumentId = null;
		this.similarChapterTitle = null;
		this.similarChapterTerms = null;
		this.activityArtefact = null;
		
		// TreeSet: http://stackoverflow.com/a/8725470
		this.published = new TreeSet<Date>();
		addPublishedDate(jsonActivity);
		
		this.roles = new ArrayList<String>();
		addEmployeeRole(jsonActivity);
		
		this.successors = new ArrayList<TaskCandidate>();
	}
	
	public TaskCandidate(JSONObject jsonActivity, String mainDocumentId) {
		this(jsonActivity);
		this.mainDocumentId = mainDocumentId;
	}
	
	/*
	public void setSimilarChapter(MinedDocument similarChapter) {
		this.similarChapter = similarChapter;
		this.similarChapterTitle = similarChapter.getName();
	} */
	
	public JSONObject getActivity() {
		return this.activity;
	}
	
	public String getType() {
		return this.activity.getString(ShindigConnector.ACTIVITY_VERB)+"|" +
				this.activity.getJSONObject(
						ShindigConnector.ACTIVITY_OBJECT).getString(ShindigConnector.ACTIVITY_OBJECT_OBJECTTYPE
				);
	}
	
	public String getObjectType() {
		return this.activity.getJSONObject(
					ShindigConnector.ACTIVITY_OBJECT).getString(ShindigConnector.ACTIVITY_OBJECT_OBJECTTYPE
				);
	}
	
	public String getGenerator() {
		return this.activity.getJSONObject(ShindigConnector.ACTIVITY_GENERATOR).getString(ShindigConnector.DISPLAYNAME);
	}
	
	public void setSimilarChapterTitle(String chapterTitle) {
		this.similarChapterTitle = chapterTitle;
	}
	public String getSimilarChapteTitle() {
		return this.similarChapterTitle;
	}
	
	public void setSimilarChapterTerms(String[] chapterTerms) {
		this.similarChapterTerms = chapterTerms;
	}
	public String[] getSimilarChapterTerms() {
		return this.similarChapterTerms;
	}
	
	public boolean hasSimilarChapter() {
		return (this.similarChapterTitle!=null);
	}
	
	public float getCosineSimilarity() {
		return cosineSimilarity;
	}

	public void setCosineSimilarity(double cosineSimilarity) {
		this.cosineSimilarity = (float)cosineSimilarity;
	}
	
	public void setmainDocumentId(String mainDocumentId) {
		this.mainDocumentId = mainDocumentId;
	}
	public String getMainDocumentId() {
		return this.mainDocumentId;
	}
	
	public void setArtefact(IArtefact activityArtefact) {
		this.activityArtefact = activityArtefact;
	}
	public IArtefact getArtefact() {
		return this.activityArtefact;
	}
	
	public void addEmployeeRole(JSONObject activity) {
		try {
			addEmployeeRole(activity.getJSONObject(ShindigConnector.ACTIVITY_ACTOR).getString(ShindigConnector.ID));
		} catch (JSONException e) {
			System.out.println("TaskCandidate has no [\"actor\"] in activity. (TaskCandidate.addEmployeeRole)");
		}
	}
	
	public void addEmployeeRole(String userId) {
		String role;
		if(Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT)) {
			ShindigDirectConnector shindigCon = new ShindigDirectConnector();
			// disabled
//			role = shindigCon.getUserRole(userId);
			role = null;
		} else {
			role = ShindigRESTConnector._getUserRole(userId);
		}
		if(role!=null && !role.equals(""))
			this.roles.add(role);
	}
	
	public String getRolesString() {
		Set<String> unique = new HashSet<String>(this.roles);
		StringBuffer buf = new StringBuffer();
		
		int i=0;
		for(String role : unique) {
			if(i>0)
				buf.append(" or ");
			buf.append(role);
			i++;
			if(i>2) break;
		}
		return buf.toString();
	}
	
	public void addPublishedDate(JSONObject activity) {
		try {
			String published = activity.getString(ShindigConnector.ACTIVITY_PUBLISHED);
			addPublishedDate(published);
		} catch (JSONException e) {
			System.out.println("TaskCandidate has no [\"published\"] in activity. (TaskCandidate.addPublishedDate)");
		}
	}
	public void addPublishedDate(String publishedDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		try {
			Date published = df.parse(publishedDate);
			addPublishedDate(published);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void addPublishedDate(Date publishedDate) {
		this.published.add(publishedDate);
	}
	
	public void addPublishedDates(Set<Date> dates) {
		this.published.addAll(dates);
	}
	
	/*
	public Date getOwnPublishedDate() {
		return this.published.get(0);
	} */
	public TreeSet<Date> getPublishedDates() {
		return this.published;
	}
	
	
	public List<TaskCandidate> getSuccessors() {
		return successors;
	}
	public void addSuccessor(TaskCandidate tc) {
		if(this.successors==null)
			this.successors = new ArrayList<TaskCandidate>();
		this.successors.add(tc);
	}

	public boolean equals(Object o) {
		if(o==null || !(o instanceof TaskCandidate) ) {
			return false;
		} else {
			TaskCandidate otherTC = (TaskCandidate) o;
			if( !this.getType().equals(otherTC.getType()) )
				return false;
			if(this.getSimilarChapteTitle()==null)
				return false;
			if( !this.getSimilarChapteTitle().equals(otherTC.getSimilarChapteTitle()) )
				return false;
			
			System.out.println("return true");
//			addPublishedDate(otherTC.getOwnPublishedDate());
//			otherTC.addPublishedDate(this.getOwnPublishedDate());
			otherTC.addPublishedDates(this.getPublishedDates());
			return true;
		}
	}
	
	public int hashCode() {
		return (this.getType()+this.getSimilarChapteTitle()).hashCode();
	}
}