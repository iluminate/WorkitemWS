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

public class QueryUtil {

	public static IQueryDescriptor findPersonalQuery(String queryName, IProjectArea projectArea,
			IContributorHandle userHandle, IProgressMonitor monitor) throws TeamRepositoryException {
		if (null == projectArea) {
			return null;
		}
		IQueryClient queryClient = getWorkItemClient(projectArea).getQueryClient();
		List<IQueryDescriptor> queries = queryClient.findPersonalQueries(projectArea.getProjectArea(), userHandle,
				QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE, monitor);
		return findQuery(queryName, queries);
	}

	public static IQueryDescriptor findSharedQuery(String queryName, IProjectArea projectArea,
			List<IAuditableHandle> sharingTargets, IProgressMonitor monitor) throws TeamRepositoryException {
		IQueryClient queryClient = getWorkItemClient(projectArea).getQueryClient();
		List<IQueryDescriptor> queries = queryClient.findSharedQueries(projectArea.getProjectArea(), sharingTargets,
				QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE, monitor);
		return findQuery(queryName, queries);
	}

	private static IQueryDescriptor findQuery(String queryName, List<IQueryDescriptor> queries)
			throws TeamRepositoryException {
		for (Iterator<IQueryDescriptor> iterator = queries.iterator(); iterator.hasNext();) {
			IQueryDescriptor iQueryDescriptor = (IQueryDescriptor) iterator.next();
			if (iQueryDescriptor.getName().equals(queryName)) {
				return iQueryDescriptor;
			}
		}
		return null;
	}

	private static IWorkItemClient getWorkItemClient(IProjectAreaHandle projectArea) {
		return (IWorkItemClient) ((ITeamRepository) projectArea.getOrigin()).getClientLibrary(IWorkItemClient.class);
	}

	public static IQueryResult<IResult> getUnresolvedQueryResult(IQueryDescriptor query, boolean overrideResultLimit)
			throws TeamRepositoryException {
		if (query == null) {
			throw new WorkItemCommandLineException("Query must not be null");
		}
		IQueryClient queryClient = getWorkItemClient(query.getProjectArea()).getQueryClient();
		IQueryResult<IResult> results = queryClient.getQueryResults(query);
		if (overrideResultLimit) {
			((QueryResultIterator) results).setLimit(Integer.MAX_VALUE);
		}
		return results;
	}

	public static List<IAuditableHandle> findSharingTargets(String sharingTargetNames,
			IProcessClientService processClientService, IProgressMonitor monitor) throws TeamRepositoryException {
		List<IAuditableHandle> sharingTargets = new ArrayList<IAuditableHandle>(10);
		List<String> processAreaNames = StringUtil.splitStringToList(sharingTargetNames,
				WorkItemUpdateHelper.ITEM_SEPARATOR);
		for (String processAreaName : processAreaNames) {
			IProcessArea area = ProcessAreaUtil.findProcessAreaByFQN(processAreaName, processClientService, monitor);
			if (area != null) {
				sharingTargets.add(area);
			}
		}
		return sharingTargets;
	}

}
