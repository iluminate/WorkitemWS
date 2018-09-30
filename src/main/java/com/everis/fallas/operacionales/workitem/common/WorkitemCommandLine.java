package com.everis.fallas.operacionales.workitem.common;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import com.everis.fallas.operacionales.workitem.framework.IWorkItemCommand;
import com.ibm.team.repository.client.TeamPlatform;

public class WorkitemCommandLine extends UnicastRemoteObject {
	
	private static final long serialVersionUID = -3533140774804941834L;
	
	private HashMap<String, IWorkItemCommand> supportedCommands = new HashMap<String, IWorkItemCommand>();

	public WorkitemCommandLine() throws RemoteException {
		super();
	}
	
	private static boolean isServer = false;
	
	public static boolean isServer() {
		return isServer;
	}
	
	private static class TerminateRuntimeHook extends Thread {
		@Override
		public void run() {
			if (TeamPlatform.isStarted()) {
				TeamPlatform.shutdown();
			}
		}
	}
	
	private static final TerminateRuntimeHook hook;
	static {
		hook = new TerminateRuntimeHook();
		Runtime.getRuntime().addShutdownHook(hook);
	}

	@SuppressWarnings("unused")
	private HashMap<String, IWorkItemCommand> getSupportedCommands() {
		return supportedCommands;
	}
}
