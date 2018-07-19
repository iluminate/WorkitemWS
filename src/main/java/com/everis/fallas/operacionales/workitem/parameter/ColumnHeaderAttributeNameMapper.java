/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 20015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.utils.ReferenceUtil;
import com.ibm.team.links.common.registry.IEndPointDescriptor;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;

/**
 * Class to map between internal representations and display values of work item
 * attributes.
 * 
 */
public class ColumnHeaderAttributeNameMapper {

	private HashMap<String, String> nameIDMap = new HashMap<String, String>(50);
	private HashMap<String, String> idNameMap = new HashMap<String, String>(50);
	private HashMap<String, IAttribute> attributeMap = new HashMap<String, IAttribute>(
			50);
	private HashMap<String, IEndPointDescriptor> linkMap = new HashMap<String, IEndPointDescriptor>(
			50);

	/**
	 * To get a new mapper
	 * 
	 * @param projectAreaHandle
	 * @param workItemCommon
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	public ColumnHeaderAttributeNameMapper(
			IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workItemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {
		super();
		getAttributeNameMap(projectAreaHandle, workItemCommon, monitor);
	}

	/**
	 * Get and if necessary fill the map
	 * 
	 * @param projectAreaHandle
	 * @param workItemCommon
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	private void getAttributeNameMap(IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workItemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {

		// Add all the attributes
		List<IAttribute> attributes = workItemCommon.findAttributes(
				projectAreaHandle, monitor);
		for (IAttribute attribute : attributes) {
			String displayName = attribute.getDisplayName();
			String id = attribute.getIdentifier();
			nameIDMap.put(displayName, id);
			idNameMap.put(id, displayName);
			attributeMap.put(id, attribute);
		}
		// Add all the links
		Set<String> linkNames = ParameterLinkIDMapper.getLinkNames();
		for (String linkName : linkNames) {
			String linkID = ParameterLinkIDMapper.getinternalID(linkName);
			nameIDMap.put(linkName, linkID);
			idNameMap.put(linkID, linkName);
			linkMap.put(linkID,
					ReferenceUtil.getReferenceEndpointDescriptor(linkID));
		}
	}

	/**
	 * Check if there is an attribute/link with the matching display name and
	 * get its ID.
	 * 
	 * @param propertyName
	 *            - the display name of the property
	 * @return the id of the attribute or null
	 */
	private String getIDForName(String propertyName) {
		String id = nameIDMap.get(propertyName);
		if (id == null) {
			return null;
		}
		return id;
	}

	/**
	 * Check if there is an alias available for a property ID and pass it back
	 * if available.
	 * 
	 * @param attributeName
	 * @return
	 */
	private String getAliasID(String attributeName) {
		return ParameterIDMapper.getAlias(attributeName);
	}

	/**
	 * 
	 * 
	 * @param attributeName
	 * @return
	 */
	private boolean hasID(String attributeName) {
		String alias = ParameterIDMapper.getAlias(attributeName);
		return idNameMap.containsKey(alias);
	}

	/**
	 * Get an internal ID for an Id or a DisplayName
	 * 
	 * @param column
	 * @return
	 */
	public String getID(String nameOrID) {
		if (hasID(nameOrID)) {
			// The column is an ID
			nameOrID = getAliasID(nameOrID);
		} else {
			// the column must be a name
			String idFromName = getIDForName(nameOrID);
			if (idFromName != null) {
				nameOrID = getAliasID(idFromName);
			}
		}
		return nameOrID;
	}

	/**
	 * @param attributeID
	 * @return
	 */
	public IAttribute getAttribute(String attributeID) {
		return attributeMap.get(attributeID);
	}

	/**
	 * @param id
	 * @return
	 */
	public String getDisplayNameForID(String id) {
		return this.nameIDMap.get(id);
	}

	/**
	 * @param linkID
	 * @return
	 */
	public boolean isLinkType(String linkID) {
		return linkMap.containsKey(linkID);
	}

}
