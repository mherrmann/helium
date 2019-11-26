package com.heliumhq.inttest_api;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class DragIT extends BrowserAT {

	private WebElement dragTarget;

	@Override
	public void setUp() {
		super.setUp();
		dragTarget = driver.findElement(By.id("target"));
	}

	@Override
	protected String getPage() {
		return "inttest_drag/default.html";
	}

	@Test
	public void testDrag() throws InterruptedException {
		drag("Drag me.", to(dragTarget));
		assertEquals("Success!", readResultFromBrowser());
	}

	@Test
	public void testDragToPoint() throws InterruptedException {
		org.openqa.selenium.Point location = dragTarget.getLocation();
		Dimension size = dragTarget.getSize();
		Point targetPoint = Point(
				location.getX() + size.getWidth() / 2,
				location.getY() + size.getHeight() / 2
		);
		drag("Drag me.", to(targetPoint));
		assertEquals("Success!", readResultFromBrowser());
	}

}