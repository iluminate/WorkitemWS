package com.everis.fallas.operacionales.workitem.framework;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.ibm.team.repository.common.TeamRepositoryException;

public interface IWorkItemCommand {

	public String getCommandName();

	public void initialize();

	public OperationResult execute(IProgressMonitor monitor) throws TeamRepositoryException;

	public String helpUsage();

	public void validateRequiredParameters();
}
