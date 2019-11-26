package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class UnboundReprIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@Test
	public void testUnbound$Repr() {
		assertEquals(
			"$(\".cssClass\")", $(".cssClass").toString()
		);
	}

	@Test
	public void testUnbound$ReprBelow() {
		assertEquals(
			"$(\".cssClass\", below(\"Home\"))",
			$(".cssClass", below("Home")).toString()
		);
	}

	@Test
	public void testUnboundTextRepr() {
		assertEquals(
			"Text(\"Hello World!\")", Text("Hello World!").toString()
		);
	}

	@Test
	public void testUnboundLinkRepr() {
		assertEquals(
			"Link(\"Download\")", Link("Download").toString()
		);
	}

	@Test
	public void testUnboundListItemRepr() {
		assertEquals(
			"ListItem(\"Home\")", ListItem("Home").toString()
		);
	}

	@Test
	public void testUnboundButtonRepr() {
		assertEquals(
			"Button(\"Home\")", Button("Home").toString()
		);
	}

	@Test
	public void testUnboundImageRepr() {
		assertEquals(
			"Image(\"Logo\")", Image("Logo").toString()
		);
	}

	@Test
	public void testUnboundTextFieldRepr() {
		assertEquals(
			"TextField(\"File name\")", TextField("File name").toString()
		);
	}

	@Test
	public void testUnboundComboBoxRepr() {
		assertEquals(
			"ComboBox(\"Language\")", ComboBox("Language").toString()
		);
	}

	@Test
	public void testUnboundCheckBoxRepr() {
		assertEquals(
			"CheckBox(\"True?\")", CheckBox("True?").toString()
		);
	}

	@Test
	public void testUnboundRadioButtonRepr() {
		assertEquals(
			"RadioButton(\"Option A\")", RadioButton("Option A").toString()
		);
	}

	@Test
	public void testUnboundWindowRepr() {
		assertEquals(
			"Window(\"Main\")", Window("Main").toString()
		);
	}

	@Test
	public void testUnboundAlertRepr() {
		assertEquals(
			"Alert()", Alert().toString()
		);
	}

	@Test
	public void testUnboundAlertReprWithSearchText() {
		assertEquals(
			"Alert(\"Hello World\")", Alert("Hello World").toString()
		);
	}

}