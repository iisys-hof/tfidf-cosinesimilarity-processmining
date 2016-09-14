package de.iisys.schub.processMining.activities.network;

import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * Abstract class for the Shindig connectors.
 * 
 * @author Christian Ochsenkühn
 *
 */
public abstract class ShindigConnector {
	
	public static String DISPLAYNAME = "displayName";
	public static String ID = "id";
	public static String EMAILS = "emails";
	public static String ENTRY = "entry";
	public static String LIST = "list";
	public static String TITLE = "title";
	public static String TOTALRESULTS = "totalResults";
	public static String TYPE = "type";
	public static String VALUE = "value";
	
	public static String ACTIVITY_ACTOR = "actor";
	public static String ACTIVITY_GENERATOR = "generator";
	public static String ACTIVITY_OBJECT = "object";
	public static String ACTIVITY_OBJECT_OBJECTTYPE = "objectType";
	public static String ACTIVITY_PUBLISHED = "published";
	public static String ACTIVITY_TARGET = "target";
	public static String ACTIVITY_VERB = "verb";
	
	public static String HIER_MANAGED_BY = "@managed_by";
	public static String HIER_MANAGER_OF = "@manager_of";
	public static String HIER_RELATION = "relationship";
	
	public static String MESSAGE_OUTBOX = "@outbox";
	
	public static String OPTION_CONTAINS = "contains";
	public static String OPTION_FILTER_BY = "filterBy";
	public static String OPTION_FILTER_OP = "filterOp";
	public static String OPTION_FILTER_VALUE = "filterValue";
	
	public static String PERSON_ORGANIZATIONS = "organizations";
	public static String PERSON_ORGANIZATIONS_DEPARTMENT = "department";
	public static String PERSON_ORGANIZATIONS_DEPARTMENTHEAD = "departmentHead";
	public static String PERSON_ORGANIZATIONS_MANAGERID = "managerId";
	
	/**
	 * Returns the organization role of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public abstract String getUserRole(String userId);
	
	/**
	 * Returns the department of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public abstract String getUserDepartment(String userId);
	
	/**
	 * Returns true if the user with the given userId is the head of his department.
	 * @param userId
	 * @return
	 */
	public abstract boolean userIsDepartmentHead(String userId);

	/**
	 * Returns the userId of the manager of the user with the given userId.
	 * @param userId
	 * @return
	 */
	public abstract String getUserManagerId(String userId);
	
	/**
	 * Returns a single outbox message with the given msgId.
	 * @param userId: id of the message sender
	 * @param msgId
	 * @return
	 */
	public abstract JSONObject getOutboxMessage(String userId, String msgId);
	
	/**
	 * Returns the organization hierarchy between user1 and user2.
	 * @param userId1
	 * @param userId2
	 * @return
	 */
	public abstract JSONObject getOrgaHierarchy(String userId1, String userId2);
	
	/**
	 * Returns the proper userId for the given mail address.
	 * @param mail
	 * @return
	 */
	public abstract String getUserIdViaMail(String mail);
	
	/**
	 * Returns a map, containing the given userIds as values and the proper email addresses as keys.
	 * @return Map<EmailAddress, UserId>
	 */
	public abstract Map<String, String> getUserEMailAddress(Set<String> userIds);
	
	
	// helper:
	
	public static String getActorIdFromActivity(JSONObject activity) {
		return activity.getJSONObject(ACTIVITY_ACTOR).getString(ID);
	}
	
	public static String getTargetIdFromActivity(JSONObject activity) {
		return activity.getJSONObject(ACTIVITY_TARGET).getString(ID);
	}
	
}
