package com.heliumhq.inttest_api;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WindowHandlingIT extends BrowserAT {

	private String mainWindowHandle;

	@Override
	protected String getPage() {
		return "inttest_window_handling/main.html";
	}

	@Test
	public void testWriteWritesInActiveWindow() {
		write("Main window");
		assertEquals("Main window", getValue("mainTextField"));
		openPopup();
		write("Popup");
		assertEquals("Popup", getValue("popupTextField"));
	}

	@Test
	public void testWriteSearchesInActiveWindow() {
		write("Main window", into("Text field"));
		assertEquals("Main window", getValue("mainTextField"));
		openPopup();
		write("Popup", into("Text field"));
		assertEquals("Popup", getValue("popupTextField"));
	}

	@Test
	public void testSwitchToSearchTextField() {
		write("Main window", into("Text field"));
		assertEquals("Main window", TextField("Text field").getValue());
		openPopup();
		write("Popup", into("Text field"));
		assertEquals("Popup", TextField("Text field").getValue());
		switchTo("inttest_window_handling - Main");
		assertEquals("Main window", TextField("Text field").getValue());
	}

	@Test
	public void testHandlesClosedWindowGracefully() {
		openPopup();
		getDriver().close();
		boolean isBackInMainWindow = Link("Open popup").exists();
		assertTrue(isBackInMainWindow);
	}

	@Test
	public void testSwitchToAfterWindowClosed() {
		openPopup();
		getDriver().close();
		switchTo("inttest_window_handling - Main");
	}

	public void setUp() {
		super.setUp();
		mainWindowHandle = driver.getWindowHandle();
	}

	@After
	public void tearDown() {
		for (String windowHandle : driver.getWindowHandles()) {
			if (! windowHandle.equals(mainWindowHandle)) {
				driver.switchTo().window(windowHandle);
				driver.close();
				driver.switchTo().window(mainWindowHandle);
			}
		}
	}

	private String getValue(String elementId) {
		return driver.findElement(By.id(elementId)).getAttribute("value");
	}

	private void openPopup() {
		click("Open popup");
		waitUntil(isInPopup);
	}

	private ExpectedCondition<Boolean> isInPopup =
		new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				String title = driver.getTitle();
				return title.equals("inttest_window_handling - Popup");
			}
		};

}