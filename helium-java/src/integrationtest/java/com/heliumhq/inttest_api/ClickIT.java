package com.heliumhq.inttest_api;

import com.heliumhq.TemporaryImplicitWait;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.heliumhq.API.click;
import static org.junit.Assert.assertEquals;

public class ClickIT extends BrowserAT {
	protected String getPage() {
		return "inttest_click.html";
	}
	@Test
	public void testClick() throws InterruptedException {
		click("Click me!");
		assertEquals("Success!", readResultFromBrowser());
	}
	@Test(expected = NoSuchElementException.class)
	public void testClickNonExistentElement() {
		TemporaryImplicitWait tempImplicitWait = new TemporaryImplicitWait(1);
		try {
			click("Non-existent");
		} finally {
			tempImplicitWait.end();
		}
	}
}