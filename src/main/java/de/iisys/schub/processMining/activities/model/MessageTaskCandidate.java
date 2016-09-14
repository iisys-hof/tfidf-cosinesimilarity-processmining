package de.iisys.schub.processMining.activities.model;

import org.json.JSONObject;

public class MessageTaskCandidate extends TaskCandidate {
	
	private final String ACTIVITY_USERRELATION = "userRelation";
	
	private int userRelation;
	private String userRelationAddition; // department or role
	
	public MessageTaskCandidate(JSONObject activity, String mainDocumentId) {
		super(activity, mainDocumentId);
		this.userRelationAddition = null;
	}
	
	// getter:
	public int getUserRelation() {
		return this.userRelation;
	}
	
	public String getUserRelationAddition() {
		return this.userRelationAddition;
	}
	
	// setter:
	public void setUserRelation(int userRelation) {
		this.userRelation = userRelation;
	}
	
	public void setUserRelationAddition(String userRelationAddition) {
		this.userRelationAddition = userRelationAddition;
//		this.activity = new JSONObject(this.activity);
		this.activity.put(ACTIVITY_USERRELATION, userRelationAddition);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o==null || !(o instanceof MessageTaskCandidate || o instanceof MailTaskCandidate) ) {
			return false;
		} else {
			MessageTaskCandidate otherTC = (MessageTaskCandidate) o;
			
			// if same userRelation and no addition, return true
			if( this.userRelation != otherTC.getUserRelation() )
				return false;
			else if(this.userRelationAddition==null)
				return true;
			// else:
			// if same userRelation and addition are the same, return true
			if( this.userRelationAddition.equals(otherTC.getUserRelationAddition()) )
				return true;
			
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		String addition = "";
		
		if(this.userRelationAddition!=null) {
			addition = this.userRelationAddition;
		}
		
		return ("MessageTaskCandidate" + String.valueOf(this.userRelation) + addition).hashCode();
	}
}
