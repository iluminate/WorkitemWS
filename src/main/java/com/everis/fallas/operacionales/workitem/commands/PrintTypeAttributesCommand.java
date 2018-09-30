package com.everis.fallas.operacionales.workitem.commands;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.framework.AbstractTeamRepositoryCommand;
import com.everis.fallas.operacionales.workitem.framework.IWorkItemCommand;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.helper.WorkItemTypeHelper;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.everis.fallas.operacionales.workitem.utils.ProcessAreaUtil;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItemType;

public class PrintTypeAttributesCommand extends AbstractTeamRepositoryCommand implements IWorkItemCommand {

	public PrintTypeAttributesCommand(ParameterManager parameterManager) {
		super(parameterManager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemCommandLineConstants.COMMAND_PRINT_TYPE_ATTRIBUTES;
	}

	public void setRequiredParameters() {
		super.setRequiredParameters();
		getParameterManager().syntaxAddRequiredParameter(
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddRequiredParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY_EXAMPLE);
	}

	@Override
	public OperationResult process() throws TeamRepositoryException {
		String projectAreaName = getParameterManager()
				.consumeParameter(IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY).trim();

		IProjectArea projectArea = ProcessAreaUtil.findProjectAreaByFQN(projectAreaName, getProcessClientService(),
				getMonitor());
		if (projectArea == null) {
			throw new WorkItemCommandLineException("Project Area not found: " + projectAreaName);
		}

		String workItemTypeID = getParameterManager()
				.consumeParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY).trim();
		IWorkItemType workItemType = getWorkItemCommon().findWorkItemType(projectArea, workItemTypeID, getMonitor());
		if (workItemType == null) {
			throw new WorkItemCommandLineException("Work item type not found: " + workItemTypeID);
		}
		this.addOperationResult(printTypeAttributes(projectArea, workItemType));
		return getResult();
	}

	private OperationResult printTypeAttributes(IProjectArea projectArea, IWorkItemType workItemType)
			throws TeamRepositoryException {

		WorkItemTypeHelper workItemTypeHelper = new WorkItemTypeHelper(projectArea, getMonitor());

		return workItemTypeHelper.printAttributesOfType(projectArea, workItemType, getMonitor());
	}

	@Override
	public String helpSpecificUsage() {
		return "";
	}

}