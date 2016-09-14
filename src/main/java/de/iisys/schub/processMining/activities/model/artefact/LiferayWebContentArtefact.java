package de.iisys.schub.processMining.activities.model.artefact;

public class LiferayWebContentArtefact extends IArtefact {
	
	private boolean isMeetingMinutes = false;
	
	public LiferayWebContentArtefact(String webContentArticleId) {
		super(webContentArticleId);
	}

	public boolean isMeetingMinutes() {
		return this.isMeetingMinutes;
	}
	
	public void setIsMeetingMinutes(boolean isMeetingMinutes) {
		this.isMeetingMinutes = isMeetingMinutes;
	}
}
