package com.everis.fallas.operacionales.workitem.parameter;

import java.util.HashMap;
import java.util.Iterator;

public class ParameterList implements Iterable<Parameter> {

	private HashMap<String, Parameter> fParameterList = new HashMap<String, Parameter>();
	private Parameter fCommand;

	public ParameterList() {
		super();
	}

									public void addParameterValue(String name, String value) {
		fParameterList.put(name, Parameter.createParameterValue(name, value));

	}

							public void addCommand(String name) {
		Parameter command = Parameter.createCommand(name);
		this.fCommand = command;
		fParameterList.put(command.getName(), command);
	}

							public void addParameter(Parameter parameter) {
		fParameterList.put(parameter.getName(), parameter);
	}

							public void addSwitch(String switchName, String switchValue) {
		Parameter aSwitch = Parameter.createSwitch(switchName, switchValue);
		fParameterList.put(aSwitch.getName(), aSwitch);
	}

						public Parameter getCommand() {
		return fCommand;
	}

								public boolean hasSwitch(String name) {
		Parameter param = fParameterList.get(name);
		return (param != null && param.isSwitch());
	}

								public String consumeParameter(String name) {
		Parameter param = fParameterList.get(name);
		if (param != null) {
			param.setConsumed();
			return param.getValue();
		}
		return null;
	}

								public Parameter getParameter(String name) {
		return fParameterList.get(name);
	}

						@Override
	public Iterator<Parameter> iterator() {
		return fParameterList.values().iterator();
	}

				public boolean isEmpty() {
		return fParameterList.isEmpty();
	}

}
