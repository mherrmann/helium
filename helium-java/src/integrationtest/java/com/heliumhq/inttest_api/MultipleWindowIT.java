package com.heliumhq.inttest_api;

import com.heliumhq.Environment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertTrue;

public class MultipleWindowIT extends WindowIT {
	/**
	 * The purpose of this IT is to run the same tests as WindowIT, but with an
	 * additional pop up window open.
	 */
	@BeforeClass
	public static void setUpClass() {
		WindowIT.setUpClass();
		goTo(Environment.getITFileURL("inttest_window/inttest_window.html"));
		click("Click here to open a popup.");
		waitUntil(Window("inttest_window - popup").exists);
	}

	@Test
	public void testPopupWindowExists() {
		assertTrue(Window("inttest_window - popup").exists());
	}

	@Override
	public void setUp() {
		// Don't let super goTo(...).
	}

	@AfterClass
	public static void tearDownClass() {
		String popupWindowHandle = Window("inttest_window - popup").getHandle();
		String mainWindowHandle = Window("inttest_window").getHandle();
		getDriver().switchTo().window(popupWindowHandle).close();
		getDriver().switchTo().window(mainWindowHandle);
		WindowIT.tearDownClass();
	}
}
