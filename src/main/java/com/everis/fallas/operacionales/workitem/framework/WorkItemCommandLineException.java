/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.framework;

/**
 * Exception used internally to wrap issues to throw and process them.
 * 
 */
public class WorkItemCommandLineException extends RuntimeException {

	Throwable ex = null;

	/**
	 * Base constructor
	 */
	public WorkItemCommandLineException() {
		super();
	}

	/**
	 * Just throw with a simple message.
	 * 
	 * @param message
	 */
	public WorkItemCommandLineException(String message) {
		super(message);
	}

	/**
	 * Just throw another throwable
	 * 
	 * @param throwable
	 */
	public WorkItemCommandLineException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Constructor to warp an exception into another one and provide both info.
	 * 
	 * @param message
	 * @param throwable
	 */
	public WorkItemCommandLineException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7933361626497401499L;

}
