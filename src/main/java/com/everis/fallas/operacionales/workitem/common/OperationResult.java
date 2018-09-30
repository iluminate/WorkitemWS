package com.everis.fallas.operacionales.workitem.common;

import java.io.Serializable;

public class OperationResult implements Serializable {
	private static final long serialVersionUID = -3508263528723690071L;

	private String resultMessage = "";
	private boolean result = false;

	public OperationResult() {
		super();
		this.resultMessage = "";
		this.result = false;
	}

	public void addOperationResult(OperationResult result) {
		appendResultString(result.getResultString());
		this.result = result.isSuccess();
	}

	public String getResultString() {
		return resultMessage;
	}

	public void appendResultString(String value) {
		if (!WorkitemCommandLine.isServer()) {
			System.out.println(value);
		} else {
			this.resultMessage = this.resultMessage.concat(value + "\n");
		}
	}

	public boolean isSuccess() {
		return result;
	}

	public void setSuccess() {
		this.result = true;
	}

	public void setFailed() {
		this.result = false;
	}
}