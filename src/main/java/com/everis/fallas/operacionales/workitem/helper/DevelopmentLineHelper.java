package com.everis.fallas.operacionales.workitem.helper;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.everis.fallas.operacionales.workitem.utils.ProcessAreaUtil;
import com.ibm.team.process.common.IDevelopmentLine;
import com.ibm.team.process.common.IDevelopmentLineHandle;
import com.ibm.team.process.common.IIteration;
import com.ibm.team.process.common.IIterationHandle;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.common.model.ItemProfile;

public class DevelopmentLineHelper {

	private enum Mode {
		BYID, BYNAME, BYLABEL
	};

	public static final Mode BYID = Mode.BYID;
	public static final Mode BYNAME = Mode.BYNAME;
	public static final Mode BYLABEL = Mode.BYLABEL;

	private ITeamRepository fTeamRepository;
	private IProgressMonitor fMonitor;
	private IAuditableClient fAuditableClient;

	public DevelopmentLineHelper(ITeamRepository teamRepository, IProgressMonitor monitor) {
		fTeamRepository = teamRepository;
		fMonitor = monitor;
	}

	public IDevelopmentLine findDevelopmentLine(IProjectArea projectArea, List<String> path, Mode comparemode)
			throws TeamRepositoryException {
		int level = 0;
		String fookFor = path.get(level);
		IDevelopmentLineHandle[] developmentLineHandles = projectArea.getDevelopmentLines();
		for (IDevelopmentLineHandle developmentLineHandle : developmentLineHandles) {
			IDevelopmentLine developmentLine = fAuditableClient.resolveAuditable(developmentLineHandle,
					ItemProfile.DEVELOPMENT_LINE_DEFAULT, fMonitor);
			String compare = "";
			switch (comparemode) {
			case BYID:
				compare = developmentLine.getId().trim();
				break;
			case BYNAME:
				compare = developmentLine.getName().trim();
				break;
			case BYLABEL:
				compare = developmentLine.getLabel().trim();
				break;
			}
			if (fookFor.equals(compare)) {
				return developmentLine;
			}
		}
		return null;
	}

	public IIteration findIteration(IProjectAreaHandle iProjectAreaHandle, List<String> path, Mode comparemode)
			throws TeamRepositoryException {
		fAuditableClient = (IAuditableClient) fTeamRepository.getClientLibrary(IAuditableClient.class);
		IIteration foundIteration = null;
		IProjectArea projectArea = ProcessAreaUtil.resolveProjectArea(iProjectAreaHandle, fMonitor);
		IDevelopmentLine developmentLine = findDevelopmentLine(projectArea, path, comparemode);
		if (developmentLine != null) {
			foundIteration = findIteration(developmentLine.getIterations(), path, 1, comparemode);
		}
		return foundIteration;
	}

	private IIteration findIteration(IIterationHandle[] iterations, List<String> path, int level, Mode comparemode)
			throws TeamRepositoryException {
		String lookFor = path.get(level);
		for (IIterationHandle iIterationHandle : iterations) {

			IIteration iteration = fAuditableClient.resolveAuditable(iIterationHandle, ItemProfile.ITERATION_DEFAULT,
					fMonitor);
			String compare = "";
			switch (comparemode) {
			case BYID:
				compare = iteration.getId().trim();
				break;
			case BYNAME:
				compare = iteration.getName().trim();
				break;
			case BYLABEL:
				compare = iteration.getLabel().trim();
				break;
			}
			if (lookFor.equals(compare)) {
				if (path.size() > level + 1) {
					IIteration found = findIteration(iteration.getChildren(), path, level + 1, comparemode);
					if (found != null) {
						return found;
					}
				} else {
					return iteration;
				}
			}
		}
		return null;
	}

	public IIteration resolveIteration(IIterationHandle handle) throws TeamRepositoryException {
		if (handle instanceof IIteration) {
			return (IIteration) handle;
		}
		IIteration iteration = (IIteration) fTeamRepository.itemManager().fetchCompleteItem((IIterationHandle) handle,
				IItemManager.DEFAULT, fMonitor);
		return iteration;
	}

	public IDevelopmentLine resolveDevelopmentLine(IDevelopmentLineHandle handle) throws TeamRepositoryException {
		if (handle instanceof IDevelopmentLine) {
			return (IDevelopmentLine) handle;
		}
		IDevelopmentLine devLine = (IDevelopmentLine) fTeamRepository.itemManager().fetchCompleteItem(handle,
				IItemManager.DEFAULT, fMonitor);
		return devLine;
	}

	public String getDevelopmentLineAsString(IDevelopmentLineHandle handle, Mode mode) throws TeamRepositoryException {
		IDevelopmentLine devLine = resolveDevelopmentLine(handle);
		switch (mode) {
		case BYID:
			return devLine.getId();
		case BYNAME:
			return devLine.getName();
		case BYLABEL:
			return devLine.getLabel();
		}
		return devLine.getLabel();
	}

	public String getIterationAsString(IIterationHandle handle, Mode mode) throws TeamRepositoryException {
		IIteration iteration = resolveIteration(handle);
		switch (mode) {
		case BYID:
			return iteration.getId();
		case BYNAME:
			return iteration.getName();
		case BYLABEL:
			return iteration.getLabel();
		}
		return iteration.getLabel();
	}

	public String getIterationAsFullPath(IIterationHandle handle, Mode mode) throws TeamRepositoryException {
		IIteration iteration = resolveIteration(handle);
		String fullPath = getIterationAsString(iteration, mode);
		IIterationHandle parent = iteration.getParent();
		if (parent == null) {
			IDevelopmentLineHandle devLineHandle = iteration.getDevelopmentLine();
			return getDevelopmentLineAsString(devLineHandle, mode) + WorkItemUpdateHelper.PATH_SEPARATOR + fullPath;
		} else {
			return getIterationAsFullPath(parent, mode) + WorkItemUpdateHelper.PATH_SEPARATOR + fullPath;
		}
	}
}
