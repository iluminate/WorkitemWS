package com.everis.fallas.operacionales.workitem.utils;

public class ConstantesUtil {

	public static final String REGEX_SPACE = "%20";

	public static final String ETIQUETA_PUNTO = ".";
	public static final String ETIQUETA_COMA = ",";
	public static final String ETIQUETA_PUNTOYCOMA = ";";
	public static final String ETIQUETA_PIPE = "\\|";
	public static final String ETIQUETA_ESPACIO = " ";

	public static final int NUMERO_CUATRO = 4;
	public static final int NUMERO_CINCO = 5;

	public static final String ETIQUETA_INC = "INC";
	public static final String ETIQUETA_PROY = "PROY";

	public static final String LISTA_COMPONENTES = "EAI,OSB_MR,PVUDB";

	public static final String W_TIPO_PROYECTO = "com.ibm.team.workitem.workItemType.milestone";
	public static final String W_TIPO_FALLA = "com.ibm.team.workitem.workItemType.businessneed";
	public static final String W_TIPO_PROMOCION = "promover";
	public static final String W_TIPO_AUTORIZACION = "authorization";
	public static final String W_TIPO_ACTIVIDAD = "task";
	public static final String W_TIPO_DOCUMENTACION = "documentacion";

	public static final String PL_TIPO_PROYECTO = "Cod_Proyect_Obj,{2}|id.proyecto.solicutd,SI|nombre.proyecto.solicitud,{0}|internalPriority,Alta|owner,E751106|Proyecto.Responsable,C14681|summary,{0}|tipo.proyecto.solicitud,Proyectos|tipo_gerencia_objec,PROYECTOS VENTA|tipo_proveedores_object,EVERIS";
	public static final String PL_TIPO_FALLA = "Aplicacion,{3}|category,CEQ.{0}.{3}|Cod_Proyec_Sol,{2}|Código_de_Proyecto,{1}|Coordinador_de_Desarrollo,E78949|description,{1}|Falla.Responsable,C14681|id_urgencias00,SI|internalPriority,Alta|owner,E751106|summary,SOL.{0}.{3}|target,LT.FALLA.{0}.{3}/R.FALLA.{0}.{3}/01.{0}.{3}.DESARROLLO|tipo.gerencia.sol,PROYECTOS VENTA|tipo.proveedores.sol,EVERIS|type.solicitud,Proyectos";
	public static final String PL_TIPO_PROMOCION = "category,CEQ.{0}.{3}|internalPriority,Alta|owner,E704447|summary,PRM.{0}.{3}.DESARROLLO|target,LT.FALLA.{0}.{3}/R.FALLA.{0}.{3}/01.{0}.{3}.DESARROLLO";
	public static final String PL_TIPO_AUTORIZACION = "category,CEQ.{0}.{3}|cod_project_au,{0}|owner,E751106|summary,AUT.{0}.{3}.DESARROLLO|target,LT.FALLA.{0}.{3}/R.FALLA.{0}.{3}/01.{0}.{3}.DESARROLLO";
	public static final String PL_TIPO_ACTIVIDAD = "Acti_Cod_Project,{0}|category,CEQ.{0}.{3}|Id_nombre_actividad,DES_FUENTES|id_proceso_negocio_Act,PROYECTOS VENTA|internalPriority,Alta|owner,E78949|summary,ACT.{0}.{3}.DES_FUENTES|target,LT.FALLA.{0}.{3}/R.FALLA.{0}.{3}/01.{0}.{3}.DESARROLLO|tipo_gerencia_act,PROYECTOS VENTA|tipo_proveedores,EVERIS";
	public static final String PL_TIPO_DOCUMENTACION = "category,CEQ.{0}.{3}|owner,E705590|summary,DOC.{0}.{3}.DESARROLLO|target,LT.FALLA.{0}.{3}/R.FALLA.{0}.{3}/01.{0}.{3}.DESARROLLO";
}
