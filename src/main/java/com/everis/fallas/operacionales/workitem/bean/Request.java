package com.everis.fallas.operacionales.workitem.bean;

import java.util.List;

public class Request {
	private Auditoria auditoria;
	private List<Workitem> workitem;
	
	public Auditoria getAuditoria() {
		return auditoria;
	}
	public void setAuditoria(Auditoria auditoria) {
		this.auditoria = auditoria;
	}
	public List<Workitem> getWorkitem() {
		return workitem;
	}
	public void setWorkitem(List<Workitem> workitem) {
		this.workitem = workitem;
	}
}
