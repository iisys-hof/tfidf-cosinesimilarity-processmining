package de.iisys.schub.processMining.similarity.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.language.LanguageIdentifier;
import org.jsoup.Jsoup;

/**
 * Class to parse files and extract the plain texts.
 * Uses jsoup to parse html-files (http://jsoup.org/, License: MIT).
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
public class TextParser {
	
	private DocxParser docxParser;
	
	private List<String> fileNames;
	private List<String> fileTexts;
	
	/**
	 * Parses all files of a given path.
	 * @param filesPath
	 */
	public TextParser(String filesPath) {		
		fileNames = new ArrayList<String>();
		try {
			fileTexts = this.parseFiles(filesPath);
		} catch (FileNotFoundException e) {
			System.out.println("Files not found at given path "+filesPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns all parsed texts as list.
	 * @return
	 */
	public List<String> getTexts() {
		return this.fileTexts;
	}
	
	/**
	 * Returns the filenames of all parsed texts as list.
	 * @return
	 */
	public List<String> getFileNames() {
		return this.fileNames;
	}
	
	public static String checkLanguage(String text) {
		LanguageIdentifier langIdf = new LanguageIdentifier(text);
		return langIdf.getLanguage().toUpperCase();
	}
	
	private List<String> parseFiles(String filePath) throws FileNotFoundException, IOException {
    	File[] allFiles = new File(filePath).listFiles();
    	if(allFiles==null) {
    		allFiles = new File[1];
    		allFiles[0] = new File(filePath);
    	}
        List<String> docTexts = new ArrayList<String>();
        BufferedReader in = null;
        for (File f : allFiles) {
        	fileNames.add(f.getName());
        	if(f.getName().endsWith(".html")) {			// HTML
        		docTexts.add(this.parseHtml(f));
        	} else if(f.getName().endsWith(".docx")) {	// DOCX
        		docTexts.add(this.parseDocx(f));
        	} else if(f.getName().endsWith(".doc")) {	// DOC
        		docTexts.add(this.parseDoc(f));
        	} else { 									// ELSE: TXT, NONE,...
            	docTexts.add(this.parseTxt(f, in));
            } 
        }
        if(fileNames.size() > 0)
        	return docTexts;
        else
        	return null;
    }
	
	private String parseTxt(File f, BufferedReader in) throws IOException, FileNotFoundException {
    	in = new BufferedReader(
    			new InputStreamReader(
    					new FileInputStream(f)
    					, "UTF8")
    			);
    			
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = in.readLine()) != null) {
            sb.append(" "+s);
        }
        in.close();
        return sb.toString();
	}
	
	private String parseHtml(File f) throws IOException {
		org.jsoup.nodes.Document html = Jsoup.parse(f, "UTF8");
		return html.body().text();
	}
	
	private String parseDocx(File f) {
		docxParser = new DocxParser(f.getPath());
		return docxParser.parseDocxSimple();
	}
	
	private String parseDoc(File f) {
		return DocxParser.parseDocSimple(f.getPath());
	}
	
	
	public static String parseHtml(String html) {
		org.jsoup.nodes.Document jsoup = Jsoup.parse(html);
		return jsoup.body().text();
	}
}
