package com.everis.fallas.operacionales.workitem.utils;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.ItemProfile;

public class WorkItemUtil {

	public static IWorkItem findWorkItemByID(String id, ItemProfile<IWorkItem> profile, IWorkItemCommon workitemCommon,
			IProgressMonitor monitor) throws TeamRepositoryException {
		Integer idVal;
		try {
			idVal = new Integer(id);
		} catch (NumberFormatException e) {
			throw new WorkItemCommandLineException(" WorkItem ID: Number format exception, ID is not a number: " + id);
		}
		return workitemCommon.findWorkItemById(idVal.intValue(), profile, monitor);
	}

	public static IWorkItem resolveWorkItem(IAuditableHandle handle, ItemProfile<IWorkItem> profile,
			IWorkItemCommon common, IProgressMonitor monitor) throws TeamRepositoryException {
		return (IWorkItem) common.getAuditableCommon().resolveAuditable(handle, profile, monitor);
	}
}
