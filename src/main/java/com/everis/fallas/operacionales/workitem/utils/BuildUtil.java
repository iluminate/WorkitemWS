package com.everis.fallas.operacionales.workitem.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.ibm.team.build.common.model.IBuildDefinition;
import com.ibm.team.build.common.model.IBuildDefinitionHandle;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.build.common.model.IBuildResultHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IFetchResult;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;

public class BuildUtil {

	public static IBuildResult findBuildResultbyID(String buildResultID, ITeamRepository teamRepository,
			IProgressMonitor monitor) throws TeamRepositoryException, WorkItemCommandLineException {

		IBuildResultHandle resultHandle;
		try {
			resultHandle = (IBuildResultHandle) IBuildResult.ITEM_TYPE.createItemHandle(UUID.valueOf(buildResultID),
					null);
		} catch (Exception e) {
			throw new WorkItemCommandLineException("Exception getting build from label: " + e.getMessage(), e);
		}
		ArrayList<IBuildResultHandle> handleList = new ArrayList<IBuildResultHandle>();
		handleList.add(resultHandle);

		IFetchResult fetchResult = teamRepository.itemManager().fetchCompleteItemsPermissionAware(handleList,
				IItemManager.REFRESH, monitor);
		@SuppressWarnings("rawtypes")
		List retrieved = fetchResult.getRetrievedItems();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = retrieved.iterator(); iterator.hasNext();) {
			Object result = (Object) iterator.next();
			if (result instanceof IBuildResult) {
				return (IBuildResult) result;
			}
		}
		if (fetchResult.hasPermissionDeniedItems()) {
			throw new WorkItemCommandLineException("Access denied to Build Result: " + buildResultID);
		}
		if (fetchResult.hasNotFoundItems()) {
			throw new WorkItemCommandLineException("Build Result not found " + buildResultID);
		}

		throw new WorkItemCommandLineException("Build Result not found " + buildResultID);
	}

	public static IBuildResult resolveBuildResult(IBuildResultHandle buildResult, ITeamRepository teamRepository,
			IProgressMonitor monitor) throws TeamRepositoryException, WorkItemCommandLineException {

		ArrayList<IBuildResultHandle> handleList = new ArrayList<IBuildResultHandle>();
		handleList.add(buildResult);

		IFetchResult fetchResult = teamRepository.itemManager().fetchCompleteItemsPermissionAware(handleList,
				IItemManager.REFRESH, monitor);
		@SuppressWarnings("rawtypes")
		List retrieved = fetchResult.getRetrievedItems();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = retrieved.iterator(); iterator.hasNext();) {
			Object result = (Object) iterator.next();
			if (result instanceof IBuildResult) {
				return (IBuildResult) result;
			}
		}
		if (fetchResult.hasPermissionDeniedItems()) {
			throw new WorkItemCommandLineException("Access denied to Build Result: " + buildResult.getItemId());
		}
		if (fetchResult.hasNotFoundItems()) {
			throw new WorkItemCommandLineException("Build Result not found " + buildResult.getItemId());
		}

		throw new WorkItemCommandLineException("Build Result not found " + buildResult.getItemId());
	}

	public static IBuildDefinition resolveBuildDefinition(IBuildDefinitionHandle buildDefinition,
			ITeamRepository teamRepository, IProgressMonitor monitor) throws TeamRepositoryException {
		ArrayList<IBuildDefinitionHandle> handleList = new ArrayList<IBuildDefinitionHandle>();
		handleList.add(buildDefinition);

		IFetchResult fetchResult = teamRepository.itemManager().fetchCompleteItemsPermissionAware(handleList,
				IItemManager.REFRESH, monitor);
		@SuppressWarnings("rawtypes")
		List retrieved = fetchResult.getRetrievedItems();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = retrieved.iterator(); iterator.hasNext();) {
			Object result = (Object) iterator.next();
			if (result instanceof IBuildDefinition) {
				return (IBuildDefinition) result;
			}
		}
		if (fetchResult.hasPermissionDeniedItems()) {
			throw new WorkItemCommandLineException("Access denied to Build Definition: " + buildDefinition.getItemId());
		}
		if (fetchResult.hasNotFoundItems()) {
			throw new WorkItemCommandLineException("Build Definition not found " + buildDefinition.getItemId());
		}

		throw new WorkItemCommandLineException("Build Definition not found " + buildDefinition.getItemId());
	}

}
