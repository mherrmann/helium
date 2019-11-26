package com.heliumhq.inttest_api;

import com.heliumhq.TemporaryImplicitWait;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import java.awt.*;

import static com.heliumhq.API.hover;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeThat;

public class HoverIT extends BrowserAT {
	protected String getPage() {
		return "inttest_hover.html";
	}
	@Override
	@Before
	public void setUp() {
		// This test fails if the mouse cursor happens to be over one of the
		// links in inttest_hover.html. Move the mouse cursor to (0, 0) to
		// prevent spurious test failures:
		moveMouseCursorToOrigin();
		super.setUp();
	}
	private void moveMouseCursorToOrigin() {
		try {
			new Robot().mouseMove(0, 0);
		} catch (AWTError error) {
			if (error.getMessage().contains("Assistive Technology not found"))
				throw new RuntimeException(
					"You need to install the Java Access Bridge! Please " +
					"follow the instructions at http://docs.oracle.com/" +
					"javase/accessbridge/2.0.2/setup.htm.", error
				);
			else
				throw error;
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}
	@Test
	public void testHoverOne() throws InterruptedException {
		hover("Dropdown 1");
		String result = readResultFromBrowser();
		assertEquals(
			"Got unexpected result " + result + ". Maybe the mouse cursor " +
			"was over the browser window and interfered with the test?",
			"Dropdown 1", result
		);
	}
	@Test
	public void testHoverTwoConsecutively() throws InterruptedException {
		assumeThat(
			"This test fails on the CI server when no VNC viewer is watching." +
			" When recording a video and inspecting afterwards, it appears " +
			"that the test fails because the dropdown elements 'Item A', etc." +
			" disappear too quickly after hovering 'Dropdown 2'.",
			getTestBrowserName(), is(equalTo("firefox"))
		);
		hover("Dropdown 2");
		hover("Item C");
		String result = readResultFromBrowser();
		assertEquals(
			"Got unexpected result " + result + ". Maybe the mouse cursor " +
			"was over the browser window and interfered with the test?",
			"Dropdown 2 - Item C", result
		);
	}
	@Test
	public void testHoverHidden() {
		TemporaryImplicitWait temporaryWait = new TemporaryImplicitWait(1);
		try {
			NoSuchElementException expectedException = null;
			try {
				hover("Item C");
			} catch (NoSuchElementException e) {
				expectedException = e;
			}
			assertNotNull(
				"Didn't receive expected NoSuchElementException. Maybe the " +
				"mouse cursor was over the browser window and interfered " +
				"with the test?", expectedException
			);
		} finally {
			temporaryWait.end();
		}
	}
}