package com.heliumhq.inttest_api;

import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.Collections;
import java.util.List;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FindAllIT extends BrowserAT {

	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@Test
	public void testFindAllDuplicateButton() {
		assertEquals(4, findAll(Button("Duplicate Button")).size());
	}

	@Test
	public void testFindAllDuplicateButtonToRightOf() {
		assertEquals(
			2, findAll(Button("Duplicate Button", toRightOf("Row 1"))).size()
		);
	}

	@Test
	public void testFindAllDuplicateButtonBelowToRightOf() {
		assertEquals(
			1, findAll(Button(
				"Duplicate Button", below("Column 1"), toRightOf("Row 1")
			)).size()
		);
	}

	@Test
	public void testFindAllNonExistentButton() {
		assertEquals(
				Collections.EMPTY_LIST, findAll(Button("Non-existent Button"))
		);
	}

	@Test
	public void testFindAllYieldsApiElements() {
		assertTrue(
			findAll(TextField("Example Text Field")).get(0) instanceof TextField
		);
	}

	@Test
	public void testInteractWithFoundElements() {
		List<TextField> allTFs = findAll(TextField());
		TextField exampleTF = null;
		for (TextField textField : allTFs) {
			String id;
			try {
				id = textField.getWebElement().getAttribute("id");
			} catch (StaleElementReferenceException e) {
				// This may happen for found web elements in different iframes.
				// TODO: Improve this, eg. by adding a .getId() property to
				// TextField (/HTMLElement) which handles this problem.
				continue;
			}
			if (id.equals("exampleTextFieldId"))
				exampleTF = textField;
		}
		assertNotNull(exampleTF);
		write("testInteractWithFoundElements", into(exampleTF));
		assertEquals(
			"testInteractWithFoundElements",
			TextField("Example Text Field").getValue()
		);
	}

}