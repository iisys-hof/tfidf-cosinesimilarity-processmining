package de.iisys.schub.processMining.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration.
 * Use it this way: Config.LIFERAY_URL.getValue();
 * Find the config file in src/main/resources.
 * 
 * @author Christian Ochsenkühn
 *
 */
public enum Config  {
	// common:
	THREADS("threads"),
	// NLP:
	TOKENIZER("tokenizer"),
	STOPWORDS("stopwords"),
	STEMMER("stemmer"),
	POS_TAGGER("posTagger"),
	ALLOW_NUMBER_AS_TERM("allowNumberAsTerm"),
	// output:
	OUTPUT_FILE("outputFile"),
	PERCENTILE("percentile"),
	SEND_TO_CAMUNDA("send_to_camunda"),
	// network:
	CAMUNDA_URL("camunda_url"),
	ELASTICSEARCH_URL("elasticsearch_url"),
	ELASTICSEARCH_INDEX("elasticsearch_index"),
	LIFERAY_URL("liferay_url"),
	NUXEO_URL("nuxeo_url"),
	SHINDIG_URL("shindig_url"),
	SHINDIG_USAGE("shindig_usage");
	
	private static final String PROPERTIES_FILE = "config.properties";
	
	public static final String VAL_SHINDIG_DIRECT = "DIRECT";
	
	private static Properties props;
	/*
	// older version for standalone:
	static {
		props = new Properties();
		try {
			props.load(new FileInputStream(PROPERTIES_FILE));
		} catch(IOException ioe) {
			throw new RuntimeException("Error while loading config file.");
		}
	} */
	
	// works with maven project and in maven jar file (e.g. as shindig library):
	static {
		props = new Properties();
		try {
			props.load(ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE));
		} catch(IOException ioe) {
			throw new RuntimeException("Error while loading config file.");
		}
	}
	
	private String key;
	
	Config(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return props.getProperty(key);
	}
}

