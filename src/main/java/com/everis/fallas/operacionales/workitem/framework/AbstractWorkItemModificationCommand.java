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

public abstract class AbstractWorkItemModificationCommand extends
		AbstractTeamRepositoryCommand {

	private boolean fIgnoreErrors = false;

							public void setIgnoreErrors(boolean value) {
		this.fIgnoreErrors = value;
	}

						public boolean isIgnoreErrors() {
		return this.fIgnoreErrors;
	}

							protected AbstractWorkItemModificationCommand(
			ParameterManager parametermanager) {
		super(parametermanager);
	}

						protected class ModifyWorkItem extends WorkItemOperation {

														public ModifyWorkItem(String message) {
			super(message, IWorkItem.FULL_PROFILE);
		}

														public ModifyWorkItem(String message, ItemProfile<IWorkItem> profile) {
			super(message, profile);
		}

																						@Override
		protected void execute(WorkItemWorkingCopy workingCopy,
				IProgressMonitor monitor) throws TeamRepositoryException,
				RuntimeException {
			update(workingCopy);
		}
	}

										public void update(WorkItemWorkingCopy workingCopy)
			throws RuntimeException, TeamRepositoryException {

		ParameterList arguments = getParameterManager().getArguments();

		WorkItemUpdateHelper workItemHelper = new WorkItemUpdateHelper(
				workingCopy, arguments, getMonitor());

		for (Parameter parameter : arguments) {
			if (!(parameter.isConsumed() || parameter.isSwitch() || parameter
					.isCommand())) {
				String propertyName = parameter.getName();
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

						protected class ChangeType extends WorkItemOperation {

		private IWorkItemType fOldType;
		private IWorkItemType fNewType;

														public ChangeType(String message, IWorkItemType oldType,
				IWorkItemType newType) {
			super(message, IWorkItem.FULL_PROFILE);
			this.fOldType = oldType;
			this.fNewType = newType;
		}

																						@Override
		protected void execute(WorkItemWorkingCopy workingCopy,
				IProgressMonitor monitor) throws TeamRepositoryException,
				RuntimeException {
			getWorkItemCommon().updateWorkItemType(workingCopy.getWorkItem(),
					fNewType, fOldType, getMonitor());
		}
	}

}
