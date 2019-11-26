package com.heliumhq.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileUtilTest {
	@Test
	public void testTextFileCopy() throws IOException {
		File source = File.createTempFile("src", ".txt");
		File destination = File.createTempFile("dest", ".txt");
		assertTrue(destination.exists());
		destination.delete();
		assertFalse(destination.exists());
		FileUtil.copyFile(source, destination);
		assertTrue(destination.exists());
		destination.delete();
		assertFalse(destination.exists());
		source.deleteOnExit();
	}

	@Test
	public void testTextFileCopyNonExistentDir() throws IOException {
		File source = File.createTempFile("src", ".txt");
		File destination = new File(new File(java.lang.System.getProperty("java.io.tmpdir", null), "helium-tempdir"), source.getName());
		File destDir = FileUtil.getDirectory(destination);
		assertFalse(destDir.exists());
		assertFalse(destination.exists());
		FileUtil.copyFile(source, destination);
		assertTrue(destDir.exists());
		assertTrue(destination.exists());
		source.deleteOnExit();
		destination.delete();
		destDir.delete();
	}
}
