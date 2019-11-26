package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.Window;
import static com.heliumhq.util.StringUtils.isEmpty;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WindowIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_window/inttest_window.html";
	}

	@Test
	public void testWindowExists() {
		assertTrue(Window("inttest_window").exists());
	}

	@Test
	public void testWindowNotExists() {
		assertFalse(Window("non-existent").exists());
	}

	@Test
	public void testNoArgWindowExists() {
		assertTrue(Window().exists());
	}

	@Test
	public void testHandle() {
		String handle = Window("inttest_window").getHandle();
		assertFalse(handle, isEmpty(handle));
	}

	@Test
	public void testTitle() {
		assertEquals("inttest_window", Window("inttest_window").getTitle());
	}
}
