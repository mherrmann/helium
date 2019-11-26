package com.heliumhq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {

	public static void copyFile(File sourceFile, File destFile) throws
			IOException {
		// make sure the destination directory exists
		File outDirectory = getDirectory(destFile);
		if (!outDirectory.exists()) {
			outDirectory.mkdirs();
		}

		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static File getDirectory(File file) {
		int sep = file.getAbsolutePath().lastIndexOf(File.separator);
		File outDirectory = new File(file.getAbsolutePath().substring(0, sep));
		return outDirectory;
	}
}
