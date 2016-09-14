package de.iisys.schub.processMining.similarity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for a main document whose chapters also should be analyzed.
 * It contains a list of chapters which are implemented as MinedDocument objects.
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
public class MinedMainDocument extends MinedDocument {

	private List<MinedDocument> chapters;
	
	public MinedMainDocument(String name, String text) {
		super(name, text);
		chapters = new ArrayList<MinedDocument>();
	}

	public List<MinedDocument> getChapters() {
		return chapters;
	}

	public void setChapters(List<MinedDocument> chapters) {
		this.chapters = chapters;
	}
	
	public void addChapter(MinedDocument chapter) {
		this.chapters.add(chapter);
	}
	
	public void addChapters(List<String> names, List<String> texts) {
		if(names!=null && texts!=null && names.size() == texts.size()) {
			for(int i=0; i<names.size(); i++) {
				this.chapters.add(new MinedDocument(names.get(i), texts.get(i)));
			}
		}
	}
	
	public MinedDocument getChapter(int i) {
		if(i < chapters.size())
			return this.chapters.get(i);
		else
			return null;
	}
	
	public boolean hasChapters() {
		if(this.chapters.size() > 0)
			return true;
		else
			return false;
	}
	
	public int chapterCount() {
		return this.chapters.size();
	}
}
