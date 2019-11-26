package com.heliumhq.inttest_api;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.heliumhq.API.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class PointIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_point.html";
	}

	@Override
	public void setUp() {
		assumeThat(
			"This test currently fails because of a bug with Firefox 27.0.1 " +
			"and Selenium 2.39.0:" +
			"https://code.google.com/p/selenium/issues/detail?id=7005",
			getTestBrowserName(), is(not(equalTo("firefox")))
		);
		super.setUp();
	}

	@Test
	public void testTopLeft() {
		assertIsInRange(Point(2, 3), Button("Button 1").getTopLeft(), 0, 1);
	}

	private void assertIsInRange(
			Point expected, Point actual, int deltaX, int deltaY
	) {
		assertAround(expected.getX(), actual.getX(), deltaX);
		assertAround(expected.getY(), actual.getY(), deltaY);
	}

	private void assertAround(int expected, int actual, int delta) {
		assertAround(expected, actual, delta, "");
	}

	private void assertAround(
			int expected, int actual, int delta, String message
	) {
		assertThat(message, actual, greaterThanOrEqualTo(expected - delta));
		assertThat(message, actual, lessThanOrEqualTo(expected + delta));
	}

	@Test
	public void testClickTopLeft() throws InterruptedException {
		click(Button("Button 1").getTopLeft());
		assertResultIs("Button 1 clicked at offset (0, 0).", 1, 1);
	}

	@Test
	public void testClickPoint() throws InterruptedException {
		click(Point(39, 13));
		assertResultIs("Button 1 clicked at offset (37, 10).", 0, 1);
	}

	@Test
	public void testClickTopLeftOffset() throws InterruptedException {
		click(Button("Button 3").getTopLeft().withOffset(3, 4));
		assertResultIs("Button 3 clicked at offset (3, 4).");
	}

	@Test
	public void testHoverTopLeft() throws InterruptedException {
		hover(Button("Button 1").getTopLeft());
		assertResultIs("Button 1 hovered at offset (0, 0).", 1, 1);
	}

	@Test
	public void testHoverPoint() throws InterruptedException {
		hover(Point(39, 13));
		assertResultIs("Button 1 hovered at offset (37, 10).", 0, 1);
	}

	@Test
	public void testHoverTopLeftOffset() throws InterruptedException {
		hover(Button("Button 3").getTopLeft().withOffset(3, 4));
		assertResultIs("Button 3 hovered at offset (3, 4).");
	}

	@Test
	public void testRightclickTopLeft() throws InterruptedException {
		rightclick(Button("Button 1").getTopLeft());
		assertResultIs("Button 1 rightclicked at offset (0, 0).", 1, 1);
	}

	@Test
	public void testRightclickPoint() throws InterruptedException {
		rightclick(Point(39, 13));
		assertResultIs("Button 1 rightclicked at offset (37, 10).", 0, 1);
	}

	@Test
	public void testRightclickTopLeftOffset() throws InterruptedException {
		rightclick(Button("Button 3").getTopLeft().withOffset(3, 4));
		assertResultIs("Button 3 rightclicked at offset (3, 4).");
	}

	@Test
	public void testDoubleclickTopLeft() throws InterruptedException {
		doubleclick(Button("Button 1").getTopLeft());
		assertResultIs("Button 1 doubleclicked at offset (0, 0).", 1, 1);
	}

	@Test
	public void testDoubleclickPoint() throws InterruptedException {
		doubleclick(Point(39, 13));
		assertResultIs("Button 1 doubleclicked at offset (37, 10).", 0, 1);
	}

	@Test
	public void testDoubleclickTopLeftOffset() throws InterruptedException {
		doubleclick(Button("Button 3").getTopLeft().withOffset(3, 4));
		assertResultIs("Button 3 doubleclicked at offset (3, 4).");
	}

	private void assertResultIs(String expected) throws InterruptedException {
		assertResultIs(expected, 0, 0);
	}

	private void assertResultIs(String expected, int deltaX, int deltaY)
		throws InterruptedException {
		String actual = readResultFromBrowser();
		String expectedOffset = extractOffset(expected);
		String actualOffset = extractOffset(actual);
		int expectedX = extractOffsetX(expectedOffset);
		int expectedY = extractOffsetY(expectedOffset);
		int actualX = extractOffsetX(actualOffset);
		int actualY = extractOffsetY(actualOffset);
		assertAround(expectedX, actualX, deltaX);
		assertAround(expectedY, actualY, deltaY);
		assertAround(
				expectedX, actualX, deltaX,
				String.format(
					"Offset (%s, %s) is not in expected range (%s+-%s, %s+-%s)",
					actualX, actualY, expectedX, deltaX, expectedY, deltaY
				)
		);
		assertAround(
				expectedY, actualY, deltaY,
				String.format(
					"Offset (%s, %s) is not in expected range (%s+-%s, %s+-%s)",
					actualX, actualY, expectedX, deltaX, expectedY, deltaY
				)
		);
		String expectedPrefix = expected.split(expectedOffset)[0];
		String expectedSuffix = expected.split(expectedOffset)[1];
		String actualPrefix = actual.split(actualOffset)[0];
		String actualSuffix = actual.split(actualOffset)[1];
		assertEquals(expectedPrefix, actualPrefix);
		assertEquals(expectedSuffix, actualSuffix);
	}

	private String extractOffset(String resultInBrowser) {
		Pattern p = Pattern.compile("(\\([^,]+, [^\\)]+\\))");
		Matcher m = p.matcher(resultInBrowser);
		assertTrue(resultInBrowser, m.find());
		return m.group(1);
	}

	private int extractOffsetX(String offset) {
		return Integer.parseInt(offset.substring(1, offset.indexOf(',')));
	}

	private int extractOffsetY(String offset) {
		return Integer.parseInt(
				offset.substring(offset.indexOf(", ") + 2, offset.length() - 1)
		);
	}

}