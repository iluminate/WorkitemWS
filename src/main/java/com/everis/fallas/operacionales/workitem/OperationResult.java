package com.everis.fallas.operacionales.workitem;

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
	public String getResultString() {
		return resultMessage;
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