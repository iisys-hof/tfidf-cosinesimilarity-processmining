package de.iisys.schub.processMining.similarity.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import de.iisys.schub.processMining.similarity.Tokenizing;
import de.iisys.schub.processMining.similarity.stopwords.IStopwords;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;

/**
 * Class to tokenize texts in threads.
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
@SuppressWarnings("rawtypes")
public class TokenizeCallable implements Callable {
	
	String doc;
	Tokenizer tok;
	IStopwords stopwords;
	Stemmer stemmer;
	POSTagger pos;
	boolean allowNumbers;
	
	public TokenizeCallable(String doc, Tokenizer tok, IStopwords stopwords, Stemmer stemmer, POSTagger pos, boolean allowNumbers) {
		this.doc = doc;
		this.tok = tok;
		this.stopwords = stopwords;
		this.stemmer = stemmer;
		this.pos = pos;
		this.allowNumbers = allowNumbers;
	}

	@Override
	public Object call() throws Exception {
		String[] allTokenizedTerms = tok.tokenize(doc);
		
		String posTags[] = null;
		if(pos != null) {
			posTags = pos.tag(allTokenizedTerms);
		}
		
		List<String> cleanedTokenizedTerms = new ArrayList<String>();
		
		int i = 0;
        for (String term : allTokenizedTerms) {

        	if(posTags!=null && !Tokenizing.isAcceptablePosTag(posTags[i]))
        		continue;
        	
        	if(!allowNumbers && isNumeric(term))
        		continue;
        	
        	if(term.length()>1 && (stopwords==null || !stopwords.isStopword(term)) ) {
        		if(stemmer!=null)
        			term = ((String) stemmer.stem(term));
        		
                cleanedTokenizedTerms.add(term.toLowerCase());
        	}
        	i++;
        }
		
		return cleanedTokenizedTerms;
	}

	private boolean isNumeric(String s) {  
	    return s.matches("[-+]?\\d*\\.?\\d+");  
	} 
}
