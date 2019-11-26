package com.heliumhq.api_impl.application_context;

import com.heliumhq.environment.ResourceLocator;
import com.heliumhq.api_impl.APIImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class APIConfig {

	private APIImpl apiImpl;

	public APIConfig() {
		initializeLogging();
	}

	protected abstract void initializeLogging();

	public APIImpl getAPIImpl() {
		if (apiImpl == null)
			apiImpl = new APIImpl(getResourceLocator());
		return apiImpl;
	}

	public abstract ResourceLocator getResourceLocator();

	protected void suppressSeleniumLoggers() {
		Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
	}

}
