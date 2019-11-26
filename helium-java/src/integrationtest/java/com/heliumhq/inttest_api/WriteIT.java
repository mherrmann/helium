package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class WriteIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_write.html";
	}

	@Test
	public void testWrite() {
		write("Hello World!");
		assertEquals(
				"Hello World!", TextField("Autofocus text field").getValue()
		);
	}

	@Test
	public void testWriteInto() {
		write("Hi there!", into("Normal text field"));
		assertEquals("Hi there!", TextField("Normal text field").getValue());
	}

	@Test
	public void testWriteIntoTextFieldToRightOf() {
		write("Hi there!", into(TextField(toRightOf("Normal text field"))));
		assertEquals("Hi there!", TextField("Normal text field").getValue());
	}

}