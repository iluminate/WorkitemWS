package com.everis.fallas.operacionales.workitem.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.common.OperationResult;
import com.everis.fallas.operacionales.workitem.framework.AbstractTeamRepositoryCommand;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.helper.WorkItemTypeHelper;
import com.everis.fallas.operacionales.workitem.parameter.ParameterManager;
import com.everis.fallas.operacionales.workitem.utils.ProcessAreaUtil;
import com.everis.fallas.operacionales.workitem.utils.WorkItemUtil;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.expression.AttributeExpression;
import com.ibm.team.workitem.common.expression.Expression;
import com.ibm.team.workitem.common.expression.IQueryableAttribute;
import com.ibm.team.workitem.common.expression.QueryableAttributes;
import com.ibm.team.workitem.common.expression.Term;
import com.ibm.team.workitem.common.model.AttributeOperation;
import com.ibm.team.workitem.common.model.AttributeTypes;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.model.Identifier;
import com.ibm.team.workitem.common.model.ItemProfile;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResult;

public class MigrateWorkItemAttributeCommand extends AbstractTeamRepositoryCommand {

	public static final String COMMAND_MIGRATE_ENUMERATION_LIST_ATTRIBUTE = "migrateattribute";
	public static final String PARAMETER_SOURCE_ATTRIBUTE_ID = "sourceAttributeID";
	public static final String PARAMETER_SOURCE_ATTRIBUTE_ID_EXAMPLE = "com.acme.custom.enum.multiselect";
	public static final String PARAMETER_TARGET_ATTRIBUTE_ID = "targetAttributeID";
	public static final String PARAMETER_TARGET_ATTRIBUTE_ID_EXAMPLE = "com.acme.custom.enum.list";

	public static final String SEPARATOR_ENUMERATION_LITERAL_ID_LIST = ",";

	private boolean fIgnoreErrors = false;

	private void setIgnoreErrors() {
		fIgnoreErrors = true;
	}

	private boolean isIgnoreErrors() {
		return fIgnoreErrors;
	}

	public MigrateWorkItemAttributeCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public void setRequiredParameters() {
		super.setRequiredParameters();
		getParameterManager().syntaxAddRequiredParameter(
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddRequiredParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY,
				IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddSwitch(IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS);
		getParameterManager().syntaxAddRequiredParameter(PARAMETER_SOURCE_ATTRIBUTE_ID,
				PARAMETER_SOURCE_ATTRIBUTE_ID_EXAMPLE);
		getParameterManager().syntaxAddRequiredParameter(PARAMETER_TARGET_ATTRIBUTE_ID,
				PARAMETER_TARGET_ATTRIBUTE_ID_EXAMPLE);
	}

	@Override
	public String getCommandName() {
		return COMMAND_MIGRATE_ENUMERATION_LIST_ATTRIBUTE;
	}

	@Override
	public OperationResult process() throws TeamRepositoryException {
		String projectAreaName = getParameterManager()
				.consumeParameter(IWorkItemCommandLineConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY).trim();
		IProjectArea projectArea = ProcessAreaUtil.findProjectAreaByFQN(projectAreaName, getProcessClientService(),
				getMonitor());
		if (projectArea == null) {
			throw new WorkItemCommandLineException("Project Area not found: " + projectAreaName);
		}

		String workItemTypeID = getParameterManager()
				.consumeParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_TYPE_PROPERTY).trim();
		IWorkItemType workItemType = WorkItemTypeHelper.findWorkItemType(workItemTypeID, projectArea.getProjectArea(),
				getWorkItemCommon(), getMonitor());

		String sourceAttributeID = getParameterManager().consumeParameter(PARAMETER_SOURCE_ATTRIBUTE_ID).trim();
		IAttribute sourceIAttribute = getWorkItemCommon().findAttribute(projectArea, sourceAttributeID, getMonitor());
		if (sourceIAttribute == null) {
			throw new WorkItemCommandLineException("Source Attribute not found: " + sourceAttributeID);
		}
		if (!AttributeTypes.STRING_TYPES.contains(sourceIAttribute.getAttributeType())) {
			throw new WorkItemCommandLineException("Source Attribute is not a String type: " + sourceAttributeID);
		}

		String targetAttributeID = getParameterManager().consumeParameter(PARAMETER_TARGET_ATTRIBUTE_ID).trim();
		IAttribute targetIAttribute = getWorkItemCommon().findAttribute(projectArea, targetAttributeID, getMonitor());
		if (targetIAttribute == null) {
			throw new WorkItemCommandLineException("Target Attribute not found: " + targetAttributeID);
		}
		if (!AttributeTypes.isEnumerationListAttributeType(targetIAttribute.getAttributeType())) {
			throw new WorkItemCommandLineException("Target Attribute is not an EnumerationList: " + targetAttributeID);
		}
		if (getParameterManager().hasSwitch(IWorkItemCommandLineConstants.SWITCH_IGNOREERRORS)) {
			setIgnoreErrors();
		}
		String wiID = getParameterManager()
				.consumeParameter(IWorkItemCommandLineConstants.PARAMETER_WORKITEM_ID_PROPERTY);
		if (wiID != null) {
			IWorkItem wi = WorkItemUtil.findWorkItemByID(wiID, IWorkItem.SMALL_PROFILE, getWorkItemCommon(),
					getMonitor());
			if (!wi.getWorkItemType().equals(workItemType.getIdentifier())) {
				throw new WorkItemCommandLineException("Work item type mismatch: " + workItemType.getIdentifier()
						+ " specified " + workItemType.getIdentifier());
			}
			migrateSingleWorkItem(wi, sourceIAttribute, targetIAttribute);
		} else {
			migrateAllWorkItems(projectArea, workItemType, sourceIAttribute, targetIAttribute);
		}
		getResult().setSuccess();
		return getResult();
	}

