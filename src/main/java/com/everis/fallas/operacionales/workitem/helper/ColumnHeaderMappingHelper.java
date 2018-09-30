package com.everis.fallas.operacionales.workitem.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.framework.ParameterValue;
import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;
import com.everis.fallas.operacionales.workitem.parameter.ColumnHeaderAttributeNameMapper;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;

public class ColumnHeaderMappingHelper {

	public static final String SEPARATOR_COLUMNS = ",";
	private static final String DEFAULT_COLUMNS = "workItemType,id,internalState,internalPriority,internalSeverity,summary,owner,creator";

	IProjectAreaHandle fProjectArea;
	IWorkItemCommon fWorkItemCommon;
	IProgressMonitor fMonitor;

	List<ParameterValue> columns = new ArrayList<ParameterValue>();
	private String[] fColumns = null;

								public ColumnHeaderMappingHelper(IProjectAreaHandle projectArea,
			IWorkItemCommon workItemCommon, IProgressMonitor monitor) {
		super();
		this.fProjectArea = projectArea;
		this.fWorkItemCommon = workItemCommon;
		this.fMonitor = monitor;
	}

						public List<String> analyzeColumnHeader(boolean getIDs)
			throws TeamRepositoryException, WorkItemCommandLineException {
		String[] exportColumns = getColumns();

		if (exportColumns == null) {
			throw new WorkItemCommandLineException(
					"Column header can not be null in column ");
		}

		ColumnHeaderAttributeNameMapper nameToIdMapper = new ColumnHeaderAttributeNameMapper(
				fProjectArea, fWorkItemCommon, fMonitor);
		int size = exportColumns.length;
		List<String> header = new ArrayList<String>(size);

		for (int i = 0; i < size; i++) {
			String col = exportColumns[i];
			if (col == null) {
				throw new WorkItemCommandLineException(
						"Column ID can not be null in column " + i);
			}
			String val = col.trim();
			String id = nameToIdMapper.getID(val);
			if (id == null) {
				throw new WorkItemCommandLineException("Column header " + col
						+ " ID can not be mapped in column " + i);
			}
			if (getIDs) {
				header.add(id);
			} else {
				header.add(col);
			}
			ParameterValue columnParameter = new ParameterValue(id, null,
					fProjectArea, fMonitor);
			addColumnParameter(i, columnParameter);
		}
		return header;
	}

							private void addColumnParameter(int i, ParameterValue columnParameter) {
		getParameters().add(i, columnParameter);
	}

						public List<ParameterValue> getParameters() {
		return this.columns;
	}

						public void setColumns(String columns) {
		fColumns = columns.split(SEPARATOR_COLUMNS);
	}

						public void setColumns(String[] columns) {
		fColumns = columns;
	}

						public String[] getColumns() {
		if (fColumns == null) {
			fColumns = DEFAULT_COLUMNS.split(SEPARATOR_COLUMNS);
		}
		return fColumns;
	}
}
