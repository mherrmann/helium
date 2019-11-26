package com.heliumhq;

import static com.heliumhq.api_impl.application_context.DevelopmentAPIConfig.
		getHeliumFile;

public class Environment {

	public static String getIntegrationtestResource(String relPath) {
		return getHeliumFile("src/integrationtest/resources/" + relPath);
	}

	public static String getITFileURL(String page) {
		return pathToFileURL(getIntegrationtestResource(page));
	}

	private static String pathToFileURL(String path) {
		return "file:///" + path.replace("\\", "/");
	}

}