	private void migrateSingleWorkItem(IWorkItem wi, IAttribute sourceIAttribute, IAttribute targetIAttribute)
			throws TeamRepositoryException {
		MigrateWorkItem operation = new MigrateWorkItem("Migrate", IWorkItem.FULL_PROFILE, sourceIAttribute,
				targetIAttribute);
		performMigration((IWorkItemHandle) wi.getItemHandle(), operation);
	}

	private void migrateAllWorkItems(IProjectArea projectArea, IWorkItemType workItemType, IAttribute sourceIAttribute,
			IAttribute targetIAttribute) throws TeamRepositoryException {
		IQueryableAttribute attribute = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(projectArea,
				IWorkItem.PROJECT_AREA_PROPERTY, getAuditableCommon(), getMonitor());
		IQueryableAttribute type = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(projectArea,
				IWorkItem.TYPE_PROPERTY, getAuditableCommon(), getMonitor());
		Expression inProjectArea = new AttributeExpression(attribute, AttributeOperation.EQUALS, projectArea);
		Expression isType = new AttributeExpression(type, AttributeOperation.EQUALS, workItemType.getIdentifier());
		Term typeinProjectArea = new Term(Term.Operator.AND);
		typeinProjectArea.add(inProjectArea);
		typeinProjectArea.add(isType);

		IQueryClient queryClient = getWorkItemClient().getQueryClient();
		IQueryResult<IResult> results = queryClient.getExpressionResults(projectArea, typeinProjectArea);
		results.setLimit(Integer.MAX_VALUE);
		MigrateWorkItem operation = new MigrateWorkItem("Migrate", IWorkItem.FULL_PROFILE, sourceIAttribute,
				targetIAttribute);
		while (results.hasNext(getMonitor())) {
			IResult result = (IResult) results.next(getMonitor());
			performMigration((IWorkItemHandle) result.getItem(), operation);
		}
	}

