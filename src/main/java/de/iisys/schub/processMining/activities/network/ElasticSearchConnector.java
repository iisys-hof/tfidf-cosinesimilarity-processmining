package de.iisys.schub.processMining.activities.network;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.iisys.schub.processMining.config.Config;

/**
 * Connector for all ElasticSearch connections.
 * Use the config.properties to configure the ElasticSearch URL and the name of the ElasticSearch index.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class ElasticSearchConnector {
	
	public static final String EMAIL = "email";
	public static final String FROM = "from";
	public static final String HITS = "hits";
	public static final String ID = "uid";
	public static final String SOURCE = "_source";
	public static final String SUBJECT = "subject";
	public static final String TEXTCONTENT = "textContent";
	public static final String TO = "to";
	
	private static String ES_URL = Config.ELASTICSEARCH_URL.getValue();
	private static String ES_INDEX = Config.ELASTICSEARCH_INDEX.getValue();
	
		
	/**
	 * Returns all mails between the given dates.
	 * @param emails
	 * @param from
	 * @param until
	 * @return
	 */
	public static JSONArray getEmails(Set<String> emails, Date from, Date until) {
		if(from==null || until==null)
			return null;
		else
			return getEmails(emails, from.getTime(), until.getTime());
	}
	/**
	 * Returns all mails between the given dates (number of milliseconds since January 1, 1970, 00:00:00 GMT).
	 * @param emails
	 * @param from
	 * @param until
	 * @return
	 */
	public static JSONArray getEmails(Set<String> emails, long from, long until) {
		if(emails.size()<1)
			return null;
		
		String contextPath = "/_search?type=mail&size=25";
		
		JSONArray andObjects;
		JSONArray orObjects = new JSONArray();
		for(String email : emails) {
			String[] emailParts = email.split("@");
			if(emailParts.length<2)
				continue;
			
			andObjects = new JSONArray()
				.put(new JSONObject()
					.put("term", new JSONObject()
						.put("from.email", emailParts[0])
					)
				)
				.put(new JSONObject()
					.put("term", new JSONObject()
						.put("from.email", emailParts[1])
					)
				);
			
			orObjects.put(new JSONObject()
				.put("and", andObjects)	
			);
		}
		
		
		
		JSONObject query = new JSONObject();
		query.put("query", new JSONObject()
			.put("filtered", new JSONObject()
				.put("query", new JSONObject()
					.put("match_all", new JSONObject())
				)
				.put("filter", new JSONObject()
					.put("bool", new JSONObject()
						.put("must", new JSONArray()
							// 1st must-filter:
							.put(new JSONObject()
								.put("range", new JSONObject()
									.put("sentDate", new JSONObject()
										.put("gte", from)
										.put("lte", until)
									)
								)
							)
							// 2nd must-filter:
							.put(new JSONObject()
								.put("term", new JSONObject()
									.put("folderFullName", "INBOX")
								)
							)
							// 3rd must-filter:
							.put(new JSONObject()
								.put("nested", new JSONObject()
									.put("path", "from")
									.put("filter", new JSONObject()
										.put("or", orObjects)
									)
//									.put("_cache", false)
								)
							)
						)
					)
				)
			)
		);
		
		try {
			URL url = new URL(ES_URL+"/"+ES_INDEX+contextPath);
			String result = HttpUtil.sendRequest("GET", url, query.toString());
			
			if(result==null)
				return null;
			
//			System.out.println(result);			
			JSONObject resultJson = new JSONObject(result);		
			return resultJson.getJSONObject(HITS).getJSONArray(HITS);
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Only for testing purposes.
	 * @return
	 */
	public static JSONArray test() {
		System.out.println("ElasticSearchConnector.test()");
		String contextPath = "/_search?type=mail";
		String email = "baerbel.bitte@broton.de";
		
		String email1stPart = email.split("@")[0];
		System.out.println("1st: "+email1stPart);
		String email2ndPart = email.split("@")[1];
		System.out.println("2nd: "+email2ndPart);
		
		
		JSONObject query = new JSONObject();
		query.put("query", new JSONObject()
			.put("filtered", new JSONObject()
				.put("query", new JSONObject()
					.put("match_all", new JSONObject())
				)
				.put("filter", new JSONObject()
					.put("bool", new JSONObject()
						.put("must", new JSONArray()
							.put(new JSONObject()
								.put("range", new JSONObject()
									.put("sentDate", new JSONObject()
										.put("gte", 1436789763000L)
										.put("lte", 1436789967000L)
									)
								)
							)
							// second must-filter
							.put(new JSONObject()
								.put("term", new JSONObject()
									.put("folderFullName", "INBOX")
								)
							)
							// 3rd must-filter
							.put(new JSONObject()
								.put("nested", new JSONObject()
									.put("path", "from")
									.put("filter", new JSONObject()
										.put("bool", new JSONObject()
											.put("must", new JSONArray()
												.put(new JSONObject()
													.put("term", new JSONObject()
														.put("from.email", email1stPart)
													)
												)
												.put(new JSONObject()
													.put("term", new JSONObject()
														.put("from.email", email2ndPart)
													)
												)
											)
										)
									)
//									.put("_cache", false)
								)
							)
						)
					)
				)
			)
		);

		/*
		JSONObject query = new JSONObject();
		query.put("query", new JSONObject()
			.put("filtered", new JSONObject()
				.put("query", new JSONObject()
					.put("match_all", new JSONObject())
				)
				.put("filter", new JSONObject()
					.put("nested", new JSONObject()
						.put("path", "from")
						.put("filter", new JSONObject()
//							.put("bool", new JSONObject()
//								.put("must", new JSONArray()
//									.put(new JSONObject()
										.put("term", new JSONObject()
											.put("from.email", email)
										)
//									)
//								)
//							)
						)
					)
				)
			)
		); */
		
		System.out.println("\n"+query.toString()+"\n");
		
		try {
			URL url = new URL(ES_URL + "/" + ES_INDEX + contextPath);
			String result = HttpUtil.sendRequest("GET", url, query.toString());
			
			System.out.println("RESULT:\n"+result);
//			JSONObject resultJson = new JSONObject(result);		
//			return resultJson.getJSONObject(HITS).getJSONArray(HITS);
			return null;
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Do not use!
	 * Returns ALL emails from the configured ElasticSearch index.
	 * @return
	 */
	public static JSONArray getAllEmails() {
		String contextpath = "/_search?type=mail";
		
		try {
			URL url = new URL(ES_URL + contextpath);
			String result = HttpUtil.sendRequest("GET", url, null);
			
			JSONObject resultJson = new JSONObject(result);		
			return resultJson.getJSONObject(HITS).getJSONArray(HITS);
			
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	// helper:
	
	/**
	 * Returns hit i from the given hits list.
	 * @param jHits
	 * @param i
	 * @return
	 */
	public static JSONObject getHitFromHitsList(JSONArray jHits, int i) {
		return jHits.getJSONObject(i);
	}
	
	/**
	 * Returns the senderId from the given hit.
	 * @param jHit
	 * @return
	 */
	public static String getSenderMailFromHit(JSONObject jHit) {
		return jHit.getJSONObject(ElasticSearchConnector.SOURCE)
				.getJSONObject(ElasticSearchConnector.FROM).getString(ElasticSearchConnector.EMAIL);
	}
	
	/**
	 * Returns the receiverId from the given hit.
	 * @param jHit
	 * @param i
	 * @return
	 */
	public static String getReceiverMailFromHit(JSONObject jHit, int i) {
		return jHit.getJSONObject(ElasticSearchConnector.SOURCE)
				.getJSONArray(ElasticSearchConnector.TO).getJSONObject(i).getString(ElasticSearchConnector.EMAIL);
	}
}
