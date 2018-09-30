package com.everis.fallas.operacionales.workitem.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.everis.fallas.operacionales.workitem.bean.Stream;
import com.everis.fallas.operacionales.workitem.bean.WiProyecto;
import com.everis.fallas.operacionales.workitem.bean.Workitem;

public class TemplateUtil {

	private final static Logger log = Logger.getLogger(TemplateUtil.class);
	private static String message;

	public static List<Workitem> build(Stream string, String messagelog) {
		message = messagelog;
		log.info(message + "Validando Stream: " + string.getName());
		log.info(message + "tipo: " + string.getType());
		List<Workitem> response = new ArrayList<Workitem>();

		switch (string.getType()) {
		case "FA":
			response.add((new WiProyecto(string.getName())));
			break;
		case "PR":
			response.add((new WiProyecto(string.getName())));
			break;
		case "PF":
			response.add((new WiProyecto(string.getName())));
			break;
		}
		return response;
	}
}
