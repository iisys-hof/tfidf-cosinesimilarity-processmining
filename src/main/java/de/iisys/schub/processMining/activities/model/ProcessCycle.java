package de.iisys.schub.processMining.activities.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import de.iisys.schub.processMining.activities.model.artefact.IArtefact;
import de.iisys.schub.processMining.activities.model.artefact.NuxeoDocArtefact;

public class ProcessCycle {
	
	private String mainDocId;
	private String docType;
	
	private Date startDate;
	private Date endDate;
	private Set<String> userIds;
	
	private NuxeoDocArtefact mainArtefact;
	private List<JSONObject> activities;	// activities.get(i) <> artefacts.get(i)
	private List<IArtefact> artefacts;		// belong together
	
	private List<TaskCandidate> taskCandidates;
	
	public ProcessCycle(String mainDocId, String docType) {
		this.mainDocId = mainDocId;
		this.docType = docType;
		this.mainArtefact = null;
		this.artefacts = new ArrayList<IArtefact>();
		this.taskCandidates = new ArrayList<TaskCandidate>();
		this.userIds = new HashSet<String>();
	}
	
	public ProcessCycle(String mainDocId, String docType, List<JSONObject> activities) {
		this(mainDocId, docType);		
		this.activities = activities;
	}

	
	public void setActivities(List<Map<String,Object>> activities) {
		if(this.activities==null)
			this.activities = new ArrayList<JSONObject>();
		
		for(Map<String,Object> activityMap : activities) {
			String mapString = activityMap.toString();
			this.activities.add(new JSONObject(mapString));
		}
	}
	public void setActivitiesJSONs(List<JSONObject> activities) {
		if(this.activities==null)
			this.activities = new ArrayList<JSONObject>();
		
		this.activities.addAll(activities);
	}
	
	public String getMainDocumentId() {
		return this.mainDocId;
	}
	
	public String getDocType() {
		return this.docType;
	}
	
	public NuxeoDocArtefact getMainArtefact() {
		return this.mainArtefact;
	}
	
	public void setMainArtefact(NuxeoDocArtefact artefact) {
		this.mainArtefact = artefact;
	}
	
	public List<TaskCandidate> getTaskCandidates() {
		return this.taskCandidates;
	}
	
	public void addTaskCandidate(TaskCandidate taskCandidate) {
		this.taskCandidates.add(taskCandidate);
	}
	
	public void addAllTaskCandidates(List<TaskCandidate> tcs) {
		if(this.taskCandidates==null)
			this.taskCandidates = new ArrayList<TaskCandidate>();
		
		this.taskCandidates.addAll(tcs);
	}
	
	public int getActivitiesSize() {
		if(activities!=null)
			return this.activities.size();
		else
			return 0;
	}
	
	public JSONObject getActivity(int i) {
		if(activities!=null && activities.size()>i)
			return this.activities.get(i);
		else
			return null;
	}
	public int addActivity(JSONObject activity) {
		this.activities.add(activity);
		return activities.size()-1;
	}
	
	public IArtefact getArtefact(int i) {
		if(artefacts!=null && artefacts.size()>i)
			return this.artefacts.get(i);
		else
			return null;
	}
	
	public List<IArtefact> getAllArtefacts() {
		return this.artefacts;
	}
	
	public void addActivityArtefact(IArtefact artefact, int i) {
		this.artefacts.add(artefact);
	}


	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Set<String> getUserIds() {
		return userIds;
	}
	public void setUserIds(Set<String> userIds) {
		this.userIds = userIds;
	}
	public void addUserId(String userId) {
		this.userIds.add(userId);
	}
	
}