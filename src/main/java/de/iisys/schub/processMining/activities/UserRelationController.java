package de.iisys.schub.processMining.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import de.iisys.schub.processMining.activities.model.MailTaskCandidate;
import de.iisys.schub.processMining.activities.model.TaskCandidate;
import de.iisys.schub.processMining.activities.model.artefact.MailArtefact;
import de.iisys.schub.processMining.activities.network.ElasticSearchConnector;
import de.iisys.schub.processMining.activities.network.ShindigConnector;
import de.iisys.schub.processMining.activities.network.ShindigDirectConnector;
import de.iisys.schub.processMining.activities.network.ShindigRESTConnector;
import de.iisys.schub.processMining.config.Config;

/**
 * Identifies the relation between users: hierarchical.
 * 
 * @author Christian Ochsenk�hn
 *
 */
public class UserRelationController {
	
	// sender and receiver are...
	
	// ...within the same organizational unit (OU) on the same level
	public static final int REL_SAME_OU = 10; 
	
	// ...subordinate on first or second level (both directions possible)
	public static final int REL_SUBORDINATE_SENDER = 20;	
	public static final int REL_SUBORDINATE_RECEIVER = 21;
	public static final int FIRST_LVL = 1;
	public static final int SECOND_LVL = 2;
	
	// receiver always is in specific OU, irrespective of sender's OU
	// or: receiver always has specific job position, irrespective of sender's job position
	public static final int REL_MAYBE_SPECIFIC_OU_OR_JOBPOSITION_RECEIVER = 30;
	public static final int REL_MAYBE_SPECIFIC_OU_RECEIVER = 31;
	public static final int REL_MAYBE_SPECIFIC_JOBPOSITION_RECEIVER = 32;
	
	// skills currently not used:
	// receiver always has specific skill combination, irrespective of sender:
	public static final int SKILLREL_SPECIFIC_SKILLS_RECEIVER = 50;
	// receiver has certain skill that sender does not have. other skills are the same:
	public static final int SKILLREL_ADDITIONAL_SKILL_RECEIVER = 51;
	// sender and receiver have the same skills:
	public static final int SKILL_REL_SAME_SKILLS = 52;
	
	
	public static final int REL_NO_RELATION = 0;
	
	
	/**
	 * Identifies the hierarchical relation between sender and receiver.
	 * @param tc
	 * 		TaskCandidate containing a sender and a receiver
	 * @return
	 * 	[0]: The relation (e.g., who is subordinate)
	 * 	[1]: On which level is she subordinate (1st or 2nd level)
	 * 	Besides: 
	 * 	If [0] is REL_SAME_OU (same department), then [1] is the department.
	 * 	If [0] is REL_MAYBE_SPECIFIC_OU_OR_JOBPOSITION_RECEIVER, 
	 * 	then [1] is the receiver's department, and [2] is the receiver's role.
	 */
	public static String[] getUserRelation(TaskCandidate tc) {
		String senderId = null;
		String receiverId = null;
		
		if(tc instanceof MailTaskCandidate) {
			senderId = ((MailArtefact)tc.getArtefact()).getSenderId();
			receiverId = ((MailArtefact)tc.getArtefact()).getReceiverId();

			if(receiverId==null) {
				String receiverMail = ElasticSearchConnector.getReceiverMailFromHit(((MailTaskCandidate)tc).getMailHit(), 0);
				
				if(Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT)) {
					ShindigDirectConnector shindigCon = new ShindigDirectConnector();
					// disabled
//					receiverId = shindigCon.getUserIdViaMail(receiverMail);
					receiverId = null;
				} else {
					receiverId = ShindigRESTConnector._getUserIdViaMail(receiverMail);
				}
			}
		} else {
			senderId = ShindigConnector.getActorIdFromActivity(tc.getActivity());
			receiverId = ShindigConnector.getTargetIdFromActivity(tc.getActivity());
		}
		return getUserRelation(senderId, receiverId);
	}
	
	/**
	 * Identifies the hierarchical relation between sender and receiver.
	 * @param senderId
	 * @param receiverId
	 * @return
	 * 	[0]: The relation (e.g., who is subordinate)
	 * 	[1]: On which level is she subordinate (1st or 2nd level)
	 * 	Besides: 
	 * 	If [0] is REL_SAME_OU (same department), then [1] is the department.
	 * 	If [0] is REL_MAYBE_SPECIFIC_OU_OR_JOBPOSITION_RECEIVER, 
	 * 	then [1] is the receiver's department, and [2] is the receiver's role.
	 */
	private static String[] getUserRelation(String senderId, String receiverId) {
		String[] rel = new String[]{String.valueOf(REL_NO_RELATION), ""};
		
		ShindigConnector shindigCon;
		if(Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT))
		    // disabled
