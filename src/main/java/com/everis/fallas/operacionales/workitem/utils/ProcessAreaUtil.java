package com.everis.fallas.operacionales.workitem.utils;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProcessAreaHandle;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.process.common.ITeamAreaHandle;
import com.ibm.team.process.common.ITeamAreaHierarchy;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;

public class ProcessAreaUtil {

	public static final String PATH_SEPARATOR = "/";

												public static IProcessArea findProcessAreaByFQN(String name,
			IProcessClientService processClient, IProgressMonitor monitor)
			throws TeamRepositoryException {
		URI uri = getURIFromName(name);
		return (IProcessArea) processClient.findProcessArea(uri,
				IProcessItemService.ALL_PROPERTIES, monitor);
	}

												public static IProjectArea findProjectAreaByFQN(String name,
			IProcessClientService processClient, IProgressMonitor monitor)
			throws TeamRepositoryException {
		IProcessArea processArea = findProcessAreaByFQN(name, processClient,
				monitor);
		if (null != processArea && processArea instanceof IProjectArea) {
			return (IProjectArea) processArea;
		}
		return null;
	}

												public static ITeamArea findTeamAreaByFQN(String name,
			IProcessClientService processClient, IProgressMonitor monitor)
			throws TeamRepositoryException {
		IProcessArea processArea = findProcessAreaByFQN(name, processClient,
				monitor);
		if (null != processArea && processArea instanceof ITeamArea) {
			return (ITeamArea) processArea;
		}
		return null;
	}

										public static IProjectArea getProjectAreaFormUUID(UUID uuid,
			ITeamRepository teamRepositroy, IProgressMonitor monitor)
			throws TeamRepositoryException {
		IItemHandle handle = null;
		handle = IProjectArea.ITEM_TYPE.createItemHandle(uuid, null);
		IProjectArea area = (IProjectArea) teamRepositroy.itemManager()
				.fetchCompleteItem(handle, IItemManager.DEFAULT, monitor);

		return area;
	}

										public static ITeamArea getTeamAreaFormUUID(UUID uuid,
			ITeamRepository teamRepositroy, IProgressMonitor monitor)
			throws TeamRepositoryException {
		IItemHandle handle = null;
		handle = ITeamArea.ITEM_TYPE.createItemHandle(uuid, null);
		ITeamArea area = (ITeamArea) teamRepositroy.itemManager()
				.fetchCompleteItem(handle, IItemManager.DEFAULT, monitor);

		return area;
	}

							public static URI getURIFromName(String name) {
		URI uri = URI.create(name.replaceAll(" ", "%20"));
		return uri;
	}

							public static URI getNameFromURI(String name) {
		URI uri = URI.create(name.replaceAll("%20", " "));
		return uri;
	}

									public static IProcessArea resolveProcessArea(IProcessAreaHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		if (handle instanceof IProcessArea) {
			return (IProcessArea) handle;
		}
		return (IProcessArea) ((ITeamRepository) handle.getOrigin())
				.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT,
						monitor);
	}

									public static ITeamArea resolveTeamArea(IProcessAreaHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		if (handle instanceof ITeamArea) {
			return (ITeamArea) handle;
		}
		return (ITeamArea) ((ITeamRepository) handle.getOrigin()).itemManager()
				.fetchCompleteItem(handle, IItemManager.DEFAULT, monitor);
	}

									public static IProjectArea resolveProjectArea(IProcessAreaHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		if (handle instanceof IProjectArea) {
			return (IProjectArea) handle;
		}
		return (IProjectArea) ((ITeamRepository) handle.getOrigin())
				.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT,
						monitor);
	}

										public static String getName(IProcessAreaHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		IProcessArea area = resolveProcessArea(handle, monitor);
		return area.getName();
	}

							public static String getFullQualifiedName(IProcessAreaHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {

		IProcessArea area = resolveProcessArea(handle, monitor);
		if (area instanceof IProjectArea) {
			return area.getName();
		} else if (area instanceof ITeamArea) {
			ITeamArea tArea = (ITeamArea) area;
			IProjectArea pArea = (IProjectArea) resolveProjectArea(
					area.getProjectArea(), monitor);
			return pArea.getName()
					+ PATH_SEPARATOR
					+ getFullQualifiedName(tArea, pArea.getTeamAreaHierarchy(),
							monitor);
		}
		return "";
	}

								private static String getFullQualifiedName(ITeamArea tArea,
			ITeamAreaHierarchy teamAreaHierarchy, IProgressMonitor monitor)
			throws TeamRepositoryException {
		ITeamAreaHandle parent = teamAreaHierarchy.getParent(tArea);
		String teamAreName = tArea.getName();
		if (parent == null) {
			return teamAreName;
		}
		ITeamArea parentArea = resolveTeamArea(parent, monitor);
		return getFullQualifiedName(parentArea, teamAreaHierarchy, monitor)
				+ PATH_SEPARATOR + teamAreName;
	}

}
