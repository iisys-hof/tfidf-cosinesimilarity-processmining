package de.iisys.schub.processMining.similarity.stopwords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This class can be used if you want to fill a hashtable/hashmap 
 * with your own stopwords in a stopwords class.
 * Just use the main method!
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
public class StopwordGeneratorMain {
	
	
	public static void main(String[] args) {
		String stopwordFile = "stopwords.txt";
		if(args.length>0)
			stopwordFile = args[0];
		
		StopwordGeneratorMain gen = new StopwordGeneratorMain();
		try {
			gen.formatStopWords(stopwordFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	
	/**
     * Creates a formatted list of stopwords, which you have to put in hashtable of your stopwords class.
     * Format: stopwords.put("term", x);
     * The formatted list is saved in "inputfileName_formatted.txt".
     * @param stopwordListFile
     * 		A textfile with each stopword in a separate line.
     * @throws IOException
     */
    public void formatStopWords(String stopwordListFile) throws IOException {
    	File f = new File(stopwordListFile);

        BufferedReader in = null;
    	in = new BufferedReader(
    			new InputStreamReader(
    					new FileInputStream(f)
    					, "UTF8")
    			);
    			
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = in.readLine()) != null) {
            sb.append("stopwords.put(\""+s+"\", x);"+"\n");
        }
        in.close();
        
        Writer out;
        try {
    		out = new BufferedWriter(new OutputStreamWriter(
            	    new FileOutputStream("stopwords_formatted.txt"), "UTF-8"));
    	    try {
    	    	out.write(sb.toString());
    	    	System.out.println("Successfully saved stopwords to stopwords_formatted.txt!");
    	    } finally {
    	    	out.close();
    	    }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
