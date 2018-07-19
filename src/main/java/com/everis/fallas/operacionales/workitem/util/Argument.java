package com.everis.fallas.operacionales.workitem.util;

import java.util.List;

import com.everis.fallas.operacionales.workitem.bean.Workitem;
import com.ibm.team.repository.common.TeamRepositoryException;

public class Argument {
	public boolean valid(List<Workitem> wi) throws TeamRepositoryException {
		int countWi = 0;
		for (Workitem workitem : wi) {
			countWi++;
			if (workitem.getType() == null || workitem.getType() == "") {
				throw new TeamRepositoryException("Debe introducir un tipo de workitem {" + countWi + "}");
			}
		}
		return true;
	}
}