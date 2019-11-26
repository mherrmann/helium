package com.heliumhq.api_impl.application_context;

public class ApplicationContext {

	private static APIConfig APPLICATION_CONTEXT;

	public static APIConfig getApplicationContext() {
		if (APPLICATION_CONTEXT == null) {
			if (isDevelopment())
				APPLICATION_CONTEXT = new DevelopmentAPIConfig();
			else
				APPLICATION_CONTEXT = new StandaloneAPIConfig();
		}
		return APPLICATION_CONTEXT;
	}

	private static boolean isDevelopment() {
		return ! isRunningFromJar();
	}

	private static boolean isRunningFromJar() {
		String className = ApplicationContext.class.getName().replace('.', '/');
		String classFileLocation = ApplicationContext.class.getResource(
				"/" + className + ".class"
		).toString();
		return classFileLocation.startsWith("jar:");
	}

}
