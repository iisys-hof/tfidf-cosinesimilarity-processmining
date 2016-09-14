package de.iisys.schub.processMining.activities.model.artefact;

public class MailArtefact extends IArtefact {
	
	private String subject;
	private String content;
	private String senderId;
	private String receiverId;

	public MailArtefact(String id) {
		super(id);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	/**
	 * Might return null.
	 * @return
	 */
	public String getReceiverId() {
		if(this.receiverId!=null)
			return receiverId;
		else
			return null;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	
}
