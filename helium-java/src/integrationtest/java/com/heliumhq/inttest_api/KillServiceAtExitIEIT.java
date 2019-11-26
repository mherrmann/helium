package com.heliumhq.inttest_api;

import java.io.IOException;
import java.util.List;

import static com.heliumhq.API.startIE;
import static com.heliumhq.util.System.isWindows;
import static com.heliumhq.inttest_api.BrowserAT.getTestBrowserName;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

public class KillServiceAtExitIEIT extends KillServiceAtExitAT {

	@Override
	public void testKillServiceAtExit() throws IOException,
			InterruptedException {
		assumeThat(getTestBrowserName(), is(equalTo("ie")));
		assumeTrue(isWindows());
		super.testKillServiceAtExit();
	}

	protected List<String> getServiceProcessNames() {
		return asList("IEDriverServer.exe");
	}

	protected String getBrowserProcessName() {
		return "iexplore.exe";
	}

	protected void startBrowser() {
		startIE();
	}

}