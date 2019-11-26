package com.heliumhq.inttest_api;

import com.heliumhq.TemporaryImplicitWait;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertTrue;

public class HighlightIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@Test
	public void testHighlight() {
		Button button = Button("Input Button");
		highlight(button);
		checkIsHighlighted(button);
	}

	@Test
	public void testHighlightString() {
		highlight("Text with id");
		checkIsHighlighted(Text("Text with id"));
	}

	@Test(expected = NoSuchElementException.class)
	public void testHighlightNonexistent() {
		TemporaryImplicitWait tempImplicitWait = new TemporaryImplicitWait(0.5);
		try {
			highlight(Button("foo"));
		} finally {
			tempImplicitWait.end();
		}
	}

	private void checkIsHighlighted(HTMLElement htmlElement) {
		String style = htmlElement.getWebElement().getAttribute("style");
		assertTrue(style, style.contains("border: 2px solid red;"));
		assertTrue(style, style.contains("font-weight: bold;"));
	}

}