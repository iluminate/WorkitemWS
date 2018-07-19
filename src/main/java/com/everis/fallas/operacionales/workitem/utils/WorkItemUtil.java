/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.utils;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.ItemProfile;

/**
 * Small helper to assist with work items
 * 
 */
public class WorkItemUtil {

	/**
	 * Find a work item by its ID provided as string.
	 * 
	 * @param id
	 *            - the work item ID as string
	 * @param profile
	 *            - the load profile to use
	 * @param workitemCommon
	 *            - the IWorkItemCommon client library
	 * @param monitor
	 *            - a progress monitor or null
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IWorkItem findWorkItemByID(String id,
			ItemProfile<IWorkItem> profile, IWorkItemCommon workitemCommon,
			IProgressMonitor monitor) throws TeamRepositoryException {
		Integer idVal;
		try {
			idVal = new Integer(id);
		} catch (NumberFormatException e) {
			throw new WorkItemCommandLineException(
					" WorkItem ID: Number format exception, ID is not a number: "
							+ id);
		}
		return workitemCommon.findWorkItemById(idVal.intValue(), profile,
				monitor);
	}

	/**
	 * Resolve a WorkItem from a handle
	 * 
	 * @param handle
	 * @param profile
	 * @param common
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IWorkItem resolveWorkItem(IAuditableHandle handle,
			ItemProfile<IWorkItem> profile, IWorkItemCommon common,
			IProgressMonitor monitor) throws TeamRepositoryException {
		return (IWorkItem) common.getAuditableCommon().resolveAuditable(handle,
				profile, monitor);
	}
}
