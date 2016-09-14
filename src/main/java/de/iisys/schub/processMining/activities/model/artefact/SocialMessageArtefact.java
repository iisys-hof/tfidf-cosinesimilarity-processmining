package de.iisys.schub.processMining.activities.model.artefact;

public class SocialMessageArtefact extends IArtefact {

	private String userId;
	
	public SocialMessageArtefact(String messageId) {
		super(messageId);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
