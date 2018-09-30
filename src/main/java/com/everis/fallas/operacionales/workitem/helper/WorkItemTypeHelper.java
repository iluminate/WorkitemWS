package com.everis.fallas.operacionales.workitem.helper;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IFetchResult;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IAttributeHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;

public class WorkItemTypeHelper {
	IProgressMonitor monitor = null;
	IProjectArea fArea = null;
	ITeamRepository fTeamRepository = null;

					public WorkItemTypeHelper(IProjectArea projectArea, IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
		this.fArea = projectArea;
		this.fTeamRepository = (ITeamRepository) projectArea.getOrigin();
	}

				private IWorkItemClient getWorkItemCommon() {
		ITeamRepository repo = getTeamRepository();
		IWorkItemClient workItemClient = (IWorkItemClient) repo
				.getClientLibrary(IWorkItemClient.class);
		return workItemClient;
	}

	private ITeamRepository getTeamRepository() {
		return fTeamRepository;
	}

										public OperationResult printAttributesOfType(IProjectArea projectArea,
			IWorkItemType workItemType, IProgressMonitor monitor)
			throws TeamRepositoryException {
		OperationResult result = new OperationResult();
		List<IAttributeHandle> builtInAttributeHandles = getWorkItemCommon()
				.findBuiltInAttributes(projectArea, monitor);
		IFetchResult builtIn = fTeamRepository.itemManager()
				.fetchCompleteItemsPermissionAware(builtInAttributeHandles,
						IItemManager.REFRESH, monitor);

		List<IAttributeHandle> custAttributeHandles = workItemType
				.getCustomAttributes();

		IFetchResult custom = fTeamRepository.itemManager()
				.fetchCompleteItemsPermissionAware(custAttributeHandles,
						IItemManager.REFRESH, monitor);

		result.appendResultString("Attributes of Work Item type: "
				+ workItemType.getDisplayName() + " Type ID: "
				+ workItemType.getIdentifier());
		result.appendResultString("  Built In Attributes");
		result.appendResultString(printAttributesAndTypes(
				builtIn.getRetrievedItems(), monitor));
		result.appendResultString("  Custom Attributes");
		result.appendResultString(printAttributesAndTypes(
				custom.getRetrievedItems(), monitor));
		result.setSuccess();
		return result;
	}

								private String printAttributesAndTypes(List<?> items,
			IProgressMonitor monitor) {
		String message = "";
		message = message + "\tNumber of attributes: "
				+ new Integer(items.size()).toString() + "\n";
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = items.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof IAttribute) {
				IAttribute iAttribute = (IAttribute) object;
				message = message + "\t " + iAttribute.getDisplayName()
						+ " \tID: " + iAttribute.getIdentifier()
						+ " \tValueType: " + iAttribute.getAttributeType()
						+ "\n";
			}
		}
		return message;
	}

																	public static IWorkItemType findWorkItemTypeByIDAndDisplayName(
			String workItemTypeDisplayName,
			IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workitemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {

		IWorkItemType workItemType = findWorkItemType2(workItemTypeDisplayName,
				projectAreaHandle, workitemCommon, monitor);
		if (workItemType != null) {
			return workItemType;
		}
		List<IWorkItemType> allTypes = workitemCommon.findWorkItemTypes(
				projectAreaHandle, monitor);
		for (IWorkItemType aWorkItemType : allTypes) {
			if (aWorkItemType.getDisplayName().equals(workItemTypeDisplayName)) {
				return aWorkItemType;
			}
		}
		return null;
	}

																	public static IWorkItemType findWorkItemType2(String workItemTypeID,
			IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workitemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {

		IWorkItemType workItemType = workitemCommon.findWorkItemType(
				projectAreaHandle, workItemTypeID, monitor);
		if (workItemType != null) {
			return workItemType;
		}
		return null;
	}

																	public static IWorkItemType findWorkItemType(String workItemTypeID,
			IProjectAreaHandle projectAreaHandle,
			IWorkItemCommon workitemCommon, IProgressMonitor monitor)
			throws TeamRepositoryException {

		IWorkItemType workItemType = WorkItemTypeHelper.findWorkItemType2(
				workItemTypeID, projectAreaHandle, workitemCommon, monitor);
		if (workItemType != null) {
			return workItemType;
		}
		throw new WorkItemCommandLineException("Work item type not found: "
				+ workItemTypeID);
	}
}
