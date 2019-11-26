package com.heliumhq.inttest_api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

import static com.heliumhq.API.goTo;
import static com.heliumhq.inttest_api.BrowserAT.startBrowser;
import static com.heliumhq.Environment.getITFileURL;
import static org.junit.Assert.assertEquals;

public class StartGoToIT {

	private String url;
	private WebDriver driver;

	@Before
	public void setUp() {
		url = getITFileURL("inttest_start_go_to.html");
		driver = null;
	}

	@Test
	public void testGoTo() throws MalformedURLException {
		driver = startBrowser();
		goTo(url);
		assertUrlEquals(url, driver.getCurrentUrl());
	}

	private void assertUrlEquals(String expected, String actual) throws
			MalformedURLException {
		expected = expected.toLowerCase().replace('\\', '/')
				.replace("file:///", "file://")
                .replace("file:///", "file://");
		actual = actual.toLowerCase().replace('\\', '/')
				.replace("file:///", "file://")
				.replace("file:///", "file://");
		assertEquals(expected, actual);
	}

	@Test
	public void testStartWithUrl() throws MalformedURLException {
		driver = startBrowser(url);
		assertUrlEquals(url, driver.getCurrentUrl());
	}

	@After
	public void tearDown() {
		if (driver != null)
			driver.quit();
	}
}
