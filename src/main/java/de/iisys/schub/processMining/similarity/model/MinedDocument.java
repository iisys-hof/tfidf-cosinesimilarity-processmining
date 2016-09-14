package de.iisys.schub.processMining.similarity.model;

import java.util.ArrayList;
import java.util.List;

import de.iisys.schub.processMining.similarity.CosineSimilarity;
import de.iisys.schub.processMining.similarity.parsing.TextParser;

/**
 * The model of a document which can contain some data for mining purposes.
 * It contains a name, a plain text, a list of all terms and tf-idf vector.
 * 
 * 
 * LICENSE:
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class MinedDocument {
	
	private String name;
	private String text;
	private String lang;
	private String[] terms;
	private double[] tfIdfVector;
	
//	private Map<MinedDocument, String[]> similarDocs;
	
	private List<MinedDocument> similarDocs;
	private List<String[]> similarDocsTerms;
	private List<Double> similarDocsCosines;
	
	
	public MinedDocument(String name, String text) {
		this.name = name;
		this.text = text;
		this.lang = TextParser.checkLanguage(text);
//		this.similarDocs = new LinkedHashMap<MinedDocument, String[]>();
		
		this.similarDocs = new ArrayList<MinedDocument>();
		this.similarDocsTerms = new ArrayList<String[]>();
		this.similarDocsCosines = new ArrayList<Double>();
	}
	
	
	public double compareSimilarity(MinedDocument otherDoc) {
		if(otherDoc!=null)
			return CosineSimilarity.getCosineSimilarity(tfIdfVector, otherDoc.getTfIdfVector());
		else
			return 0;
	}
	
	public Similarity compareSimilarity(MinedDocument otherDoc, List<String> allTerms) {
		if(otherDoc!=null)
			return CosineSimilarity.getCosineSimilarityAndSimilarTerms(tfIdfVector, otherDoc.getTfIdfVector(), allTerms);
		else
			return null;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double[] getTfIdfVector() {
		return tfIdfVector;
	}

	public void setTfIdfVector(double[] tfIdfVector) {
		this.tfIdfVector = tfIdfVector;
	}

	public String[] getTerms() {
		return terms;
	}

	public void setTerms(String[] terms) {
		this.terms = terms;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	/*
	public Map<MinedDocument, String[]> getSimilarDocs() {
		return this.similarDocs;
	} */
	
	public void addSimilarDoc(MinedDocument doc, double cosSim) {
//		this.similarDocs.put(doc, null);
		this.similarDocs.add(doc);
		this.similarDocsTerms.add(null);
		this.similarDocsCosines.add(cosSim);
	}
	
	public void addSimilarDoc(MinedDocument doc, String[] similarTerms, double cosSim) {
//		this.similarDocs.put(doc, similarTerms);
		this.similarDocs.add(doc);
		this.similarDocsTerms.add(similarTerms);
		this.similarDocsCosines.add(cosSim);
	}
	
	public List<MinedDocument> getSimilarDocs() {
		return this.similarDocs;
	}
	public MinedDocument getSimilarDoc(int i) {
		if(this.similarDocs.size()>i)
			return this.similarDocs.get(i);
		else
			return null;
	}
	
	public List<String[]> getSimilarDocsTerms() {
		return this.similarDocsTerms;
	}
	public String[] getSimilarDocTerms(int i) {
		if(this.similarDocsTerms.size()>i)
			return this.similarDocsTerms.get(i);
		else
			return null;
	}
	
	public double getSimilarDocsCosine(int i) {
		return this.similarDocsCosines.get(i);
	}
}
