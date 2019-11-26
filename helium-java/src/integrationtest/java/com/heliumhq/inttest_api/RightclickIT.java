package com.heliumhq.inttest_api;

import org.junit.Ignore;
import org.junit.Test;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class RightclickIT extends BrowserAT {
	protected String getPage() {
		return "inttest_rightclick.html";
	}
	@Test
	public void testSimpleRightclick() throws InterruptedException {
		rightclick("Perform a normal rightclick here.");
		assertEquals("Normal rightclick performed.", readResultFromBrowser());
	}
	@Test
	public void testRightClickSelectNormalItem() throws InterruptedException {
		rightclick("Rightclick here for context menu.");
		click("Normal item");
		assertEquals("Normal item selected.", readResultFromBrowser());
	}
	@Ignore("This test is too unstable.") @Test
	public void testRightClickSelectSubItem() throws InterruptedException {
		rightclick("Rightclick here for context menu.");
		hover("Item with sub items");
		click("Sub item 1");
		assertEquals("Sub item 1 selected.", readResultFromBrowser());
	}
}