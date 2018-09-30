package com.everis.fallas.operacionales.workitem.bean;

import java.util.List;

public class Workitem {

	public static final String W_TIPO_PROYECTO = "com.ibm.team.workitem.workItemType.milestone";
	public static final String W_TIPO_FALLA = "com.ibm.team.workitem.workItemType.businessneed";
	public static final String W_TIPO_PROMOCION = "promover";
	public static final String W_TIPO_AUTORIZACION = "authorization";
	public static final String W_TIPO_ACTIVIDAD = "task";
	public static final String W_TIPO_DOCUMENTACION = "documentacion";

	private String type;
	private List<Parametro> parametro;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Parametro> getParametro() {
		return parametro;
	}

	public void setParametro(List<Parametro> parametro) {
		this.parametro = parametro;
	}
}
