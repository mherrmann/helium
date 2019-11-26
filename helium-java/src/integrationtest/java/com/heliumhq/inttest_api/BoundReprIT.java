package com.heliumhq.inttest_api;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class BoundReprIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@Test
	public void testBound$Repr() {
		$ bound$ = bind($("#checkBoxId"));
		assertHtmlEltWithMultipleAttributesEquals(
			"<input type=\"checkbox\" id=\"checkBoxId\" " +
			"name=\"checkBoxName\" class=\"checkBoxClass\">",
			bound$.toString()
		);
	}

	@Test
	public void testBound$ReprLongContent() {
		$ body = bind($("body"));
		assertEquals("<body>...</body>", body.toString());
	}

	@Test
	public void testBoundButtonRepr() {
		Button boundButton = bind(Button("Enabled Button"));
		assertEquals(
			"<button type=\"button\">Enabled Button</button>",
			boundButton.toString()
		);
	}

	@Test
	public void testBoundLinkReprNestedTag() {
		Link link = bind(Link("Link with title"));
		assertHtmlEltWithMultipleAttributesEquals(
			"<a href=\"#\" title=\"Link with title\">...</a>", link.toString()
		);
	}

	@Test
	public void testBoundReprDuplicateButton() {
		assertEquals(
			"[<button type=\"button\">Duplicate Button</button>," +
			" <button type=\"button\">Duplicate Button</button>," +
			" <button type=\"button\">Duplicate Button</button>," +
			" <button type=\"button\">Duplicate Button</button>]",
			findAll(Button("Duplicate Button")).toString()
		);
	}

	@Test
	public void testBoundWindowRepr() {
		Window boundWindow = bind(Window());
		assertEquals(
			"Window(\"Test page for browser system tests\")",
			boundWindow.toString()
		);
	}

	@Test
	public void testBoundWindowReprWithSearchText() {
		Window boundWindow = bind(Window("Test page for"));
		assertEquals(
			"Window(\"Test page for browser system tests\")",
			boundWindow.toString()
		);
	}

	private <H extends HTMLElement> H bind(H predicate) {
		// Reading a property such as webElement waits for the element to exist
		// and binds the predicate to it:
		predicate.getWebElement();
		return predicate;
	}

	private Window bind(Window predicate) {
		// Reading a property such as the handle waits for the window to exist
		// and binds the predicate to it:
		predicate.getHandle();
		return predicate;
	}

	private void assertHtmlEltWithMultipleAttributesEquals(
		String expected, String actual
	) {
		String[] expectedComponents = expected.split(">", 2);
		String[] actualComponents = actual.split(">", 2);
		String expStartTag = expectedComponents[0];
		String expRemainder = expectedComponents[1];
		String actStartTag = actualComponents[0];
		String actRemainder = actualComponents[1];
		String attributesRE = "[a-zA-Z]+=\"[^\"]+\"";
		List<String> expAttributes = findAllRegex(attributesRE, expStartTag);
		List<String> actAttributes = findAllRegex(attributesRE, actStartTag);
		assertEquals(
			new HashSet<String>(expAttributes),
			new HashSet<String>(actAttributes)
		);
		assertEquals(expRemainder, actRemainder);
	}

	private List<String> findAllRegex(String regex, String s) {
		List<String> result = new ArrayList<String>();
		Matcher m = Pattern.compile(regex).matcher(s);
		while (m.find())
			result.add(m.group());
		return result;
	}

}