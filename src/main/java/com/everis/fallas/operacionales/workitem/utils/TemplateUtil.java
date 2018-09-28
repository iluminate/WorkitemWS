package com.everis.fallas.operacionales.workitem.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.everis.fallas.operacionales.workitem.bean.Parametro;
import com.everis.fallas.operacionales.workitem.bean.Stream;
import com.everis.fallas.operacionales.workitem.bean.WiProyecto;
import com.everis.fallas.operacionales.workitem.bean.Workitem;

public class TemplateUtil {
	
	private final static Logger log = Logger.getLogger(TemplateUtil.class);
	private static String message;
	
	public static List<Workitem> build(Stream string, String messagelog)
	{
		message = messagelog;
		log.info(message + "Validando Stream: " + string.getName());
		log.info(message + "tipo: " + string.getType());
		//String[] parte = string.split(Pattern.quote(ConstantesUtil.ETIQUETA_PUNTO));
		List<Workitem> response = new ArrayList<Workitem>();
		
		switch (string.getType()) {
		case "FA":
			response.add((new WiProyecto(string.getName())));
			
			/*
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROYECTO));		// INC000000000000
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_FALLA));			// SOL.INC000000000000.EAI
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.INC000000000000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.INC000000000000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.INC000000000000.EAI.COMITE_PP
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.INC000000000000.EAI.PRODUCCION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.INC000000000000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.INC000000000000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_ACTIVIDAD));		// ACT.INC000000000000.EAI.DES_FUENTES
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_DOCUMENTACION));	// DOC.INC000000000000.EAI.DESARROLLO
			*/
			break;
		case "PR":
			response.add((new WiProyecto(string.getName())));
			/*
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROYECTO));		// PROY-00000
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_FALLA));			// SOL.PROY-00000.EAI
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.EAI.ANALISIS
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.EAI.COMITE_PP
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.EAI.PRODUCCION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.PROY-00000.EAI.ANALISIS
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.PROY-00000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.PROY-00000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_ACTIVIDAD));		// ACT.PROY-00000.EAI.DES_FUENTES
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_DOCUMENTACION));	// DOC.PROY-00000.EAI.DESARROLLO
			*/
			break;
		case "PF":
			response.add((new WiProyecto(string.getName())));
			/*
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROYECTO));		// PROY-00000.INC000000000000
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_FALLA));			// SOL.PROY-00000.INC000000000000.EAI
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.INC000000000000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.INC000000000000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.INC000000000000.EAI.COMITE_PP
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_PROMOCION));		// PRM.PROY-00000.INC000000000000.EAI.PRODUCCION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.PROY-00000.INC000000000000.EAI.DESARROLLO
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_AUTORIZACION));	// AUT.PROY-00000.INC000000000000.EAI.CERTIFICACION
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_ACTIVIDAD));		// ACT.PROY-00000.INC000000000000.EAI.DES_FUENTES
			response.add(pltWorkitem(ConstantesUtil.W_TIPO_DOCUMENTACION));	// DOC.PROY-00000.INC000000000000.EAI.DESARROLLO
			*/
			break;
		}
		return response;
	}
	
	/*private static Workitem pltWorkitem(String tipo) {
		String listaParametros = null;
		switch (tipo) {
		case ConstantesUtil.W_TIPO_PROYECTO:
			listaParametros = ConstantesUtil.PL_TIPO_PROYECTO;
			break;
		case ConstantesUtil.W_TIPO_FALLA:
			listaParametros = ConstantesUtil.PL_TIPO_FALLA;
			break;
		case ConstantesUtil.W_TIPO_PROMOCION:
			listaParametros = ConstantesUtil.PL_TIPO_PROMOCION;
			break;
		case ConstantesUtil.W_TIPO_AUTORIZACION:
			listaParametros = ConstantesUtil.PL_TIPO_AUTORIZACION;
			break;
		case ConstantesUtil.W_TIPO_ACTIVIDAD:
			listaParametros = ConstantesUtil.PL_TIPO_ACTIVIDAD;
			break;
		case ConstantesUtil.W_TIPO_DOCUMENTACION:
			listaParametros = ConstantesUtil.PL_TIPO_DOCUMENTACION;
			break;
		default:
			break;
		}
		Workitem workitem = new Workitem();
		workitem.setType(tipo);
		List<Parametro> parametros = new ArrayList<Parametro>();
		String[] nombres = listaParametros.split(ConstantesUtil.ETIQUETA_PIPE);
		for (String nombre : nombres) {
			Parametro parametro = new Parametro();
			parametro.setName(nombre.split(ConstantesUtil.ETIQUETA_COMA)[0]);
			String v = nombre.split(ConstantesUtil.ETIQUETA_COMA)[1];
			String valor = MessageFormat.format(v, "CERO","UNO","DOS","TRES");
			parametro.setValue(valor);
			parametros.add(parametro);
		}
		workitem.setParametro(parametros);
		return workitem;
	}
	*/
}