	private void performMigration(IWorkItemHandle handle, MigrateWorkItem operation)
			throws WorkItemCommandLineException {
		String workItemID = "undefined";
		try {
			IWorkItem workItem = WorkItemUtil.resolveWorkItem((IWorkItemHandle) handle, IWorkItem.SMALL_PROFILE,
					getWorkItemCommon(), getMonitor());
			workItemID = getWorkItemIDString(workItem);
			operation.run(handle, getMonitor());
			getResult().appendResultString("Migrated work item " + workItemID + ".");
		} catch (TeamRepositoryException e) {
			throw new WorkItemCommandLineException(getResult().getResultString() + "TeamRepositoryException: Work item "
					+ workItemID + " attribute not migrated. " + e.getMessage(), e);
		} catch (WorkItemCommandLineException e) {
			String message = "WorkItemCommandLineException Work item " + workItemID + " attribute not migrated. "
					+ e.getMessage();
			if (!isIgnoreErrors()) {
				throw new WorkItemCommandLineException(getResult().getResultString() + message, e);
			} else {
				getResult().appendResultString(message);
			}
		}
	}

	private IWorkItemClient getWorkItemClient() {
		return (IWorkItemClient) getTeamRepository().getClientLibrary(IWorkItemClient.class);
	}

	private String getWorkItemIDString(IWorkItem workItem) {
		return new Integer(workItem.getId()).toString();
	}

	private class MigrateWorkItem extends WorkItemOperation {

		IAttribute fsourceAttribute = null;
		IAttribute fTargetAttribute = null;

		public MigrateWorkItem(String message, ItemProfile<?> profile, IAttribute sourceAttribute,
				IAttribute targetAttribute) {
			super(message, profile);
			fsourceAttribute = sourceAttribute;
			fTargetAttribute = targetAttribute;
		}

		@Override
		protected void execute(WorkItemWorkingCopy workingCopy, IProgressMonitor monitor)
				throws TeamRepositoryException, RuntimeException {

			IWorkItem workItem = workingCopy.getWorkItem();
			String thisItemID = getWorkItemIDString(workItem);
			if (!workItem.hasAttribute(fsourceAttribute)) {
				throw new WorkItemCommandLineException(
						"Work Item " + thisItemID + " Source Attribute not available - Synchronize Attributes: "
								+ fsourceAttribute.getIdentifier());
			}
			if (!workItem.hasAttribute(fTargetAttribute)) {
				throw new WorkItemCommandLineException(
						"Work Item " + thisItemID + " Target Attribute not available - Synchronize Attributes: "
								+ fTargetAttribute.getIdentifier());
			}
			Object ovalue = workItem.getValue(fsourceAttribute);
			String sourceValues = "";
			if (null != ovalue && ovalue instanceof String) {
				sourceValues = (String) ovalue;
			}
			if (!sourceValues.equals("")) {
				String[] values = sourceValues.split(SEPARATOR_ENUMERATION_LITERAL_ID_LIST);
				IEnumeration<? extends ILiteral> enumeration = getWorkItemCommon().resolveEnumeration(fTargetAttribute,
						monitor);

				List<Object> results = new ArrayList<Object>();
				for (String literalID : values) {
					if (literalID == "") {
						continue;
					}
					Identifier<? extends ILiteral> literal = getLiteralEqualsIDString(enumeration, literalID);
					if (null == literal) {
						throw new WorkItemCommandLineException(
								"Work Item " + thisItemID + " Target literal ID not available: " + literalID
										+ " Attribute " + fTargetAttribute.getIdentifier());
					}
					results.add(literal);
				}
				workItem.setValue(fTargetAttribute, results);
			}
			getResult().appendResultString("Migrated work item " + thisItemID);
		}

		private Identifier<? extends ILiteral> getLiteralEqualsIDString(
				final IEnumeration<? extends ILiteral> enumeration, String literalIDString)
				throws TeamRepositoryException {
			List<? extends ILiteral> literals = enumeration.getEnumerationLiterals();
			for (Iterator<? extends ILiteral> iterator = literals.iterator(); iterator.hasNext();) {
				ILiteral iLiteral = (ILiteral) iterator.next();
				if (iLiteral.getIdentifier2().getStringIdentifier().equals(literalIDString.trim())) {
					return iLiteral.getIdentifier2();
				}
			}
			return null;
		}
	}

	@Override
	public String helpSpecificUsage() {
		return "";
	}
}
