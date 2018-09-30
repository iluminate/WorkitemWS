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

public class ColumnHeaderAttributeNameMapper {

	private HashMap<String, String> nameIDMap = new HashMap<String, String>(50);
	private HashMap<String, String> idNameMap = new HashMap<String, String>(50);
	private HashMap<String, IAttribute> attributeMap = new HashMap<String, IAttribute>(
			50);
	private HashMap<String, IEndPointDescriptor> linkMap = new HashMap<String, IEndPointDescriptor>(
			50);

									public ColumnHeaderAttributeNameMapper(
			IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workItemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {
		super();
		getAttributeNameMap(projectAreaHandle, workItemCommon, monitor);
	}

									private void getAttributeNameMap(IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workItemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {

		List<IAttribute> attributes = workItemCommon.findAttributes(
				projectAreaHandle, monitor);
		for (IAttribute attribute : attributes) {
			String displayName = attribute.getDisplayName();
			String id = attribute.getIdentifier();
			nameIDMap.put(displayName, id);
			idNameMap.put(id, displayName);
			attributeMap.put(id, attribute);
		}
		Set<String> linkNames = ParameterLinkIDMapper.getLinkNames();
		for (String linkName : linkNames) {
			String linkID = ParameterLinkIDMapper.getinternalID(linkName);
			nameIDMap.put(linkName, linkID);
			idNameMap.put(linkID, linkName);
			linkMap.put(linkID,
					ReferenceUtil.getReferenceEndpointDescriptor(linkID));
		}
	}

									private String getIDForName(String propertyName) {
		String id = nameIDMap.get(propertyName);
		if (id == null) {
			return null;
		}
		return id;
	}

								private String getAliasID(String attributeName) {
		return ParameterIDMapper.getAlias(attributeName);
	}

							private boolean hasID(String attributeName) {
		String alias = ParameterIDMapper.getAlias(attributeName);
		return idNameMap.containsKey(alias);
	}

							public String getID(String nameOrID) {
		if (hasID(nameOrID)) {
			nameOrID = getAliasID(nameOrID);
		} else {
			String idFromName = getIDForName(nameOrID);
			if (idFromName != null) {
				nameOrID = getAliasID(idFromName);
			}
		}
		return nameOrID;
	}

					public IAttribute getAttribute(String attributeID) {
		return attributeMap.get(attributeID);
	}

					public String getDisplayNameForID(String id) {
		return this.nameIDMap.get(id);
	}

					public boolean isLinkType(String linkID) {
		return linkMap.containsKey(linkID);
	}

}
