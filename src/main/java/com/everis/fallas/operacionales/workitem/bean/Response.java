package com.everis.fallas.operacionales.workitem.bean;

import java.util.List;

public class Response {
	private String codigoError;
	private String mensajeError;
	private List<String> listaWorkitem;

	public String getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public List<String> getListaWorkitem() {
		return listaWorkitem;
	}

	public void setListaWorkitem(List<String> listaWorkitem) {
		this.listaWorkitem = listaWorkitem;
	}
}
