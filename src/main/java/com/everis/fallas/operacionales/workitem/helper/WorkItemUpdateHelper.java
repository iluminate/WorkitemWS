package com.everis.fallas.operacionales.workitem.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.common.IWorkItemCommandLineConstants;
import com.everis.fallas.operacionales.workitem.framework.ParameterValue;
import com.everis.fallas.operacionales.workitem.framework.ReferenceData;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.parameter.ParameterList;
import com.everis.fallas.operacionales.workitem.utils.AccessContextUtil;
import com.everis.fallas.operacionales.workitem.utils.AttachmentUtil;
import com.everis.fallas.operacionales.workitem.utils.BuildUtil;
import com.everis.fallas.operacionales.workitem.utils.ProcessAreaUtil;
import com.everis.fallas.operacionales.workitem.utils.ReferenceUtil;
import com.everis.fallas.operacionales.workitem.utils.SimpleDateFormatUtil;
import com.everis.fallas.operacionales.workitem.utils.StringUtil;
import com.everis.fallas.operacionales.workitem.utils.WorkItemUtil;
import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.common.model.IBuildResultHandle;
import com.ibm.team.build.common.model.query.IBaseBuildResultQueryModel.IBuildResultQueryModel;
import com.ibm.team.foundation.common.text.XMLString;
import com.ibm.team.links.common.IItemReference;
import com.ibm.team.links.common.IReference;
import com.ibm.team.links.common.factory.IReferenceFactory;
import com.ibm.team.links.common.registry.IEndPointDescriptor;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.IContributorHandle;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.ItemNotFoundException;
import com.ibm.team.repository.common.Location;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.query.IItemQuery;
import com.ibm.team.repository.common.query.IItemQueryPage;
import com.ibm.team.repository.common.query.ast.IPredicate;
import com.ibm.team.repository.common.service.IQueryService;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.IAuditableCommon;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.internal.IAdditionalSaveParameters;
import com.ibm.team.workitem.common.internal.util.SeparatedStringList;
import com.ibm.team.workitem.common.model.AttributeTypes;
import com.ibm.team.workitem.common.model.IApproval;
import com.ibm.team.workitem.common.model.IApprovalDescriptor;
import com.ibm.team.workitem.common.model.IApprovals;
import com.ibm.team.workitem.common.model.IAttachment;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IAttributeHandle;
import com.ibm.team.workitem.common.model.ICategoryHandle;
import com.ibm.team.workitem.common.model.IComment;
import com.ibm.team.workitem.common.model.IComments;
import com.ibm.team.workitem.common.model.IDeliverable;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;
import com.ibm.team.workitem.common.model.IResolution;
import com.ibm.team.workitem.common.model.IState;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemReferences;
import com.ibm.team.workitem.common.model.Identifier;
import com.ibm.team.workitem.common.model.WorkItemApprovals;
import com.ibm.team.workitem.common.model.WorkItemEndPoints;
import com.ibm.team.workitem.common.model.WorkItemLinkTypes;
import com.ibm.team.workitem.common.workflow.IWorkflowAction;
import com.ibm.team.workitem.common.workflow.IWorkflowInfo;

public class WorkItemUpdateHelper {

	private static final int XML_GROWTH_CONSTANT = 50;
	public static final String STRING_TYPE_HTML = "HTML";
	public static final String STRING_TYPE_WIKI = "WIKI";
	public static final String STRING_TYPE_PLAINSTRING = "STRING";
	public static final String STRING_LINEBREAK_HTML_BR = "<br>";
	public static final String STRING_LINEBREAK_BACKSLASH_N = "\\n";
	public static final String TYPE_PROCESS_AREA = "ProcessArea";
	public static final String TYPE_TEAM_AREA = "TeamArea";
	public static final String TYPE_PROJECT_AREA = "ProjectArea";
	public static final String TYPE_CATEGORY = "Category";
	public static final String TYPE_CONTRIBUTOR = "User";
	public static final String TYPE_ITERATION = "Iteration";
	public static final String TYPE_WORKITEM = "WorkItem";
	public static final String TYPE_SCM_COMPONENT = "SCMComponent";

	public static final String STATECHANGE_FORCESTATE = "forceState";
	public static final String PSEUDO_ATTRIBUTE_ATTACHFILE = "@attachFile";
	public static final String PSEUDO_ATTRIBUTE_LINK = "@link_";
	public static final String PSEUDO_ATTRIBUTE_TRIGGER_WORKFLOW_ACTION = "@workflowAction";
	public static final String APPROVAL_TYPE_VERIFICATION = "verification";
	public static final String APPROVAL_TYPE_REVIEW = "review";
	public static final String APPROVAL_TYPE_APPROVAL = "approval";

	public static final String HTTP_PROTOCOL_PREFIX = IWorkItemCommandLineConstants.HTTP_PROTOCOL_PREFIX;	public static final String PREFIX_REFERENCETYPE = "@";

	public static final String APPROVAL_SEPARATOR = ":";
	public static final String FORCESTATE_SEPARATOR = APPROVAL_SEPARATOR;
	public static final String LINK_SEPARATOR = "\\|";
	public static final String LINK_SEPARATOR_HELP = "|";
	public static final String ITEMTYPE_SEPARATOR = ":";
	public static final String ITEM_SEPARATOR = ",";
	public static final String PATH_SEPARATOR = "/";
	public static final String ATTACHMENT_SEPARATOR = ITEM_SEPARATOR;

	private static final String VALUE_TRUNCATED_POSTFIX = ".. Truncated";

	private IProgressMonitor monitor = null;
	private IWorkItem fItem = null;
	private WorkItemWorkingCopy fWorkingCopy = null;
	private ITeamRepository fTeamRepository = null;
	private ParameterList fParameters = new ParameterList();
	private boolean fEnforceSizeLimits = false;
	private boolean fBulkupdate = false;

					private class ApprovalInputData {

		private String approvalName = null;
		private String approvalType = null;
		private String approverList = null;

												public ApprovalInputData(ParameterValue parameter) {
			List<String> approvalData = StringUtil.splitStringToList(
					parameter.getValue(), APPROVAL_SEPARATOR);
			if (approvalData.size() < 2 || 3 < approvalData.size()) {
				throw new WorkItemCommandLineException(
						"Incorrect approval format: "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue() + helpUsageApprovals());
			}
			String approvalTypeString = approvalData.get(0);
			approvalName = approvalData.get(1);

			if (APPROVAL_TYPE_APPROVAL.equals(approvalTypeString.trim())) {
				this.approvalType = WorkItemApprovals.APPROVAL_TYPE
						.getIdentifier();
			} else if (APPROVAL_TYPE_REVIEW.equals(approvalTypeString.trim())) {
				this.approvalType = WorkItemApprovals.REVIEW_TYPE
						.getIdentifier();
			} else if (APPROVAL_TYPE_VERIFICATION.equals(approvalTypeString
					.trim())) {
				this.approvalType = WorkItemApprovals.VERIFICATION_TYPE
						.getIdentifier();
			} else {
				throw new WorkItemCommandLineException(
						"Approval type not found: "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue() + helpUsageApprovals());
			}
			if (approvalData.size() == 3) {
				this.approverList = approvalData.get(2);
			}
		}

								public String getApprovalType() {
			return approvalType;
		}

								public String getApprovalName() {
			return approvalName;
		}

								public String getApprovers() {
			return approverList;
		}
	}

						public WorkItemUpdateHelper() {
	}

											public WorkItemUpdateHelper(WorkItemWorkingCopy workingCopy,
			ParameterList parameters, IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
		if (parameters != null) {
			this.fParameters = parameters;
		}
		this.fWorkingCopy = workingCopy;
		this.fItem = fWorkingCopy.getWorkItem();
		this.fTeamRepository = (ITeamRepository) fItem.getOrigin();
		setEnforceSizeJimits(parameters
				.hasSwitch(IWorkItemCommandLineConstants.SWITCH_ENFORCE_SIZE_LIMITS));
		setBatchOperation(parameters
				.hasSwitch(IWorkItemCommandLineConstants.SWITCH_BULK_OPERATION));
	}

						private void setBatchOperation(boolean hasSwitch) {
		this.fBulkupdate = hasSwitch;
	}

						private boolean isBulkUpadte() {
		return this.fBulkupdate;
	}

							private void setEnforceSizeJimits(boolean flag) {
		this.fEnforceSizeLimits = flag;
	}

				private boolean isEnforceSizeLimits() {
		return fEnforceSizeLimits;
	}

							private IWorkItem getWorkItem() {
		return fItem;
	}

						private WorkItemWorkingCopy getWorkingCopy() {
		return fWorkingCopy;
	}

				private ParameterList getParameters() {
		return fParameters;
	}

