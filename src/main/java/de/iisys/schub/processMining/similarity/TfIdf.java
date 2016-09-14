package de.iisys.schub.processMining.similarity;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.iisys.schub.processMining.similarity.model.MinedDocument;
import de.iisys.schub.processMining.similarity.model.MinedMainDocument;
import de.iisys.schub.processMining.similarity.threading.IdfCallable;
import de.iisys.schub.processMining.similarity.threading.TfCallable;

/**
 * Class to calculate TfIdf of given documents.
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
 * @author Mubin Shrestha
 * 		Source: http://computergodzilla.blogspot.de/2013/07/how-to-calculate-tf-idf-of-document.html
 * @author Christian Ochsenkühn
 */
public class TfIdf {
	
	private List<String> allTerms;
	private Timestamp lastTimestamp = null;
	
	/**
	 * Needs a list of all unique terms of all documents.
	 * @param allTerms
	 */
	public TfIdf(List<String> allTerms) {
		this.allTerms = allTerms;
	}
	
	/**
	 * Creates a tf-idf vector for each document (and the chapters of the main document)
	 * and saves them directly in the documents' objects.
	 * @author Christian Ochsenkühn
	 * 
	 * @param mainDoc
	 * @param compareDocs
	 * @param threads:
	 * 		The tf-ifs will be calculated by this number of threads.
	 * @return
	 * 		Returns a list of all documents in the order: compareDocs, mainDoc, chapters of mainDoc
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MinedDocument> calculateTfIdfsThreadable(MinedMainDocument mainDoc, List<MinedDocument> compareDocs, int threads)
    		throws InterruptedException, ExecutionException {
//    	tfidfDocsVector = new ArrayList<double[]>();
        
		System.out.print("\t\t"); printTimestamp(false); System.out.println(": idf...");
		
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future> futures = new ArrayList<Future>();
        
        System.out.println("DEBUG| calculateTf... compareDocs.size: "+compareDocs.size());
        
        // Idfs:
        List<MinedDocument> allDocs = new ArrayList<MinedDocument>(compareDocs);
        int mainDocPosition = allDocs.size();
        allDocs.add(mainDoc);
        
        double[] allIdfs = new double[allTerms.size()];
        
        for(String term : allTerms) {
        	futures.add( executor.submit(new IdfCallable(term, allDocs)) );
        }
        
        int i = 0;
        // the Future.get() methods wait until their threads have finished
        for(Future fut: futures) {
			allIdfs[i++] = (double) fut.get();
        }
        executor.shutdown();
        futures.clear();
        System.out.print("\t\t"); printTimestamp(true); System.out.println(": Done!");
        
     // Tfs:
        System.out.print("\t\t"); printTimestamp(false); System.out.println(": tf and tf*idf...");
        
        executor = Executors.newFixedThreadPool(threads);  
//        List<List<Future>> tfFutures = new ArrayList<List<Future>>();
//        List<Future> docFutures;
        
        futures = new ArrayList<Future>();
        
        if(mainDoc.hasChapters()) {
        	// the chapters must not be added before the idf calculation!
        	allDocs.addAll(mainDoc.getChapters());
        }
        
        /*
        for (MinedDocument doc : allDocs) {
        	docFutures = new ArrayList<Future>();
        	for(String term : allTerms) {
        		docFutures.add( executor.submit(new TfCallable(term, doc.getTerms())) );
        	}
        	tfFutures.add(docFutures);
        } */
        
        for(MinedDocument doc : allDocs) {
        		futures.add( executor.submit(new TfCallable(doc, allTerms)) );
        }
        
        double[] tfidfvector;
        double tfidf;
        int docCount = 0;
        /*
        for(List<Future> futureList : tfFutures) {
            tfidfvector = new double[allTerms.size()];
            
            int termCount = 0;
            for(Future fut : futureList) {
                tfidf = (double)fut.get() * allIdfs[termCount];
                tfidfvector[termCount] = tfidf;
                termCount++;
            }
            
            allDocs.get(docCount).setTfIdfVector(tfidfvector);
            docCount++;
        } */
        
        for(Future fut : futures) {
        	if(fut!=null) {
	        	tfidfvector = new double[allTerms.size()];
	        	
	        	double[] tfs = (double[])fut.get();
	
	        	for(int j=0; j<tfs.length; j++) {
	        		tfidf = tfs[j] * allIdfs[j];
	        		tfidfvector[j] = tfidf;
	        	}
	        	
	        	allDocs.get(docCount).setTfIdfVector(tfidfvector);
        	}
        	docCount++;
        }
        executor.shutdown();
        
        System.out.print("\t\t"); printTimestamp(true); System.out.println(": Done!");
        
        mainDoc = (MinedMainDocument) allDocs.get(mainDocPosition);
        if(allDocs.size() > mainDocPosition)
        	mainDoc.setChapters(allDocs.subList(mainDocPosition+1, allDocs.size()));
        compareDocs = allDocs.subList(0, mainDocPosition);
        
        return allDocs;
    }
	
    
    /**
     * Calculates the tf of the term 'termToCheck'
     * @author Mubin Shrestha
     * 
     * @param totalterms : Array of all the words under processing document
     * @param termToCheck : term of which tf is to be calculated.
     * @return tf(term frequency) of term termToCheck
     */
    public static double tfCalculator(String[] totaltermsInDoc, String termToCheck) {
        double count = 0;
        for (String s : totaltermsInDoc) {
            if (s.equalsIgnoreCase(termToCheck)) {
                count++;
            }
        }
        return count / totaltermsInDoc.length;
    }
    
    /**
     * Calculates idf of the term 'termToCheck'
     * @author Mubin Shrestha
     * 
     * @param allTerms : all the terms of all the documents
     * @param termToCheck
     * @return idf(inverse document frequency) score
     */
    public static double idfCalculator(List<MinedDocument> allDocs, String termToCheck) {
        double count = 0;
        for (MinedDocument doc : allDocs) {
        	if(doc==null) continue;
        	
            for (String s : doc.getTerms()) {
                if (s.equalsIgnoreCase(termToCheck)) {
                    count++;
                    break;
                }
            }
        }
        return 1 + Math.log(allDocs.size() / count);
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
    
    public static void showMemory() {
    	int mb = 1024*1024;
        
        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
         
        System.out.println("##### Heap utilization statistics [MB] #####");
         
        //Print used memory
        System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / mb);
 
        //Print free memory
        System.out.println("Free Memory:"
            + runtime.freeMemory() / mb);
         
        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
 
        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}