package com.everis.fallas.operacionales.workitem.parameter;

import java.util.HashMap;
import java.util.Set;

import com.ibm.team.workitem.common.model.IWorkItem;

public class ParameterIDMapper {

	public static final String ID_COM_IBM_TEAM_WORKITEM_ATTRIBUTE_WORKITEMTYPE = "com.ibm.team.workitem.attribute.workitemtype";
	public static final String PSEUDO_ATTRIBUTE_ATTACHMENTS = "Attachments";

	protected HashMap<String, String> iDMap = null;

	private static ParameterIDMapper theMapper = null;

	private ParameterIDMapper() {
		this.iDMap = new HashMap<String, String>();
		setMappings();
	}

	private void setMappings() {
		putMap("SEVERITY", IWorkItem.SEVERITY_PROPERTY);
		putMap("PRIORITY", IWorkItem.PRIORITY_PROPERTY);
		putMap("FOUND_IN", IWorkItem.FOUND_IN_PROPERTY);
		putMap("ID", IWorkItem.ID_PROPERTY);
		putMap("TYPE", IWorkItem.TYPE_PROPERTY);
		putMap("PROJECT_AREA", IWorkItem.PROJECT_AREA_PROPERTY);
		putMap("SUMMARY", IWorkItem.SUMMARY_PROPERTY);
		putMap("STATE", IWorkItem.STATE_PROPERTY);
		putMap("CREATOR", IWorkItem.CREATOR_PROPERTY);
		putMap("OWNER", IWorkItem.OWNER_PROPERTY);
		putMap("DESCRIPTION", IWorkItem.DESCRIPTION_PROPERTY);
		putMap("CREATION_DATE", IWorkItem.CREATION_DATE_PROPERTY);
		putMap("RESOLUTION", IWorkItem.RESOLUTION_PROPERTY);
		putMap("DUE_DATE", IWorkItem.DUE_DATE_PROPERTY);
		putMap("ESTIMATE", "duration");
		putMap("CORRECTED_ESTIMATE", "correctedEstimate");
		putMap("TIME_SPENT", "timeSpent");
		putMap("FILED_AGAINST", IWorkItem.CATEGORY_PROPERTY);
		putMap("PLANNED_FOR", IWorkItem.TARGET_PROPERTY);
		putMap("RESOLVER", IWorkItem.RESOLVER_PROPERTY);
		putMap("RESOLUTION_DATE", IWorkItem.RESOLUTION_DATE_PROPERTY);
		putMap("TAGS", IWorkItem.TAGS_PROPERTY);
		putMap("MODIFIED", IWorkItem.MODIFIED_PROPERTY);
		putMap("MODIFIED_BY", IWorkItem.MODIFIED_BY_PROPERTY);

		putMap("com.ibm.team.workitem.attribute.severity", IWorkItem.SEVERITY_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.priority", IWorkItem.PRIORITY_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.version", IWorkItem.FOUND_IN_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.id", IWorkItem.ID_PROPERTY);
		putMap(ID_COM_IBM_TEAM_WORKITEM_ATTRIBUTE_WORKITEMTYPE, IWorkItem.TYPE_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.projectarea", IWorkItem.PROJECT_AREA_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.summary", IWorkItem.SUMMARY_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.state", IWorkItem.STATE_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.creator", IWorkItem.CREATOR_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.owner", IWorkItem.OWNER_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.description", IWorkItem.DESCRIPTION_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.creationdate", IWorkItem.CREATION_DATE_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.resolutiondate", IWorkItem.RESOLUTION_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.duedate", IWorkItem.DUE_DATE_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.duration", IWorkItem.DURATION_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.correctedestimate", "correctedEstimate");
		getMap().put("com.ibm.team.workitem.attribute.timespent", "timeSpent");
		putMap("com.ibm.team.workitem.attribute.category", IWorkItem.CATEGORY_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.target", IWorkItem.TARGET_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.resolver", IWorkItem.RESOLVER_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.resolutiondate", IWorkItem.RESOLUTION_DATE_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.tags", IWorkItem.TAGS_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.modified", IWorkItem.MODIFIED_PROPERTY);
		putMap("com.ibm.team.workitem.attribute.modifiedby", IWorkItem.MODIFIED_BY_PROPERTY);
	}

	private static ParameterIDMapper getMapper() {
		if (theMapper == null) {
			theMapper = new ParameterIDMapper();
		}
		return theMapper;
	}

	public static String getAlias(String propertyID) {
		return getMapper().getFromAlias(propertyID);
	}

	public static String helpParameterMappings() {
		return getMapper().helpMappings();
	}

	protected HashMap<String, String> getMap() {
		return this.iDMap;
	}

	protected void putMap(String key, String value) {
		getMap().put(key, value);
	}

	public String getFromAlias(String propertyID) {
		String newVal = iDMap.get(propertyID);
		if (null != newVal)
			return newVal;
		return propertyID;
	}

	private String helpMappings() {
		String mappings = "Available mappings:\n";

		Set<String> keys = iDMap.keySet();
		for (String key : keys) {
			mappings += "\n\t" + key + ": " + iDMap.get(key);
		}
		return mappings + "\n";
	}

}
