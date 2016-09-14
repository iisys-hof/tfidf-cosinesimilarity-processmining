package de.iisys.schub.processMining.activities.network;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.iisys.schub.processMining.config.Config;

/**
 * Connector for all Shindig REST connections.
 * Use the config.properties to configure the Shindig URL.
 *
 * You can use it static. Every call will receive a new object from shindig.
 * You can use its object. Only one call goes to shindig. Other calls use the received object (when possible).
 * 
 * @author Christian Ochsenkühn
 *
 */
public class ShindigRESTConnector extends ShindigConnector {
	
	private static String SHINDIG_URL = Config.SHINDIG_URL.getValue();
	private static String API_PATH = "/social/rest";
	
	private JSONObject userJson;
	
	public ShindigRESTConnector() {}
	
	public ShindigRESTConnector(String userId) {
		this.userJson = getUser(userId);
	}

	/**
	 * Returns the user with the given userId as json object.
	 * @param userId
	 * @return
	 */
	private static JSONObject getUser(String userId) {
		String contextPath = "/people/"+userId;
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			String role = HttpUtil.sendRequest("GET", url, null);
			
			JSONObject userJson = new JSONObject(role);

			return userJson;
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// user role:
	
	public String getUserRole() {
		return getUserRole(this.userJson);
	}

	/**
	 * Returns the organization role of the user with the given userId.
	 */
	public String getUserRole(String userId) {
		if(this.userJson!=null && this.userJson.getJSONObject(ENTRY).get(ID).equals(userId))
			return getUserRole(this.userJson);
		else
			return _getUserRole(userId);
	}
	
	/**
	 * Returns the organization role of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public static String _getUserRole(String userId) {
		JSONObject userJson = getUser(userId);
		return getUserRole(userJson);
	}
	
	/**
	 * Returns the organization role of the given json user object.
	 * @param userJson
	 * @return
	 */
	private static String getUserRole(JSONObject userJson) {
		try {
			if(userJson!=null)
				return userJson.getJSONObject(ENTRY)
						.getJSONArray(PERSON_ORGANIZATIONS).getJSONObject(0).getString(TITLE);
			else
				return null;
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// user department:
	
	public String getUserDepartment() {
		return getUserDepartment(this.userJson);
	}
	
	/**
	 * Returns the department of the user with the given userId.
	 */
	public String getUserDepartment(String userId) {
		if(this.userJson!=null && this.userJson.getJSONObject(ENTRY).get(ID).equals(userId))
			return getUserDepartment(this.userJson);
		else
			return _getUserDepartment(userId);
	}
	
	/**
	 * Returns the department of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public static String _getUserDepartment(String userId) {
		JSONObject userJson = getUser(userId);
		return getUserDepartment(userJson);
	}
	
	/**
	 * Returns the department of the given json user object.
	 * @param userJson
	 * @return
	 */
	private static String getUserDepartment(JSONObject userJson) {
		try {
			if(userJson!=null)
				return userJson.getJSONObject(ENTRY)
						.getJSONArray(PERSON_ORGANIZATIONS).getJSONObject(0).getString(PERSON_ORGANIZATIONS_DEPARTMENT);
			else
				return null;
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// is department head:
	
	public boolean userIsDepartmentHead() {
		return userIsDepartmentHead(this.userJson);
	}
	
	/**
	 * Returns true if the user with the given userId is the head of his department.
	 */
	public boolean userIsDepartmentHead(String userId) {
		if(this.userJson!=null && this.userJson.getJSONObject(ENTRY).get(ID).equals(userId))
			return userIsDepartmentHead(this.userJson);
		else
			return _userIsDepartmentHead(userId);
	}
	
	/**
	 * Returns true if the user with the given userId is the head of his department.
	 * @param userId
	 * @return
	 */
	public static boolean _userIsDepartmentHead(String userId) {
		JSONObject userJson = getUser(userId);
		return userIsDepartmentHead(userJson);
	}
	
	/**
	 * Returns true if the given user is the head of his department.
	 * @param userJson
	 * @return
	 */
	@SuppressWarnings("null")
	private static boolean userIsDepartmentHead(JSONObject userJson) {
		try {
			if(userJson!=null)
				return userJson.getJSONObject(ENTRY)
						.getJSONArray(PERSON_ORGANIZATIONS).getJSONObject(0)
						.getBoolean(PERSON_ORGANIZATIONS_DEPARTMENTHEAD);
			else
				return (Boolean) null;
		} catch(JSONException e) {
			e.printStackTrace();
			return (Boolean) null;
		}
	}
	
	// user manager id:
	
	public String getUserManagerId() {
		return getUserManagerId(this.userJson);
	}
	
	/**
	 * Returns the userId of the manager of the user with the given userId.
	 */
	public String getUserManagerId(String userId) {
		if(this.userJson!=null && this.userJson.getJSONObject(ENTRY).get(ID).equals(userId))
			return getUserManagerId(this.userJson);
		else
			return _getUserManagerId(userId);
	}
	
	/**
	 * Returns the userId of the manager of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public static String _getUserManagerId(String userId) {
		JSONObject userJson = getUser(userId);
		return getUserManagerId(userJson);
	}
	
	/**
	 * Returns the userId of the manager of the given user.
	 * @param userJson
	 * @return
	 */
	private static String getUserManagerId(JSONObject userJson) {
		try {
			if(userJson!=null)
				return userJson.getJSONObject(ENTRY)
						.getJSONArray(PERSON_ORGANIZATIONS).getJSONObject(0)
						.getString(PERSON_ORGANIZATIONS_MANAGERID);
			else
				return null;
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// outbox messages:
	
	/**
	 * Returns a single outbox message with the given msgId.
	 */
	public JSONObject getOutboxMessage(String userId, String msgId) {
		return _getOutboxMessage(userId, msgId);
	}
	 /**
	  * Returns a single outbox message with the given msgId.
	  * @param userId: id of the message sender
	  * @param msgId
	  * @return
	  */
	public static JSONObject _getOutboxMessage(String userId, String msgId) {
		String contextPath = "/messages/"+userId+"/@outbox"+
				"?filterBy=id&filterOp=contains&filterValue="+msgId;
		
		String msg = "";
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			msg = HttpUtil.sendRequest("GET", url, null);
			
			JSONObject msgJson = new JSONObject(msg);
			return msgJson.getJSONArray("list").getJSONObject(0);
			
		} catch (MalformedURLException me) {
			me.printStackTrace();
			return null;
		} catch(JSONException e) {
			e.printStackTrace();
			System.out.println("result: "+msg);
			return null;
		}
	}
	
	/**
	 * Only for testing purposes.
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static JSONObject getActivity(String userId, String activityId) {
		String contextPath = "/activitystreams/"+userId+
				"?filterBy=id&filterOp=contains&filterValue="+activityId;
		
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			String activity = HttpUtil.sendRequest("GET", url, null);
			
			JSONObject activityJson = new JSONObject(activity);
			return activityJson.getJSONArray("list").getJSONObject(0);
			
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the organization hierarchy between user1 and user2.
	 */
	public JSONObject getOrgaHierarchy(String userId1, String userId2) {
		return _getOrgaHierarchy(userId1, userId2);
	}
	
	/**
	 * Returns the organization hierarchy between user1 and user2.
	 * @param userId1
	 * @param userId2
	 * @return
	 */
	public static JSONObject _getOrgaHierarchy(String userId1, String userId2) {
		String contextPath = "/organization/"+userId1+"/hierarchypath/"+userId2+"?fields=id";
		
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			String hierarchy = HttpUtil.sendRequest("GET", url, null);
			
			JSONObject hierarchyJson = new JSONObject(hierarchy);
			return hierarchyJson;
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns the proper userId for the given mail address.
	 */
	public String getUserIdViaMail(String mail) {
		return _getUserIdViaMail(mail);
	}
	/**
	 * Returns the proper userId for the given mail address.
	 * @param mail
	 * @return
	 */
	public static String _getUserIdViaMail(String mail) {
		String contextPath = "/user?fields=id&filterBy=emails&filterOp=contains&filterValue="+mail;
		
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			String userList = HttpUtil.sendRequest("GET", url, null);
			JSONObject jUserList = new JSONObject(userList);
			if(jUserList.getInt(ShindigConnector.TOTALRESULTS)!=1)
				return null;
			
			return jUserList.getJSONArray(ShindigConnector.LIST).getJSONObject(0)
								.getString(ShindigConnector.ID);
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns a map, containing the given userIds as values and
	 * the proper email addresses as keys.
	 * @return Map<EmailAddress, UserId>
	 */
	public Map<String,String> getUserEMailAddress(Set<String> userIds) {
		return _getUserEMailAddress(userIds);
	}
	/**
	 * Returns a map, containing the given userIds as values and
	 * the proper email addresses as keys.
	 * @param userIds
	 * @return Map<EmailAddress, UserId>
	 */
	public static Map<String, String> _getUserEMailAddress(Set<String> userIds) {
		Map<String, String> userMap = new HashMap<String, String>();
		
		StringBuffer userIdString = new StringBuffer();
		int i=0;
		for(String id : userIds) {
			if(i>0)
				userIdString.append(",");
			userIdString.append(id);
			i++;
		}		
		String contextPath = "/people/"+userIdString+"?fields=id,emails";
		
		try {
			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
			String userList = HttpUtil.sendRequest("GET", url, null);
			JSONArray jUserList = new JSONObject(userList).getJSONArray(ShindigConnector.LIST);
			
			for(i=0; i<jUserList.length(); i++) {
				userMap.put(jUserList.getJSONObject(i).getJSONArray(ShindigConnector.EMAILS)
								.getJSONObject(0).getString(ShindigConnector.VALUE),
							jUserList.getJSONObject(i).getString(ShindigConnector.ID));
			}
			
		} catch (MalformedURLException | JSONException e) {
			e.printStackTrace();
		}
		
		return userMap;
	}
	
}
