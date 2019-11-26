package com.heliumhq.util;

import org.junit.Test;

import static com.heliumhq.util.HtmlUtils.normalizeWhitespace;
import static org.junit.Assert.assertEquals;

public class NormalizeWhitespaceTest {

	@Test
	public void testStringWithoutWhitespace() {
		assertEquals("Foo", normalizeWhitespace("Foo"));
	}

	@Test
	public void testStringOneWhitespace() {
		assertEquals("Hello World!", normalizeWhitespace("Hello World!"));
	}

	@Test
	public void testStringLeadingWhitespace() {
		assertEquals("Hello World!", normalizeWhitespace(" Hello World!"));
	}

	@Test
	public void testStringComplexWhitespace() {
		assertEquals(
			"Hello World!", normalizeWhitespace("\n\t Hello\t\t    World!  \n")
		);
	}

	@Test
	public void testTagWithSpacesAroundInnerHtml() {
		assertEquals(
			"<span>Hi there!</span>",
			normalizeWhitespace("<span> Hi there! </span>")
		);
	}

}
