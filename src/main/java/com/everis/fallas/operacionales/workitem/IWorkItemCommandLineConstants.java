package com.everis.fallas.operacionales.workitem;

public interface IWorkItemCommandLineConstants {

	public static final String VERSIONINFO = "V3.2";

	public static final String COMMAND_CREATE = "create";
	public static final String COMMAND_UPDATE = "update";
	public static final String COMMAND_PRINT_TYPE_ATTRIBUTES = "printtypeattributes";
	public static final String COMMAND_IMPORT_WORKITEMS = "importworkitems";
	public static final String COMMAND_EXPORT_WORKITEMS = "exportworkitems";

	public static final String SWITCH_IGNOREERRORS = "ignoreErrors";

	public static final String SWITCH_ENABLE_DELETE_ATTACHMENTS = "enableDeleteAttachment";
	public static final String SWITCH_ENABLE_DELETE_APPROVALS = "enableDeleteApprovals";

	public static final String SWITCH_IMPORT_DEBUG = "importdebug";
	public static final String SWITCH_BULK_OPERATION = "bulkupdate";

	public static final String SWITCH_ENFORCE_SIZE_LIMITS = "enforceSizeLimits";

	public static final String PARAMETER_REPOSITORY_URL_PROPERTY = "repository";
	public static final String PARAMETER_REPOSITORY_URL_PROPERTY_EXAMPLE = "\"https://clm.example.com:9443/ccm\"";
	public static final String PARAMETER_USER_ID_PROPERTY = "user";
	public static final String PARAMETER_USER_ID_PROPERTY_EXAMPLE = "user";
	public static final String PARAMETER_PASSWORD_PROPERTY = "password";
	public static final String PARAMETER_PASSWORD_PROPERTY_EXAMPLE = "password";

	public static final String PARAMETER_WORKITEM_ID_PROPERTY = "id";
	public static final String PROPERTY_WORKITEM_ID_PROPERTY_EXAMPLE = "123";

	public static final String PARAMETER_PROJECT_AREA_NAME_PROPERTY = "projectArea";
	public static final String PARAMETER_PROJECT_AREA_NAME_PROPERTY_EXAMPLE = "\"JKE Banking (Change Mangement)\"";

	public static final String PARAMETER_WORKITEM_TYPE_PROPERTY = "workItemType";
	public static final String PARAMETER_WORKITEM_TYPE_PROPERTY_EXAMPLE = "defect";

	public static final String TIMESTAMP_EXPORT_IMPORT_FORMAT_MMM_D_YYYY_HH_MM_A = "MMM d, yyyy hh:mm a";

	public static final String PARAMETER_TIMESTAMP_ENCODING = "timestampFormat";
	public static final String PARAMETER_TIMESTAMP_ENCODING_EXAMPLE = "\"TIMESTAMP_EXPORT_IMPORT_FORMAT_MMM_D_YYYY_HH_MM_A\"";

	public static final String INFIX_PARAMETER_VALUE_SEPARATOR = "=";
	public static final String PREFIX_COMMAND = "-";
	public static final String PREFIX_SWITCH = "/";

	public static final String RESULT_SUCCESS = "Success!";
	public static final String RESULT_FAILED = "Failed!";

	public static final String SWITCH_RMISERVER = "rmiServer";
	public static final String SWITCH_RMICLIENT = "rmiClient";

	public static final String HTTP_PROTOCOL_PREFIX = "http";

	public static final char DEFAULT_DELIMITER = ',';

	public static final String DEFAULT_ENCODING_UTF_16LE = "UTF-16LE";

	public static final char DEFAULT_QUOTE_CHAR = '"';

	public static final String PARAMETER_ENCODING = "encoding";
	public static final String PARAMETER_ENCODING_EXAMPLE = "\"UTF_16LE\"";

	public static final String PARAMETER_DELIMITER = "delimiter";
	public static final String PARAMETER_DELIMITER_EXAMPLE = "\",\"";

	public static final String SWITCH_EXPORT_SUPPRESS_ATTRIBUTE_EXCEPTIONS = "suppressAttributeExceptions";

}
