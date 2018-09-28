/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 * The Interface to be used and implemented in RMI mode
 * 
 * The server has to implement the method. The client calls this method.
 * 
 * The result (e.g. error messages and other strings) is passed back
 * 
 */
public interface IRemoteWorkItemOperationCall extends Remote {

	public static final String LOCALHOST_REMOTE_WORKITEM_COMMANDLINE_SERVER = "//localhost/RemoteWorkitemCommandLineServer";
	public static final int RMI_REGISTRY_PORT = 1099;

	/**
	 * The main entry to call
	 * 
	 * @param args
	 * @return
	 * @throws TeamRepositoryException
	 * @throws RemoteException
	 */
	public abstract OperationResult runOperation(String[] args)
			throws TeamRepositoryException, RemoteException;
}