package com.everis.fallas.operacionales.workitem.utils;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;

public class AttributeUtil {

																					public static IAttribute resolveAttribute(String attributeID,
			IProjectAreaHandle projectAreaHandle, IProgressMonitor monitor)
			throws TeamRepositoryException {
		if (projectAreaHandle == null) {
			throw new WorkItemCommandLineException(
					"Resolve Attribute: project area handle must not be null");
		}
		ITeamRepository teamRepository = (ITeamRepository) projectAreaHandle
				.getOrigin();
		IWorkItemCommon workItemCommon = (IWorkItemCommon) teamRepository
				.getClientLibrary(IWorkItemCommon.class);
		return workItemCommon.findAttribute(projectAreaHandle, attributeID,
				monitor);
	}
}
