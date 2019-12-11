package com.heliumhq.api_impl.application_context;

import com.heliumhq.environment.ResourceLocator;

import java.io.File;
import java.util.Arrays;

import static com.heliumhq.util.System.*;

public class DevelopmentAPIConfig extends APIConfig {

	private ResourceLocator resourceLocator;

	public static String getHeliumFile(String relPath) {
		File result = getHeliumDir();
		for (String relPathComponent : relPath.split("/"))
			result = new File(result, relPathComponent);
		return result.getAbsolutePath();
	}

	public static File getHeliumDir() {
		File result = new File(".").getAbsoluteFile();
		while (! Arrays.asList(result.list()).contains("pom.xml") ||
				! result.getName().equalsIgnoreCase("helium"))
			result = result.getParentFile();
		return result;
	}

	@Override
	protected void initializeLogging() {
		suppressSeleniumLoggers();
	}

	@Override
	public ResourceLocator getResourceLocator() {
		if (resourceLocator == null) {
			String platfDir;
			if (isWindows())
				platfDir = "win";
			else if (isLinux())
				platfDir = "linux";
			else {
				assert isOSX();
				platfDir = "macosx";
			}
			resourceLocator = new ResourceLocator(
				getHeliumFile("src/main/resources/" + platfDir),
				getHeliumFile("target")
			);
		}
		return resourceLocator;
	}

}
