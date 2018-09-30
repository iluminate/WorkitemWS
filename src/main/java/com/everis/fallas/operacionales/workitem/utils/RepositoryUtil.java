package com.everis.fallas.operacionales.workitem.utils;

import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.everis.fallas.operacionales.workitem.bean.Auditoria;
import com.everis.fallas.operacionales.workitem.service.OperarWorkitem;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;

public class RepositoryUtil {
	
	private final static Logger log = Logger.getLogger(OperarWorkitem.class);
	
	private String hostname;
	private String username;
	private String password;
	private ITeamRepository teamrepo;
	private String message = "";
	
	public RepositoryUtil(Auditoria audit) {
		this.hostname = audit.getRepository();
		this.username = audit.getUsername();
		this.password = audit.getPassword();
		this.message = "[" + audit.getIdTransaccion() + "] - ";
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
		log.info(message + "Conectando . . .");
		teamrepo.login(progressMonitor, true);
		log.info(message + "Conexion exitosa.");
	}
	public void logout() {
		if(TeamPlatform.isStarted()) {
			TeamPlatform.shutdown();
		}
		log.info(message + "Desconectando . . .");
		teamrepo.logout();
		log.info(message + "Desconexion exitosa.");
	}
	public static ILoginHandler2 getLoginHandler(final String user, final String password) {
		return new ILoginHandler2() {
			public ILoginInfo2 challenge(ITeamRepository repo) {
				return new UsernameAndPasswordLoginInfo(user, password);
			}
		};
	}
}
