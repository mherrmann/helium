package com.heliumhq.inttest_api;

import com.heliumhq.PythonServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.heliumhq.API.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class IESSLIT extends BrowserAT {

	public static final int SSL_SERVER_PORT = 4443;

	private PythonServer sslServer;

	@Before
	public void setUp() {
		assumeThat(getTestBrowserName(), is(equalTo("ie")));
		sslServer = new PythonServer(
			"src/integrationtest/python/helium_integrationtest/inttest_api/" +
			"apitest_ie_ssl.py",
			String.format("SSL server started on port %s.", SSL_SERVER_PORT)
		);
		try {
			sslServer.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.setUp();
	}

	@Override
	protected String getURL() {
		return String.format("https://localhost:%s", SSL_SERVER_PORT);
	}

	@Test
	public void testContinue() {
		click("Continue to this website (not recommended).");
		assertTrue(Text("Directory listing").exists());
	}

	@After
	public void tearDown() {
		if (sslServer != null)
			sslServer.stop();
	}

}
