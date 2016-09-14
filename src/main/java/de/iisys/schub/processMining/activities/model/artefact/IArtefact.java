package de.iisys.schub.processMining.activities.model.artefact;

public abstract class IArtefact {
	
	protected String id;
	
	public IArtefact(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
}
