package com.everis.fallas.operacionales.workitem.parameter;

public class Parameter {
	private String fName = null;
	private String fValue = null;
	private String fExample = null;
	private boolean fRequired = false;
	private boolean isSwitch = false;
	private boolean isCommand = false;
	private boolean isConsumed = false;

																		private Parameter(String name, String value, boolean required,
			boolean isSwitch, boolean isCommand, String example) {
		super();
		this.fName = name;
		this.fExample = (null == example) ? "" : "=" + example;
		this.fRequired = required;
		this.isSwitch = isSwitch;
		this.isCommand = isCommand;
		this.fValue = value;
	}

											private Parameter(String name, String value) {
		super();
		this.fName = name;
		this.fExample = null;
		this.fRequired = false;
		this.isSwitch = false;
		this.isCommand = false;
		this.fValue = value;
	}

						public String getName() {
		return fName;
	}

						public String getExample() {
		return fExample;
	}

						public String getValue() {
		return fValue;
	}

						public void setValue(String value) {
		fValue = value;
	}

						public boolean isRequired() {
		return fRequired;
	}

						public boolean isSwitch() {
		return isSwitch;
	}

						public boolean isCommand() {
		return isCommand;
	}

						public boolean isConsumed() {
		return isConsumed;
	}

				public void setConsumed() {
		isConsumed = true;
	}

										public static Parameter createRequiredParameter(String name, String example) {
		return new Parameter(name, null, true, false, false, example);
	}

										public static Parameter createParameterValue(String name, String value) {
		Parameter parameter = new Parameter(name, value, false, false, false,
				null);
		parameter.setValue(value);
		return parameter;
	}

								public static Parameter createCommand(String name) {
		return new Parameter(name, null, true, false, true, null);
	}

								public static Parameter createSwitch(String name, String value) {
		return new Parameter(name, value, false, true, false, null);
	}

}
