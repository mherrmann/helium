package com.heliumhq.inttest_api;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class ScrollIT extends BrowserAT {
	protected String getPage() {
		return "inttest_scroll.html";
	}
	@Test
	public void testScrollUpWhenAtTopOfPage() {
		scrollUp();
		assertScrollPositionEquals(0, 0);
	}
	@Test
	public void testScrollDown() {
		scrollDown();
		assertScrollPositionEquals(0, 100);
	}
	@Test
	public void testScrollDownThenUp() {
		scrollDown();
		scrollUp();
		assertScrollPositionEquals(0, 0);
	}
	@Test
	public void testScrollDownTwiceThenUp() {
		scrollDown(175);
		scrollUp(100);
		assertScrollPositionEquals(0, 75);
	}
	@Test
	public void testScrollLeftWhenAtStartOfPage() {
		scrollLeft();
		assertScrollPositionEquals(0, 0);
	}
	@Test
	public void testScrollRight() {
		scrollRight();
		assertScrollPositionEquals(100, 0);
	}
	@Test
	public void testScrollRightThenLeft() {
		scrollRight();
		scrollLeft();
		assertScrollPositionEquals(0, 0);
	}
	@Test
	public void testScrollRightTwiceThenLeft() {
		scrollRight(175);
		scrollLeft(100);
		assertScrollPositionEquals(75, 0);
	}

	@After
	public void tearDown() {
		// Recent versions of Chrome(Driver) don't reset the scroll position
		// when reloading the page. Force-reset it:
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
	}

	private void assertScrollPositionEquals(long x, long y) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) getDriver();
		long scrollPositionX = (Long) jsExecutor.executeScript(
				"return window.pageXOffset || " +
						"document.documentElement.scrollLeft || " +
						"document.body.scrollLeft"
		);
		assertEquals(x, scrollPositionX);
		long scrollPositionY = (Long) jsExecutor.executeScript(
				"return window.pageYOffset || " +
						"document.documentElement.scrollTop || " +
						"document.body.scrollTop;"
		);
		assertEquals(y, scrollPositionY);
	}
}