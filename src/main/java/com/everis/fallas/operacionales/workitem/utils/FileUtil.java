/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 20015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.utils;

import java.io.File;

/**
 * Tool for file access
 * 
 * @see com.ibm.team.filesystem.rcp.core.internal.compare.ExternalCompareToolsUtil
 * @see com.ibm.team.filesystem.setup.junit.internal.SourceControlContribution
 * 
 * 
 */
public class FileUtil {

	/**
	 * Create a folder
	 * 
	 * @param aFolder
	 */
	public static void createFolderWithParents(File aFolder) {
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}

	/**
	 * Create a folder
	 * 
	 * @param aFolder
	 */
	public static void createFolderWithParents(String folderName) {
		File aFolder = new File(folderName);
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}
}
