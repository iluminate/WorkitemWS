package com.everis.fallas.operacionales.workitem.parameter;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;

public class ParameterManager {

	private ParameterList fRequiredParameters = new ParameterList();
	private ParameterList fParsedParameters = null;

							public ParameterManager(ParameterList arguments) {
		super();
		this.fParsedParameters = arguments;
	}

				private ParameterList getRequiredParameters() {
		return fRequiredParameters;
	}

									public void syntaxAddRequiredParameter(String name, String example) {
		getRequiredParameters().addParameter(
				Parameter.createRequiredParameter(name, example));
	}

							public void syntaxAddSwitch(String name, String value) {
		getRequiredParameters().addParameter(
				Parameter.createSwitch(name, value));
	}

							public void syntaxAddSwitch(String name) {
		getRequiredParameters()
				.addParameter(Parameter.createSwitch(name, null));
	}

							public boolean hasSwitch(String name) {
		return fParsedParameters.hasSwitch(name);
	}

										public String consumeParameter(String name) {
		return fParsedParameters.consumeParameter(name);
	}

						public String getCommand() {
		Parameter command = fParsedParameters.getCommand();
		if (command == null) {
			return null;
		}
		return command.getName();
	}

						public ParameterList getArguments() {
		return fParsedParameters;
	}

								public void validateRequiredParameters()
			throws WorkItemCommandLineException {
		ParameterList missingParameters = new ParameterList();
		ParameterList required = getRequiredParameters();
		for (Parameter requiredParameter : required) {
			if (requiredParameter.isRequired()) {
				if (fParsedParameters.getParameter(requiredParameter.getName()) == null
						&& fParsedParameters.getParameter(ParameterIDMapper
								.getAlias(requiredParameter.getName())) == null) {
					missingParameters.addParameter(requiredParameter);
				}
			}
		}
		if (!missingParameters.isEmpty()) {
			String missing = getParameterHelp(missingParameters, true);
			throw new WorkItemCommandLineException(
					"Missing required parameters:\n" + missing);
		}
	}

						public String helpUsageRequiredParameters() {
		return getParameterHelp(fRequiredParameters, false);
	}

											private String getParameterHelp(ParameterList parameters, boolean detailled) {
		String separator = detailled ? "\n" : " ";
		String linePrefix = detailled ? "\t Required: " : "";
		String missing = "";
		for (Parameter parameter : parameters) {
			String commandSwitchPrefix = "";
			String value = "";
			if (parameter.isSwitch()) {
				commandSwitchPrefix = IWorkItemCommandLineConstants.PREFIX_SWITCH;
			} else if (parameter.isCommand()) {
				commandSwitchPrefix = IWorkItemCommandLineConstants.PREFIX_COMMAND;
			} else {
				value = "=\"value\"";
			}
			missing += linePrefix + commandSwitchPrefix + parameter.getName()
					+ value;
			if (detailled) {
				missing += " Example: " + commandSwitchPrefix
						+ parameter.getName() + parameter.getExample();
			}
			missing += separator;
		}
		return missing;
	}
}
