/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.framework;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.OperationResult;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 * Interface to be implemented by all commands to be added to the existing list
 * of commands.
 * 
 */
public interface IWorkItemCommand {

	/**
	 * @return the name of the command that is supported by this class
	 */
	public String getCommandName();

	/**
	 * To initialize a parameter manager.
	 * 
	 * @param params
	 */
	public void initialize();

	/**
	 * Execute an operation. Passes the required interfaces and values.
	 * 
	 * @param monitor
	 *            a progress monitor, may be null.
	 * @return
	 * @throws TeamRepositoryException
	 */
	public OperationResult execute(IProgressMonitor monitor)
			throws TeamRepositoryException;

	/**
	 * Used to print user help on the command.
	 * 
	 * @return a string explaining how the command is used.
	 */
	public String helpUsage();

	/**
	 * Validate the required parameters are supplied
	 */
	public void validateRequiredParameters();
}
