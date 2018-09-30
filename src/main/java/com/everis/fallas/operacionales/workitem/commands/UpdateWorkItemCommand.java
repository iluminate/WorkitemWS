package com.everis.fallas.operacionales.workitem.commands;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.framework.AbstractWorkItemModificationCommand;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.helper.WorkItemTypeHelper;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.everis.fallas.operacionales.workitem.utils.WorkItemUtil;
import com.ibm.team.process.common.advice.TeamOperationCanceledException;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;

public class UpdateWorkItemCommand extends AbstractWorkItemModificationCommand {

				public UpdateWorkItemCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemCommandLineConstants.COMMAND_UPDATE;
	}

								public void setRequiredParameters() {
		super.setRequiredParameters();
		getParameterManager()
				.syntaxAddRequiredParameter(
						IWorkItemCommandLineConstants.PARAMETER_WORKITEM_ID_PROPERTY,
						IWorkItemCommandLineConstants.PROPERTY_WORKITEM_ID_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddSwitch(
				IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_APPROVALS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemCommandLineConstants.SWITCH_ENFORCE_SIZE_LIMITS);
	}

								@Override
	public OperationResult process() throws TeamRepositoryException {
		String wiID = getParameterManager().consumeParameter(
				IWorkItemCommandLineConstants.PARAMETER_WORKITEM_ID_PROPERTY);
		IWorkItem workItem = WorkItemUtil.findWorkItemByID(wiID,
				IWorkItem.SMALL_PROFILE, getWorkItemCommon(), getMonitor());
		if (workItem == null) {
			throw new WorkItemCommandLineException(
					"Work item cannot be found ID: " + wiID);
		}
		updateWorkItem(workItem);
		return this.getResult();
	}

								private boolean updateWorkItem(IWorkItem workItem)
			throws TeamRepositoryException {
		this.setIgnoreErrors(getParameterManager().hasSwitch(
				IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS));
		ModifyWorkItem operation = new ModifyWorkItem("Updating work Item",
				IWorkItem.FULL_PROFILE);
		try {
			this.appendResultString("Updating work item " + workItem.getId()
					+ ".");
			String workItemTypeID = getParameterManager().consumeParameter(
					IWorkItem.TYPE_PROPERTY);
			if (workItemTypeID != null) {
				IWorkItemType newType = WorkItemTypeHelper
						.findWorkItemTypeByIDAndDisplayName(workItemTypeID,
								workItem.getProjectArea(), getWorkItemCommon(),
								getMonitor());
				if (newType == null) {
					throw new WorkItemCommandLineException("Work item type "
							+ workItemTypeID + " not found in project area. ");
				}
				IWorkItemType oldType = WorkItemTypeHelper.findWorkItemType(
						workItem.getWorkItemType(), workItem.getProjectArea(),
						getWorkItemCommon(), getMonitor());
				ChangeType changeTypeOperation = new ChangeType(
						"Changing work item type", oldType, newType);
				changeTypeOperation.run(workItem, getMonitor());
			}
			operation.run(workItem, getMonitor());
			this.setSuccess();
			this.appendResultString("Updated work item " + workItem.getId()
					+ ".");
		} catch (TeamOperationCanceledException e) {
			throw new WorkItemCommandLineException("Work item not updated. "
					+ e.getMessage(), e);
		}
		return true;
	}

	@Override
	public String helpSpecificUsage() {
		return "{parameter[:mode]=value}";
	}
}
