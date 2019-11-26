package com.heliumhq.environment;

import java.io.File;

public class ResourceLocator {

	String[] rootDirs;

	public ResourceLocator(String... rootDirs) {
		this.rootDirs = rootDirs;
	}

	public String locate(String... relPathComponents) {
		for (String rootDir : rootDirs) {
			String location = constructPath(rootDir, relPathComponents);
			if (new File(location).exists())
				return location;
		}
		return constructPath(rootDirs[0], relPathComponents);
	}

	private String constructPath(String root, String... relPathComponents) {
		File result = new File(root);
		for (String relPathComponent : relPathComponents)
			result = new File(result, relPathComponent);
		return result.getAbsolutePath();
	}

}