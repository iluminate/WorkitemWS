package com.everis.fallas.operacionales.workitem.parameter;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;

public class ParameterParser {

	private ParameterList parameterList = null;

				private ParameterParser() {
		super();
		this.parameterList = new ParameterList();
	}

				private ParameterList getParameterList() {
		return parameterList;
	}

							private ParameterList parse(String[] args) {
		this.parameterList = new ParameterList();
		for (int i = 0; i < args.length; i++) {
			parseParameter(args[i]);
		}
		return this.parameterList;
	}

								public static ParameterList parseParameters(String[] args) {
		ParameterParser parser = new ParameterParser();
		return parser.parse(args);
	}

																	private void parseParameter(String inputString) {
		if (null == inputString) {
			return;
		}
		if (inputString
				.startsWith(IWorkItemCommandLineConstants.PREFIX_COMMAND)) {
			String foundCommand = inputString.substring(1);
			addCommand(foundCommand);
		} else if (inputString
				.startsWith(IWorkItemCommandLineConstants.PREFIX_SWITCH)) {
			String foundSwitch = inputString.substring(1);
			String switchValue = null;
			int delimiter = inputString
					.indexOf(IWorkItemCommandLineConstants.INFIX_PARAMETER_VALUE_SEPARATOR);
			if (delimiter > 0) {
				foundSwitch = inputString.substring(1, delimiter);
				if (inputString.length() >= delimiter + 1) {
					switchValue = inputString.substring(delimiter + 1);
				}
			}
			addSwitch(foundSwitch, switchValue);
		} else {
			String parameterName = inputString;			String parameterValue = null;

			parameterValue = null;
			int delimiter = inputString
					.indexOf(IWorkItemCommandLineConstants.INFIX_PARAMETER_VALUE_SEPARATOR);
			if (delimiter > 0) {
				parameterName = inputString.substring(0, delimiter);
				if (inputString.length() >= delimiter + 1) {
					parameterValue = inputString.substring(delimiter + 1);
				}
			}
			addParameterValue(parameterName, parameterValue);
		}
		return;
	}

									private void addParameterValue(String parameterName, String parameterValue) {
		if (getParameterList().getParameter(parameterName) != null) {
			throw new WorkItemCommandLineException("Duplicate parameter: "
					+ parameterName);
		}
		getParameterList().addParameterValue(parameterName, parameterValue);
	}

							private void addSwitch(String name, String value) {
		if (getParameterList().getParameter(name) != null) {
			throw new WorkItemCommandLineException("Duplicate switch: "
					+ IWorkItemCommandLineConstants.PREFIX_SWITCH + name);
		}
		getParameterList().addParameter(Parameter.createSwitch(name, value));
	}

							private void addCommand(String name) {
		if (getParameterList().getCommand() != null) {
			throw new WorkItemCommandLineException(
					"Ambiguous command parameter: "
							+ IWorkItemCommandLineConstants.PREFIX_COMMAND
							+ name);
		}
		getParameterList().addCommand(name);
	}
}
