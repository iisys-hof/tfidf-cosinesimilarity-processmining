package de.iisys.schub.processMining.activities.model;

import org.json.JSONObject;

public class MailTaskCandidate extends MessageTaskCandidate {
	
	private final String VERB = "send";
	private final String TYPE = "Email";

	public MailTaskCandidate(JSONObject mailHit, String mainDocumentId) {
		super(mailHit, mainDocumentId);
	}

	public JSONObject getMailHit() {
		return getActivity();
	}
	
	@Override
	public String getType() {
		return VERB+"|"+TYPE;
	}

}
