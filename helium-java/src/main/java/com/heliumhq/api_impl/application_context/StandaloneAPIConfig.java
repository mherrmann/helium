package com.heliumhq.api_impl.application_context;

import com.heliumhq.environment.ResourceLocator;
import com.heliumhq.util.FileUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

import static com.heliumhq.util.Date.createDate;
import static java.util.logging.Level.WARNING;

public class StandaloneAPIConfig extends APIConfig {

	private ResourceLocator resourceLocator;

	@Override
	protected void initializeLogging() {
		suppressApacheHttpLogsOnStderr();
		suppressSeleniumLoggers();
	}

	/**
	 * When starting IEDriverServer, the Java implementation of Selenium polls
	 * the server by sending HTTP requests to it until it has initialized. Each
	 * failed poll attempt before that prints some log output on stderr:
	 *
	 *     org.apache.http.impl.client.DefaultHttpClient tryExecute
	 *     INFO: I/O exception (java.net.SocketException) caught when processing
	 *     request: Software caused connection abort: recv failed
	 *     org.apache.http.impl.client.DefaultHttpClient tryExecute
	 *     INFO: Retrying request
	 *
	 * suppressApacheHttpLogsOnStderr() prevents this log message from
	 * appearing in most (but not all) cases.
	 *
	 * For further information on the issue, see http://jimevansmusic.blogspot.
	 * com/2012/12/seeing-info-messages-in-log-does-not.html or https://groups.
	 * google.com/forum/#!topic/selenium-users/iZKKXkhfV2k.
	 */
	private void suppressApacheHttpLogsOnStderr() {
		Logger.getLogger("org.apache.http.impl").setLevel(Level.WARNING);
	}

	@Override
	public ResourceLocator getResourceLocator() {
		if (resourceLocator == null) {
			File containingJar = urlToFile(
				StandaloneAPIConfig.class.getProtectionDomain().getCodeSource().
				getLocation()
			);
			String heliumRoot = containingJar.getParentFile().
					getParentFile().getAbsolutePath();
			resourceLocator = new ResourceLocator(heliumRoot);
		}
		return resourceLocator;
	}

	/**
	 * A file:// URL may contain special escape characters such as %20 in
	 * file:///c:/Documents%20and%20Settings/... This method converts file URLs
	 * to File objects while making a best effort to handle such cases (ie.
	 * return File("c:/Documents and Settings/...")). Unfortunately, the current
	 * implementation of this still isn't 100% bullet proof, see:
	 * https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
	 */
	private File urlToFile(URL fileURL) {
		try {
			return new File(fileURL.toURI());
		} catch (URISyntaxException e) {
			return new File(fileURL.getPath());
		}
	}

}
