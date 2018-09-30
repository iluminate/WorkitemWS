package com.everis.fallas.operacionales.workitem.bean;

import java.util.ArrayList;
import java.util.List;

public class WiProyecto extends Workitem{
	
	public static final String cod_proyect_obj				= "Cod_Proyect_Obj";
	public static final String id_proyecto_solicutd			= "id.proyecto.solicutd";
	public static final String nombre_proyecto_solicitud	= "nombre.proyecto.solicitud";
	public static final String internalpriority				= "internalPriority";
	public static final String owner						= "owner";
	public static final String proyecto_responsable			= "Proyecto.Responsable";
	public static final String summary						= "summary";
	public static final String tipo_proyecto_solicitud		= "tipo.proyecto.solicitud";
	public static final String tipo_gerencia_objec			= "tipo_gerencia_objec";
	public static final String tipo_proveedores_object		= "tipo_proveedores_object";
	
	public WiProyecto() {
		super();
	}
	public WiProyecto(String code) {
		this.setType(W_TIPO_PROYECTO);
		List<Parametro> parametro = new ArrayList<Parametro>();
		parametro.add((new Parametro(cod_proyect_obj, code)));
		parametro.add((new Parametro(id_proyecto_solicutd, "SI")));
		parametro.add((new Parametro(nombre_proyecto_solicitud, code)));
		parametro.add((new Parametro(internalpriority, "Alta")));
		parametro.add((new Parametro(owner, "E751106")));
		parametro.add((new Parametro(proyecto_responsable, "C14681")));
		parametro.add((new Parametro(summary, code)));
		parametro.add((new Parametro(tipo_proyecto_solicitud, "Proyectos")));
		parametro.add((new Parametro(tipo_gerencia_objec, "PROYECTOS VENTA")));
		parametro.add((new Parametro(tipo_proveedores_object, "EVERIS")));
		this.setParametro(parametro);
	}
	public WiProyecto(String v1,
			String v2,
			String v3,
			String v4,
			String v5,
			String v6,
			String v7,
			String v8,
			String v9,
			String v10) {
		this.setType(W_TIPO_PROYECTO);
		List<Parametro> parametro = new ArrayList<Parametro>();
		parametro.add((new Parametro(cod_proyect_obj, v1)));
		parametro.add((new Parametro(id_proyecto_solicutd, v2)));
		parametro.add((new Parametro(nombre_proyecto_solicitud, v3)));
		parametro.add((new Parametro(internalpriority, v4)));
		parametro.add((new Parametro(owner, v5)));
		parametro.add((new Parametro(proyecto_responsable, v6)));
		parametro.add((new Parametro(summary, v7)));
		parametro.add((new Parametro(tipo_proyecto_solicitud, v8)));
		parametro.add((new Parametro(tipo_gerencia_objec, v9)));
		parametro.add((new Parametro(tipo_proveedores_object, v10)));
		this.setParametro(parametro);
	}
}
