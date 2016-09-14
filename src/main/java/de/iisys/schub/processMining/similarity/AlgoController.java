package de.iisys.schub.processMining.similarity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import de.iisys.schub.processMining.activities.network.ArtefactController;
import de.iisys.schub.processMining.config.Config;
import de.iisys.schub.processMining.similarity.Tokenizing.UsePOSTagger;
import de.iisys.schub.processMining.similarity.Tokenizing.UseStemmer;
import de.iisys.schub.processMining.similarity.Tokenizing.UseStopwords;
import de.iisys.schub.processMining.similarity.Tokenizing.UseTokenizer;
import de.iisys.schub.processMining.similarity.model.MinedDocument;
import de.iisys.schub.processMining.similarity.model.MinedMainDocument;
import de.iisys.schub.processMining.similarity.model.Similarity;
import de.iisys.schub.processMining.similarity.parsing.DocxParser;
import de.iisys.schub.processMining.similarity.parsing.TextParser;

/**
 * Main controller to compare the similarity of documents.
 * You usually have a docx main document (with chapters) 
 * and some other documents/texts to compare with.
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
public class AlgoController {
	
	private int THREADS;
	private String OUTPUT_NAME;
	private double PERCENTILE;
	private UseTokenizer TOKENIZER;
	private UseStopwords STOPWORDS;
	private UseStemmer STEMMER;
	private UsePOSTagger POSTAGGER; 
	private boolean ALLOW_NUMBER_AS_TERM;
	
	public enum Language {
		EN, DE
	}
	
	private MinedMainDocument mainDoc;
	private List<MinedDocument> compareDocs;
	private List<String> allTerms;
	
	private Timestamp lastTimestamp = null;
	private double time_parsing;
    private double time_tokenizing;
    private double time_tfidf;
    private double time_cosine;
	
	public AlgoController() {
		loadProperties();
	}
	
	public AlgoController(MinedMainDocument mainDoc, List<MinedDocument> compareDocs) {
		this.mainDoc = mainDoc;
		this.compareDocs = compareDocs;
		loadProperties();
	}
	
	/**
	 * Starts a pipeline for the given documents with the following steps:
	 * Parsing, tokenizing, stopword removal, stemming, [POS tagging],
	 * tf-idf vectors creation and calculating the cosine similarities.
	 * @param mainFilePath:
	 * 		path to the main docx document with chapters
	 * @param compareFilesPath:
	 * 		path to the documents to compare with
	 * @param percentile:
	 * 		the percentile (in percent) above which documents are supposed as similar
	 */
	public void pipelineCosineSimilarity(String mainFilePath, String compareFilesPath, double percentile) {
		PERCENTILE = percentile;
		this.pipelineCosineSimilarity(mainFilePath, compareFilesPath);
	}
	
	/**
	 * Starts a pipeline for the given documents with the following steps:
	 * Parsing, tokenizing, stopword removal, stemming, [POS tagging],
	 * tf-idf vectors creation and calculating the cosine similarities.
	 * 
	 * Last Step: Saves all cosine similarities to an output file.
	 * 
	 * @param mainFilePath:
	 * 		path to the main docx document with chapters
	 * @param compareFilesPath:
	 * 		path to the documents to compare with
	 */
	public void pipelineCosineSimilarity(String mainFilePath, String compareFilesPath) {
		// step 1-3:
		this.mainPipeline(mainFilePath, compareFilesPath);
		
		// step 4: calculating cosine similarities
		printTimestamp(false); System.out.println(": Calculating cosine similarity...");
		String output = this.calculateCosineSimilarities();
		time_cosine = printTimestamp(true); System.out.println(": Done!");
		
		// step 5: save output
		saveToOutputFile(this.showMainMetaData() + output, OUTPUT_NAME);
	}
	
	/**
	 * Starts a pipeline for the given documents with the following steps:
	 * Parsing, tokenizing, stopword removal, stemming, [POS tagging],
	 * tf-idf vectors creation and calculating the cosine similarities.
	 * 
	 * Last Steps:
	 * All docs with an cosine similarity over the percentile are considered as similar.
	 * Only the similar docs are saved to an output file 
	 * (incl. the 3 most important terms, which appear in both similar documents).
	 * 
	 * @param mainFilePath
	 * @param compareFilesPath
	 * @return
	 * 		Returns the mainDocument file which contains a list of similar documents.
	 */
	public MinedMainDocument pipelineSimilarDocs(String mainFilePath, String compareFilesPath) {
		// step 1-3:
		this.mainPipeline(mainFilePath, compareFilesPath);
		
		// step 4: calculating similar docs (via cosine similarity and percentile)
		printTimestamp(false); System.out.println(": Calculating cosine similarity...");
		this.calculateSimilarDocsViaCosine();
		time_cosine = printTimestamp(true); System.out.println(": Done!");
		
		// step 5: create output string
		String output = this.showSimilarDocs(this.mainDoc);
		
		// step 6: save output
		saveToOutputFile(this.showMainMetaData() + output, OUTPUT_NAME);
		
		return this.mainDoc;
	}
	
	/**
	 * This pipeline should be used for the Shindig activity mining, coming from the ActivityController.
	 * 1. Parsing the main artifact/document and the artifacts to compare.
	 * 2. Tokenizinig (incl. stemming etc.) of the artifacts' texts.
	 * 3. Creating the tf-idf vectors of these texts.
	 * 4. Calculating the cosine similarity between these texts and finding the similar texts.
	 * 5. Creating and saving the results of the similarity to a text file.
	 * 6. Returning the MinedMainDocument, containing the similarity results.
	 * 
	 * @param artefactCon
	 * @param cycleNr
	 * @return
	 */
	public MinedMainDocument pipelineNuxeoSimilarArtefacts(ArtefactController artefactCon, int cycleNr) {
		// step 1: parsing
		printTimestamp(false); System.out.println(": Parsing files...");
		this.mainDoc = artefactCon.parseMainArtifact();
		this.compareDocs = new ArrayList<MinedDocument>();
		
		// TODO: only for testing:
//		parseFilesFromFolders(null, "docs");
		
		this.compareDocs.addAll(artefactCon.parseCompareArtifacts());
		time_parsing = printTimestamp(true); System.out.println(": Done!");
		
		// step 2: tokenizing (incl. stemming etc.)
		printTimestamp(false); System.out.println(": Tokenizing "+(compareDocs.size()+1)+" doc(s) and "+mainDoc.chapterCount()+" chapters...");
		this.tokenizeTexts();
		time_tokenizing = printTimestamp(true); System.out.println(": Done!");
		
		// step 3: creating tf-idf vectors
		printTimestamp(false); System.out.println(": Creating tf-idf vectors...");
		this.calculateTfIdfs();
		time_tfidf = printTimestamp(true); System.out.println(": Done!");
		
		// step 4: calculating similar docs (via cosine similarity and percentile)
		printTimestamp(false); System.out.println(": Calculating cosine similarity...");
		this.calculateSimilarDocsViaCosine();
		time_cosine = printTimestamp(true); System.out.println(": Done!");
		
		// step 5: create output string
		String output = this.showSimilarDocs(this.mainDoc);
		
		// step 6: save output
		saveToOutputFile(this.showMainMetaData() + output, "cycle"+cycleNr+".txt");
		
		return this.mainDoc;
	}
	
	
	private void mainPipeline(String mainFilePath, String compareFilesPath) {
		// step 1: parsing
		printTimestamp(false); System.out.println(": Parsing files...");
		this.parseFilesFromFolders(mainFilePath, compareFilesPath);
		time_parsing = printTimestamp(true); System.out.println(": Done!");
		
		// step 2: tokenizing (incl. stemming etc.)
		printTimestamp(false); System.out.println(": Tokenizing "+(compareDocs.size()+1)+" docs and "+mainDoc.chapterCount()+" chapters...");
		this.tokenizeTexts();
		time_tokenizing = printTimestamp(true); System.out.println(": Done!");
		
		// step 3: creating tf-idf vectors
		printTimestamp(false); System.out.println(": Creating tf-idf vectors...");
		this.calculateTfIdfs();
		time_tfidf = printTimestamp(true); System.out.println(": Done!");
	}
	
	private void parseFilesFromFolders(String mainFilePath, String compareFilesPath) {
		TextParser textParser;
		
		// main doc:
		if(mainFilePath!=null) {
			if(mainFilePath.endsWith(".docx")) {
				DocxParser docx = new DocxParser(mainFilePath);
				docx.parseDocxAndChapters();
				mainDoc = new MinedMainDocument(docx.getTitle(), docx.getFullText());
				mainDoc.addChapters(docx.getChapterHeadlines(), docx.getChapterTexts());
			} else {
				textParser = new TextParser(mainFilePath);
				mainDoc = new MinedMainDocument(textParser.getFileNames().get(0), textParser.getTexts().get(0));
			}
		}
		
		// compare docs:
		compareDocs = new ArrayList<MinedDocument>();
		textParser = new TextParser(compareFilesPath);
		List<String> texts = textParser.getTexts();
		List<String> names = textParser.getFileNames();
		
		for(int i=0; i<texts.size(); i++) {
			compareDocs.add(new MinedDocument(names.get(i), texts.get(i)));
		}
	}
	
	/*
	private void parseFilesFromNuxeoAndFolders(String nuxeoDocId, String compareFilesPath) {
		TextParser textParser;		

		try {
			NuxeoConnector nuxeo = new NuxeoConnector();
			InputStream is = nuxeo.getDocumentInputStream(nuxeoDocId);
			
			String title = nuxeo.getLastDocTitle();
			if(title==null) title = "Main Document";
			
			DocxParser docx = new DocxParser(is);
			docx.parseDocxAndChapters();
			this.mainDoc = new MinedMainDocument(title, docx.getFullText());
			this.mainDoc.addChapters(docx.getChapterHeadlines(), docx.getChapterTexts());
			nuxeo.close();
			nuxeo = null; is = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// compare docs:
		compareDocs = new ArrayList<MinedDocument>();
		textParser = new TextParser(compareFilesPath);
		List<String> texts = textParser.getTexts();
		List<String> names = textParser.getFileNames();
		
		for(int i=0; i<texts.size(); i++) {
			compareDocs.add(new MinedDocument(names.get(i), texts.get(i)));
		}
	} */
	
	private void tokenizeTexts() {		
		Tokenizing tok = new Tokenizing(
				TOKENIZER,
				STOPWORDS,
				STEMMER,
				POSTAGGER,
				ALLOW_NUMBER_AS_TERM);
		
		this.allTerms = tok.tokenizeTermsThreadable(mainDoc, THREADS);
		// saves terms for each doc in the doc's object
		this. allTerms = tok.tokenizeTermsThreadable(compareDocs, allTerms, THREADS);
	}
	
	private void calculateTfIdfs() {
		TfIdf tfidf = new TfIdf(this.allTerms);
		try {
			tfidf.calculateTfIdfsThreadable(mainDoc, compareDocs, THREADS);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void calculateSimilarDocsViaCosine() {
		List<Double> cosSims = new ArrayList<Double>();
		List<String[]> similarTerms = new ArrayList<String[]>();
		Similarity sim;
		
		int i;
		/*
		for(MinedDocument doc : this.compareDocs) {
			sim = mainDoc.compareSimilarity(doc, allTerms);
			cosSims.add(i, sim.getCosineSimilarity());
			similarTerms.add(i, sim.getSimilarTerms());
			i++;
		}*/
		for(i=0; i<this.compareDocs.size(); i++) {
			sim = mainDoc.compareSimilarity(this.compareDocs.get(i), allTerms);
			if(sim!=null) {
				cosSims.add(i, sim.getCosineSimilarity());
				similarTerms.add(i, sim.getSimilarTerms());
			} else {
				cosSims.add(i, 0.);
				similarTerms.add(i, null);
			}
		}
		double percentile = this.getDocsPercentile(cosSims);
		
		for(i=0; i<cosSims.size(); i++) {
			if(cosSims.get(i)!=null && cosSims.get(i) >= percentile)
				mainDoc.addSimilarDoc(compareDocs.get(i), similarTerms.get(i), cosSims.get(i));
			else
				mainDoc.addSimilarDoc(null, null, cosSims.get(i));
		}
		
		if(mainDoc.hasChapters()) {
			for(int j=0; j<mainDoc.chapterCount(); j++) {
				cosSims.clear();
				similarTerms.clear();
				
				for(MinedDocument doc : compareDocs) {
					sim = mainDoc.getChapter(j).compareSimilarity(doc, allTerms);
					if(sim!=null) {
						cosSims.add(sim.getCosineSimilarity());
						similarTerms.add(sim.getSimilarTerms());
					} else {
						cosSims.add(0.);
						similarTerms.add(null);
					}
				}
				percentile = this.getDocsPercentile(cosSims);
				for(int k=0; k<cosSims.size(); k++) {
					if(cosSims.get(k)!=null && cosSims.get(k) >= percentile)
						mainDoc.getChapter(j).addSimilarDoc(compareDocs.get(k), similarTerms.get(k), cosSims.get(k));
					else
						mainDoc.getChapter(j).addSimilarDoc(null, null, cosSims.get(k));
				}
			}
		}
	}
	
	private String calculateCosineSimilarities() {
		DecimalFormat df = new DecimalFormat("#00.00");
		double cosSim;
		List<Double> cosSims = new ArrayList<Double>();
		
		StringBuffer mainBuf = new StringBuffer("\n\nDocument '"+mainDoc.getName()+"' is...\n");
		StringBuffer docBuf = new StringBuffer();
		for(MinedDocument doc : compareDocs) {
			cosSim = mainDoc.compareSimilarity(doc);
			cosSims.add(cosSim);
			docBuf.append("\t"+df.format( cosSim*100 )+" % \t similar with \t"+" ("+doc.getLang()+")\n");
		}
		mainBuf.append(this.showDocMetaData(cosSims)+"\n\n");
		mainBuf.append(docBuf);
		
		if(mainDoc.hasChapters()) {
			for(int i=0; i<mainDoc.chapterCount(); i++) {
				docBuf = new StringBuffer();
				cosSims.clear();
				
				mainBuf.append("\n\nChapter "+mainDoc.getChapter(i).getName()+" is...\n");
				for(MinedDocument doc : compareDocs) {
					cosSim = mainDoc.getChapter(i).compareSimilarity(doc);
					cosSims.add(cosSim);
					docBuf.append("\t"+df.format( cosSim*100 )+" % \t similar with \t"+doc.getName()+" ("+doc.getLang()+")\n");
				}
				mainBuf.append(this.showDocMetaData(cosSims)+"\n\n");
				mainBuf.append(docBuf);
			}
		}
		
		return mainBuf.toString();
	}
	
	private String showSimilarDocs(MinedMainDocument mainDoc) {
		DecimalFormat df = new DecimalFormat("#00.00");
		String terms[];
		
//		Map<MinedDocument, String[]> similarDocs = mainDoc.getSimilarDocs();
		List<MinedDocument> similarDocs = mainDoc.getSimilarDocs();
		List<String[]> similarDocsTerms = mainDoc.getSimilarDocsTerms();
		StringBuffer mainBuf = new StringBuffer("\n\nDocument '"+mainDoc.getName()+"' is...\n");
		
		int j=0;
//		for(MinedDocument simDoc : similarDocs.keySet()) {
		for(MinedDocument simDoc : similarDocs) {
			if(simDoc!=null) {
//				terms = similarDocs.get(simDoc);
				terms = similarDocsTerms.get(j);
				String termsString = "";
				if(terms!=null) {
					for(int t=0; t<terms.length; t++) {
						if(t>0) termsString += ", ";
						termsString += terms[t];
					}
				}
				mainBuf.append("\t"+df.format( mainDoc.getSimilarDocsCosine(j)*100 )+" % \t similar with \t"+simDoc.getName()+"\t\t("+termsString+")\n");
			}
			j++;
		}
		
		if(mainDoc.hasChapters()) {
			for(int i=0; i<mainDoc.chapterCount(); i++) {
				similarDocs = mainDoc.getChapter(i).getSimilarDocs();
				similarDocsTerms = mainDoc.getChapter(i).getSimilarDocsTerms();
				mainBuf.append("\n\nChapter "+mainDoc.getChapter(i).getName()+" is...\n");
				j=0;
//				for(MinedDocument simDoc : similarDocs.keySet()) {
				for(MinedDocument simDoc : similarDocs) {
					if(simDoc!=null) {
//						terms = similarDocs.get(simDoc);
						terms = similarDocsTerms.get(j);
						String termsString = "";
						if(terms!=null) {
							for(int t=0; t<terms.length; t++) {
								if(t>0) termsString += ", ";
								termsString += terms[t];
							}
						}
						mainBuf.append("\t"+df.format( mainDoc.getChapter(i).getSimilarDocsCosine(j)*100 )+" % \t similar with \t"+simDoc.getName()+"\t\t("+termsString+")\n");
					}
					j++;
				}
			}
		}
		
		return mainBuf.toString();
	}
	
	private String showMainMetaData() {
		String meta = "*** Cosine TfIdf Similarity ***\n\n"+
				"* 1 Main Document with "+mainDoc.chapterCount()+" chapters \n"+
    			"* "+this.compareDocs.size()+" Documents to compare \n"+
    			"* "+this.allTerms.size()+" unique terms \n"+
    			"* "+this.THREADS+" threads \n"+
    			"\n"+
    			"* Parsing time: "+time_parsing+"s \n"+
    			"* Tokenizing time: "+time_tokenizing+"s \n"+
    			"* TfIdf time: "+time_tfidf+"s \n"+
    			"* Cosine time: "+time_cosine+"s \n"+
    			"\n"+
    			"Configuration: \n"+
    			"* Percentile: "+this.PERCENTILE+" % \n"+
    			"* Numbers as terms: "+this.ALLOW_NUMBER_AS_TERM+"\n"+
    			"* Tokenizer: "+this.TOKENIZER+"\n"+
    			"* Stemmer: "+this.STEMMER+"\n"+
    			"* Pos-Tagger: "+this.POSTAGGER+"\n"+
    			"* Stopwords: "+this.STOPWORDS+"\n"+
    			"\n";
		return meta;
	}
	
	private String showDocMetaData(List<Double> cosineSimValues) {
		DescriptiveStatistics stat = new DescriptiveStatistics();
    	
    	for(int i=0; i<cosineSimValues.size(); i++) {
    		stat.addValue(cosineSimValues.get(i));
    	}
    	
    	double min = Math.round(stat.getMin()*1000)/1000.0;
    	double max = Math.round(stat.getMax()*1000)/1000.0;
    	double arithMean = Math.round(stat.getMean()*10000)/10000.0;
    	double percentile = Math.round(stat.getPercentile(PERCENTILE)*1000)/1000.0;
    	
    	DecimalFormat df = new DecimalFormat("#00.00");
    	String meta = 	"Min: "+df.format( min*100 )+" %"+
    					", Max: "+df.format( max*100 )+" %"+
    					", Arith. Mean: "+df.format( arithMean*100 )+" %"+
    					", Percentile ("+PERCENTILE+" %): "+df.format( percentile*100 )+" %";
    	
    	return meta;
	}
	
	private double getDocsPercentile(List<Double> cosineSimValues) {
		DescriptiveStatistics stat = new DescriptiveStatistics();
		for(int i=0; i<cosineSimValues.size(); i++) {
    		stat.addValue(cosineSimValues.get(i));
    	}
		return Math.round(stat.getPercentile(PERCENTILE)*1000)/1000.0;
	}
	
	public static void saveToOutputFile(String output, String outputFileName) {
		Writer out;
        try {
    		out = new BufferedWriter(new OutputStreamWriter(
            	    new FileOutputStream(outputFileName), "UTF-8"));
    	    try {
    	    	out.write(output);
    	    	System.out.println("PROCESSMINING: Successfully saved smiliarity results to "+outputFileName+"!");
    	    } finally {
    	    	out.close();
    	    	out = null;
    	    }
    	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double printTimestamp(boolean showDuration) {
		DecimalFormat df = new DecimalFormat("#0.000");
    	double duration = 0;
    	Timestamp time = new Timestamp(Calendar.getInstance().getTime().getTime());
    	if(showDuration && this.lastTimestamp!=null) {
    		duration = (time.getTime()-this.lastTimestamp.getTime())/1000f;
    		System.out.print(time.toString()+" (" + df.format((Math.round(duration*1000)/1000.0)) + " s)");
    	} else
    		System.out.print(time);
    	
    	this.lastTimestamp = time;
    	return Math.round(duration*1000)/1000.0;
    }
	
	private void loadProperties() {	
		// threads:
		try {
			THREADS = Integer.parseInt(Config.THREADS.getValue());
		} catch (NumberFormatException nfe) {
			THREADS = 4;
		}
		// outputFile:
		OUTPUT_NAME = Config.OUTPUT_FILE.getValue();
		// percentile:
		try {
			PERCENTILE = Integer.parseInt(Config.PERCENTILE.getValue());
		} catch (NumberFormatException e) {
			PERCENTILE = 90;
		}
		// allowNumberAsTerm:
		ALLOW_NUMBER_AS_TERM = Boolean.parseBoolean(Config.ALLOW_NUMBER_AS_TERM.getValue());
		// tokenizer:
		switch(Config.TOKENIZER.getValue().toUpperCase()) {
		case "WITH_MODEL":
			TOKENIZER = UseTokenizer.WITH_MODEL; break;
		case "SIMPLE":
		default:
			TOKENIZER = UseTokenizer.SIMPLE;
		}
		// stopwords:
		switch(Config.STOPWORDS.getValue().toUpperCase()) {
		case "NONE":
			STOPWORDS = UseStopwords.NONE; break;
		case "STOPWORDS_LIST":
		default:
			STOPWORDS = UseStopwords.STOPWORDS_LIST; break;
		}
		// stemmer:
		switch(Config.STEMMER.getValue().toUpperCase()) {
		case "SNOWBALL":
			STEMMER = UseStemmer.SNOWBALL; break;
		case "NONE":
		default:
			STEMMER = UseStemmer.NONE;
		}
		// posTagger:
		switch(Config.POS_TAGGER.getValue().toUpperCase()) {
		case "MAXENT":
			POSTAGGER = UsePOSTagger.MAXENT; break;
		case "PERCEPTRON":
			POSTAGGER = UsePOSTagger.PERCEPTRON; break;
		case "NONE":
		default:
			POSTAGGER = UsePOSTagger.NONE;
		}
	}
	
}	
