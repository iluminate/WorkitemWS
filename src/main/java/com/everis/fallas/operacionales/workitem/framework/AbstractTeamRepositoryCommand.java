package com.everis.fallas.operacionales.workitem.framework;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.ITeamRepository.ILoginHandler;
import com.ibm.team.repository.client.ITeamRepository.ILoginHandler.ILoginInfo;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IAuditableCommon;
import com.ibm.team.workitem.common.IWorkItemCommon;

public abstract class AbstractTeamRepositoryCommand extends AbstractCommand {

	private ITeamRepository fTeamRepository;

	protected AbstractTeamRepositoryCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

										@Override
	public void setRequiredParameters() {
		getParameterManager()
				.syntaxAddRequiredParameter(
						IWorkItemCommandLineConstants.PARAMETER_REPOSITORY_URL_PROPERTY,
						IWorkItemCommandLineConstants.PARAMETER_REPOSITORY_URL_PROPERTY_EXAMPLE);
		getParameterManager()
				.syntaxAddRequiredParameter(
						IWorkItemCommandLineConstants.PARAMETER_USER_ID_PROPERTY,
						IWorkItemCommandLineConstants.PARAMETER_USER_ID_PROPERTY_EXAMPLE);
		getParameterManager()
				.syntaxAddRequiredParameter(
						IWorkItemCommandLineConstants.PARAMETER_PASSWORD_PROPERTY,
						IWorkItemCommandLineConstants.PARAMETER_PASSWORD_PROPERTY_EXAMPLE);
	}

	@Override
	public void initialize() {
		if (!TeamPlatform.isStarted()) {
			System.out.println("Starting Team Platform ...");
			TeamPlatform.startup();
		}
		super.initialize();
	}

	@Override
	public OperationResult execute(IProgressMonitor monitor)
			throws TeamRepositoryException {

		try {
			this.fTeamRepository = login();
		} catch (TeamRepositoryException e) {
			this.appendResultString("TeamRepositoryException: Unable to log into repository!");
			this.appendResultString(e.getMessage());
			this.setFailed();
			return getResult();
		}
		try {
			this.process();
		} catch (TeamRepositoryException e) {
			this.appendResultString("TeamRepositoryException: Unable to process!");
			this.appendResultString("This is often due to a link creation making the target work item invalid. ");
			this.appendResultString("For example creating a parent chld relationship to a work item that already has a parent.");
			this.appendResultString(e.getMessage());
			this.setFailed();
		}
		return getResult();
	}

	protected ITeamRepository getTeamRepository() {
		return fTeamRepository;
	}

				protected IProcessClientService getProcessClientService() {
		IProcessClientService fProcessClient = (IProcessClientService) getTeamRepository()
				.getClientLibrary(IProcessClientService.class);
		return fProcessClient;
	}

				protected IWorkItemCommon getWorkItemCommon() {
		IWorkItemCommon workItemClient = (IWorkItemCommon) getTeamRepository()
				.getClientLibrary(IWorkItemCommon.class);
		return workItemClient;
	}

				protected IAuditableCommon getAuditableCommon() {
		return (IAuditableCommon) getTeamRepository().getClientLibrary(
				IAuditableCommon.class);
	}

								private ITeamRepository login() throws TeamRepositoryException {
		String repository = getParameterManager()
				.consumeParameter(
						IWorkItemCommandLineConstants.PARAMETER_REPOSITORY_URL_PROPERTY);
		String user = getParameterManager().consumeParameter(
				IWorkItemCommandLineConstants.PARAMETER_USER_ID_PROPERTY);
		String password = getParameterManager().consumeParameter(
				IWorkItemCommandLineConstants.PARAMETER_PASSWORD_PROPERTY);

		ITeamRepository teamRepository = TeamPlatform
				.getTeamRepositoryService().getTeamRepository(repository);
		teamRepository.registerLoginHandler(new LoginHandler(user, password));
		teamRepository.login(getMonitor());
		return teamRepository;
	}

					private static class LoginHandler implements ILoginHandler, ILoginInfo {

		private String fUserId;
		private String fPassword;

		private LoginHandler(String userId, String password) {
			fUserId = userId;
			fPassword = password;
		}

		public String getUserId() {
			return fUserId;
		}

		public String getPassword() {
			return fPassword;
		}

		public ILoginInfo challenge(ITeamRepository repository) {
			return this;
		}
	}

}
