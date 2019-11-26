package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.Button;
import static com.heliumhq.API.TextField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AriaIT extends BrowserAT {

	protected String getPage() {
		return "inttest_aria.html";
	}

	@Test
	public void testAriaLabelButtonExists() {
		assertTrue(Button("Close").exists());
	}

	@Test
	public void testAriaLabelButtonIsEnabled() {
		assertTrue(Button("Close").isEnabled());
	}

	@Test
	public void testAriaLabelDisabledButtonIsEnabled() {
		assertFalse(Button("Disabled Close").isEnabled());
	}

	@Test
	public void testAriaLabelNonExistentButton() {
		assertFalse(Button("This doesnt exist").exists());
	}

	@Test
	public void testAriaLabelDivButtonExists() {
		assertTrue(Button("Attach files").exists());
	}

	@Test
	public void testAriaLabelDivButtonIsEnabled() {
		assertTrue(Button("Attach files").isEnabled());
	}

	@Test
	public void testAriaLabelDivDisabledButtonIsEnabled() {
		assertFalse(Button("Disabled Attach files").isEnabled());
	}

	@Test
	public void testAriaLabelSubmitButtonExists() {
		assertTrue(Button("Submit").exists());
	}

	@Test
	public void testAriaTextboxExists() {
		assertTrue(TextField("Textbox").exists());
	}

	@Test
	public void testAriaTextboxValue() {
		assertEquals("Textbox value", TextField("Textbox").getValue());
	}

}