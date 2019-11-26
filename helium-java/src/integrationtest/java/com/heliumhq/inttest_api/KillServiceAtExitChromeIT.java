package com.heliumhq.inttest_api;

import java.io.IOException;
import java.util.List;
import org.junit.Ignore;

import static com.heliumhq.API.startChrome;
import static com.heliumhq.util.System.isWindows;
import static com.heliumhq.inttest_api.BrowserAT.getTestBrowserName;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

/**
 * This test fails when run from PyCharm. The reason for this is that
 * silent-chromedriver.exe assigns its subprocess chromedriver.exe to a Job
 * Object via AssignProcessToJobObject in silent-chromedriver.cpp. This fails
 * with error code 5 (access denied) when we run from PyCharm. On the command
 * line (`mvn verify`) however, it works. It seems to be common that
 * AssignProcessToJobObject fails depending on the process' environment. See:
 *     http://stackoverflow.com/questions/89588/assignprocesstojobobject-fails-
 *     with-access-denied-error-when-running-under-the
 */
@Ignore("Fails on recent versions of Chrome")
public class KillServiceAtExitChromeIT extends KillServiceAtExitAT {

	@Override
	public void testKillServiceAtExit() throws IOException,
			InterruptedException {
		assumeThat(getTestBrowserName(), is(equalTo("chrome")));
		super.testKillServiceAtExit();
	}

	protected List<String> getServiceProcessNames() {
		if (isWindows())
			return asList("silent-chromedriver.exe", "chromedriver.exe");
		return asList("chromedriver");
	}

	protected String getBrowserProcessName() {
		return "chrome" + (isWindows() ? ".exe" : "");
	}

	protected void startBrowser() {
		startChrome();
	}

}