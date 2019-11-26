package com.heliumhq.api_impl.application_context;

import com.heliumhq.config_file.HeliumConfigFile;
import com.heliumhq.environment.ResourceLocator;
import com.heliumhq.util.FileUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.*;

import static com.heliumhq.config_file.HeliumConfigFile.CONFIG_FILE_NAME;
import static com.heliumhq.util.Date.createDate;
import static java.util.logging.Level.WARNING;

public class StandaloneAPIConfig extends APIConfig {

	private final static Logger LOG =
			Logger.getLogger(StandaloneAPIConfig.class.getName());

	private boolean isFirstRun;
	private UUID uuid;
	private HeliumConfigFile heliumConfigFile;
	private ResourceLocator resourceLocator;

	@Override
	protected void initializeLogging() {
		suppressApacheHttpLogsOnStderr();
		suppressSeleniumLoggers();
		Logger heliumLogger = Logger.getLogger("com.heliumhq");
		Handler handler;
		String heliumLogFile = getResourceLocator().locate(
			"runtime", "helium.log"
		);
		try {
			handler = new FileHandler(heliumLogFile);
		} catch (IOException e) {
			handler = new ConsoleHandler();
			handler.setLevel(WARNING);
		}
		handler.setFormatter(new SimpleFormatter());
		heliumLogger.addHandler(handler);
		heliumLogger.setLevel(Level.INFO);
		// java.util.logging by default installs a ConsoleHandler for the root
		// logger. Prevent our log entries from being passed on to it:
		heliumLogger.setUseParentHandlers(false);
		heliumLogger.info("Helium logging initialized.");
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

	private GregorianCalendar getBuildDate(String checksum) {
		// We're actually not interested in a checksum. It is merely a means of
		// encoding the build date in a non-obvious form, to prevent the user
		// from tampering while trying to work around licensing restrictions.
		int year1 = Integer.parseInt(String.valueOf(checksum.charAt(13)));
		int year2 = Integer.parseInt(String.valueOf(checksum.charAt(3)));
		int year3 = Integer.parseInt(String.valueOf(checksum.charAt(18)));
		int year4 = Integer.parseInt(String.valueOf(checksum.charAt(29)));
		int month1 = Integer.parseInt(String.valueOf(checksum.charAt(11)));
		int month2 = Integer.parseInt(String.valueOf(checksum.charAt(2)));
		int day1 = Integer.parseInt(String.valueOf(checksum.charAt(17)));
		int day2 = Integer.parseInt(String.valueOf(checksum.charAt(31)));
		int year = year1 * 1000 + year2 * 100 + year3 * 10 + year4;
		int month = month1 * 10 + month2;
		int day = day1 * 10 + day2;
		return createDate(year, month, day);
	}

	private boolean isFirstRun() {
		setUUIDAndIsFirstRun();
		return isFirstRun;
	}

	public UUID getUUID() {
		setUUIDAndIsFirstRun();
		return uuid;
	}

	private void setUUIDAndIsFirstRun() {
		if (uuid == null) {
			File uuidFile = new File(
				getResourceLocator().locate("runtime", "uuid")
			);
			isFirstRun = ! uuidFile.exists();
			if (uuidFile.exists())
				try {
					uuid = readUUIDFromFile(uuidFile);
				} catch (IOException e) {
					LOG.log(WARNING, "Could not read UUID file " + uuidFile, e);
					uuid = UUID.randomUUID();
				}
			else {
				uuid = UUID.randomUUID();
				try {
					writeUUIDToFile(uuid, uuidFile);
				} catch (IOException e) {
					LOG.log(WARNING, "Error writing UUID file " + uuidFile, e);
				}
			}
		}
	}

	private UUID readUUIDFromFile(File uuidFile) throws IOException {
		char[] uuidBuffer = new char[36];
		new FileReader(uuidFile).read(uuidBuffer);
		return UUID.fromString(String.valueOf(uuidBuffer));
	}

	private void writeUUIDToFile(UUID uuid, File uuidFile) throws IOException {
		FileWriter uuidFileWriter = null;
		try {
			uuidFileWriter = new FileWriter(uuidFile);
			uuidFileWriter.write(uuid.toString());
			uuidFileWriter.flush();
		} finally {
			if (uuidFileWriter != null)
				uuidFileWriter.close();
		}
	}

	public HeliumConfigFile getHeliumConfigFile() {
		if (heliumConfigFile == null)
			try {
				String configFilePath =
					getResourceLocator().locate("runtime", CONFIG_FILE_NAME);
				LOG.info("Reading Helium config from " + configFilePath);
				heliumConfigFile = new HeliumConfigFile(configFilePath);
			} catch(IOException e) {
				throw new StartupError("Failed to configure Helium.");
			}
		return heliumConfigFile;
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
