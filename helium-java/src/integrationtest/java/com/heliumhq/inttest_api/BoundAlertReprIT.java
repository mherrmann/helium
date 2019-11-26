package com.heliumhq.inttest_api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class BoundAlertReprIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_alert.html";
	}

	@Override @Before
	public void setUp() {
		super.setUp();
		click("Display alert");
	}

	@Test
	public void testBoundAlertRepr() {
		Alert alert = Alert();
		// Bind alert:
		alert.getText();
		assertEquals("Alert(\"Hello World!\")", alert.toString());
	}

	@Test
	public void testBoundAlertReprWithPartialSearchText() {
		Alert alert = Alert("Hello");
		// Bind alert:
		alert.getText();
		assertEquals("Alert(\"Hello World!\")", alert.toString());
	}

	@After
	public void tearDown() {
		Alert().accept();
	}

}