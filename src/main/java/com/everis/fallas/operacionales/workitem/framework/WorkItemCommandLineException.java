package com.everis.fallas.operacionales.workitem.framework;

public class WorkItemCommandLineException extends RuntimeException {

	Throwable ex = null;

	public WorkItemCommandLineException() {
		super();
	}

	public WorkItemCommandLineException(String message) {
		super(message);
	}

	public WorkItemCommandLineException(Throwable throwable) {
		super(throwable);
	}

	public WorkItemCommandLineException(String message, Throwable throwable) {
		super(message, throwable);
	}

	private static final long serialVersionUID = 7933361626497401499L;

}
