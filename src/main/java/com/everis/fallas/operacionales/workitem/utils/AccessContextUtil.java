package com.everis.fallas.operacionales.workitem.utils;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IAccessGroup;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IContext;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.workitem.common.IAuditableCommon;

public class AccessContextUtil {

				public static final String PUBLIC_ACCESS = "Public";

										public static Object getAccessContextFromUUID(UUID uuid,
			ITeamRepository teamRepository, IAuditableCommon auditableCommon,
			IProgressMonitor monitor) {
		if (uuid == null) {
			return null;
		}
		if (IContext.PUBLIC.equals(uuid)) {
			return uuid;
		}
		try {
			IProjectArea area = ProcessAreaUtil.getProjectAreaFormUUID(uuid,
					teamRepository, monitor);
			return area;
		} catch (Exception e) {
		}
		try {
			ITeamArea area = ProcessAreaUtil.getTeamAreaFormUUID(uuid,
					teamRepository, monitor);
			return area.getName();
		} catch (Exception e) {
		}
		IAccessGroup[] groups;
		try {
			groups = auditableCommon.getAccessGroups(null, Integer.MAX_VALUE,
					monitor);
			for (IAccessGroup group : groups) {
				if (group.getContextId().equals(uuid)) {
					return group;
				}
			}
		} catch (TeamRepositoryException e) {
		}
		return null;
	}

													public static UUID getAccessContextFromFQN(String value,
			ITeamRepository teamRepository, IAuditableCommon auditableCommon,
			IProcessClientService processClient, IProgressMonitor monitor)
			throws TeamRepositoryException {
		if (null == value) {
			return null;
		}
		if (value.equals(PUBLIC_ACCESS)) {
			return IContext.PUBLIC;
		}
		try {
			IProcessArea processArea = ProcessAreaUtil.findProcessAreaByFQN(
					value, processClient, monitor);
			if (processArea != null) {
				return processArea.getContextId();
			}
		} catch (TeamRepositoryException e) {
		}
		IAccessGroup[] groups;
		try {
			groups = auditableCommon.getAccessGroups(null, Integer.MAX_VALUE,
					monitor);
			for (IAccessGroup group : groups) {
				if (group.getName().equals(value)) {
					return group.getContextId();
				}
			}
		} catch (TeamRepositoryException e) {
		}
		return null;
	}
}
