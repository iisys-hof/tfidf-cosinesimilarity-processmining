package de.iisys.schub.processMining.similarity.stopwords;

import java.util.Hashtable;

/**
 * Interface for stopword classes of different languages.
 * For own languages create your own StopwordsLanguage class which extends this abstract class.
 * To create stopwords with proper syntax (to use in the hashtable) you can use the StopwordGeneratorMain class.
 * Stopword lists can be found here: http://members.unine.ch/jacques.savoy/clef/
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
public abstract class IStopwords {
	
	protected Hashtable<String,Boolean> stopwords;
	protected boolean x;
	
	public IStopwords() {
		stopwords = new Hashtable<String,Boolean>();
		x = Boolean.TRUE; // just a placeholder
		this.init();
	}

	public boolean isStopword(String term) {
		return stopwords.containsKey(term.toLowerCase());
	}

	protected abstract void init();
}
