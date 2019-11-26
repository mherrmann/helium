package com.heliumhq.inttest_api;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.UnhandledAlertException;

import static com.heliumhq.API.*;
import static com.heliumhq.util.System.isOSX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public abstract class AlertAT extends BrowserAT {

	private final static String UNHANDLED_ALERT_EXCEPTION_MSG =
		"This command is not supported when an alert is present. To accept " +
		"the alert (this usually corresponds to clicking 'OK') use `Alert()." +
		"accept()`. To dismiss the alert (ie. 'cancel' it), use `Alert()." +
		"dismiss()`. If the alert contains a text field, you can use " +
		"write(...) to set its value. Eg.: `write('hi there!')`.";

	@Override
	protected String getPage() {
		return "inttest_alert.html";
	}

	public abstract String getLinkToOpenAlert();

	public abstract String getExpectedAlertText();

	public abstract String getExpectedAlertAcceptedResult();

	public String getExpectedAlertDismissedResult() {
		return getExpectedAlertAcceptedResult();
	}

	@Before
	public void setUp() {
		super.setUp();
		click(getLinkToOpenAlert());
		waitUntil(Alert().exists);
	}

	@After
	public void tearDown() {
		if (Alert().exists())
			// We need to call .accept() instead of .dismiss() here to work
			// around ChromeDriver bug 764:
			// https://code.google.com/p/chromedriver/issues/detail?id=764
			Alert().accept();
	}

	@Test
	public void testAlertExists() {
		assertTrue(Alert().exists());
	}

	@Test
	public void testAlertTextExists() {
		assertTrue(Alert(getExpectedAlertText()).exists());
	}

	@Test
	public void testAlertTextNotExists() {
		assertFalse(Alert("Wrong text").exists());
	}

	@Test
	public void testAlertText() {
		assertEquals(getExpectedAlertText(), Alert().getText());
	}

	@Test
	public void testAlertAccept() throws InterruptedException {
		Alert().accept();
		expectResult(getExpectedAlertAcceptedResult());
	}

	@Test
	public void testAlertDismiss() throws InterruptedException {
		// Chrome driver on OSX does not support dismissing JS alerts.
		// See: https://code.google.com/p/chromedriver/issues/detail?id=764
		assumeTrue(! (isOSX() && getTestBrowserName().equals("chrome")));
		Alert().dismiss();
		expectResult(getExpectedAlertDismissedResult());
	}

	@Test
	public void testClickWithOpenAlertRaisesException() {
		expectUnhandledAlertException();
		click("OK");
	}

	@Test
	public void testPressWithOpenAlertRaisesException() {
		expectUnhandledAlertException();
		press(ENTER);
	}

	/**
	 * This method waits up to one second for the given result to appear. It
	 * should not be needed but Chrome sometimes returns from
	 * .accept()/.dismiss() before the JavaScript in inttest_alert.html has set
	 * the corresponding result.
	 */
	protected void expectResult(String expectedResult)
		throws InterruptedException {
		long ONE_SECOND = 1000;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + ONE_SECOND) {
			String actualResult = readResultFromBrowser(300);
			if (actualResult.equals(expectedResult))
				return;
			Thread.sleep(200);
		}
		assertEquals(expectedResult, readResultFromBrowser());
	}

	protected void expectUnhandledAlertException() {
		expectedEx.expect(UnhandledAlertException.class);
		expectedEx.expectMessage(UNHANDLED_ALERT_EXCEPTION_MSG);
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

}
