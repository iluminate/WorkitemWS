package com.everis.fallas.operacionales.workitem.bean;

import java.util.List;

public class Workitem {
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
