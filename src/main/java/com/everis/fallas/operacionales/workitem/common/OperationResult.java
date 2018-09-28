/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2008-20015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.common;

import java.io.Serializable;

/**
 * Manages the result of an operation across RMI calls
 * 
 */
public class OperationResult implements Serializable {
	/**
	 * Serialisation for RMI
	 */
	private static final long serialVersionUID = -3508263528723690071L;

	private String resultMessage = "";
	private boolean result = false;

	/**
	 * Basic constructor
	 */
	public OperationResult() {
		super();
		this.resultMessage = "";
		this.result = false;
	}

	/**
	 * Add an operation result to an existing one and owerwrite the result
	 * 
	 * @param result
	 */
	public void addOperationResult(OperationResult result) {
		appendResultString(result.getResultString());
		this.result = result.isSuccess();
	}

	/**
	 * @return the result string
	 */
	public String getResultString() {
		return resultMessage;
	}

	/**
	 * Append a line to an existing result
	 * 
	 * @param value
	 */
	public void appendResultString(String value) {
		if (!WorkitemCommandLine.isServer()) {
			System.out.println(value);
		} else {
			this.resultMessage = this.resultMessage.concat(value + "\n");
		}
	}

	/**
	 * @return if the result is successful or not
	 */
	public boolean isSuccess() {
		return result;
	}

	/**
	 * Set the result to success
	 */
	public void setSuccess() {
		this.result = true;
	}

	/**
	 * Set the result to failed
	 */
	public void setFailed() {
		this.result = false;
	}
}