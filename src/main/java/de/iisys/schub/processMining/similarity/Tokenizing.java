package de.iisys.schub.processMining.similarity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.iisys.schub.processMining.similarity.model.MinedDocument;
import de.iisys.schub.processMining.similarity.model.MinedMainDocument;
import de.iisys.schub.processMining.similarity.stopwords.IStopwords;
import de.iisys.schub.processMining.similarity.stopwords.StopwordsEnglish;
import de.iisys.schub.processMining.similarity.stopwords.StopwordsGerman;
import de.iisys.schub.processMining.similarity.threading.TokenizeCallable;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Class to tokenize, stem, [tag] documents' terms and remove stopwords.
 * The POS tagging should not be used in this version. If you want to use it, put single sentences in the tagger.
 * 
 * Models for tokenizer and POS can be found at: http://opennlp.sourceforge.net/models-1.5/
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
public class Tokenizing {
	
	public enum UseTokenizer {
		SIMPLE, WITH_MODEL
	}
	public enum UsePOSTagger {
		NONE, MAXENT, PERCEPTRON
	}
	public enum UseStemmer {
		NONE, SNOWBALL
	}
	public enum UseStopwords {
		NONE, STOPWORDS_LIST
	}
	private UseTokenizer usedTokenizer;
	private UsePOSTagger usedPosTagger;
	private UseStemmer usedStemmer;
	private UseStopwords usedStopwords;
	private boolean allowNumbers;
	
	// config:	
	private String BIN_TOKENIZER_MODEL_GERMAN = "resources/de-token.bin";
	private String BIN_POS_MODEL_MAXENT_GERMAN = "resources/de-pos-maxent.bin";
	private String BIN_POS_MODEL_PERCEPTRON_GERMAN = "resources/de-pos-perceptron.bin";
	
	private String BIN_TOKENIZER_MODEL_ENGLISH = "resources/en-token.bin";
	private String BIN_POS_MODEL_MAXENT_ENGLISH = "resources/en-pos-maxent.bin";
	private String BIN_POS_MODEL_PERCEPTRON_ENGLISH = "resources/en-pos-perceptron.bin";
	// config END
	
	private TokenizerModel tokModel;
	private POSModel posModel;
	
	/**
	 * You have to init this class by choosing a
	 * tokenizer, stemmer, posTagger and if you want to allow numbers as terms.
	 * Use the enums of this class for that purpose.
	 * @param tok
	 * @param stemmer
	 * @param pos
	 * @param allowNumbers
	 */
	public Tokenizing(UseTokenizer tok, UseStopwords stopwords, UseStemmer stemmer, UsePOSTagger pos, boolean allowNumbers) {
		this.usedTokenizer = tok;
		this.usedStopwords = stopwords;
		this.usedPosTagger = pos;
		this.usedStemmer = stemmer;
		this.allowNumbers = allowNumbers;
	}
	
	/**
	 * Tokenizes a main document and its chapters
	 * and saves the tokenized terms directly in the documents' objects.
	 * @param doc
	 * @param threads
	 * 		The actions will be executed by this number of threads.
	 * @return
	 * 		Returns a list of all unique terms of the main document.
	 */
	public List<String> tokenizeTermsThreadable(MinedMainDocument doc, int threads) {
		List<MinedDocument> list = new ArrayList<MinedDocument>();
		list.add(doc);
		
		if(doc.hasChapters()) {
			this.tokenizeTermsThreadable(doc.getChapters(), null, threads);
		}
		
		return this.tokenizeTermsThreadable(list, null, threads);
	}
	
