package com.everis.fallas.operacionales.workitem.commands;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.framework.AbstractWorkItemModificationCommand;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.helper.WorkItemTypeHelper;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.everis.fallas.operacionales.workitem.utils.ProcessAreaUtil;
import com.everis.fallas.operacionales.workitem.utils.WorkItemUtil;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.advice.TeamOperationCanceledException;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;

public class CreateWorkItemCommand extends AbstractWorkItemModificationCommand {

	public CreateWorkItemCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemCommandLineConstants.COMMAND_CREATE;
	}

	public void setRequiredParameters() {
		super.setRequiredParameters();
		getParameterManager().syntaxAddRequiredParameter(
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddRequiredParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddSwitch(IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS);
		getParameterManager().syntaxAddSwitch(IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS);
		getParameterManager().syntaxAddSwitch(IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_APPROVALS);
		getParameterManager().syntaxAddSwitch(IWorkItemCommandLineConstants.SWITCH_ENFORCE_SIZE_LIMITS);

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
		IWorkItemType workItemType = WorkItemTypeHelper.findWorkItemType(workItemTypeID, projectArea.getProjectArea(),
				getWorkItemCommon(), getMonitor());
		createWorkItem(workItemType);
		return this.getResult();
	}

	private boolean createWorkItem(IWorkItemType workItemType) throws TeamRepositoryException {

		ModifyWorkItem operation = new ModifyWorkItem("Creating Work Item");
		this.setIgnoreErrors(getParameterManager().hasSwitch(IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS));
		IWorkItemHandle handle;
		try {
			handle = operation.run(workItemType, getMonitor());
		} catch (TeamOperationCanceledException e) {
			throw new WorkItemCommandLineException("Work item not created. " + e.getMessage(), e);
		}
		if (handle == null) {
			throw new WorkItemCommandLineException("Work item not created, cause unknown.");
		} else {
			IWorkItem workItem = WorkItemUtil.resolveWorkItem(handle, IWorkItem.SMALL_PROFILE, getWorkItemCommon(),
					getMonitor());
			this.appendResultString("Created work item " + workItem.getId() + ".");
			this.setSuccess();
		}
		return true;
	}

	@Override
	public String helpSpecificUsage() {
		return "{parameter[:mode]=value}";
	}
}
