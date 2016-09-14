package de.iisys.schub.processMining.similarity.threading;

import java.util.List;
import java.util.concurrent.Callable;

import de.iisys.schub.processMining.similarity.TfIdf;
import de.iisys.schub.processMining.similarity.model.MinedDocument;

/**
 * Class to calculate idfs in threads.
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
public class IdfCallable implements Callable {
	
	private String term;
	private List<MinedDocument> allDocs;
	
	public IdfCallable(String term, List<MinedDocument> allDocs) {
		this.term = term;
		this.allDocs = allDocs;
	}

	@Override
	public Object call() throws Exception {
		return TfIdf.idfCalculator(allDocs, term);
	}

}
