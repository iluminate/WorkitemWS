package com.everis.fallas.operacionales.workitem.bean;

import org.apache.log4j.Logger;

import com.everis.fallas.operacionales.workitem.utils.TemplateUtil;

public class Stream {
	
	private final static Logger log = Logger.getLogger(TemplateUtil.class);
	
	private String type;
	private String name;
	
	public Stream() {}
	public Stream(String string) {
		this.name = string; 
		this.type = obtenerTipo(string);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String obtenerTipo(String string) {
		String[] partes = string.split("\\.");
		int numbPartes = partes.length;
		String tipo = "";
		log.info("string: " + string);
		log.info("numbPartes: " + numbPartes);
		for (int i = 0; i < partes.length; i++) {
			log.info(partes[i]);
		}
		if ( numbPartes == 5 ) {
			tipo = "PF";
		} else if (numbPartes == 4 && partes[2].contains("PROY")) {
			tipo = "PR";
		} else if (numbPartes == 4 && partes[2].contains("INC")) {
			tipo = "FA";
		}
		return tipo;
	}
}