														public void updateProperty(String propertyID, String value)
			throws TeamRepositoryException, WorkItemCommandLineException,
			IOException {
		ParameterValue parameter = new ParameterValue(propertyID, value,
				getWorkItem().getProjectArea(), monitor);
		List<Exception> exceptions = new ArrayList<Exception>();

		if (parameter.getAttributeID().equals(IWorkItem.ID_PROPERTY)) {
			throw new WorkItemCommandLineException(
					"ID of work item can not be changed: "
							+ parameter.getAttributeID() + " Value: "
							+ parameter.getValue());
		} else if (parameter.getAttributeID().equals(
				IWorkItem.CREATION_DATE_PROPERTY)) {
			throw new WorkItemCommandLineException(
					"Creation date of work item can not be changed: "
							+ parameter.getAttributeID() + " Value: "
							+ parameter.getValue());
		} else if (parameter.getAttributeID().equals(IWorkItem.TYPE_PROPERTY)) {
			throw new WorkItemCommandLineException(
					"Type of work item must be changed outside: "
							+ parameter.getAttributeID() + " Value: "
							+ parameter.getValue());
		} else if (parameter.getAttributeID().equals(
				IWorkItem.CONTEXT_ID_PROPERTY)) {
			UUID contextID = calculateUUID(parameter, exceptions);
			getWorkItem().setContextId(contextID);
		} else if (parameter.getAttributeID()
				.equals(IWorkItem.SUMMARY_PROPERTY)) {
																																	
			String summary = enforceSizeLimits(
					calculateXMLDescription(parameter, getWorkItem()
							.getHTMLSummary(), STRING_TYPE_PLAINSTRING),
					parameter.getIAttribute().getAttributeType());

			getWorkItem().setHTMLSummary(XMLString.createFromXMLText(summary));
		} else if (parameter.getAttributeID().equals(
				IWorkItem.DESCRIPTION_PROPERTY)) {
																																																String description = enforceSizeLimits(
					calculateXMLDescription(parameter, getWorkItem()
							.getHTMLDescription(), STRING_TYPE_HTML), parameter
							.getIAttribute().getAttributeType());
			getWorkItem().setHTMLDescription(
					XMLString.createFromXMLText(description));
		} else if (parameter.getAttributeID().equals(
				IWorkItem.COMMENTS_PROPERTY)) {
			updateComments(parameter);
		} else if (parameter.getAttributeID().equals(IWorkItem.STATE_PROPERTY)) {
			updateState(parameter);
		} else if (parameter.getAttributeID().equals(
				PSEUDO_ATTRIBUTE_TRIGGER_WORKFLOW_ACTION)) {
			updateWorkFlowAction(parameter);
		} else if (parameter.getAttributeID().equals(
				IWorkItem.PROJECT_AREA_PROPERTY)) {
			throw new WorkItemCommandLineException(
					"Project Area can not be changed, set the workitem category ("
							+ IWorkItem.CATEGORY_PROPERTY + ") instead: "
							+ parameter.getAttributeID() + " !");
		} else if (parameter.getAttributeID().equals(
				IWorkItem.RESOLUTION_PROPERTY)) {
			updateResolution(parameter);
		} else if (parameter.getAttributeID().equals(
				IWorkItem.SUBSCRIPTIONS_PROPERTY)) {
			updateSubscribers(parameter, exceptions);
		} else if (parameter.getAttributeID().equals(IWorkItem.TAGS_PROPERTY)) {
			updateBuiltInTags(parameter);
		} else if (StringUtil.hasPrefix(parameter.getAttributeID(),
				IWorkItem.APPROVALS_PROPERTY)) {
			updateApprovals(parameter, exceptions);
		} else if (StringUtil.hasPrefix(parameter.getAttributeID(),
				PSEUDO_ATTRIBUTE_ATTACHFILE)) {
			updateAttachments(parameter);
		} else if (StringUtil.hasPrefix(parameter.getAttributeID(),
				PSEUDO_ATTRIBUTE_LINK)) {
			updateLinks(parameter, exceptions);
		} else {
			updateGeneralAttribute(parameter, exceptions);
		}
		throwComplexException(parameter, exceptions);
	}

