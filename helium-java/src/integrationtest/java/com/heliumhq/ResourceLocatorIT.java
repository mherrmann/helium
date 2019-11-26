package com.heliumhq;

import com.heliumhq.environment.ResourceLocator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.heliumhq.Environment.getIntegrationtestResource;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResourceLocatorIT {

	private String rootDir;

	@Before
	public void setUp() {
		rootDir = getIntegrationtestResource("inttest_environment");
	}

	@Test
	public void testLocateFile() {
		ResourceLocator resourceLocator = new ResourceLocator(rootDir);
		String result = resourceLocator.locate("file.txt");
		assertNotNull(result);
		assertTrue(new File(result).exists());
	}

	@Test
	public void testLocateFileInDir() {
		ResourceLocator resourceLocator = new ResourceLocator(rootDir);
		String result = resourceLocator.locate("dir", "file_in_dir.txt");
		assertNotNull(result);
		assertTrue(new File(result).exists());
	}

	@Test
	public void testLocateNonExistentFile() {
		ResourceLocator resourceLocator = new ResourceLocator(rootDir);
		String result = resourceLocator.locate("non-existent file");
		assertNotNull(result);
	}

	@Test
	public void testMultipleRootDirectories() {
		ResourceLocator resourceLocator = new ResourceLocator(
			rootDir, getIntegrationtestResource("inttest_environment/dir")
		);
		String result = resourceLocator.locate("file_in_dir.txt");
		assertNotNull(result);
		assertTrue(new File(result).exists());
	}

}