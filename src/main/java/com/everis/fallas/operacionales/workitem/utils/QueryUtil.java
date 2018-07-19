/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.helper.WorkItemUpdateHelper;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.internal.query.QueryResultIterator;
import com.ibm.team.workitem.common.query.IQueryDescriptor;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResult;
import com.ibm.team.workitem.common.query.QueryTypes;

/**
 * Utility Class to find IQueryDescriptors for - Personal Queries - Shared
 * queries
 * 
 * And Utility Class to get results from queries
 * 
 */
@SuppressWarnings("restriction")
public class QueryUtil {

	/**
	 * Find users personal query by name
	 * 
	 * @param queryName
	 * @param projectArea
	 * @param userHandle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IQueryDescriptor findPersonalQuery(String queryName,
			IProjectArea projectArea, IContributorHandle userHandle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		if (null == projectArea) {
			return null;
		}
		IQueryClient queryClient = getWorkItemClient(projectArea)
				.getQueryClient();
		List<IQueryDescriptor> queries = queryClient.findPersonalQueries(
				projectArea.getProjectArea(), userHandle,
				QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE,
				monitor);
		return findQuery(queryName, queries);
	}

	/**
	 * Find shared query by name
	 * 
	 * @param queryName
	 * @param projectArea
	 * @param sharingTargets
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IQueryDescriptor findSharedQuery(String queryName,
			IProjectArea projectArea, List<IAuditableHandle> sharingTargets,
			IProgressMonitor monitor) throws TeamRepositoryException {
		IQueryClient queryClient = getWorkItemClient(projectArea)
				.getQueryClient();
		List<IQueryDescriptor> queries = queryClient.findSharedQueries(
				projectArea.getProjectArea(), sharingTargets,
				QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE,
				monitor);
		return findQuery(queryName, queries);
	}

	/**
	 * Find query with matching name from list
	 * 
	 * @param queryName
	 * @param queries
	 * @return
	 * @throws TeamRepositoryException
	 */
	private static IQueryDescriptor findQuery(String queryName,
			List<IQueryDescriptor> queries) throws TeamRepositoryException {
		for (Iterator<IQueryDescriptor> iterator = queries.iterator(); iterator
				.hasNext();) {
			IQueryDescriptor iQueryDescriptor = (IQueryDescriptor) iterator
					.next();
			if (iQueryDescriptor.getName().equals(queryName)) {
				return iQueryDescriptor;
			}
		}
		return null;
	}

	/**
	 * Get the workItemClient
	 * 
	 * @param projectArea
	 * @return
	 */
	private static IWorkItemClient getWorkItemClient(
			IProjectAreaHandle projectArea) {
		return (IWorkItemClient) ((ITeamRepository) projectArea.getOrigin())
				.getClientLibrary(IWorkItemClient.class);
	}

	/**
	 * Get the query result
	 * 
	 * @param query
	 * @param overrideResultLimit
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IQueryResult<IResult> getUnresolvedQueryResult(
			IQueryDescriptor query, boolean overrideResultLimit)
			throws TeamRepositoryException {
		if (query == null) {
			throw new WorkItemCommandLineException("Query must not be null");
		}
		IQueryClient queryClient = getWorkItemClient(query.getProjectArea())
				.getQueryClient();
		IQueryResult<IResult> results = queryClient.getQueryResults(query);
		if (overrideResultLimit) {
			((QueryResultIterator) results).setLimit(Integer.MAX_VALUE);
		}
		return results;
	}

	/**
	 * Find the sharing targets to find the query
	 * 
	 * @param sharingTargetNames
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static List<IAuditableHandle> findSharingTargets(
			String sharingTargetNames,
			IProcessClientService processClientService, IProgressMonitor monitor)
			throws TeamRepositoryException {
		List<IAuditableHandle> sharingTargets = new ArrayList<IAuditableHandle>(
				10);
		List<String> processAreaNames = StringUtil.splitStringToList(
				sharingTargetNames, WorkItemUpdateHelper.ITEM_SEPARATOR);
		for (String processAreaName : processAreaNames) {
			IProcessArea area = ProcessAreaUtil.findProcessAreaByFQN(
					processAreaName, processClientService, monitor);
			if (area != null) {
				sharingTargets.add(area);
			}
		}
		return sharingTargets;
	}

}
