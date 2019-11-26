package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.doubleclick;
import static org.junit.Assert.assertEquals;

public class DoubleclickIT extends BrowserAT {
	protected String getPage() {
		return "inttest_doubleclick.html";
	}
	@Test
	public void testDoubleclick() throws InterruptedException {
		doubleclick("Doubleclick here.");
		assertEquals("Success!", readResultFromBrowser());
	}
}