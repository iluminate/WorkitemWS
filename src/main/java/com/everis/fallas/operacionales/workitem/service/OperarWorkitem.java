package com.everis.fallas.operacionales.workitem.service;


import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.everis.fallas.operacionales.workitem.bean.Auditoria;
import com.everis.fallas.operacionales.workitem.bean.Parametro;
import com.everis.fallas.operacionales.workitem.bean.Request;
import com.everis.fallas.operacionales.workitem.bean.Response;
import com.everis.fallas.operacionales.workitem.bean.Workitem;
import com.everis.fallas.operacionales.workitem.business.WorkItemInitialization;
import com.everis.fallas.operacionales.workitem.util.Argument;
import com.everis.fallas.operacionales.workitem.util.Repository;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;


@Path("/workitem")
public class OperarWorkitem {
	Response response = new Response();
	@POST
	@Path("/create")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createWorkitem(Request request){
		List<Workitem> workitems = request.getWorkitem();
		Auditoria audit = request.getAuditoria();
		Argument argument = new Argument();
		Repository conexion = new Repository(audit);
		try {
			argument.valid(workitems);
			conexion.login();
			boolean result = run(request, conexion);
			if (result) {
				response.setCodigoError("0");
				response.setMensajeError("Proceso exitoso.");
			}
		} catch (TeamRepositoryException e) {
			response.setCodigoError("1");
			response.setMensajeError(e.getMessage());
		} catch (SocketTimeoutException e) {
			response.setCodigoError("1");
			response.setMensajeError(e.getMessage());
		} finally {
			conexion.logout();
			System.out.println(response.getMensajeError());
		}
		return response;
	}

	private boolean run(Request request,Repository conexion) throws TeamRepositoryException {
		IProcessClientService processClient = (IProcessClientService) conexion.getTeamrepo()
				.getClientLibrary(IProcessClientService.class);
		IWorkItemClient workItemClient = (IWorkItemClient) conexion.getTeamrepo()
				.getClientLibrary(IWorkItemClient.class);
		IAuditableClient auditableClient = (IAuditableClient) conexion.getTeamrepo()
				.getClientLibrary(IAuditableClient.class);
		IProjectArea projectArea = obtenerArea(request.getAuditoria().getArea(), processClient);
		if (projectArea == null) {
			response.setCodigoError("1");
			response.setMensajeError("No se encontro el area de proyecto: " + request.getAuditoria().getArea());
			return false;
		}
		
		List<Integer> wcode = new ArrayList<Integer>();
		
		List<String> listaWorkitem = new ArrayList<String>();
		for (Workitem workitem : request.getWorkitem()) {
			IWorkItemType workItemType = workItemClient.findWorkItemType(
					projectArea, workitem.getType(), null);
			if (workItemType == null) {
				response.setCodigoError("1");
				response.setMensajeError("No se encontro el tipo de work item: " + workitem.getType());
				return false;
			}
			
			List<Parametro> param = agregarRelaciones(workitem.getParametro(), wcode);
			WorkItemInitialization operation = new WorkItemInitialization(param);
			IWorkItemHandle handle = operation.run(workItemType, null);
			IWorkItem workItem = auditableClient.resolveAuditable(handle, IWorkItem.FULL_PROFILE, null);
			wcode.add(workItem.getId());
			System.out.println("Elemento de trabajo creado correctamente: " + workItem.getId() + " (" + workItem.getHTMLSummary().getPlainText().toString() + ").");
			listaWorkitem.add(workItem.getId() + "|" + workItem.getHTMLSummary().getPlainText().toString());
		}
		response.setListaWorkitem(listaWorkitem);
		return true;
	}

	private List<Parametro> agregarRelaciones(List<Parametro> parametro, List<Integer> wcode) {
		List<Parametro> newParams = new ArrayList<Parametro>(); 
		for (Parametro p : parametro) {
			if ("@link_parent".equals(p.getName()) || "@link_related".equals(p.getName())) {
				try {
					String value = p.getValue();
					if (value.charAt(0) == '#') {
						int nc = Integer.parseInt(p.getValue().substring(1));
						nc = wcode.get(nc - 1);
						value = String.valueOf(nc);
					}
					Parametro newParam = new Parametro();
					newParam.setName(p.getName());
					newParam.setValue(value);
					newParams.add(newParam);
				} catch (Exception e) {
					System.out.println("Ocurrio un error al obtener la relacion: " + e.getMessage());
				}
			} else {
				Parametro newParam = new Parametro();
				newParam.setName(p.getName());
				newParam.setValue(p.getValue());
				newParams.add(newParam);
			}
		}
		return newParams;
	}

	private IProjectArea obtenerArea(String area, IProcessClientService processClient) {
		URI uri = URI.create(area.replaceAll(" ", "%20"));
		IProjectArea projectArea = null;
		try {
			projectArea = (IProjectArea) processClient.findProcessArea(uri, null, null);
			if (projectArea == null) {
				System.out.println("No se encontro el area de proyecto: " + area);
			} else {
				System.out.println("Se encontro el area de proyecto: " + projectArea.getName());
			}
		} catch (TeamRepositoryException e) {
			System.out.println("Error al buscar el area de proyecto: " + area);
			e.printStackTrace();
		}
		return projectArea;
	}
}
