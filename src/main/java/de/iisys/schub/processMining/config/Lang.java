package de.iisys.schub.processMining.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Language enum for tasks.
 * Use it this way: Lang.ABOUT.getValue();
 * Find the language file in src/main/resources.
 * 
 * @author Christian
 *
 */
public enum Lang {
	// message tasks:
	ABOUT("about"),
	TALK_TO_SUPERIOR("talk_to_superior"),
	TALK_TO_SUBORDINATE("talk_to_subordinate"),
	TALK_TO_COLLEAGUE("talk_to_colleague"),
	TALK_TO_DEPARTMENT_EMPLOYEE("talk_to_department_employee"),
	TALK_TO_ROLE("talk_to_role");
	
	
	private static final String PROPERTIES_FILE = "language.properties";
	
	private static Properties props;
	/*
	static {
		props = new Properties();
		try {
			props.load(new FileInputStream(PROPERTIES_FILE));
		} catch(IOException ioe) {
			throw new RuntimeException("Error while loading config file.");
		}
	} */
	
	// works in maven jar file (e.g. as shindig lib):
	static {
		props = new Properties();
		try {
			props.load(ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE));
		} catch(IOException ioe) {
			throw new RuntimeException("Error while loading config file.");
		}
	}
	
	private String key;
	
	Lang(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return props.getProperty(key);
	}
}
