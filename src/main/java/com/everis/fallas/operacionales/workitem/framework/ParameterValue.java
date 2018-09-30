package com.everis.fallas.operacionales.workitem.framework;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.parameter.ParameterIDMapper;
import com.everis.fallas.operacionales.workitem.utils.AttributeUtil;
import com.everis.fallas.operacionales.workitem.utils.StringUtil;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IAttribute;

public class ParameterValue {
	private String mode = MODE_DEFAULT;
	private String attributeID = null;
	private String value = null;
	private IAttribute theAttribute = null;
	private IProjectAreaHandle fProjectAreaHandle;
	private IProgressMonitor fMonitor;
	public static final String POSTFIX_PARAMETER_MANIPULATION_MODE = ":";
	public static final String MODE_REMOVE = "remove";
	public static final String MODE_ADD = "add";
	public static final String MODE_SET = "set";
	public static final String MODE_DEFAULT = "default";

	public ParameterValue(String parameter, String value, IProjectAreaHandle projectAreaHandle,
			IProgressMonitor monitor) throws WorkItemCommandLineException {
		analyzeValue(parameter, value, projectAreaHandle, monitor);
	}

	private void analyzeValue(String parameter, String value, IProjectAreaHandle projectAreaHandle,
			IProgressMonitor monitor) throws WorkItemCommandLineException {
		fProjectAreaHandle = projectAreaHandle;
		fMonitor = monitor;
		if (parameter == null) {
			throw new WorkItemCommandLineException("Parameter can not be null: ");
		}
		this.value = value;
		List<String> propertyData = StringUtil.splitStringToList(parameter, POSTFIX_PARAMETER_MANIPULATION_MODE);
		this.attributeID = ParameterIDMapper.getAlias(propertyData.get(0));
		if (propertyData.size() == 2) {
			String foundMode = propertyData.get(1);
			if (MODE_ADD.equals(foundMode) || MODE_REMOVE.equals(foundMode) || MODE_SET.equals(foundMode)) {
				mode = foundMode;
			} else {
				throw new WorkItemCommandLineException(
						"Parameter update mode not recognizeable: " + parameter + " Value: " + value);
			}
		}
	}

	public String getAttributeID() {
		return this.attributeID;
	}

	public String getValue() {
		return this.value;
	}

	public IAttribute getIAttribute() throws TeamRepositoryException {
		if (this.theAttribute == null) {
			this.theAttribute = AttributeUtil.resolveAttribute(this.attributeID, fProjectAreaHandle, fMonitor);
		}
		return this.theAttribute;
	}

	public boolean isDefault() {
		return mode.equals(MODE_DEFAULT);
	}

	public boolean isAdd() {
		return mode.equals(MODE_ADD);
	}

	public boolean isRemove() {
		return mode.equals(MODE_REMOVE);
	}

	public boolean isSet() {
		return mode.equals(MODE_SET);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayName() throws TeamRepositoryException {
		IAttribute attribute = getIAttribute();
		if (attribute == null) {
			throw new WorkItemCommandLineException("Attribute not found: " + this.attributeID);
		}
		return getIAttribute().getDisplayName();
	}
}
