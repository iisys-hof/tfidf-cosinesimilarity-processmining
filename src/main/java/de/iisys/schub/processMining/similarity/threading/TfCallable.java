package de.iisys.schub.processMining.similarity.threading;

import java.util.List;
import java.util.concurrent.Callable;

import de.iisys.schub.processMining.similarity.TfIdf;
import de.iisys.schub.processMining.similarity.model.MinedDocument;

/**
 * Class to calculate tfs in threads.
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
public class TfCallable implements Callable {
	
	private MinedDocument doc;
	private List<String> allterms;
	
	public TfCallable(MinedDocument doc, List<String> allterms) {
		this.doc = doc;
		this.allterms = allterms;
	}

	@Override
	public Object call() throws Exception {			
		double[] tfidfs = new double[this.allterms.size()];
		if(doc!=null) {
			int i=0;
			for(String term : this.allterms) {
				tfidfs[i++] = TfIdf.tfCalculator(doc.getTerms(), term);
			}
		} else {
			System.out.println("DEBUG| TfCallable: doc is null!!!");
		}
		return tfidfs;
	}

}
