package com.heliumhq.inttest_api;


import com.heliumhq.TemporaryImplicitWait;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.heliumhq.API.click;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

public class ImplicitWaitIT extends BrowserAT {
	@Override
	protected String getPage() {
		return "inttest_implicit_wait.html";
	}
	@Test
	public void testClickTextImplicitWait() throws InterruptedException {
		click("Click me!");
		long startTime = System.currentTimeMillis();
		click("Now click me!");
		long endTime = System.currentTimeMillis();
		assertEquals("Success!", readResultFromBrowser());
		assertThat(endTime - startTime, is(greaterThan(3000L)));
	}
	@Test(expected = NoSuchElementException.class)
	public void testClickTextNoImplicitWait() {
		TemporaryImplicitWait temporaryWait = new TemporaryImplicitWait(0);
		try {
			click("Non-existent");
		} finally {
			temporaryWait.end();
		}
	}
	@Test(expected = NoSuchElementException.class)
	public void testClickTextTooSmallImplicitWait() {
		TemporaryImplicitWait temporaryWait = new TemporaryImplicitWait(1);
		try {
			click("Click me!");
			click("Now click me!");
		} finally {
			temporaryWait.end();
		}
	}
}
