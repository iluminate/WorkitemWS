package com.everis.fallas.operacionales.workitem.utils;

import java.io.File;

public class FileUtil {

						public static void createFolderWithParents(File aFolder) {
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}

						public static void createFolderWithParents(String folderName) {
		File aFolder = new File(folderName);
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}
}
