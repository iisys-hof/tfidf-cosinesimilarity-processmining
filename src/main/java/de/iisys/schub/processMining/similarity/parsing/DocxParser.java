package de.iisys.schub.processMining.similarity.parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/**
 * Class to parse docx and doc files.
 * For just getting the text of a doc file, you can use the static method "parseDocSimple".
 * For parsing a docx file, you have to create an instance of this class.
 * 
 * Uses the Apache POI library for Word (https://poi.apache.org/).
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
public class DocxParser {
	
	private final String HEADING1_EN = "heading1";
	private final String HEADING1_DE = "berschrift1";
	
	private final List<String> CHAPTER_STYLES;
	
	private String path;
	private XWPFDocument theDoc;
	private String fullText;
	private List<String> chapterTexts;
	private List<String> chapterHeadlines;
	
	private List< List<IBodyElement> > chapters;
	
	private DocxParser() {
		CHAPTER_STYLES = new ArrayList<String>();
		CHAPTER_STYLES.add(HEADING1_DE);
		CHAPTER_STYLES.add(HEADING1_EN);
	}
	
	public DocxParser(String filePath) {
		this();
		initParser(filePath);
	}
	
	public DocxParser(InputStream is) {
		this();
		initParser(is);
	}
	
	public void initParser(String filePath) {
		this.path = filePath;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			this.initParser(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void initParser(InputStream is) {
		theDoc = readFile(is);
	}
	
	private XWPFDocument readFile(InputStream is) {
		if(is != null) {
			try {
				XWPFDocument doc = new XWPFDocument(OPCPackage.open(is));
				is.close();
				return doc;
			} catch (InvalidFormatException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Parses the given .doc Word file and returns its text.
	 * @param filePath
	 * @return
	 */
	public static String parseDocSimple(String filePath) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(filePath);
			String text = DocxParser.parseDocSimple(fis);
			fis.close();
			return text;
		} catch (FileNotFoundException e) {
			System.out.println("DocxParser: File not found at "+filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses the given .doc Word file and returns its text.
	 * @param is
	 * @return
	 */
	public static String parseDocSimple(InputStream is) {
		try {
			WordExtractor extr = new WordExtractor(is);
			String text = extr.getText();
			extr.close();
			return text;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Only use this method if you don't want to get chapters sometimes.
	 * Otherwise use 'parseDocxAndChapters' and 'getFullText' methods.
	 * 
	 * Parses a .docx Word file and returns its text.
	 * @return
	 * 		Returns the full text (incl. tables) as string.
	 */
	public String parseDocxSimple() {
		if(theDoc!=null) {
			XWPFWordExtractor extr = new XWPFWordExtractor(theDoc);
			this.fullText =  extr.getText();
			try {
				extr.close();
			} catch (IOException e) { e.printStackTrace(); }
		}
		return this.fullText;
	}
	
	/**
	 * Parses the .docx Word file. After that, you can get its full text and title.
	 * And also its chapters and their names and texts.
	 */
	public void parseDocxAndChapters() {	
		if(theDoc!=null) {
			this.chapters = new ArrayList<List<IBodyElement>>();
			this.chapterTexts = new ArrayList<String>();
			this.chapterHeadlines = new ArrayList<String>();
			this.chapterHeadlines.add(0, "0:");
			StringBuffer buf = new StringBuffer();
			
			List<IBodyElement> tempChapter = new ArrayList<IBodyElement>();
			StringBuffer chapBuf = new StringBuffer();
			
			String tempText = "";
			
			int chapterCount=1;
			for(IBodyElement el : theDoc.getBodyElements()) {
				
				switch(el.getElementType().toString().toLowerCase()) {
				case("paragraph"):
					tempText = ((XWPFParagraph)el).getText();
				
					if( CHAPTER_STYLES.contains(((XWPFParagraph)el).getStyle()) ) {
						// if first chapter & first paragraph & headline
						if(chapterCount==1 && chapBuf.length()==0) {
							// add headline for first chapter
							chapterHeadlines.add(0, (chapterCount)+": '"+tempText+"'");
						} else {
							// save former chapter:
							chapters.add(tempChapter);
							chapterTexts.add(chapBuf.toString());
							buf.append(chapBuf);
							
							// prepare for next chapter:
							tempChapter = new ArrayList<IBodyElement>();
							chapBuf = new StringBuffer();
							// add headline for next/following chapter
							chapterHeadlines.add( (chapterCount)+": '"+tempText+"'");
						}
						chapterCount++;
					}
					break;
				case("table"):
					tempText = ((XWPFTable)el).getText();
					break;
				default:
					tempText = "";
				}
				
				chapBuf.append(tempText+" ");
				tempChapter.add(el);
			}
			// add last chapter:
			chapters.add(tempChapter);
			chapterTexts.add(chapBuf.toString());
			
			this.fullText = buf.toString();
		}
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 * 		Returns a list with the fulltext as first string 
	 * 		and the chapters' texts afterwards.
	 */
	public List<String> getFullTextAndChapterTexts() {	
		List<String> list = new ArrayList<String>();
		list.add(fullText);
		list.addAll(this.chapterTexts);
		return list;
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 */
	public List<String> getChapterHeadlines() {
		return this.chapterHeadlines;
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 */
	public List<String> getChapterTexts() {
		return this.chapterTexts;
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 * 		Returns the full text (incl. tables) as string.
	 */
	public String getFullText() {
		return this.fullText;
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 */
	public List<XWPFParagraph> getParagraphs() {
		return ((XWPFDocument)theDoc).getParagraphs();
	}
	
	/**
	 * First use 'parseDocxAndChapters' method.
	 * @return
	 */
	public String getTitle() {
		String title = theDoc.getProperties().getCoreProperties().getTitle();
		
		if(title!=null)
			return title;
		else if(this.path != null)
			return this.path;
		else
			return "";
	}
}
