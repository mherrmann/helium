package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.$;

public class $IT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@Test
	public void testFindById() {
		assertFindsEltWithId($("#checkBoxId"), "checkBoxId");
	}

	@Test
	public void testFindByName() {
		assertFindsEltWithId($("@checkBoxName"), "checkBoxId");
	}

	@Test
	public void testFindByClass() {
		assertFindsEltWithId($(".checkBoxClass"), "checkBoxId");
	}

	@Test
	public void testFindByXPath() {
		assertFindsEltWithId(
			$("//input[@type=\"checkbox\" and @id=\"checkBoxId\"]"),
			"checkBoxId"
		);
	}

	@Test
	public void testFindByCssSelector() {
		assertFindsEltWithId($("input.checkBoxClass"), "checkBoxId");
	}

}