// deactivated since it does not work properly
package de.iisys.schub.processMining.activities.network;

/*
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hofuniversity.iisys.neo4j.websock.neo4j.shindig.ShindigNativeProcedures;
import de.hofuniversity.iisys.neo4j.websock.neo4j.shindig.spi.GraphMessageSPI;
import de.hofuniversity.iisys.neo4j.websock.neo4j.shindig.spi.GraphOrganizationSPI;
import de.hofuniversity.iisys.neo4j.websock.neo4j.shindig.spi.GraphPersonSPI;
import de.hofuniversity.iisys.neo4j.websock.result.ListResult;
import de.hofuniversity.iisys.neo4j.websock.result.SingleResult;
*/

/**
 * Connector for all DIRECT Shindig connections.
 * This means: this application has to be a dependency in Shindig.
 * 
 * @author Christian Ochsenkühn
 *
 */
public class ShindigDirectConnector {
//public class ShindigDirectConnector extends ShindigConnector {
//
//	private GraphMessageSPI graphMessageSPI;
//	private GraphPersonSPI graphPersonSPI;
//	private GraphOrganizationSPI graphOrganizationSPI;
//	
//	public ShindigDirectConnector() {
//		// TODO: in shindig
//		graphMessageSPI = ShindigNativeProcedures.getService(GraphMessageSPI.class);
//		graphPersonSPI = ShindigNativeProcedures.getService(GraphPersonSPI.class);
//		graphOrganizationSPI = ShindigNativeProcedures.getService(GraphOrganizationSPI.class);
//	}
//	
//	/**
//	 * Returns the organization role of the user with the given userId.
//	 */
//	@SuppressWarnings("unchecked")
//	public String getUserRole(String userId){
//		List<String> fields = new ArrayList<String>();
//		SingleResult result = graphPersonSPI.getPerson(userId, fields);
//		
//		try {
//			String role = (String)
//			((Map<String,?>)
//				((List<Map<String,?>>)
//					((Map<String,?>)result.getResults())
//				.get(PERSON_ORGANIZATIONS)).get(0))
//			.get(TITLE);
//			
//			return role;
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns the department of the user with the given userId.
//	 */
//	@SuppressWarnings("unchecked")
//	public String getUserDepartment(String userId){
//		List<String> fields = new ArrayList<String>();
//		SingleResult result = graphPersonSPI.getPerson(userId, fields);
//		
//		try {
//			String department = (String)
//			((Map<String,?>)
//				((List<Map<String,?>>)
//					((Map<String,?>)result.getResults())
//				.get(PERSON_ORGANIZATIONS)).get(0))
//			.get(PERSON_ORGANIZATIONS_DEPARTMENT);
//			
//			return department;
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns true if the user with the given userId is the head of his department.
//	 */
//	@SuppressWarnings({ "unchecked", "null" })
//	public boolean userIsDepartmentHead(String userId){
//		List<String> fields = new ArrayList<String>();
//		SingleResult result = graphPersonSPI.getPerson(userId, fields);
//		
//		try {
//			boolean isHead = (Boolean)
//			((Map<String,?>)
//				((List<Map<String,?>>)
//					((Map<String,?>)result.getResults())
//				.get(PERSON_ORGANIZATIONS)).get(0))
//			.get(PERSON_ORGANIZATIONS_DEPARTMENT);
//			
//			return isHead;
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			return (Boolean)null;
//		}
//	}
//
//	/**
//	 * Returns the userId of the manager of the user with the given userId.
//	 */
//	@SuppressWarnings("unchecked")
//	public String getUserManagerId(String userId){
//		List<String> fields = new ArrayList<String>();
//		SingleResult result = graphPersonSPI.getPerson(userId, fields);
//		
//		try {
//			String managerId = (String)
//			((Map<String,?>)
//				((List<Map<String,?>>)
//					((Map<String,?>)result.getResults())
//				.get(PERSON_ORGANIZATIONS)).get(0))
//			.get(PERSON_ORGANIZATIONS_MANAGERID);
//			
//			return managerId;
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns a single outbox message with the given msgId.
//	 * The userId is the id of the message sender
//	 */
//	@SuppressWarnings("unchecked")
//	public JSONObject getOutboxMessage(String userId, String msgId){
//		/*
//		String contextPath = "/messages/"+userId+"/@outbox"+
//				"?filterBy=id&filterOp=contains&filterValue="+msgId;
//		
//		try {
//			URL url = new URL(SHINDIG_URL+API_PATH+contextPath);
//			String msg = HttpUtil.sendRequest("GET", url, null);
//			
//			JSONObject msgJson = new JSONObject(msg);
//			return msgJson.getJSONArray("list").getJSONObject(0);
//			
//		} catch (MalformedURLException | JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
//		*/
//		
//		List<String> msgIds = new ArrayList<String>();
//		msgIds.add(msgId);
//		
//		/*
//		Map<String,Object> options = new HashMap<String,Object>();
//		options.put(OPTION_FILTER_BY, ID);
//		options.put(OPTION_FILTER_OP, OPTION_CONTAINS);
//		options.put(OPTION_FILTER_VALUE, msgId);
//		*/
//		
//		ListResult result = this.graphMessageSPI.getMessages(userId, MESSAGE_OUTBOX, msgIds, null, null);
//		try {
//			JSONObject json = new JSONObject(
//					((List<Map<String, Object>>)result.getResults()).get(0)
//			);
//			return json;
//		} catch(ClassCastException | JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns the organization hierarchy between user1 and user2.
//	 */
//	@SuppressWarnings("unchecked")
//	public JSONObject getOrgaHierarchy(String userId1, String userId2){
//		List<String> fields = new ArrayList<String>();
//		fields.add(ID);
//		ListResult result = this.graphOrganizationSPI.getHierarchyPath(userId1, userId2, fields);
//		
//		try {
//			List<Map<String, Object>> resultList = (List<Map<String, Object>>)result.getResults();
//			
//			JSONObject json = new JSONObject();
//			json.put(TOTALRESULTS, resultList.size());
//			json.put(LIST, new JSONArray(resultList));
//			return json;
//			
//		} catch(ClassCastException | JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns the proper userId for the given mail address.
//	 */
//	@SuppressWarnings("unchecked")
//	public String getUserIdViaMail(String mail){
//		List<String> fields = new ArrayList<String>();
//		fields.add(ID);
//		
//		Map<String,Object> options = new HashMap<String,Object>();
//		options.put(OPTION_FILTER_BY, EMAILS);
//		options.put(OPTION_FILTER_OP, OPTION_CONTAINS);
//		options.put(OPTION_FILTER_VALUE, mail);
//		
//		ListResult result = this.graphPersonSPI.getAllPeople(options, fields);
//		if(result.getSize()!=1)
//			return null;
//		
//		try {
//			return (String)((Map<String, Object>)result.getResults().get(0)).get(ID);
//		} catch(ClassCastException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * Returns a map, containing the given userIds as values and the proper email addresses as keys.
//	 * @return Map<EmailAddress, UserId>
//	 */
//	@SuppressWarnings("unchecked")
//	public Map<String, String> getUserEMailAddress(Set<String> userIds){
//		Map<String, String> userMap = new HashMap<String, String>();
//		
//		List<String> fields = new ArrayList<String>();
//		fields.add(ID);
//		fields.add(EMAILS);
//		
//		Map<String,Object> options = new HashMap<String,Object>();
//		
//		ListResult result = this.graphPersonSPI.getPeople(new ArrayList<String>(userIds), null, options, fields);
//		
//		
//		for(int i=0; i<result.getSize(); i++) {
//			try {
//				Map<String,Object> user = (Map<String, Object>)result.getResults().get(i);
//				String email = (String)((List<Map<String,Object>>)user.get(EMAILS)).get(0).get(VALUE);
//				userMap.put(email, (String)user.get(ID));
//			} catch(ClassCastException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return userMap;
//	}
}
