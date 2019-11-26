package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.getDriver;
import static com.heliumhq.API.Text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IframeIT extends BrowserAT {
	@Override
	protected String getPage() {
		return "inttest_iframe/main.html";
	}
	@Test
	public void testTextInIframeExists() {
		assertTrue(Text("This text is inside an iframe.").exists());
	}
	@Test
	public void testTextInNestedIframeExists() {
		assertTrue(Text("This text is inside a nested iframe.").exists());
	}
	@Test
	public void testFindsElementInParentIframe() {
		testTextInNestedIframeExists();
		// Now we're "focused" on the nested IFrame. Check that we can still
		// find the element an the parent IFrame:
		testTextInIframeExists();
	}
	@Test
	public void testAccessAttributesAcrossIframes() {
		Text text = Text("This text is inside an iframe.");
		assertEquals("This text is inside an iframe.", text.getValue());
		getDriver().switchTo().defaultContent();
		assertEquals("This text is inside an iframe.", text.getValue());
	}
}
