package de.iisys.schub.processMining.cmmn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMMNController {

	private final boolean WITH_DIAGRAM = true;
	
//	private final String CMMN_SPEC_VERSION = "20151109";
	// CMMN 1.1:
	private final String CMMN_SPEC_VERSION = "20150516";
	// CMMN 1.0:
//	private final String CMMN_SPEC_VERSION = "20131201";
	
	// diagram:
	private final int MILESTONE_WIDTH = 125;
	private final int MILESTONE_HEIGHT = 50;
	private final int SENTRY_WIDTH = 16;
	private final int SENTRY_HEIGHT = 24;
	private final int TASK_WIDTH = 125;
	private final int TASK_HEIGHT = 90;	
	private final int ITEMS_PER_ROW = 6;
	private final int ITEM_PADDING = 35;
	private final int OFFSET = 35;
	
	private final String ID_HUMANTASK_PREV = "HumanTask_";
	private final String ID_MILESTONE_PREV = "Milestone_";
	private final String ID_ONPART_PREV = "OnPart_";
	private final String ID_SENTRY_PREV = "Sentry_";
	private final String ID_CASEPLANMODEL = "CasePlanModel";
	
	private String caseName;
	private String defId;
	private List<String> planItemDefinitions;
//	private List<String> planItems;	// instances
	
	private List<List<String>> planItemGroups;
	private List<String> milestonesForGroups;
	private List<String> otherPlanItems;
	
	private String outputXml;
	
	// count:
	private int count_HumanTasks = 0;
	
	public CMMNController(String cmmnDefId) {
		createNewCMMN(cmmnDefId);
	}
	
	public void createNewCMMN(String caseName) {
		this.outputXml = null;
		this.planItemDefinitions = new ArrayList<String>();
//		this.planItems = new ArrayList<String>();
		this.planItemGroups = new ArrayList<List<String>>();
		this.milestonesForGroups = new ArrayList<String>();
		this.caseName = caseName;
		this.defId = UUID.randomUUID().toString();
	}
	
	public String addHumanTaskToGroup(String name, boolean startNewGroup) {
		return addHumanTask(name, false, null, true, startNewGroup);
	}
	public String addHumanTask(String name, boolean isBlocking) {
		return addHumanTask(name, isBlocking, null, false, false);
	}
	public String addHumanTask(String name, boolean isBlocking, String assignee, boolean inGroup, boolean startNewGroup) {
		String id = ID_HUMANTASK_PREV + this.count_HumanTasks++;
		
		String[] task = getHumanTask(name, id, isBlocking, assignee);
		if(!inGroup) {
			this.otherPlanItems.add(task[0]);
		} else {
			if(planItemGroups.size()==0 || startNewGroup) {
				planItemGroups.add(new ArrayList<String>());
			}
			planItemGroups.get(planItemGroups.size()-1).add(task[0]);
		}
		this.planItemDefinitions.add(task[1]);
		
		return id;
	}
	
	public void addMilestone(String name, String[] entryCriterionIds) {
		String[] sentryRefs = new String[entryCriterionIds.length];
		
		int i=0;
		for(String planItemRef : entryCriterionIds) {
			String id = ID_SENTRY_PREV+planItemDefinitions.size();
			planItemDefinitions.add(getSentry(id, "PI_"+planItemRef));
			sentryRefs[i] = id;
			i++;
		}
		
		String[] milestone = getMilestone(name, ID_MILESTONE_PREV+milestonesForGroups.size(), sentryRefs);
//		planItems.add(milestone[0]);
		this.milestonesForGroups.add(milestone[0]);
		this.planItemDefinitions.add(milestone[1]);
	}
	
	public String getCMMNxml() {
		if(this.outputXml!=null) {
			return this.outputXml;
		} else {
			if(defId.length()>32)
				defId = defId.substring(0, 32);
			String caseId = this.caseName.toLowerCase().replace(" ", "_");
			
			StringBuffer xml = new StringBuffer(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+	"\n"+
					"<cmmn:definitions "+
//					"id=\""+this.defId+"\" "+
					"name=\""+this.caseName+"\" "+
					"exporter=\"SCHub Activity Mining\" "+
					"targetNamespace=\"http://cmmn.org\" "+
					"xmlns:cmmn=\"http://www.omg.org/spec/CMMN/"+CMMN_SPEC_VERSION+"/MODEL\" "+
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
					"xmlns:cmmndi=\"http://www.omg.org/spec/CMMN/"+CMMN_SPEC_VERSION+"/CMMNDI\" "+
					"xmlns:di=\"http://www.omg.org/spec/CMMN/"+CMMN_SPEC_VERSION+"/DI\" "+
					"xmlns:dc=\"http://www.omg.org/spec/CMMN/"+CMMN_SPEC_VERSION+"/DC\" "+
					"xmlns:camunda=\"http://camunda.org/schema/1.0/cmmn\"> "+				"\n"+
																							"\n"+
					"<cmmn:case id=\"doc_"+caseId+"\">"+									"\n"+
						"\t"+"<cmmn:casePlanModel autoComplete=\"false\" "+
							"name=\""+this.caseName+"\" "+
							"id=\""+ID_CASEPLANMODEL+"\" >"+										"\n");
			
			xml.append("\n\t\t<!-- Plan Items --> \n");
			
			for(List<String> planItems : this.planItemGroups) {
				for(String planItem : planItems) {
					xml.append("\t\t"+planItem+"\n");
				}
			}
			for(String milestonePlanItem : this.milestonesForGroups) {
				xml.append("\t\t"+milestonePlanItem+"\n");
			}
			
			xml.append("\n\t\t<!-- Plan Item Definitions --> \n");
			for(String planItemDef : this.planItemDefinitions) {
				xml.append("\t\t"+planItemDef+"\n");
			}
			
			xml.append("\t"+"</cmmn:casePlanModel>"+										"\n"+
					"</cmmn:case>"+															"\n");
			
			if(WITH_DIAGRAM)
				xml.append("\n" + createDiagram() + "\n");
			
			xml.append("</cmmn:definitions>");
			
			this.outputXml = xml.toString();
			return this.outputXml;
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param id
	 * @param isBlocking
	 * @param assignee
	 * @return
	 * 		String[0]: plan item
	 * 		String[1]: plan item definition
	 */
	private String[] getHumanTask(String name, String id, boolean isBlocking, String assignee) {	
		String planItemDefinition = "<cmmn:humanTask"+
				" isBlocking=\""+isBlocking+"\""+
				" name=\""+name.replace("'", "")+"\""+
				" id=\""+id+"\"";
		
//		if(desc!=null && !desc.equals(""))
//			planItemDefinition += " description=\""+desc.replace("'", "")+"\"";

		if(assignee!=null)
			planItemDefinition += " camunda:assignee=\""+assignee+"\"";
		
		planItemDefinition +=	" />";
		
		return new String[]{getPlanItem(id, null), planItemDefinition};
	}
	
	private String[] getMilestone(String name, String id, String[] sentryRefs) {
		String milestoneDefinition = "<cmmn:milestone"+
				" id=\""+id+"\""+
				" name=\""+name+"\""+
				" />";
		
		String planItem = getPlanItem(id, sentryRefs);
		
		return new String[]{planItem, milestoneDefinition};
	}
	
	private String getSentry(String id, String planItemRef) {
		String sentry = 
			"<cmmn:sentry id=\""+id+"\">"+
				"<cmmn:planItemOnPart id=\""+ID_ONPART_PREV+planItemRef+"\" sourceRef=\""+planItemRef+"\">"+
					"<cmmn:standardEvent>complete</cmmn:standardEvent>"+
				"</cmmn:planItemOnPart>"+
			"</cmmn:sentry>";
		return sentry;
	}
	
	private String getPlanItem(String definitionRef, String[] sentryRefs) {
		StringBuffer planItem = new StringBuffer(
				"<cmmn:planItem id=\"PI_"+definitionRef+"\" definitionRef=\""+definitionRef+"\"");
		
		if(sentryRefs==null) {
			planItem.append(" />");
		} else {
			planItem.append(" >");
				int nr = 0;
				for(String sentryRef : sentryRefs) {
					planItem.append("\n\t\t\t"+getEntryCriterion(sentryRef, "PI_entry_"+definitionRef+"_"+nr));
					nr++;
				}
			planItem.append("\n\t\t"+"</cmmn:planItem>");
		}
		
		return planItem.toString();
	}
	
	private String getEntryCriterion(String sentryRef, String id) {
		return "<cmmn:entryCriterion "+
				"id=\""+id+"\""+
				" sentryRef=\""+sentryRef+"\" />";
	}
	
	
	// Diagram:
	
	private String createDiagram() {
		// casePlanModel width:
		int width = ITEMS_PER_ROW * (TASK_WIDTH+ITEM_PADDING) + ITEM_PADDING;
		
		// casePlanModel height:
		int linesHumanTasks = 0;
		for(List<String> group : this.planItemGroups) {
			linesHumanTasks += Math.ceil(((double)group.size()/(double)ITEMS_PER_ROW));
		}
		
		int height = (linesHumanTasks+1) * (TASK_HEIGHT+ITEM_PADDING)
				+ ITEM_PADDING
				+ this.milestonesForGroups.size()*(ITEM_PADDING+MILESTONE_HEIGHT);
		
		// casePlanModel x, y
		int x_casePlanModel = OFFSET;
		int y_casePlanModel = OFFSET;
		
		// diagram:
		StringBuffer gram = new StringBuffer(
				"<cmmndi:CMMNDI>"+"\n"+
				"<cmmndi:CMMNDiagram di:name=\""+this.caseName+" Diagram\" di:id=\"_"+this.defId+"\">"+"\n"+
					"\t"+"<cmmndi:Size xsi:type=\"dc:Dimension\" "+
					"height=\""+(height+OFFSET+100)+".0\" width=\""+(width+OFFSET+100)+".0\" />"+
				"\n\n");
		
		gram.append( getDiCMMNShape(ID_CASEPLANMODEL, width, height, x_casePlanModel, y_casePlanModel) );
		

		// plan items in groups:
		int tempCount_humanTasks = 0;
		int tempCount_humanTasks2 = 0;
		int lastY = OFFSET;
		System.out.println("++++ planItemGroups.size: "+planItemGroups.size());
		for(int j=0; j<this.planItemGroups.size(); j++) {
			// for edges:
			List<int[]> edgeStartWayPoints = new ArrayList<int[]>();
			int edgeEndX;
			int edgeEndY;
			
			List<String> curPlanItems = planItemGroups.get(j);
			
			int y=0; int i=0;
			for(i=0; i<curPlanItems.size(); i++) {
				y = (int)Math.ceil( (i+1.) / (float)ITEMS_PER_ROW );
				y = (TASK_HEIGHT*(y-1) + ITEM_PADDING*y) + lastY;
				
				int x = (i+1) % ITEMS_PER_ROW;
				if(x==0) x = ITEMS_PER_ROW;
				x = (TASK_WIDTH*(x-1) + ITEM_PADDING*x) + OFFSET;
				
				System.out.println("group "+j+", task "+i+": x="+x+", y="+y);
				gram.append( getDiCMMNShape("PI_"+ID_HUMANTASK_PREV + tempCount_humanTasks++, TASK_WIDTH, TASK_HEIGHT, x, y) );
				
				edgeStartWayPoints.add( new int[]{(x+(TASK_WIDTH/2)), (y+TASK_HEIGHT)} );
			}
			
			// one row further (milestone)
//			y = (int)Math.ceil( (i+1.) / (float)ITEMS_PER_ROW )+1;			
//			y = (TASK_HEIGHT*(y-1) + ITEM_PADDING*y) + OFFSET + lastY;
			y += TASK_HEIGHT+ITEM_PADDING*2;
			
			edgeEndX = width/2;
			int x = (int)(edgeEndX - (MILESTONE_WIDTH/2));
			System.out.println("group "+j+", milestone: x="+x+", y="+y);
			gram.append( getDiCMMNShape("PI_"+ID_MILESTONE_PREV+j, MILESTONE_WIDTH, MILESTONE_HEIGHT, x, y));
			
			lastY = y + MILESTONE_HEIGHT;		
			
			// milestone sentries:
			y = y - (SENTRY_HEIGHT/2);
			x = edgeEndX - (SENTRY_WIDTH/2);
			edgeEndY = y;
			for(i=0; i<curPlanItems.size(); i++) {
				// sentry:
				gram.append( getDiCMMNShape("PI_entry_"+ID_MILESTONE_PREV+j+"_"+i, SENTRY_WIDTH, SENTRY_HEIGHT, x, y));
				
				// edge:
				List<int[]> waypoints = new ArrayList<int[]>();
				// waypoint 1:
				int wp_X = edgeStartWayPoints.get(i)[0];
				int wp_Y = edgeStartWayPoints.get(i)[1];
				waypoints.add( new int[]{wp_X,wp_Y} );
				// waypoint 2:
				wp_Y = (edgeEndY-wp_Y)/2 + wp_Y;
				waypoints.add( new int[]{wp_X,wp_Y} );
				// waypoint 3:
				waypoints.add( new int[]{edgeEndX,wp_Y} );
				// waypoint 4:
				waypoints.add( new int[]{edgeEndX,edgeEndY} );				
				gram.append( getDiCMMNEdge(ID_ONPART_PREV+"PI_"+ID_HUMANTASK_PREV+(tempCount_humanTasks2++), 
											"PI_entry_"+ID_MILESTONE_PREV+j+"_"+i, 
											waypoints));
			}			
		}
		
		/*
		// other plan items:
		for(int i=0; i<this.planItems.size(); i++) {
			System.out.println("DiCMMN: other plan items..."); // TODO
			int y = (int)Math.ceil( (i+1.) / (float)ITEMS_PER_ROW );
			y = (TASK_HEIGHT*(y-1) + ITEM_PADDING*y) + OFFSET;
			
			int x = (i+1) % ITEMS_PER_ROW;
			if(x==0) x = ITEMS_PER_ROW;
			x = (TASK_WIDTH*(x-1) + ITEM_PADDING*x) + OFFSET;
			
			gram.append( getDiCMMNShape("PI_"+ID_HUMANTASK_PREV+i, TASK_WIDTH, TASK_HEIGHT, x, y) );
		}
		*/
		
		gram.append( "</cmmndi:CMMNDiagram>"+"\n"+
			"</cmmndi:CMMNDI>" );
		
		return gram.toString();
	}
	
	private String getDiCMMNShape(String elementRef, int width, int height, int x, int y) {
		String shape = "\t"+"<cmmndi:CMMNShape cmmnElementRef=\""+elementRef+"\" di:id=\"shape_"+elementRef+"\">"+"\n"+
					"\t\t"+"<dc:Bounds height=\""+height+".0\" width=\""+width+".0\" x=\""+x+".0\" y=\""+y+".0\" />"+"\n"+
					"\t\t"+"<cmmndi:CMMNLabel />"+"\n"+
				"\t"+"</cmmndi:CMMNShape>"+"\n";
		return shape;
	}
	
	private String getDiCMMNEdge(String fromId, String toId, List<int[]> waypoints) {
		StringBuffer edge = new StringBuffer(
			"\t"+"<cmmndi:CMMNEdge cmmnElementRef=\""+fromId+"\" targetCMMNElementRef=\""+toId+"\">"+"\n");
		if(waypoints!=null) {
			for(int[] waypoint : waypoints) {
				if(waypoint.length==2)
					edge.append("\t\t"+"<di:waypoint xsi:type=\"dc:Point\" x=\""+waypoint[0]+"\" y=\""+waypoint[1]+"\" />"+"\n");
			}
		}
		edge.append("\t"+"</cmmndi:CMMNEdge>"+"\n");
		
		return edge.toString();
	}
	
}
