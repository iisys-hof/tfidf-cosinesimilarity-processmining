package de.iisys.schub.processMining.activities.model.artefact;

public class LiferayWikiArtefact extends IArtefact {
	
	private String title;
	
	public LiferayWikiArtefact(String pagePrimaryKey) {
		super(pagePrimaryKey);
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String pageTitle) {
		this.title = pageTitle;
	}
}
