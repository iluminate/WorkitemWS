/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
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

/**
 * Command to update a work item, set the provided values and save it. The
 * operation is governed by the process and might fail if required parameters
 * are missing.
 * 
 */
public class UpdateWorkItemCommand extends AbstractWorkItemModificationCommand {

	/**
	 * @param parametermanager
	 */
	public UpdateWorkItemCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemCommandLineConstants.COMMAND_UPDATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.workitem.commandline.framework.AbstractWorkItemCommand
	 * #setRequiredParameters()
	 */
	public void setRequiredParameters() {
		super.setRequiredParameters();
		// Add the parameters required to perform the operation
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.workitem.commandline.framework.AbstractWorkItemCommand
	 * #process()
	 */
	@Override
	public OperationResult process() throws TeamRepositoryException {
		// Get the parameters such as the work item ID and run the operation
		String wiID = getParameterManager().consumeParameter(
				IWorkItemCommandLineConstants.PARAMETER_WORKITEM_ID_PROPERTY);
		IWorkItem workItem = WorkItemUtil.findWorkItemByID(wiID,
				IWorkItem.SMALL_PROFILE, getWorkItemCommon(), getMonitor());
		if (workItem == null) {
			throw new WorkItemCommandLineException(
					"Work item cannot be found ID: " + wiID);
		}
		// Update the work item
		updateWorkItem(workItem);
		return this.getResult();
	}

	/**
	 * Run the update work item operation
	 * 
	 * @param workItem
	 * @return
	 * @throws TeamRepositoryException
	 */
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
					// If we have no type we can't create the work item
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
