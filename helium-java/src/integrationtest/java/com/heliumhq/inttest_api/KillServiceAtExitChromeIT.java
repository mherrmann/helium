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
			return asList("chromedriver.exe");
		return asList("chromedriver");
	}

	protected String getBrowserProcessName() {
		return "chrome" + (isWindows() ? ".exe" : "");
	}

	protected void startBrowser() {
		startChrome();
	}

}