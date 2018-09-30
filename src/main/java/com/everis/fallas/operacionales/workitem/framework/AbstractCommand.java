package com.everis.fallas.operacionales.workitem.framework;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.ibm.team.repository.common.TeamRepositoryException;

public abstract class AbstractCommand implements IWorkItemCommand {

	private ParameterManager params;
	private IProgressMonitor monitor = null;
	private OperationResult result = new OperationResult();

	protected void addOperationResult(OperationResult operationResult) {
		this.result.addOperationResult(operationResult);
	}

	public void appendResultString(String value) {
		this.result.appendResultString(value);
	}

	public void setSuccess() {
		this.result.setSuccess();
	}

	public void setFailed() {
		this.result.setFailed();
	}

	public OperationResult getResult() {
		return this.result;
	}

	protected AbstractCommand(ParameterManager parametermanager) {
		this.params = parametermanager;
		initialize();
	}

	protected IProgressMonitor getMonitor() {
		return monitor;
	}

	protected ParameterManager getParameterManager() {
		return params;
	}

	public void setParameterManager(ParameterManager params) {
		this.params = params;
	}

	public void initialize() {
		setRequiredParameters();
	}

	public abstract void setRequiredParameters();

	@Override
	public OperationResult execute(IProgressMonitor monitor) throws TeamRepositoryException {
		return process();
	}

	@Override
	public String helpUsage() {
		return getParameterManager().helpUsageRequiredParameters() + " " + helpSpecificUsage();
	}

	public abstract String helpSpecificUsage();

	@Override
	public void validateRequiredParameters() {
		getParameterManager().validateRequiredParameters();
	}

	public abstract OperationResult process() throws TeamRepositoryException;

}