	/**
	 * Tokenizes the given documents
	 * and saves the tokenized terms directly in the documents' objects.
	 * @param docs
	 * @param threads
	 * 		The actions will be executed by this number of threads.
	 * @return
	 * 		Returns a list of all unique terms of all documents.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> tokenizeTermsThreadable(List<MinedDocument> docs, List<String> allTerms, int threads) {
    	if(allTerms==null)
			allTerms = new ArrayList<String>();
    	
    	ExecutorService executor = Executors.newFixedThreadPool(threads);
    	List<Future> futures = new ArrayList<Future>();
    	for(MinedDocument doc : docs) {
    		if(doc!=null) {
	    		futures.add( executor.submit(new TokenizeCallable(
	    										doc.getText(), 
	    										this.getTokenizerInstance(doc.getLang()), 
	    										this.getStopwordsInstance(doc.getLang()), 
	    										this.getStemmerInstance(doc.getLang()), 
	    										this.getPosTaggerInstance(doc.getLang()),
	    										this.allowNumbers
	    									)
	    		));
    		} else {
    			futures.add(null);
    		}
    	}
    	
    	int i=0;
    	for(Future fut : futures) {
			if(fut!=null) {
	    		try {
					List<String> cleanedTokenizedTerms = (List<String>) fut.get();
					docs.get(i).setTerms(cleanedTokenizedTerms.toArray(new String[cleanedTokenizedTerms.size()]));
					
					for(String term : cleanedTokenizedTerms) {
						if (!allTerms.contains(term)) {
		                    allTerms.add(term);
		                }
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			i++;
        }
    	executor.shutdown();
    	
    	return allTerms;
    }
	
	/**
	 * Returns true if the given POS tag is an acceptable tag.
	 * 
	 * @param posTag
	 * @return
	 */
	public static boolean isAcceptablePosTag(String posTag) {
		switch(posTag) {
		case("NN"):			// noun
		case("VVFIN"): 		// verb
		case("VMFIN"): 		// verb
		case("VVPP"): 		// verb
		case("VVIZU"): 		// verb
		case("VMPP"): 		// verb
		case("ADJA"): 		// adj
		case("ADJD"): 		// adj
		case("NE"):			// name
		case("CARD"):		// number
			return true;
		default:
			return false;
		}
	}
	
	
	private Tokenizer getTokenizerInstance(String language) {
		Tokenizer tokenizer;
    	switch(this.usedTokenizer) {
    	case WITH_MODEL:
    		TokenizerModel tokModel = this.getTokenizerModel(language);
    		if(tokModel!=null) {
    			tokenizer = new TokenizerME(tokModel);
    			break;
    		} // else: uses SimpleTokenizer
    	case SIMPLE:
    	default:
    		tokenizer = SimpleTokenizer.INSTANCE;
    	}
    	
    	return tokenizer;
	}
	
	private TokenizerModel getTokenizerModel(String language) {
		if(this.tokModel!=null) {
			return tokModel;
		} else {
	    	InputStream modelIn = null;
	    	try {
	    		switch(language) {
	        	case "DE":
	        		modelIn = new FileInputStream(BIN_TOKENIZER_MODEL_GERMAN); break;
	        	case "EN":
	        	default:
	        		modelIn = new FileInputStream(BIN_TOKENIZER_MODEL_ENGLISH);
//		    		modelIn = new FileInputStream(getClass().getResource(BIN_TOKENIZER_MODEL_ENGLISH).getPath());
	    		}
	    		tokModel = new TokenizerModel(modelIn);
	    		return tokModel;
	    	} catch (IOException e) {
	    		System.out.println("Warning: Could not read de-token.bin! Using SimpleTokenizer instead.");
	    	} finally {
				if (modelIn != null) {
					try { 
						modelIn.close();
					} catch (IOException e) { }
				}
	    	}
	    	return null;
		}
	}
	
	private IStopwords getStopwordsInstance(String language) {
		IStopwords stopwords;
		switch(this.usedStopwords) {
		case STOPWORDS_LIST:
			switch(language) {
			case "DE":
				stopwords = new StopwordsGerman();
				break;
			case "EN":
			default:
				stopwords = new StopwordsEnglish();
			}
			break;
		case NONE:
		default:
			return null;
		}
		
		return stopwords;
	}
	
	private Stemmer getStemmerInstance(String language) {
		Stemmer stemmer;
    	switch(this.usedStemmer) {
    	case SNOWBALL:
    		switch(language) {
    		case "DE":
    			stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
    			break;
    		case "EN":
    		default:
    			stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
    		}
    		break;
    	case NONE:	
    	default:
    		return null;
    	}
    	
    	return stemmer;
	}
	
	private POSTagger getPosTaggerInstance(String language) {
		switch(this.usedPosTagger) {
		case NONE:
			return null;
		default:
			return new POSTaggerME(this.getPosModel(language));
		}
	}
	
	private POSModel getPosModel(String language) {
		if(this.posModel!=null) {
			return this.posModel;
		} else {
	    	InputStream modelIn = null;
	    	try {
	    		switch(this.usedPosTagger) {
	    		case MAXENT:
	    			switch(language) {
	    			case "DE":
	    				modelIn = new FileInputStream(BIN_POS_MODEL_MAXENT_GERMAN);
	    				break;
	    			case "EN":
	    			default:
	    				modelIn = new FileInputStream(BIN_POS_MODEL_MAXENT_ENGLISH);
	    			}
	    			break;
	    		case PERCEPTRON:
	    		default:
	    			switch(language) {
	    			case "DE":
	    				modelIn = new FileInputStream(BIN_POS_MODEL_PERCEPTRON_GERMAN);
		    			break;
	    			case "EN":
	    			default:
	    				modelIn = new FileInputStream(BIN_POS_MODEL_PERCEPTRON_ENGLISH);
	    			}
	    		}
	    		this.posModel = new POSModel(modelIn);
	    		return this.posModel;
	    	} catch (IOException e) {
	    		System.out.println("Warning: Could not read POS model binary! Can not use POS tagger.");
	    	} finally {
				if (modelIn != null) {
					try { 
						modelIn.close();
					} catch (IOException e) { }
				}
	    	}
	    	return null;
		}
	}
	
}
