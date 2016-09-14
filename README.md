# Tf-Idf Cosine Similarity

## Description
This application calculates the similarity between a main document (usually .docx) and other documents.
It calculates the *inverse document frequency* (idf) of all documents' terms and the *term frequency* (tf) for each single document (and for the chapters of the main document).
With these results a tf-idf vector is created for each document. (for each term: tf-idf = tf * idf).
After that, the cosine similarity between two documents' tf-idf vectors are calculated, in order to compare the two documents.
### Steps
To get the similarity the following steps are executed:
1. **Parsing** the main document (, its chapters) and the other documents and detecting their language.
2. **Tokenizing** the documents (incl. stemming, stopword removal and optionally using a POS tagger).
3. **Creating Tf-Idf vectors** for all documents (and chapters).
4. **Calculating the cosine similarities** between the main document (and its chapters) and the other documents.
5. Currently: saving the results in an output file.

## Using the .jar
Simple Usage:

* Build a jar file using maven
* Put main document "mainDoc.docx" in the same folder as the jar file.
* Put texts to compare in the folder "docs" (.txt, .html, .doc, .docx files)
* In cmd or bash: **java -jar cosineSimilarity.jar**
* Similarities can be found in the file output.txt

Configuration:
* Optionally edit the configuration in `config.properties`

Version 0 (default):
* **java -jar cosineSimilarity 0**
* Shows all cosine similarities in the output file.

Version 1:
* **java -jar cosineSimilarity 1**
* Shows the similar documents (cosine >= [percentile](https://en.wikipedia.org/wiki/Percentile)) of the main document and similar terms between those two

Using your own main document name:
* **java -jar cosineSimilarity.jar version relativePathToMainDoc**

Using your own main document and docs folder:
* **java -jar cosineSimilarity.jar version relativePathToMainDoc relativePathToDocs**
* Example: `java -jar cosineSimilarity.jar 1 collaborativeDoc.docx otherDocsFolder`


## Development

### Used code, libraries or other stuff
* Library: [Apache Commons Math 3](http://commons.apache.org/proper/commons-math/)
  * What: Mathematics Library
  * Where: Used to calculate the percentile, arithmetic mean, etc.
  * License: [Apache License 2.0](http://www.apache.org/licenses/)
* Library: [Apache OpenNLP Tools](http://opennlp.apache.org/)
  * What: Machine learning based toolkit for the processing of natural language text.
  * Where: Used for tokenizing, stemming and part-of-speech tagging.
  * License: [Apache License 2.0](http://www.apache.org/licenses/)
* Library: [Apache POI Word](https://poi.apache.org/)
  * What: Java API for Microsoft Documents
  * Where: Used XWPF and HWPF to parse Microsoft Word files (docx, doc).
  * License: [Apache License 2.0](http://www.apache.org/licenses/)
* Library: [Apache Tika Core](https://tika.apache.org/)
  * What: Content Analysis Toolkit
  * Where: Used to detect the languages of the documents.
  * License: [Apache License 2.0](http://www.apache.org/licenses/)
* Library: [jsoup 1.8.3](http://jsoup.org/)
  * What: Java Html Parser
  * Where: Used to parse html files.
  * License: [MIT License](http://jsoup.org/license)
* Other: [IR Multilingual Resources at UniNE](http://members.unine.ch/jacques.savoy/clef/)
  * Where: Used the German and English stopword lists.
  * License: [BSD License](http://opensource.org/licenses/bsd-license.html)
* Code [Shrestha Mubin](http://computergodzilla.blogspot.de/2013/07/how-to-calculate-tf-idf-of-document.html)
  * What: Code example for tf-idf and cosine similarity.
  * Where: Used the methods to calculate the tf, idf and the cosine similarity.
  
### Using in an IDE (e.g eclipse)
* Put the project in your IDE (import...)
* You need the "docs" and resources (/target/resources) folder in your main/classpath folder (next to "src" etc.).
* Use mainDoc.docx as your main document (or change the MiningMain class).
* Start the mining via the main method in the MiningMain class.

Export:
* If you export the app as a runnable jar, you should **extract the required libraries into the generated jar** (should be an option in your IDE), to increase the parsing speed.

### Configurations
class AlgoController:
* PROPERTIES_FILE: the path to your properties file (e.g. config.properties)

**config.properties:**
* threads: number of threads you want to use for tokenizing and tf-idf calculating
* tokenizer: choose a tokenizer
  * Possibilities: `simple`, `with_model`
  * "with_model" currently supports German and English tokenizing (see resource folder).
  * If you want to add your own model: edit class 'Tokenizing' and add [a model](http://opennlp.sourceforge.net/models-1.5/) to the resouce folder.
* stopwords: choose a stopword list
  * Possibilities: `none`, `stopwords_list`
  * "stopwords_list" currently supports German and English [stopword lists](http://members.unine.ch/jacques.savoy/clef/).
  * If you want to use you own stopwords list: follow the instructions in the classes "IStopwords" and "StopwordGeneratorMain".
* stemmer: choose a stemmer
  * `none`, `snowball`
  * "snowball" currently supports the German and English [snowball stemmer](http://snowball.tartarus.org/texts/introduction.html).
  * If you want to add your own stemmer: edit class 'Tokenizing' and add the out-of-the-box snowball implementations of OpenNLP.
* posTagger: choose a part-of-speech tagger
  * `none` (currently recommended), `maxent`, `perceptron`
  * "maxent" currently supports the German and English maxent implementations
  * "perceptron" currently supports the German and English maxent implementations
  * If you want to add your own model: edit class 'Tokenizing' and add [a model](http://opennlp.sourceforge.net/models-1.5/) to the resouce folder.
  * Important: adjust the 'isAcceptablePosTag' method in class 'Tokenizing' if you want to use a pos tagger.
* allowNumberAsTerm: `true` if you want to allow numbers as terms (otherwise: `false`)
* outputFile: the file to save the output; e.g. `output.txt`
* percentile: the default percentile (in percent); e.g. `85`

class Tokenizing:
* e.g. BIN_TOKENIZER_MODEL_ENGLISH: Here you can change the paths for the tokenizer and pos model binaries.
* Binaries can be downloaded here: [OpenNLP](http://opennlp.sourceforge.net/models-1.5/)