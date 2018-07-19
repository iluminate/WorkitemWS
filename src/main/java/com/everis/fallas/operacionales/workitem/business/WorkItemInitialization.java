package com.everis.fallas.operacionales.workitem.business;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.bean.Parametro;
import com.everis.fallas.operacionales.workitem.helper.WorkItemUpdateHelper;
import com.everis.fallas.operacionales.workitem.parameter.ParameterList;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;

public class WorkItemInitialization extends WorkItemOperation {

	private List<Parametro> parametros;
	private WorkItemWorkingCopy workingCopy;
	
	public WorkItemInitialization(
			List<Parametro> parametros
			) {
		super("Crear Workitem.");
		this.parametros = parametros;
	}

	@Override
	protected void execute(WorkItemWorkingCopy workingCopy,
			IProgressMonitor monitor) throws TeamRepositoryException {
		this.workingCopy = workingCopy;
		try {
			update(parametros);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void update(List<Parametro> parametros) throws TeamRepositoryException, IOException {
		IProgressMonitor monitor = null;
		ParameterList arguments = new ParameterList();
		WorkItemUpdateHelper workItemHelper = new WorkItemUpdateHelper(workingCopy, arguments, monitor);
		for (Parametro parametro : parametros) {
			try {
				workItemHelper.updateProperty(parametro.getName(), parametro.getValue());
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			System.out.println(parametro.getName() + "|" + parametro.getValue());
		}
	}
}
