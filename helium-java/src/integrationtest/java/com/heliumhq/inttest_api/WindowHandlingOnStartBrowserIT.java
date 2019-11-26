package com.heliumhq.inttest_api;

import org.junit.Before;
import org.junit.Test;

import static com.heliumhq.API.Text;
import static org.junit.Assert.assertTrue;

public class WindowHandlingOnStartBrowserIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_window_handling/main_immediate_popup.html";
	}

	@Test
	public void testSwitchesToPopup() {
		assertTrue(Text("In popup.").exists());
	}

}