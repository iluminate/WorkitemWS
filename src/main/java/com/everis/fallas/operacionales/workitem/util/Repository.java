package com.everis.fallas.operacionales.workitem.util;

import java.net.SocketTimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.everis.fallas.operacionales.workitem.bean.Auditoria;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;

public class Repository {
	private String hostname;
	private String username;
	private String password;
	private ITeamRepository teamrepo;
	public Repository(Auditoria audit) {
		this.hostname = audit.getRepository();
		this.username = audit.getUsername();
		this.password = audit.getPassword();
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ITeamRepository getTeamrepo() {
		return teamrepo;
	}
	public void login() throws TeamRepositoryException, SocketTimeoutException {
		if(!TeamPlatform.isStarted()) {
			TeamPlatform.startup();
		}
		teamrepo = TeamPlatform.getTeamRepositoryService().getTeamRepository(this.getHostname());
		teamrepo.registerLoginHandler(getLoginHandler(this.username, this.getPassword()));
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		System.out.println("logining . . .");
		teamrepo.login(progressMonitor);
		System.out.println("login exitoso.");
	}
	public void logout() {
		if(TeamPlatform.isStarted()) {
			TeamPlatform.shutdown();
		}
		teamrepo.logout();
	}
	public static ILoginHandler2 getLoginHandler(final String user, final String password) {
		return new ILoginHandler2() {
			public ILoginInfo2 challenge(ITeamRepository repo) {
				return new UsernameAndPasswordLoginInfo(user, password);
			}
		};
	}
}
