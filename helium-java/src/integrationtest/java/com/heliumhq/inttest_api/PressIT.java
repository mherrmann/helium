package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.SHIFT;
import static com.heliumhq.API.TextField;
import static com.heliumhq.API.press;
import static org.junit.Assert.assertEquals;

public class PressIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_write.html";
	}

	@Test
	public void testPressSingleCharacter() {
		press("a");
		assertEquals("a", TextField("Autofocus text field").getValue());
	}

	@Test
	public void testPressUpperCaseCharacter() {
		press("A");
		assertEquals("A", TextField("Autofocus text field").getValue());
	}

	@Test
	public void testPressShiftPlusLowerCaseCharacter() {
		press(SHIFT + "a");
		assertEquals("A", TextField("Autofocus text field").getValue());
	}

}