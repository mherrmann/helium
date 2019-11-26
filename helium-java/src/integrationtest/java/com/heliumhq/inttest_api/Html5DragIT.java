package com.heliumhq.inttest_api;

import org.junit.Test;
import org.openqa.selenium.By;

import static com.heliumhq.API.drag;
import static com.heliumhq.API.to;
import static org.junit.Assert.assertEquals;

public class Html5DragIT extends BrowserAT {
	@Override
	protected String getPage() {
		return "inttest_drag/html5.html";
	}
	@Test
	public void testHtml5Drag() throws InterruptedException {
		drag("Drag me.", to(driver.findElement(By.id("target"))));
		assertEquals("Success!", readResultFromBrowser());
	}
}