//			shindigCon = new ShindigDirectConnector();
		    shindigCon = null;
		else
			shindigCon = new ShindigRESTConnector();
		
		if(senderId!=null && receiverId!=null) {
			JSONObject relation1; JSONObject relation2;
			JSONObject hierarchy;

			hierarchy = shindigCon.getOrgaHierarchy(senderId, receiverId);
			
			JSONArray hierarchyList = hierarchy.getJSONArray(ShindigConnector.LIST);
			if(hierarchy.getInt(ShindigConnector.TOTALRESULTS)==3) {
				relation1 = hierarchyList.getJSONObject(1);
				if(relation1.getBoolean(ShindigConnector.HIER_RELATION)!=true)
					return null;
				if(relation1.getString(ShindigConnector.TYPE).equals(ShindigConnector.HIER_MANAGED_BY)) {
					// sender is subordinate on 1st level
					rel = new String[]{
							String.valueOf(REL_SUBORDINATE_SENDER), 
							String.valueOf(FIRST_LVL)
						};
				} else {
					// sender is superior on 1st level
					rel = new String[]{
							String.valueOf(REL_SUBORDINATE_RECEIVER),
							String.valueOf(FIRST_LVL)
						};
				}
			} else if(hierarchy.getInt(ShindigConnector.TOTALRESULTS)==5) {
				relation1 = hierarchyList.getJSONObject(1);
				relation2 = hierarchyList.getJSONObject(3);
				
				if(relation1.getString(ShindigConnector.TYPE).equals(ShindigConnector.HIER_MANAGED_BY)) {
					if(relation2.getString(ShindigConnector.TYPE).equals(ShindigConnector.HIER_MANAGED_BY)) {
						// sender is subordinate on 2nd level
						rel = new String[]{
								String.valueOf(REL_SUBORDINATE_SENDER), 
								String.valueOf(SECOND_LVL)
							};
					} else {
						// sender and receiver are on the same level
					}
				} else {
					if(relation2.getString(ShindigConnector.TYPE).equals(ShindigConnector.HIER_MANAGER_OF)) {
						// sender is superior on 2nd level
						rel = new String[]{
								String.valueOf(REL_SUBORDINATE_RECEIVER),
								String.valueOf(SECOND_LVL)
							};
					}
				}
				
			} else {			
				
				String senderDepartment = shindigCon.getUserDepartment(senderId);				
				if(! Config.SHINDIG_USAGE.getValue().equals(Config.VAL_SHINDIG_DIRECT)) {
					shindigCon = new ShindigRESTConnector(receiverId);
				} 
				String receiverRole = shindigCon.getUserRole(receiverId);
				String receiverDepartment = shindigCon.getUserDepartment(receiverId);
				
				
				if(senderDepartment.equals(receiverDepartment)) { // same department
					rel = new String[]{
						String.valueOf(REL_SAME_OU), 
						senderDepartment
					};
					/*
				} else if(senderManagerId.equals(receiverManagerId)) { // same manager TODO: keep?
					rel = new String[]{
							String.valueOf(REL_SAME_OU), 
							senderDepartment
						};
					*/
				} else {
					// maybe specific OU or job position of receiver
					rel = new String[] {
						String.valueOf(REL_MAYBE_SPECIFIC_OU_OR_JOBPOSITION_RECEIVER),
						receiverDepartment,
						receiverRole
					};
				}
			}
		}
		
		return rel;
	}
	
}
