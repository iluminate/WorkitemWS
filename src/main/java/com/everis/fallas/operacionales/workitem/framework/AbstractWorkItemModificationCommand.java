/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.framework;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.helper.WorkItemUpdateHelper;
import com.everis.fallas.operacionales.workitem.parameter.Parameter;
import com.everis.fallas.operacionales.workitem.parameter.ParameterList;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.model.ItemProfile;

/**
 * Abstract Command that can be used as base class to implement new commands.
 * The class provides all the required methods to run in the context. This class
 * provides a WorkItemOperation that is used to perform the modifications within
 * a transaction.
 * 
 * It adds a new method Update that performs the attribute update operation and
 * does the error handling.
 * 
 */
public abstract class AbstractWorkItemModificationCommand extends
		AbstractTeamRepositoryCommand {

	// in some modes we want to be able to ignore an error e.g. when setting
	// multiple attributes, we might want to try to not fail if one attribute
	// can not be set.
	private boolean fIgnoreErrors = false;

	/**
	 * Set the flag
	 * 
	 * @param value
	 *            true or false
	 */
	public void setIgnoreErrors(boolean value) {
		this.fIgnoreErrors = value;
	}

	/**
	 * Check if we are in ignore error mode
	 * 
	 * @return true if we are in ignore error mode, false if not
	 */
	public boolean isIgnoreErrors() {
		return this.fIgnoreErrors;
	}

	/**
	 * The constructor that initializes the class.
	 * 
	 * @param parametermanager
	 *            Used to pass the parameters and manage required parameters.
	 */
	protected AbstractWorkItemModificationCommand(
			ParameterManager parametermanager) {
		super(parametermanager);
	}

	/**
	 * The @see com.ibm.team.workitem.client.WorkItemOperation that is used to
	 * perform the modifications.
	 * 
	 */
	protected class ModifyWorkItem extends WorkItemOperation {

		/**
		 * Constructor
		 * 
		 * @param The
		 *            title message for the operation
		 */
		public ModifyWorkItem(String message) {
			super(message, IWorkItem.FULL_PROFILE);
		}

		/**
		 * @param message
		 *            The title message for the operation
		 * @param profile
		 *            The item load profile to be used for the operation.
		 */
		public ModifyWorkItem(String message, ItemProfile<IWorkItem> profile) {
			super(message, profile);
		}

		/*
		 * This is run by the framework
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.ibm.team.workitem.client.WorkItemOperation#execute(com.ibm.team
		 * .workitem.client.WorkItemWorkingCopy,
		 * org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected void execute(WorkItemWorkingCopy workingCopy,
				IProgressMonitor monitor) throws TeamRepositoryException,
				RuntimeException {
			// run the special method in the execute.
			// This is called by the framework.
			update(workingCopy);
		}
	}

	/**
	 * This operation does the main task of updating the work item
	 * 
	 * @param workingCopy
	 *            the workingcopy of the workitem to be updated.
	 * 
	 * @throws RuntimeException
	 * @throws TeamRepositoryException
	 */
	public void update(WorkItemWorkingCopy workingCopy)
			throws RuntimeException, TeamRepositoryException {

		ParameterList arguments = getParameterManager().getArguments();

		// We use a WorkItemHelper to do the real work
		WorkItemUpdateHelper workItemHelper = new WorkItemUpdateHelper(
				workingCopy, arguments, getMonitor());

		// Run through all properties not yet consumed and try to set the values
		// as provided
		for (Parameter parameter : arguments) {
			if (!(parameter.isConsumed() || parameter.isSwitch() || parameter
					.isCommand())) {
				// Get the property ID
				String propertyName = parameter.getName();
				// Get the property value
				String propertyValue = parameter.getValue();
				try {
					workItemHelper.updateProperty(propertyName, propertyValue);
				} catch (WorkItemCommandLineException e) {
					if (this.isIgnoreErrors()) {
						this.appendResultString("Exception! " + e.getMessage());
						this.appendResultString("Ignored....... ");
					} else {
						throw e;
					}
				} catch (RuntimeException e) {
					this.appendResultString("Runtime Exception: Property "
							+ propertyName + " Value " + propertyValue + " \n"
							+ e.getMessage());
					e.printStackTrace();
					throw e;
				} catch (IOException e) {
					this.appendResultString("IO Exception: Property "
							+ propertyName + " Value " + propertyValue + " \n"
							+ e.getMessage());
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * The @see com.ibm.team.workitem.client.WorkItemOperation that is used to
	 * change a work item type.
	 * 
	 */
	protected class ChangeType extends WorkItemOperation {

		private IWorkItemType fOldType;
		private IWorkItemType fNewType;

		/**
		 * Constructor
		 * 
		 * @param The
		 *            title message for the operation
		 */
		public ChangeType(String message, IWorkItemType oldType,
				IWorkItemType newType) {
			super(message, IWorkItem.FULL_PROFILE);
			this.fOldType = oldType;
			this.fNewType = newType;
		}

		/*
		 * This is run by the framework
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.ibm.team.workitem.client.WorkItemOperation#execute(com.ibm.team
		 * .workitem.client.WorkItemWorkingCopy,
		 * org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected void execute(WorkItemWorkingCopy workingCopy,
				IProgressMonitor monitor) throws TeamRepositoryException,
				RuntimeException {
			// change the type
			getWorkItemCommon().updateWorkItemType(workingCopy.getWorkItem(),
					fNewType, fOldType, getMonitor());
		}
	}

}