									private void updateGeneralAttribute(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException,
			WorkItemCommandLineException {
		IAttribute theAttribute = parameter.getIAttribute();
		if (theAttribute != null) {
			if (!getWorkItem().hasAttribute(theAttribute)) {
				throw new WorkItemCommandLineException(
						"Attribute not available at work item: "
								+ parameter.getAttributeID()
								+ " Value: "
								+ parameter.getValue()
								+ ". Check the work item type or consider synchronizing the attributes.");
			} else {
				Object result;
				try {
					result = getRepresentation(parameter, exceptions);
				} catch (WorkItemCommandLineException e) {
					throw new WorkItemCommandLineException(
							"Exception getting attribute representation: ["
									+ parameter.getAttributeID() + "] Value: ["
									+ parameter.getValue()
									+ "]  Original exception: \n"
									+ e.getMessage(), e);
				}
				getWorkItem().setValue(theAttribute, result);
			}
		} else {
			throw new WorkItemCommandLineException("Attribute not found: "
					+ parameter.getAttributeID() + " Value: "
					+ parameter.getValue());
		}
	}

																private Object getRepresentation(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException,
			WorkItemCommandLineException {
		String attribType = parameter.getIAttribute().getAttributeType();
		if (AttributeTypes.isListAttributeType(attribType)) {
			if (AttributeTypes.isItemListAttributeType(attribType)) {
				if (attribType.equals(AttributeTypes.CONTRIBUTOR_LIST)) {
					return calculateContributorList(parameter, exceptions);
				}
				if (attribType.equals(AttributeTypes.PROCESS_AREA_LIST)) {
					return calculateProcessAreaList(parameter, exceptions);
				}
				if (attribType.equals(AttributeTypes.PROJECT_AREA_LIST)) {
					return calculateProcessAreaList(parameter, exceptions);
				}
				if (attribType.equals(AttributeTypes.TEAM_AREA_LIST)) {
					return calculateProcessAreaList(parameter, exceptions);
				}
				if (attribType.equals(AttributeTypes.WORK_ITEM_LIST)) {
					return calculateWorkItemList(parameter, exceptions);
				}
				if (attribType.equals(AttributeTypes.ITEM_LIST)) {
					return calculateItemList(parameter, exceptions);
				}
			}
			if (attribType.equals(AttributeTypes.TAGS)) {
				return calculateTagList(parameter);
			}
			if (attribType.equals(AttributeTypes.STRING_LIST)) {
				return enforceSizeLimitsStringCollection(
						calculateStringList(parameter, exceptions), attribType);
			}
			if (AttributeTypes.isEnumerationListAttributeType(attribType)) {
				return calculateEnumerationLiteralList(parameter, exceptions);
			}
			throw new WorkItemCommandLineException(
					"Type not recognized - type not yet supported: "
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: " + parameter.getValue() + " - "
							+ helpGetTypeProperties(attribType));
		} else {
																																																																																																						if (attribType.equals(AttributeTypes.WIKI)) {
				return calculateStringValue(parameter, STRING_TYPE_WIKI);
			}
			if (AttributeTypes.STRING_TYPES.contains(attribType)) {

				return enforceSizeLimits(
						calculateStringValue(parameter, STRING_TYPE_PLAINSTRING),
						attribType);
			}
			if (AttributeTypes.HTML_TYPES.contains(attribType)) {

				return enforceSizeLimits(
						calculateStringValue(parameter, STRING_TYPE_HTML),
						attribType);
			}
			if (parameter.isAdd() || parameter.isRemove()) {
				throw modeNotSupportedException(
						parameter,
						"Mode not supported for this operation. Single value attributes only support the default and the "
								+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
								+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET
								+ " modes.");
			}
			if (attribType.equals(AttributeTypes.BOOLEAN)) {
				return new Boolean(parameter.getValue());
			}
			if (AttributeTypes.NUMBER_TYPES.contains(attribType)) {
				try {
					if (attribType.equals(AttributeTypes.INTEGER)) {
						return new Integer(parameter.getValue());
					}
					if (attribType.equals(AttributeTypes.LONG)) {
						return new Long(parameter.getValue());
					}
					if (attribType.equals(AttributeTypes.FLOAT)) {
						return new Float(parameter.getValue());
					}
					if (attribType.equals(AttributeTypes.DECIMAL)) {
						return new BigDecimal(parameter.getValue());
					}
					if (attribType.equals(AttributeTypes.DURATION)) {
						return getDurationFromString(parameter.getValue());
					}
				} catch (NumberFormatException e) {
					throw new WorkItemCommandLineException(
							"Attribute Value not valid - Number format exception: "
									+ parameter.getValue(), e);
				}
			}
			if (attribType.equals(AttributeTypes.DELIVERABLE)) {
				return calculateDeliverable(parameter);
			}
			if (attribType.equals(AttributeTypes.CATEGORY)) {
				return calculateCategory(parameter);
			}
			if (attribType.equals(AttributeTypes.ITERATION)) {
				return calculateIteration(parameter);
			}
			if (attribType.equals(AttributeTypes.CONTRIBUTOR)) {
				return calculateContributor(parameter);
			}
			if (attribType.equals(AttributeTypes.TIMESTAMP)) {
				return calculateTimestamp(parameter);
			}
			if (attribType.equals(AttributeTypes.PROJECT_AREA)) {
				return calculateProcessArea(parameter, TYPE_PROJECT_AREA);
			}
			if (attribType.equals(AttributeTypes.TEAM_AREA)) {
				return calculateProcessArea(parameter, TYPE_TEAM_AREA);
			}
			if (attribType.equals(AttributeTypes.PROCESS_AREA)) {
				return calculateProcessArea(parameter, TYPE_PROCESS_AREA);
			}
			if (attribType.equals(AttributeTypes.WORK_ITEM)) {
				return calculateWorkItem(parameter);
			}
			if (attribType.equals(AttributeTypes.ITEM)) {
				return calculateItem(parameter, exceptions);
			}
			if (attribType.equals(AttributeTypes.UUID)) {
				return calculateUUID(parameter, exceptions);
			}
			if (AttributeTypes.isEnumerationAttributeType(attribType)) {
				return calculateEnumerationLiteral(parameter);
			}
			throw new WorkItemCommandLineException(
					"AttributeType not yet supported: "
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: " + parameter.getValue() + " - "
							+ helpGetTypeProperties(attribType));
		}
	}

									private Collection<String> enforceSizeLimitsStringCollection(
			Collection<String> input, String attribType) {
		if (!isEnforceSizeLimits()) {
			return input;
		}

		Collection<String> result = new ArrayList<String>();
		for (String value : input) {
			result.add(enforceSizeLimits(value, attribType));
		}
		return result;
	}

									private String enforceSizeLimits(String input, String attribType) {
		if (!isEnforceSizeLimits()) {
			return input;
		}
		return truncateString(input, attribType);
	}

								private String truncateString(String value, String attribType) {
		Long sizeLimit = Long.MAX_VALUE;
		if (attribType.equals(AttributeTypes.SMALL_STRING)) {
			sizeLimit = IAttribute.MAX_SMALL_STRING_BYTES;
		}
		if (attribType.equals(AttributeTypes.MEDIUM_STRING)
				|| attribType.equals(AttributeTypes.MEDIUM_HTML)
				|| attribType.equals(AttributeTypes.STRING_LIST)
				|| attribType.equals(AttributeTypes.COMMENTS)) {
			sizeLimit = IAttribute.MAX_MEDIUM_STRING_BYTES;
		}
		if (attribType.equals(AttributeTypes.LARGE_STRING)
				|| attribType.equals(AttributeTypes.LARGE_HTML)) {
			sizeLimit = IAttribute.MAX_LARGE_STRING_BYTES;
		}
		if (value.length() >= sizeLimit) {
			int lastpos = sizeLimit.intValue()
					- (VALUE_TRUNCATED_POSTFIX.length() + XML_GROWTH_CONSTANT);
			value = value.substring(0, lastpos) + VALUE_TRUNCATED_POSTFIX;
		}
		return value;
	}

											private void throwComplexException(ParameterValue parameter,
			List<Exception> exceptions) throws WorkItemCommandLineException {
		if (!exceptions.isEmpty()) {
			String exceptionInfo = "";
			for (Exception exception : exceptions) {
				exceptionInfo += "Recoverable Exception getting attribute representation: "
						+ parameter.getAttributeID()
						+ " Value: "
						+ parameter.getValue()
						+ " \n"
						+ exception.getMessage()
						+ "\n";
			}
			throw new WorkItemCommandLineException(exceptionInfo);
		}
	}

																		private void updateApprovals(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException,
			WorkItemCommandLineException {

		boolean enableDeleteApprovals = getParameters().hasSwitch(
				IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_APPROVALS);
		if (parameter.isRemove() || parameter.isSet()) {
			if (!enableDeleteApprovals) {
				throw modeNotSupportedException(
						parameter,
						"Deletion of Approvals not enabled: "
								+ " use the switch "
								+ IWorkItemCommandLineConstants.PREFIX_SWITCH
								+ IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_APPROVALS
								+ " to enable deletion of approvals. Parameter: "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue());
			}
		}
		ApprovalInputData approvalData = new ApprovalInputData(parameter);
		if (parameter.isSet()) {
			updateRemoveAllApprovalsOfSameType(approvalData);
			createApproval(parameter, approvalData);
		} else if (parameter.isRemove()) {
			if (!updateRemoveApproval(approvalData)) {
				exceptions.add(new WorkItemCommandLineException(
						"Remove Approval: approval not found: "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue()));
			}
		} else {
			createApproval(parameter, approvalData);
		}
	}

													private void updateAttachments(ParameterValue parameter)
			throws TeamRepositoryException {

		boolean switchDeleteAttachments = getParameters().hasSwitch(
				IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS);
		if ((parameter.isSet() || parameter.isRemove())
				&& !switchDeleteAttachments) {
			throw new WorkItemCommandLineException(
					"Deletion of attachments not enabled: "
							+ " use the switch "
							+ IWorkItemCommandLineConstants.PREFIX_SWITCH
							+ IWorkItemCommandLineConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS
							+ " to enable deletion of attachments. Parameter: "
							+ parameter.getAttributeID() + " Value: "
							+ parameter.getValue());

		}
		List<String> attachmentData = StringUtil.splitStringToList(
				parameter.getValue(), ATTACHMENT_SEPARATOR);
		if (attachmentData.size() != 4) {
			throw new WorkItemCommandLineException(
					"Incorrect attachment format: "
							+ parameter.getAttributeID() + " Value: "
							+ parameter.getValue()
							+ helpUsageAttachmentUpload());
		}

		String fileName = attachmentData.get(0);
		String description = attachmentData.get(1);
		String contentType = attachmentData.get(2);
		String encoding = attachmentData.get(3);
		if (parameter.isRemove()) {
			AttachmentUtil.removeAttachment(fileName, description,
					getWorkItem(), getWorkItemCommon(), monitor);
			return;
		} else if (parameter.isSet()) {
			AttachmentUtil.removeAllAttachments(getWorkItem(),
					getWorkItemCommon(), monitor);
			attachFile(fileName, description, contentType, encoding);
			return;
		} else {
			attachFile(fileName, description, contentType, encoding);
			return;
		}
	}

											private void updateBuiltInTags(ParameterValue parameter) {
		List<String> newTags = getTags(parameter.getValue());
		List<String> oldTags = getWorkItem().getTags2();
		if (parameter.isRemove()) {
			oldTags.removeAll(newTags);
			getWorkItem().setTags2(oldTags);
		} else if (parameter.isSet()) {
			getWorkItem().setTags2(newTags);
		} else {
			List<String> toAdd = getTagsList(oldTags, newTags);
			oldTags.addAll(toAdd);
			getWorkItem().setTags2(oldTags);
		}
	}

									private void updateComments(ParameterValue parameter)
			throws WorkItemCommandLineException, TeamRepositoryException {
		if (!(parameter.isDefault() || parameter.isAdd())) {
			throw modeNotSupportedException(
					parameter,
					"Mode not supported. Comments only supports the default and the "
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_ADD
							+ " modes. ");
		}

		XMLString commentContent = XMLString
				.createFromXMLText(insertLineBreaks(parameter.getValue(),
						STRING_TYPE_HTML));
		XMLString limitedContent = XMLString
				.createFromXMLText(enforceSizeLimits(commentContent
						.getXMLText(), parameter.getIAttribute()
						.getAttributeType()));
		IComments comments = getWorkItem().getComments();
		IComment newComment = comments.createComment(getTeamRepository()
				.loggedInContributor(), limitedContent);

		comments.append(newComment);
	}

															private void updateLinks(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<ReferenceData> references;
		boolean setUpdateBackLinks = false;
		String linkType = StringUtil.removePrefix(parameter.getAttributeID(),
				PSEUDO_ATTRIBUTE_LINK);
		IEndPointDescriptor endpoint = ReferenceUtil
				.getWorkItemEndPointDescriptorMap().get(linkType);
		if (endpoint == null) {
			endpoint = ReferenceUtil.getCLM_URI_EndPointDescriptorMap().get(
					linkType);
		}
		if (endpoint == null) {
			endpoint = ReferenceUtil.getCLM_WI_EndPointDescriptorMap().get(
					linkType);
		}
		if (endpoint == null) {
			endpoint = ReferenceUtil.getBuild_EndPointDescriptorMap().get(
					linkType);
		}
		if (endpoint == null) {
			throw new WorkItemCommandLineException(
					"Link Type unknown or not yet supported: " + linkType
							+ helpUsageAllLinks());
		}
		IWorkItemReferences wiReferences = getWorkingCopy().getReferences();
		List<IReference> current = wiReferences.getReferences(endpoint);
		if (parameter.isSet()) {
			for (IReference iReference : current) {
				getWorkingCopy().getReferences().remove(iReference);
				if (WorkItemLinkTypes.isCalmLink(endpoint)) {
					setUpdateBackLinks = true;
				}
			}
		}
		references = createReferences(linkType, parameter, exceptions);
		for (ReferenceData newReferences : references) {
			IReference foundReference = null;
			for (IReference iReference : current) {
				if (iReference.sameDetailsExcludingCommentAs(newReferences
						.getReference())) {
					foundReference = iReference;
					break;
				}
			}
			if (parameter.isDefault() || parameter.isAdd() || parameter.isSet()) {
				if (foundReference == null) {
					getWorkingCopy().getReferences().add(
							newReferences.getEndPointDescriptor(),
							newReferences.getReference());
					if (WorkItemLinkTypes.isCalmLink(newReferences
							.getEndPointDescriptor())) {
						setUpdateBackLinks = true;
					}
				}
			} else if (parameter.isRemove()) {
				if (foundReference != null) {
					getWorkingCopy().getReferences().remove(foundReference);
					if (WorkItemLinkTypes.isCalmLink(newReferences
							.getEndPointDescriptor())) {
						setUpdateBackLinks = true;
					}
				}
			}
		}
		if (setUpdateBackLinks) {
			getWorkingCopy().getAdditionalSaveParameters().add(
					IAdditionalSaveParameters.UPDATE_BACKLINKS);
		}
	}

								private void updateRemoveAllApprovalsOfSameType(
			ApprovalInputData approvalData) {
		IApprovals approvals = getWorkItem().getApprovals();
		List<IApproval> approvalContent = approvals.getContents();
		for (IApproval anApproval : approvalContent) {
			IApprovalDescriptor descriptor = anApproval.getDescriptor();
			if (descriptor != null
					&& descriptor.getTypeIdentifier().equals(
							approvalData.getApprovalType())) {
				approvals.remove(anApproval);
				approvals.remove(descriptor);
			}
		}
	}

								private boolean updateRemoveApproval(ApprovalInputData approvalData) {
		IApprovals approvals = getWorkItem().getApprovals();
		List<IApproval> approvalContent = approvals.getContents();
		for (IApproval anApproval : approvalContent) {
			IApprovalDescriptor descriptor = anApproval.getDescriptor();
			if (descriptor.getTypeIdentifier().equals(
					approvalData.getApprovalType())
					&& descriptor.getName().equals(
							approvalData.getApprovalName())) {
				approvals.remove(anApproval);
				approvals.remove(descriptor);
				return true;
			}
		}
		return false;
	}

								private void updateResolution(ParameterValue parameter)
			throws WorkItemCommandLineException, TeamRepositoryException {
		if (!(parameter.isDefault() || parameter.isSet())) {
			throw new WorkItemCommandLineException(
					"Comments only supports the default and the "
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET
							+ " modes: " + parameter.getAttributeID() + " !");
		}
		Identifier<IResolution> resolution = findResolution(parameter
				.getValue());
		if (resolution == null) {
			throw new WorkItemCommandLineException("Resolution not found: "
					+ parameter.getAttributeID() + " Value: "
					+ parameter.getValue());
		}
		getWorkItem().setResolution2(resolution);
	}

														private void updateSubscribers(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IContributor> subscriberList = getContributors(
				parameter.getValue(), ITEM_SEPARATOR, notFoundList);
		if (parameter.isSet()) {
			IContributorHandle[] subscribed = getWorkItem().getSubscriptions()
					.getContents();
			for (IContributorHandle removeContributor : subscribed) {
				getWorkItem().getSubscriptions().remove(removeContributor);
			}
		}
		for (IContributor subscriber : subscriberList.values()) {
			if (parameter.isRemove()) {
				getWorkItem().getSubscriptions().remove(subscriber);
			} else {
				getWorkItem().getSubscriptions().add(subscriber);
			}
		}
		if (!notFoundList.isEmpty()) {
			exceptions.add(new WorkItemCommandLineException(
					"Sunscriber not found: " + parameter.getAttributeID()
							+ " Subscribers: "
							+ helpGetDisplayStringFromList(notFoundList)));
		}
	}

									private void updateState(ParameterValue parameter)
			throws WorkItemCommandLineException, TeamRepositoryException {
		if (!(parameter.isDefault() || parameter.isSet())) {
			throw modeNotSupportedException(
					parameter,
					"Mode not supported for this operation. State change only supports the default and the "
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET
							+ " mode.");
		}
		setState(parameter);
	}

										private void updateWorkFlowAction(ParameterValue parameter)
			throws WorkItemCommandLineException, TeamRepositoryException {
		if (!(parameter.isDefault() || parameter.isSet())) {
			throw modeNotSupportedException(
					parameter,
					"Mode not supported for this operation. State change only supports the default and the "
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
							+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET
							+ " modes.");
		}
		setWorkFlowAction(parameter);
	}

									private Object calculateCategory(ParameterValue parameter)
			throws TeamRepositoryException {
		ICategoryHandle category = findCategory(parameter.getValue());
		if (category == null) {
			throw new WorkItemCommandLineException("Category not found: "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());
		}
		return category;
	}

									private Object calculateContributor(ParameterValue parameter)
			throws TeamRepositoryException {

		IContributor user = findContributorFromIDorName(parameter.getValue()
				.trim());
		if (user == null) {
			throw new WorkItemCommandLineException("Contributor ID not found: "
					+ parameter.getValue());
		}
		return user;
	}

													private Object calculateContributorList(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IContributor> foundItems = getContributors(
				parameter.getValue(), ITEM_SEPARATOR, notFoundList);
		if (!notFoundList.isEmpty()) {
			exceptions.add(new WorkItemCommandLineException(
					"Contributors not found: "
							+ parameter.getIAttribute().getIdentifier()
							+ " Contributors: "
							+ helpGetDisplayStringFromList(notFoundList)));
		}

		if (parameter.isSet()) {
			return foundItems.values();
		}
		List<Object> results = new ArrayList<Object>();
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw new WorkItemCommandLineException(
					"Attribute type not a list attribute type, can not calculate current value: "
							+ parameter.getIAttribute().getIdentifier());
		}
		List<?> currentList = (List<?>) current;
		for (Object currentObject : currentList) {
			if (!(currentObject instanceof IItemHandle)) {
				exceptions.add(incompatibleAttributeValueTypeException(
						parameter, "Reading List Attribute value ("
								+ currentObject.toString() + ") "));
			}
			IItemHandle currentHandle = (IItemHandle) currentObject;
			if (!foundItems.containsKey(currentHandle.getItemId()
					.getUuidValue())) {
				results.add(currentHandle);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems.values());
		}
		return results;
	}

									private Object calculateDeliverable(ParameterValue parameter)
			throws TeamRepositoryException {
		IDeliverable result = findDeliverable(parameter.getValue());
		if (null == result) {
			throw new WorkItemCommandLineException("Deliverable not found: "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());
		}
		return result;
	}

									private Object calculateEnumerationLiteral(ParameterValue parameter)
			throws TeamRepositoryException {
		try {
			Identifier<? extends ILiteral> result = getEnumerationLiteralEqualsStringOrID(
					parameter.getIAttribute(), parameter.getValue());
			if (null == result) {
				throw new WorkItemCommandLineException(
						"Enumeration literal could not be resolved: "
								+ parameter.getIAttribute().getIdentifier()
								+ " Value: " + parameter.getValue());
			} else {
				return result;
			}
		} catch (RuntimeException e) {
			throw new WorkItemCommandLineException(
					"Type could not be identified - Enumeration could not be resolved: "
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: " + parameter.getValue());
		}
	}

												private Object calculateEnumerationLiteralList(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<String> values = StringUtil.splitStringToList(
				parameter.getValue(), ITEM_SEPARATOR);
		HashMap<String, Identifier<? extends ILiteral>> foundItems = new HashMap<String, Identifier<? extends ILiteral>>();

		for (String displayValue : values) {
			Identifier<? extends ILiteral> result = getEnumerationLiteralEqualsStringOrID(
					parameter.getIAttribute(), displayValue);
			if (null == result) {
				exceptions.add(new WorkItemCommandLineException(
						"Enumeration literal could not be resolved: "
								+ parameter.getIAttribute().getIdentifier()
								+ " Value: " + displayValue));
			} else {
				foundItems.put(result.getStringIdentifier(), result);
			}
		}
		if (parameter.isSet()) {
			return foundItems.values();
		}
		List<Object> results = new ArrayList<Object>();
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw new WorkItemCommandLineException(
					"Attribute type not a list attribute type, can not calculate current value: "
							+ parameter.getIAttribute().getIdentifier());
		}
		List<?> currentList = (List<?>) current;
		for (Object currentObject : currentList) {
			if (!(currentObject instanceof Identifier<?>)) {
				exceptions.add(incompatibleAttributeValueTypeException(
						parameter, "Reading List Attribute value ("
								+ currentObject.toString() + ") "));
			}
			Identifier<?> currentIdentifier = (Identifier<?>) currentObject;
			if (!foundItems
					.containsKey(currentIdentifier.getStringIdentifier())) {
				results.add(currentIdentifier);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems.values());
		}
		return results;
	}

													private Object calculateItem(ParameterValue parameter,
			List<Exception> exceptions) throws WorkItemCommandLineException,
			TeamRepositoryException {
		List<String> value = StringUtil.splitStringToList(parameter.getValue(),
				ITEMTYPE_SEPARATOR);
		if (value.size() != 2) {
			throw new WorkItemCommandLineException("Unrecognizable encoding: "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue() + " - "
					+ helpUsageUnspecifiedItemValues());
		}
		String itemType = value.get(0);
		String itemValue = value.get(1).trim();
		parameter.setValue(itemValue);
		if (itemType.equals(TYPE_PROJECT_AREA)
				|| itemType.equals(TYPE_TEAM_AREA)
				|| itemType.equals(TYPE_PROCESS_AREA)) {
			return calculateProcessArea(parameter, itemType);
		}
		if (itemType.equals(TYPE_CATEGORY)) {
			return calculateCategory(parameter);
		}
		if (itemType.equals(TYPE_CONTRIBUTOR)) {
			return calculateContributor(parameter);
		}
		if (itemType.equals(TYPE_ITERATION)) {
			return calculateIteration(parameter);
		}
		if (itemType.equals(TYPE_WORKITEM)) {
			return calculateWorkItem(parameter);
		}
		if (itemType.equals(TYPE_SCM_COMPONENT)) {
			return calculateSCMComponent(parameter, exceptions);
		}
		throw new WorkItemCommandLineException("Unrecognized item type ( "
				+ itemType + ") :" + parameter.getIAttribute().getIdentifier()
				+ " Value: " + parameter.getValue() + " - "
				+ helpUsageUnspecifiedItemValues());
	}

											private Object calculateItemList(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		String originalInputValue = parameter.getValue();
		HashMap<String, Object> foundItems = new HashMap<String, Object>();
		List<String> items = StringUtil.splitStringToList(originalInputValue,
				ITEM_SEPARATOR);
		for (String itemSpecification : items) {

			parameter.setValue(itemSpecification);
			try {
				Object item = calculateItem(parameter, exceptions);
				if (item instanceof IItemHandle) {
					IItemHandle anItemHandle = (IItemHandle) item;
					foundItems.put(anItemHandle.getItemId().getUuidValue(),
							anItemHandle);
				} else {
					exceptions.add(incompatibleAttributeValueTypeException(
							parameter,
							"Incompatible value type found computing List Attribute value ("
									+ item.toString() + ") "));
				}
			} catch (WorkItemCommandLineException e) {
				exceptions.add(e);
			}
		}
		if (parameter.isSet()) {
			return foundItems.values();
		}
		List<Object> results = new ArrayList<Object>();
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw incompatibleAttributeValueTypeException(parameter,
					"Attribute type not a list attribute type, can not calculate current value");
		}
		List<?> currentList = (List<?>) current;
		for (Object currentObject : currentList) {
			if (!(currentObject instanceof IItemHandle)) {
				exceptions.add(incompatibleAttributeValueTypeException(
						parameter,
						"Incompatible value type found computing List Attribute value ("
								+ currentObject.toString() + ") "));
			}
			IItemHandle currentHandle = (IItemHandle) currentObject;
			if (!foundItems.containsKey(currentHandle.getItemId()
					.getUuidValue())) {
				results.add(currentHandle);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems.values());
		}
		return results;
	}

									private Object calculateIteration(ParameterValue parameter)
			throws TeamRepositoryException {
		List<String> path = StringUtil.splitStringToList(parameter.getValue(),
				PATH_SEPARATOR);
		DevelopmentLineHelper dh = new DevelopmentLineHelper(
				getTeamRepository(), monitor);
		IProjectAreaHandle projectArea = getWorkItem().getProjectArea();
		IIteration iteration = dh.findIteration(projectArea, path,
				DevelopmentLineHelper.BYID);
		if (iteration == null) {			iteration = dh.findIteration(projectArea, path,
					DevelopmentLineHelper.BYLABEL);
		}
		if (iteration == null) {
			throw new WorkItemCommandLineException("Iteration not found: "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());
		}
		if (!iteration.hasDeliverable()) {
			throw new WorkItemCommandLineException(
					"Iteration has no deliverable planned (A release is scheduled for this iteration): "
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: " + parameter.getValue());
		}
		return iteration;
	}

									private IProcessArea calculateProcessArea(ParameterValue parameter,
			String areaType) throws TeamRepositoryException {
		IProcessArea processArea = null;
		IProcessArea area = ProcessAreaUtil.findProcessAreaByFQN(parameter
				.getValue().trim(), getProcessClientService(), monitor);
		if (area == null) {
			throw new WorkItemCommandLineException(areaType + " not found "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());
		}
		if (areaType.equals(TYPE_PROJECT_AREA)) {
			if (area instanceof IProjectArea) {
				processArea = area;
			} else {
				throw new WorkItemCommandLineException(
						"Process Areas found but was not Project Area: "
								+ parameter.getAttributeID() + " Area Name: "
								+ parameter.getValue());
			}
		} else if (areaType.equals(TYPE_TEAM_AREA)) {
			if (area instanceof ITeamArea) {
				processArea = area;
			} else {
				new WorkItemCommandLineException(
						"Process Areas found but was not Team Area: "
								+ parameter.getAttributeID() + " Area Name: "
								+ parameter.getValue());
			}
		} else {
			processArea = area;
		}
		return processArea;
	}

													private Object calculateProcessAreaList(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		String areaType = TYPE_PROCESS_AREA;
		if (parameter.getIAttribute().getAttributeType()
				.equals(AttributeTypes.PROJECT_AREA_LIST)) {
			areaType = TYPE_PROJECT_AREA;
		} else if (parameter.getIAttribute().getAttributeType()
				.equals(AttributeTypes.TEAM_AREA_LIST)) {
			areaType = TYPE_TEAM_AREA;
		}
		HashMap<String, IProcessArea> foundItems = new HashMap<String, IProcessArea>();
		List<String> notFoundList = new ArrayList<String>();
		List<String> processAreaNames = StringUtil.splitStringToList(
				parameter.getValue(), ITEM_SEPARATOR);
		for (String processAreaName : processAreaNames) {
			processAreaName = processAreaName.trim();
			IProcessArea area = ProcessAreaUtil.findProcessAreaByFQN(
					processAreaName, getProcessClientService(), monitor);
			if (area == null) {
				notFoundList.add(processAreaName);
			} else {
				if (areaType.equals(TYPE_PROJECT_AREA)) {
					if (area instanceof IProjectArea) {
						foundItems.put(area.getItemId().getUuidValue(), area);
					} else {
						exceptions.add(new WorkItemCommandLineException(
								"Process Areas found but was not Project Area: "
										+ parameter.getAttributeID()
										+ " Area Name: " + processAreaName));
					}
				} else if (areaType.equals(TYPE_TEAM_AREA)) {
					if (area instanceof ITeamArea) {
						foundItems.put(area.getItemId().getUuidValue(), area);
					} else {
						exceptions.add(new WorkItemCommandLineException(
								"Process Areas found but was not Team Area: "
										+ parameter.getAttributeID()
										+ " Area Name: " + processAreaName));
					}
				} else {
					foundItems.put(area.getItemId().getUuidValue(), area);
				}
			}
		}
		if (!notFoundList.isEmpty()) {
			exceptions.add(new WorkItemCommandLineException(
					"Process Areas not found: " + parameter.getAttributeID()
							+ " process areas: "
							+ helpGetDisplayStringFromList(notFoundList)));
		}
		if (parameter.isSet()) {
			return foundItems.values();
		}
		List<Object> results = new ArrayList<Object>();
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw new WorkItemCommandLineException(
					"Attribute type not a list attribute type, can not calculate current value: "
							+ parameter.getIAttribute().getIdentifier());
		}
		List<?> currentList = (List<?>) current;
		for (Object currentObject : currentList) {
			if (!(currentObject instanceof IItemHandle)) {
				exceptions.add(incompatibleAttributeValueTypeException(
						parameter,
						"Incompatible value type found computing List Attribute value ("
								+ currentObject.toString() + ") "));
			}
			IItemHandle currentHandle = (IItemHandle) currentObject;
			if (!foundItems.containsKey(currentHandle.getItemId()
					.getUuidValue())) {
				results.add(currentHandle);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems.values());
		}
		return results;
	}

													private Object calculateSCMComponent(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform
				.getWorkspaceManager(getTeamRepository());
		IComponentSearchCriteria criteria = IComponentSearchCriteria.FACTORY
				.newInstance();
		criteria.setExactName(parameter.getValue());
		List<IComponentHandle> found = wm.findComponents(criteria,
				Integer.MAX_VALUE, monitor);
		if (found.size() == 0) {
			new WorkItemCommandLineException("SCM Component not found :"
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());
		}
		if (found.size() > 1) {
			exceptions.add(new WorkItemCommandLineException(
					"Ambigious SCM Component name - returning first hit :"
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: " + parameter.getValue()));
		}
		return found.get(0);
	}

										private String calculateStringValue(ParameterValue parameter,
			String stringType) throws TeamRepositoryException {

		if (parameter.isRemove()) {
			throw modeNotSupportedException(parameter,
					"Mode not supported for this operation.");
		}
		if (parameter.isAdd()) {
			Object current = getWorkItem().getValue(parameter.getIAttribute());
			if (current instanceof String) {
				String content = (String) current;
				return content.concat(insertLineBreaks(parameter.getValue(),
						stringType));
			} else {
				throw incompatibleAttributeValueTypeException(parameter,
						"Set String value.");
			}
		}
		return insertLineBreaks(parameter.getValue(), stringType);
	}

											private Collection<String> calculateStringList(ParameterValue parameter,
			List<Exception> errors) throws TeamRepositoryException {
		List<String> foundItems = StringUtil.splitStringToList(
				parameter.getValue(), ITEM_SEPARATOR);
		if (parameter.isSet()) {
			return foundItems;
		}
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw new WorkItemCommandLineException(
					"Attribute type not a String list attribute type, can not calculate current value: "
							+ parameter.getIAttribute().getIdentifier());
		}
		HashSet<String> results = new HashSet<String>();
		List<?> currentList = (List<?>) current;
		for (Object object : currentList) {
			if (object instanceof String) {
				results.add((String) object);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems);
		} else if (parameter.isRemove()) {
			for (String value : foundItems) {
				results.remove(value);
			}
		}
		return results;
	}

										private Object calculateTagList(ParameterValue parameter)
			throws TeamRepositoryException {
		SeparatedStringList tags = (SeparatedStringList) getWorkItem()
				.getValue(parameter.getIAttribute());
		List<String> newTags = getTags(parameter.getValue());
		if (parameter.isRemove()) {
			tags.removeAll(newTags);
		} else if (parameter.isSet()) {
			tags.clear();
			tags.addAll(newTags);
		} else if (parameter.isAdd() || parameter.isDefault()) {
			List<String> theTags = getTagsList(tags, newTags);
			tags.clear();
			tags.addAll(theTags);
		}
		return new SeparatedStringList(tags);
	}

									private Object calculateTimestamp(ParameterValue parameter)
			throws TeamRepositoryException {
		try {
			return SimpleDateFormatUtil
					.createTimeStamp(
							parameter.getValue(),
							SimpleDateFormatUtil.SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z);
		} catch (IllegalArgumentException e) {
			throw new WorkItemCommandLineException(
					"Wrong Timestamp format: "
							+ parameter.getIAttribute().getIdentifier()
							+ " Value: "
							+ parameter.getValue()
							+ " use format: "
							+ SimpleDateFormatUtil.SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z);
		}
	}

										private UUID calculateUUID(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		UUID accessContext = AccessContextUtil.getAccessContextFromFQN(
				parameter.getValue(), getTeamRepository(),
				getAuditableCommon(), getProcessClientService(), null);
		if (accessContext == null) {
			throw new WorkItemCommandLineException("UUID not found: "
					+ parameter.getIAttribute().getIdentifier() + " Value: "
					+ parameter.getValue());

		}
		return accessContext;
	}

										private Object calculateWorkItem(ParameterValue parameter)
			throws TeamRepositoryException, WorkItemCommandLineException {
		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IWorkItem> workItems = findWorkItemsByIDValues(
				parameter.getValue(), notFoundList, ITEM_SEPARATOR);
		Collection<IWorkItem> workItemList = workItems.values();
		if (workItemList.size() > 0) {
			return workItemList.iterator().next();
		} else {
			throw new WorkItemCommandLineException("Work Item not found: "
					+ parameter.getAttributeID() + " Value: "
					+ parameter.getValue());
		}
	}

											private Object calculateWorkItemList(ParameterValue parameter,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IWorkItem> foundItems = findWorkItemsByIDValues(
				parameter.getValue(), notFoundList, ITEM_SEPARATOR);
		if (!notFoundList.isEmpty()) {
			exceptions.add(new WorkItemCommandLineException(
					"WorkItems not found: Attribute"
							+ parameter.getAttributeID() + " value "
							+ parameter.getValue() + " WorkItems: "
							+ helpGetDisplayStringFromList(notFoundList)));
		}
		if (parameter.isSet()) {
			return foundItems.values();
		}
		List<Object> results = new ArrayList<Object>();
		Object current = getWorkItem().getValue(parameter.getIAttribute());
		if (!(current instanceof List<?>)) {
			throw new WorkItemCommandLineException(
					"Attribute type not a list attribute type, can not calculate current value: "
							+ parameter.getIAttribute().getIdentifier());
		}
		List<?> currentList = (List<?>) current;
		for (Object currentObject : currentList) {
			if (!(currentObject instanceof IItemHandle)) {
				exceptions.add(incompatibleAttributeValueTypeException(
						parameter,
						"Incompatible value type found computing List Attribute value ("
								+ currentObject.toString() + ") "));
			}
			IItemHandle currentHandle = (IItemHandle) currentObject;
			if (!foundItems.containsKey(currentHandle.getItemId()
					.getUuidValue())) {
				results.add(currentHandle);
			}
		}
		if (parameter.isAdd() || parameter.isDefault()) {
			results.addAll(foundItems.values());
		}
		return results;
	}

																	private String calculateXMLDescription(ParameterValue parameter,
			XMLString originalContent, String stringType) {
		XMLString input = XMLString.createFromPlainText("");
		if (parameter.isRemove()) {
			throw modeNotSupportedException(parameter,
					"Mode not supported for this operation.");
		}

		input = XMLString.createFromXMLText(insertLineBreaks(
				parameter.getValue(), stringType));

		if (parameter.isAdd()) {
			return originalContent.concat(input).getXMLText();
		}
		return input.getXMLText();
	}

										private void createApproval(ParameterValue parameter,
			ApprovalInputData approvalData) throws TeamRepositoryException {

		IApprovals approvals = getWorkItem().getApprovals();
		IApprovalDescriptor descriptor = approvals.createDescriptor(
				approvalData.getApprovalType(), approvalData.getApprovalName());

		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IContributor> approvers = getContributors(
				approvalData.getApprovers(), ITEM_SEPARATOR, notFoundList);
		for (IContributorHandle approver : approvers.values()) {
			IApproval newApproval = approvals.createApproval(descriptor,
					approver);
			approvals.add(newApproval);
		}
		if (!notFoundList.isEmpty()) {
			throw new WorkItemCommandLineException("Approvers not found: "
					+ parameter + " Approvers: "
					+ helpGetDisplayStringFromList(notFoundList));
		}
	}

											private HashMap<String, IWorkItem> findWorkItemsByIDValues(String value,
			List<String> notFoundList, String separator)
			throws TeamRepositoryException {
		HashMap<String, IWorkItem> items = new HashMap<String, IWorkItem>();
		List<String> workItemIDs = StringUtil.splitStringToList(value,
				separator);
		for (String id : workItemIDs) {
			IWorkItem item = null;
			try {
				item = WorkItemUtil.findWorkItemByID(id,
						IWorkItem.SMALL_PROFILE, getWorkItemCommon(), monitor);
			} catch (WorkItemCommandLineException e) {
			}
			if (item == null) {
				notFoundList.add(id);
			} else {
				items.put(((IItemHandle) item).getItemId().getUuidValue(), item);
			}
		}
		return items;
	}

										private IBuildResultHandle findBuildResultByLabel(String buildResultLabel)
			throws WorkItemCommandLineException {

		ITeamBuildClient buildClient = getBuildClient();
		IBuildResultQueryModel buildResultQueryModel = IBuildResultQueryModel.ROOT;
		IItemQuery query = IItemQuery.FACTORY
				.newInstance(buildResultQueryModel);
								IPredicate buildByLabel = (buildResultQueryModel.label()._eq(query
				.newStringArg()));
		query.filter(buildByLabel);
				query.orderByDsc(buildResultQueryModel.buildStartTime());
		try {
															IItemQueryPage queryPage = buildClient.queryItems(query,
					new Object[] { buildResultLabel },
					IQueryService.ITEM_QUERY_MAX_PAGE_SIZE, monitor);
			IItemHandle[] results = queryPage.handlesAsArray();

			if (results.length == 1) {
				if (results[0] instanceof IBuildResultHandle) {
					return (IBuildResultHandle) results[0];
				}
			}
		} catch (TeamRepositoryException e) {
			throw new WorkItemCommandLineException("Build Result not found "
					+ buildResultLabel, e);
		}
		throw new WorkItemCommandLineException("Build Result not found "
				+ buildResultLabel);
	}

										private ICategoryHandle findCategory(String value)
			throws TeamRepositoryException {
		List<String> path = StringUtil.splitStringToList(value, PATH_SEPARATOR);
		ICategoryHandle category = getWorkItemCommon().findCategoryByNamePath(
				getWorkItem().getProjectArea(), path, monitor);
		return category;
	}

										@SuppressWarnings("rawtypes")
	private IContributor findContributorFromIDorName(String userID)
			throws TeamRepositoryException {
		IContributor foundUser = null;
		if (userID.isEmpty()) {
			return foundUser;
		}
		try {
			foundUser = getTeamRepository().contributorManager()
					.fetchContributorByUserId(userID, monitor);
		} catch (ItemNotFoundException e) {
			List allContributors = getTeamRepository().contributorManager()
					.fetchAllContributors(monitor);
			for (Iterator iterator = allContributors.iterator(); iterator
					.hasNext();) {
				IContributor contributor = (IContributor) iterator.next();
				if (contributor.getName().equals(userID)) {
					foundUser = contributor;
					break;
				}
			}
		}
		return foundUser;
	}

										private IDeliverable findDeliverable(String value)
			throws TeamRepositoryException {
		return getWorkItemCommon().findDeliverableByName(
				getWorkItem().getProjectArea(), value,
				IDeliverable.FULL_PROFILE, monitor);
	}

											private Identifier<IResolution> findResolution(String value)
			throws TeamRepositoryException {
		IWorkflowInfo workflowInfo = getWorkItemCommon().getWorkflow(
				getWorkItem().getWorkItemType(),
				getWorkItem().getProjectArea(), monitor);
		Identifier<IResolution>[] resolutions = workflowInfo
				.getAllResolutionIds();
		for (Identifier<IResolution> resolution : resolutions) {
			if (workflowInfo.getResolutionName(resolution).equals(value)) {
				return resolution;
			} else if (resolution.toString().equals(value)) {
				return resolution;
			}
		}
		return null;
	}

											private Identifier<IWorkflowAction> findActionToTargetState(String value)
			throws TeamRepositoryException {
		IWorkflowInfo workflowInfo = getWorkItemCommon().getWorkflow(
				getWorkItem().getWorkItemType(),
				getWorkItem().getProjectArea(), monitor);
		Identifier<IWorkflowAction>[] ActionIDs = workflowInfo
				.getActionIds(getWorkItem().getState2());
		for (Identifier<IWorkflowAction> action : ActionIDs) {
			Identifier<IState> targetState = workflowInfo
					.getActionResultState(action);
			if (workflowInfo.getStateName(targetState).equals(value)) {
				return action;
			} else if ((targetState).getStringIdentifier().equals(value)) {
				return action;
			}
		}
		return null;
	}

											private Identifier<IWorkflowAction> findAction(String value)
			throws TeamRepositoryException {
		IWorkflowInfo workflowInfo = getWorkItemCommon().getWorkflow(
				getWorkItem().getWorkItemType(),
				getWorkItem().getProjectArea(), monitor);
		Identifier<IWorkflowAction>[] ActionIDs = workflowInfo
				.getActionIds(getWorkItem().getState2());
		for (Identifier<IWorkflowAction> action : ActionIDs) {
			if (workflowInfo.getActionName(action).equals(value)) {
				return action;
			} else if (action.getStringIdentifier().equals(value)) {
				return action;
			}
		}
		return null;
	}

										private Identifier<IState> findTargetState(String value)
			throws TeamRepositoryException {
		IWorkflowInfo workflowInfo = getWorkItemCommon().getWorkflow(
				getWorkItem().getWorkItemType(),
				getWorkItem().getProjectArea(), monitor);
		Identifier<IState>[] states = workflowInfo.getAllStateIds();
		for (Identifier<IState> stateId : states) {
			if (workflowInfo.getStateName(stateId).equals(value)) {
				return stateId;
			} else if (stateId.getStringIdentifier().equals(value)) {
				return stateId;
			}
		}
		return null;
	}

											private boolean isInState(IWorkItem workItem,
			IWorkItemCommon workItemCommon, String value)
			throws TeamRepositoryException {
		IWorkflowInfo workflowInfo = workItemCommon.getWorkflow(
				workItem.getWorkItemType(), workItem.getProjectArea(), monitor);
		Identifier<IState> currentState = getWorkItem().getState2();
		if (currentState == null) {
			return false;
		}
		if (workflowInfo.getStateName(currentState).equals(value)) {
			return true;
		} else if (currentState.getStringIdentifier().equals(value)) {
			return true;
		}
		return false;
	}

															@SuppressWarnings("deprecation")
	private void setState(ParameterValue parameter)
			throws TeamRepositoryException {
		List<String> stateList = StringUtil.splitStringToList(
				parameter.getValue(), FORCESTATE_SEPARATOR);
		if (stateList.size() == 1) {
			String targetState = stateList.get(0).trim();
			if (isInState(getWorkItem(), getWorkItemCommon(), targetState)) {
				return;
			}

			Identifier<IWorkflowAction> foundAction = findActionToTargetState(targetState);
			if (foundAction == null) {
				throw new WorkItemCommandLineException(
						"Action to target State not found: "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue());
			}
			((WorkItemWorkingCopy) getWorkingCopy())
					.setWorkflowAction(foundAction.getStringIdentifier());
		} else if (stateList.size() == 2) {
			String prefix = stateList.get(0);
			String newValue = stateList.get(1);
			if (STATECHANGE_FORCESTATE.equals(prefix)) {
				Identifier<IState> foundState = findTargetState(newValue.trim());
				if (foundState == null) {
					throw new WorkItemCommandLineException(
							"Target state not found: "
									+ parameter.getAttributeID() + " Value: "
									+ parameter.getValue());
				}
																getWorkItem().setState2(foundState);
			} else {
				throw new WorkItemCommandLineException(
						"Prefix not recognized: " + prefix + " in "
								+ parameter.getAttributeID() + " Value: "
								+ parameter.getValue());

			}
		} else {
			new WorkItemCommandLineException("Incorrect state format: "
					+ parameter.getAttributeID() + " Value: "
					+ parameter.getValue() + helpUsageStateChange());
		}
	}

										private List<ReferenceData> createReferences(String linkType,
			ParameterValue parameter, List<Exception> exceptions)
			throws TeamRepositoryException {
		IEndPointDescriptor buildEndpoint = ReferenceUtil
				.getBuild_EndPointDescriptorMap().get(linkType);
		if (buildEndpoint != null) {
			return createBuildResultReferences(parameter, buildEndpoint,
					exceptions);
		}
		IEndPointDescriptor workItemEndpoint = ReferenceUtil
				.getWorkItemEndPointDescriptorMap().get(linkType);
		if (workItemEndpoint != null) {
			return createWorkItemReferences(parameter, workItemEndpoint,
					exceptions);
		}
		IEndPointDescriptor wiCLMEndpoint = ReferenceUtil
				.getCLM_WI_EndPointDescriptorMap().get(linkType);
		if (wiCLMEndpoint != null) {
			return createCLM_WI_References(parameter, wiCLMEndpoint, exceptions);
		}
		IEndPointDescriptor uriEndpoint = ReferenceUtil
				.getCLM_URI_EndPointDescriptorMap().get(linkType);
		if (uriEndpoint != null) {
			return createCLM_URI_References(parameter, uriEndpoint, exceptions);
		}
		throw new WorkItemCommandLineException(
				"Link Type unknown or not yet supported: " + linkType
						+ helpUsageWorkItemLinks());
	}

														private List<ReferenceData> createCLM_WI_References(
			ParameterValue parameter, IEndPointDescriptor endpoint,
			List<Exception> exceptions) throws TeamRepositoryException {
		List<ReferenceData> found = new ArrayList<ReferenceData>();
		List<String> workItems = StringUtil.splitStringToList(
				parameter.getValue(), LINK_SEPARATOR);
		for (String value : workItems) {
			if (value.startsWith(HTTP_PROTOCOL_PREFIX)) {
				IReference reference;
				try {
					reference = IReferenceFactory.INSTANCE
							.createReferenceFromURI(new URI(value), value);
					found.add(new ReferenceData(endpoint, reference));
				} catch (URISyntaxException e) {
					exceptions.add(new WorkItemCommandLineException(
							"Creating URI Reference (" + value + ") failed: "
									+ e.getMessage() + " \n "
									+ parameter.getAttributeID() + " = "
									+ parameter.getValue()
									+ helpUsageBuildResultLink()));
				}
			} else {
				IWorkItem workItem = null;
				try {
					workItem = WorkItemUtil.findWorkItemByID(value,
							IWorkItem.SMALL_PROFILE, getWorkItemCommon(),
							monitor);
				} catch (WorkItemCommandLineException e) {
					exceptions.add(e);
				}
				if (workItem != null) {

					Location location = Location.namedLocation(workItem,
							((ITeamRepository) workItem.getOrigin())
									.publicUriRoot());

					found.add(new ReferenceData(endpoint,
							IReferenceFactory.INSTANCE
									.createReferenceFromURI(location
											.toAbsoluteUri())));
				}
			}
		}
		return found;
	}

															private List<ReferenceData> createWorkItemReferences(
			ParameterValue parameter, IEndPointDescriptor endpoint,
			List<Exception> exceptions) throws TeamRepositoryException,
			WorkItemCommandLineException {
		List<ReferenceData> found = new ArrayList<ReferenceData>();
		List<String> notFoundList = new ArrayList<String>();
		HashMap<String, IWorkItem> workItemList = findWorkItemsByIDValues(
				parameter.getValue(), notFoundList, LINK_SEPARATOR);
		if (!workItemList.isEmpty()) {
			for (IWorkItem item : workItemList.values()) {
				found.add(new ReferenceData(endpoint,
						IReferenceFactory.INSTANCE.createReferenceToItem(item
								.getItemHandle())));
			}
		} else {
			throw new WorkItemCommandLineException(
					"Link items - no targets found: "
							+ parameter.getAttributeID() + " = "
							+ parameter.getValue() + helpUsageWorkItemLinks());
		}
		if (!notFoundList.isEmpty()) {
			exceptions.add(new WorkItemCommandLineException(
					"Create Links WorkItems not found: "
							+ parameter.getAttributeID() + " = "
							+ parameter.getValue()
							+ helpGetDisplayStringFromList(notFoundList)));
		}
		return found;
	}

														private List<ReferenceData> createCLM_URI_References(
			ParameterValue parameter, IEndPointDescriptor endpoint,
			List<Exception> exceptions) throws TeamRepositoryException,
			WorkItemCommandLineException {
		List<ReferenceData> found = new ArrayList<ReferenceData>();
		List<String> itemURIS = StringUtil.splitStringToList(
				parameter.getValue(), LINK_SEPARATOR);
		for (String uri : itemURIS) {
			if (uri.startsWith(HTTP_PROTOCOL_PREFIX)) {
				IReference reference;
				try {
					reference = IReferenceFactory.INSTANCE
							.createReferenceFromURI(new URI(uri), uri);
					found.add(new ReferenceData(endpoint, reference));
				} catch (URISyntaxException e) {
					exceptions.add(new WorkItemCommandLineException(
							"Creating URI Reference (" + uri + ") failed: "
									+ e.getMessage() + " \n "
									+ parameter.getAttributeID() + " = "
									+ parameter.getValue()
									+ helpUsageBuildResultLink()));
				}
			}
		}
		return found;
	}

													private List<ReferenceData> createBuildResultReferences(
			ParameterValue parameter, IEndPointDescriptor endpoint,
			List<Exception> exceptions) {
		String basemessage = "Link to build result ";
		List<ReferenceData> found = new ArrayList<ReferenceData>();
		List<String> buildResults = StringUtil.splitStringToList(
				parameter.getValue(), LINK_SEPARATOR);
		if (buildResults.isEmpty()) {
			throw new WorkItemCommandLineException(basemessage
					+ " - no build ID's/Labels specified: "
					+ parameter.getAttributeID() + " = " + parameter.getValue()
					+ helpUsageBuildResultLink());
		}
		for (String buildResult : buildResults) {
			String message = basemessage + buildResult;
			IBuildResultHandle result = null;
			try {
				if (StringUtil.hasPrefix(buildResult, PREFIX_REFERENCETYPE)) {
					buildResult = StringUtil.removePrefix(buildResult,
							PREFIX_REFERENCETYPE);
					result = BuildUtil.findBuildResultbyID(buildResult,
							getTeamRepository(), monitor);
				} else {
					result = findBuildResultByLabel(buildResult);
				}
				if (result == null) {
					throw new WorkItemCommandLineException(message
							+ " failed: \n" + parameter.getValue()
							+ helpUsageBuildResultLink());
				}
				IItemReference reference = IReferenceFactory.INSTANCE
						.createReferenceToItem(result);
				found.add(new ReferenceData(endpoint, reference));
			} catch (TeamRepositoryException e) {
				throw new WorkItemCommandLineException(message + " failed: "
						+ parameter.getValue() + helpUsageBuildResultLink(), e);
			} catch (WorkItemCommandLineException e) {
				exceptions.add(new WorkItemCommandLineException(message
						+ " failed: " + e.getMessage() + " \n "
						+ parameter.getValue() + helpUsageBuildResultLink()));
			}
		}
		return found;
	}

							private void setWorkFlowAction(ParameterValue parameter)
			throws TeamRepositoryException {
		Identifier<IWorkflowAction> foundAction = findAction(parameter
				.getValue());
		if (foundAction != null) {
			((WorkItemWorkingCopy) getWorkingCopy())
					.setWorkflowAction(foundAction.getStringIdentifier());
		} else {
			throw new WorkItemCommandLineException("Action not found: "
					+ parameter.getAttributeID() + " Value: "
					+ parameter.getValue());
		}
	}

									private long getDurationFromString(String value) {
		return SimpleDateFormatUtil.convertDurationToMiliseconds(value);
	}

							private List<String> getTags(String value) {
		List<String> inputTags = StringUtil.splitStringToList(value,
				ITEM_SEPARATOR);
		List<String> trimmedTags = new ArrayList<String>(inputTags.size());
		for (String input : inputTags) {
			trimmedTags.add(input.trim());
		}
		HashSet<String> newTags = new HashSet<String>();
		newTags.addAll(trimmedTags);
		return new ArrayList<String>(newTags);
	}

											private List<String> getTagsList(List<String> oldTags, List<String> newTags) {
		HashSet<String> tags = new HashSet<String>();
		tags.addAll(oldTags);
		tags.addAll(newTags);
		return new ArrayList<String>(tags);
	}

				private ITeamRepository getTeamRepository() {
		return fTeamRepository;
	}

				private IWorkItemCommon getWorkItemCommon() {
		return (IWorkItemCommon) getTeamRepository().getClientLibrary(
				IWorkItemCommon.class);
	}

				private IAuditableCommon getAuditableCommon() {
		return (IAuditableCommon) getTeamRepository().getClientLibrary(
				IAuditableCommon.class);
	}

				private IProcessClientService getProcessClientService() {
		return (IProcessClientService) getTeamRepository().getClientLibrary(
				IProcessClientService.class);
	}

				private IWorkItemClient getWorkItemClient() {
		return (IWorkItemClient) getTeamRepository().getClientLibrary(
				IWorkItemClient.class);
	}

				private ITeamBuildClient getBuildClient() {
		return (ITeamBuildClient) getTeamRepository().getClientLibrary(
				ITeamBuildClient.class);
	}

														private HashMap<String, IContributor> getContributors(String value,
			String separator, List<String> notFoundList)
			throws TeamRepositoryException {

		HashMap<String, IContributor> contributorList = new HashMap<String, IContributor>();
		if (value == null) {
			return contributorList;
		}
		List<String> users = StringUtil.splitStringToList(value, separator);
		for (String userID : users) {
			IContributor user = findContributorFromIDorName(userID.trim());
			if (user == null) {
				notFoundList.add(userID.trim());
			} else {
				contributorList.put(user.getItemId().getUuidValue(), user);
			}
		}
		return contributorList;
	}

												@SuppressWarnings("unused")
	private Identifier<? extends ILiteral> getEnumerationLiteralStartsWithString(
			IAttributeHandle attributeHandle, String literalNamePrefix)
			throws TeamRepositoryException {
		Identifier<? extends ILiteral> literalID = null;
		IEnumeration<? extends ILiteral> enumeration = getWorkItemCommon()
				.resolveEnumeration(attributeHandle, null);

		List<? extends ILiteral> literals = enumeration
				.getEnumerationLiterals();
		for (Iterator<? extends ILiteral> iterator = literals.iterator(); iterator
				.hasNext();) {
			ILiteral iLiteral = (ILiteral) iterator.next();
			if (iLiteral.getName().startsWith(literalNamePrefix.trim())) {
				literalID = iLiteral.getIdentifier2();
				break;
			}
		}
		return literalID;
	}

								@SuppressWarnings("unused")
	private ILiteral getEnumerationLiteralByID(
			final IEnumeration<? extends ILiteral> enumeration,
			final String identifierName) {
		final Identifier<? extends ILiteral> identifier = Identifier.create(
				ILiteral.class, identifierName);
		return enumeration.findEnumerationLiteral(identifier);
	}

												private Identifier<? extends ILiteral> getEnumerationLiteralEqualsStringOrID(
			IAttributeHandle attributeHandle, String literalName)
			throws TeamRepositoryException {
		Identifier<? extends ILiteral> literalID = null;
		IEnumeration<? extends ILiteral> enumeration = getWorkItemCommon()
				.resolveEnumeration(attributeHandle, monitor);

		List<? extends ILiteral> literals = enumeration
				.getEnumerationLiterals();
		for (Iterator<? extends ILiteral> iterator = literals.iterator(); iterator
				.hasNext();) {
			ILiteral iLiteral = (ILiteral) iterator.next();

			if (iLiteral.getName().equals(literalName.trim())) {
				literalID = iLiteral.getIdentifier2();
				break;
			} else if (iLiteral.getIdentifier2().getStringIdentifier()
					.equals(literalName.trim())) {
				literalID = iLiteral.getIdentifier2();
				break;
			}
		}
		return literalID;
	}

													@SuppressWarnings("unused")
	private Identifier<? extends ILiteral> getEnumerationLiteralEqualsString_old(
			IAttributeHandle attributeHandle, String literalName)
			throws TeamRepositoryException {
		Identifier<? extends ILiteral> literalID = null;
		IEnumeration<? extends ILiteral> enumeration = getWorkItemCommon()
				.resolveEnumeration(attributeHandle, monitor);

		List<? extends ILiteral> literals = enumeration
				.getEnumerationLiterals();
		for (Iterator<? extends ILiteral> iterator = literals.iterator(); iterator
				.hasNext();) {
			ILiteral iLiteral = (ILiteral) iterator.next();
			if (iLiteral.getName().equals(literalName.trim())) {
				literalID = iLiteral.getIdentifier2();
				break;
			}
		}
		return literalID;
	}

															private void attachFile(String fileName, String description,
			String contentType, String encoding) throws TeamRepositoryException {

		File attachmentFile = new File(fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(attachmentFile);
			try {
				IAttachment newAttachment = getWorkItemClient()
						.createAttachment(getWorkItem().getProjectArea(),
								attachmentFile.getName(), description,
								contentType, encoding, fis, monitor);

				newAttachment = (IAttachment) newAttachment.getWorkingCopy();
				newAttachment = getWorkItemCommon().saveAttachment(
						newAttachment, monitor);
				IItemReference reference = WorkItemLinkTypes
						.createAttachmentReference(newAttachment);

				getWorkingCopy().getReferences().add(
						WorkItemEndPoints.ATTACHMENT, reference);

			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		} catch (FileNotFoundException e) {
			throw new WorkItemCommandLineException(
					"Attach File - File not found: " + fileName, e);
		} catch (IOException e) {
			throw new WorkItemCommandLineException(
					"Attach File - I/O Exception: " + fileName, e);
		}
	}

								private WorkItemCommandLineException modeNotSupportedException(
			ParameterValue parameter, String message) {
		String mode = "unknown";
		if (parameter.isDefault()) {
			mode = com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_DEFAULT;
		}
		if (parameter.isAdd()) {
			mode = com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_ADD;
		}
		if (parameter.isSet()) {
			mode = com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET;
		}
		if (parameter.isRemove()) {
			mode = com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_REMOVE;
		}
		return new WorkItemCommandLineException(message + " Mode: " + mode
				+ " Attribute: " + parameter.getAttributeID() + " Value: "
				+ parameter.getValue());
	}

										private WorkItemCommandLineException incompatibleAttributeValueTypeException(
			ParameterValue parameter, String message)
			throws TeamRepositoryException {
		return new WorkItemCommandLineException(message
				+ " Incompatible item type :"
				+ parameter.getIAttribute().getIdentifier() + " Value: "
				+ parameter.getValue());
	}

															private String insertLineBreaks(String contentToAdd, String stringType) {
		if (contentToAdd != null) {
			if (stringType.equals(STRING_TYPE_PLAINSTRING)) {
				contentToAdd = contentToAdd.replace(
						STRING_LINEBREAK_BACKSLASH_N, "\n");
			} else if (stringType.equals(STRING_TYPE_WIKI)) {
				contentToAdd = contentToAdd.replace(STRING_LINEBREAK_HTML_BR,
						"\n");
				contentToAdd = contentToAdd.replace(
						STRING_LINEBREAK_BACKSLASH_N, "\n");
			}
		}
		return contentToAdd;
	}

								private String helpGetTypeProperties(String attribType) {
		String message = "Type: " + attribType + " [Primitive:"
				+ AttributeTypes.isPrimitiveAttributeType(attribType)
				+ " Item:" + AttributeTypes.isItemAttributeType(attribType)
				+ " Enum:"
				+ AttributeTypes.isEnumerationAttributeType(attribType)
				+ " List:" + AttributeTypes.isListAttributeType(attribType)
				+ " Enum-List:"
				+ AttributeTypes.isEnumerationListAttributeType(attribType)
				+ " Item-List:"
				+ AttributeTypes.isItemListAttributeType(attribType)
				+ " Supported-Custom:"
				+ AttributeTypes.isSupportedCustomAttributeType(attribType)
				+ "]";
		return message;
	}

								private String helpGetDisplayStringFromList(List<String> list) {
		String display = "";
		for (String name : list) {
			display = display + name + " ";
		}
		return display;
	}

						public String helpGeneralUsage() {
		String usage = "";
		usage += "\n\nWorkItem attribute parameter and value examples:";
		usage += "\n" + helpUsageParameter();
		usage += "\n\nSpecial Properties:";
		usage += helpUsageSpecialProperties();
		usage += "\n\nWorkFlow Action: \n\tA pseudo parameter \""
				+ PSEUDO_ATTRIBUTE_TRIGGER_WORKFLOW_ACTION
				+ "\" can be used to set a workflow action to change the work item state when saving.";
		usage += helpUsageWorkflowAction();
		usage += "\n\nAttachments: \n\tA pseudo parameter "
				+ PSEUDO_ATTRIBUTE_ATTACHFILE
				+ " can be used to upload attachments.";
		usage += "\n\tThis attribute supports the modes default (same as) add, set and remove. "
				+ "\n\tSet removes all attachments, remove only removes attachments with the specified file path and description. "
				+ helpUsageAttachmentUpload();
		usage += "\n\nLinks: \n\t A pseudo parameter "
				+ PSEUDO_ATTRIBUTE_LINK
				+ " can be used to link the current work item to other objects."
				+ "\n\tLinks support the modes default (same as) add, set and remove. "
				+ "\n\tSet removes all links of the specified type before creating the new links. "
				+ helpUsageAllLinks();
		return usage;
	}

						private String helpUsageSpecialProperties() {
		String usage = "\n\tWork Item ID: Parameter \"" + IWorkItem.ID_PROPERTY
				+ "\" can not be changed.";
		usage += "\n\tProject Area: \n\tParameter \""
				+ IWorkItem.PROJECT_AREA_PROPERTY
				+ "\" can only be specified when creating the work item. It can not be set to a different value later.";
		usage += "\n\nComments: Parameter \"" + IWorkItem.COMMENTS_PROPERTY
				+ "\" can be used to add a comment.";
		usage += "\n\tThis attribute only supports the default and add mode. Comments can not be removed.";
		usage += "\n\tExample: " + IWorkItem.COMMENTS_PROPERTY + "="
				+ "\"This is a comment\"";
		usage += "\n\nSubscriptions: \n\tParameter \""
				+ IWorkItem.SUBSCRIPTIONS_PROPERTY
				+ "\" can be used to subscribe a list of users using their user ID's.";
		usage += "\n\tThis attribute supports the modes default (same as) add, set and remove mode.";
		usage += "\n\tExample set specific users: "
				+ IWorkItem.SUBSCRIPTIONS_PROPERTY
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_SET
				+ "=" + "al" + ITEM_SEPARATOR + "tammy";
		usage += "\n\tExample add users: "
				+ IWorkItem.SUBSCRIPTIONS_PROPERTY
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_ADD
				+ "=" + "deb" + ITEM_SEPARATOR + "tanuj" + ITEM_SEPARATOR
				+ "bob";
		usage += "\n\tExample remove users: "
				+ IWorkItem.SUBSCRIPTIONS_PROPERTY
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.POSTFIX_PARAMETER_MANIPULATION_MODE
				+ com.everis.fallas.operacionales.workitem.framework.ParameterValue.MODE_REMOVE
				+ "=" + "sally" + ITEM_SEPARATOR + "bob";
		usage += "\n\nTags: Parameter \"" + IWorkItem.TAGS_PROPERTY
				+ "\" can be used to add a list of tags.";
		usage += "\n\tThis attribute supports the modes default (same as) add, set and remove mode.";
		usage += "\n\tExample: " + IWorkItem.TAGS_PROPERTY + "=" + "Tag1"
				+ ITEM_SEPARATOR + ".." + ITEM_SEPARATOR + "TagN";
		usage += "\n\nApprovals: \n\tParameter \""
				+ IWorkItem.APPROVALS_PROPERTY
				+ "\" can be used to add approvals and approvers using their user ID's.";
		usage += helpUsageApprovals();
		usage += "\nWork Item State: \n\tParameter \""
				+ IWorkItem.STATE_PROPERTY
				+ "\"  can be used to change the work item state.";
		usage += helpUsageStateChange();
		return usage;
	}

						private String helpUsageParameter() {
		return "";
	}

						private String helpUsageUnspecifiedItemValues() {
		return "";
	}

						private String helpUsageApprovals() {
		return "";
	}

						private String helpUsageStateChange() {
		return "";
	}

	private String helpUsageWorkflowAction() {
		String usage = "\n\tThis attribute supports only the modes default and set. "
				+ "\n\tExample: "
				+ PSEUDO_ATTRIBUTE_TRIGGER_WORKFLOW_ACTION
				+ "=" + "\"Stop working\"";
		return usage;
	}

						private String helpUsageAttachmentUpload() {
		return "";
	}

						private String helpUsageAllLinks() {
		return "";
	}

							private String helpUsageWorkItemLinks() {
		if (isBulkUpadte()) {
			return "";
		}
		String usage = "\nFormat is:";
		usage += "\n\t" + PSEUDO_ATTRIBUTE_LINK + "linktype=value\n";
		usage += "\n\tThe parameter value is a list of one or more work items specified by their ID. The separator is:"
				+ LINK_SEPARATOR_HELP + "\n\n";
		Set<String> wiLinkTypes = ReferenceUtil
				.getWorkItemEndPointDescriptorMap().keySet();
		for (String linktype : wiLinkTypes) {
			usage += "\t" + PSEUDO_ATTRIBUTE_LINK + linktype + "=id1"
					+ LINK_SEPARATOR_HELP + "id2" + LINK_SEPARATOR_HELP
					+ "...\n";
		}
		usage += "\n\tExample:";
		usage += "\n\t\t" + PSEUDO_ATTRIBUTE_LINK + "related=123"
				+ LINK_SEPARATOR_HELP + "80";
		return usage;
	}

							private String helpUsageBuildResultLink() {
		if (isBulkUpadte()) {
			return "";
		}
		String usage = "\nFormat is:\n";
		usage += "\t" + PSEUDO_ATTRIBUTE_LINK
				+ ReferenceUtil.LINKTYPE_REPORTED_AGAINST_BUILDRESULT
				+ "=buildResult1" + LINK_SEPARATOR_HELP + "buildResult2"
				+ LINK_SEPARATOR_HELP + "...\n";
		usage += "\n\tThe parameter value is a list of one or more Buildresults specified by their ID or their label. Prefix the build labels @. The separator is:"
				+ LINK_SEPARATOR_HELP + "\n\n";
		Set<String> wiLinkTypes = ReferenceUtil
				.getBuild_EndPointDescriptorMap().keySet();
		for (String linktype : wiLinkTypes) {
			usage += "\t" + PSEUDO_ATTRIBUTE_LINK + linktype + "=id1"
					+ LINK_SEPARATOR_HELP + "@BuildLabel2"
					+ LINK_SEPARATOR_HELP + "...\n";
		}
		usage += "\n\n\tExample:";
		usage += "\n\n\t\t" + PSEUDO_ATTRIBUTE_LINK
				+ ReferenceUtil.LINKTYPE_REPORTED_AGAINST_BUILDRESULT
				+ "=@_IjluoH-oEeSHhcw_WFU6CQ" + LINK_SEPARATOR_HELP
				+ "P20141208-1713\n";
		return usage;
	}
}
