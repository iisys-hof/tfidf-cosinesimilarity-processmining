package de.iisys.schub.processMining.similarity.model;

public class Similarity {
	private double cosineSimilarity;
	private String[] similarTerms;
	
	public Similarity(double cosineSimilarity, String[] similarTerms) {
		this.setCosineSimilarity(cosineSimilarity);
		this.setSimilarTerms(similarTerms);
	}

	public double getCosineSimilarity() {
		return cosineSimilarity;
	}

	public void setCosineSimilarity(double cosineSimilarity) {
		this.cosineSimilarity = cosineSimilarity;
	}

	public String[] getSimilarTerms() {
		return similarTerms;
	}

	public void setSimilarTerms(String[] similarTerms) {
		this.similarTerms = similarTerms;
	}
}
