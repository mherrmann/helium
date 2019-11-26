package com.heliumhq.inttest_api;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.heliumhq.API.*;
import static org.junit.Assert.*;

public class GUIElementsIT extends BrowserAT {
	@Override
	protected String getPage() {
		return "inttest_gui_elements.html";
	}

	@BeforeClass
	public static void setUpClass() {
		BrowserAT.setUpClass();
		// If a test does fail, ensure it happens quickly:
		implicitWaitSecsBefore = Config.getImplicitWaitSecs();
		Config.setImplicitWaitSecs(.5);
	}
	private static double implicitWaitSecsBefore;

	@AfterClass
	public static void tearDownClass() {
		Config.setImplicitWaitSecs(implicitWaitSecsBefore);
		BrowserAT.tearDownClass();
	}

	// Button tests:
	@Test
	public void testButtonExists() {
		assertTrue(Button("Enabled Button").exists());
	}
	@Test
	public void testSubmitButtonExists() {
		assertTrue(Button("Submit Button").exists());
	}
	@Test
	public void testSubmitButtonExistsLowerCase() {
		assertTrue(Button("submit button").exists());
	}
	@Test
	public void testInputButtonExists() {
		assertTrue(Button("Input Button").exists());
	}
	@Test
	public void testButtonNotExists() {
		assertFalse(Button("Nonexistent Button").exists());
	}
	@Test
	public void testTextFieldDoesNotExistAsButton() {
		assertFalse(Button("Example Text Field").exists());
	}
	@Test
	public void testEnabledButton() {
		assertTrue(Button("Enabled Button").isEnabled());
	}
	@Test
	public void testDisabledButton() {
		assertFalse(Button("Disabled Button").isEnabled());
	}
	@Test
	public void testButtonNoText() {
		assertEquals(2, findAll(Button(toRightOf("Row 1"))).size());
	}
	@Test
	public void testDivButtonExists() {
		assertTrue(Button("DIV with role=button").exists());
	}
	@Test
	public void testButtonTagButtonExists() {
		assertTrue(Button("Button tag without type").exists());
	}
	@Test
	public void testSubmitButtonCanBeFoundByTitle() {
		assertTrue(Button("submitButtonTitle").exists());
	}

	// TextField tests:
	@Test
	public void testTextFieldExists() {
		assertTrue(TextField("Example Text Field").exists());
	}
	@Test
	public void testTextFieldLowerCaseExists() {
		assertTrue(TextField("example text field").exists());
	}
	@Test
	public void testTextFieldInSecondColExists() {
		assertTrue(TextField("Another Text Field").exists());
	}
	@Test
	public void testTextFieldNotExists() {
		assertFalse(TextField("Nonexistent TextField").exists());
	}
	@Test
	public void testTextFieldIsEditableFalse() {
		assertFalse(TextField("ReadOnly Text Field").isEditable());
	}
	@Test
	public void testTextFieldIsEditable() {
		assertTrue(TextField("Example Text Field").isEditable());
	}
	@Test
	public void testTextFieldIsEnabled() {
		assertTrue(TextField("Example Text Field").isEnabled());
	}
	@Test
	public void testTextFieldIsEnabledFalse() {
		assertFalse(TextField("Disabled Text Field").isEnabled());
	}
	@Test
	public void testTextFieldValue() {
		assertEquals("Lorem ipsum", TextField("Example Text Field").getValue());
	}
	@Test
	public void testTextFieldWithPlaceholderExists() {
		assertTrue(TextField("Placeholder Text Field").exists());
	}
	@Test
	public void testTextFieldNoTypeSpecifiedWithPlaceholderExists() {
		assertTrue(TextField("Placeholder Text Field without type").exists());
	}
	@Test
	public void testEmptyTextFieldValue() {
		assertEquals("", TextField("Empty Text Field").getValue());
	}
	@Test
	public void testReadReadonlyTextField() {
		assertEquals(
				"This is read only", TextField("ReadOnly Text Field").getValue()
		);
	}
	@Test
	public void testReadDisabledTextField() {
		assertEquals(
				"This is disabled", TextField("Disabled Text Field").getValue()
		);
	}
	@Test
	public void testReadGermanTextField() {
		assertEquals(
				"Heizölrückstoßabdämpfung", TextField("Deutsch").getValue()
		);
	}
	@Test
	public void testTextFieldInputTypeUpperCaseText() {
		assertTrue(TextField("Input type=Text").exists());
	}
	@Test
	public void testWriteIntoLabelledTextField() {
		write("Some text", into("Labelled Text Field"));
		assertEquals("Some text", TextField("Labelled Text Field").getValue());
	}
	@Test
	public void testRequiredTextFieldMarkedWithAsteriskExists() {
		assertTrue(TextField("Required Text Field").exists());
	}
	@Test
	public void testTextFieldLabelledByFreeText() {
		assertEquals(
				"TF labelled by free text",
				TextField("Text field labelled by free text").getValue()
		);
	}
	@Test
	public void testInputTypeTel() {
		assertFindsEltWithId(TextField("Input type=tel"), "inputTypeTel");
	}
	@Test
	public void testTextFieldToRightOfTextField() {
		assertFindsEltWithId(
				TextField(toRightOf(TextField("Required Text Field"))),
				"inputTypeTel"
		);
	}
	@Test
	public void testContenteditableParagraph() {
		assertFindsEltWithId(
			TextField("contenteditable Paragraph"), "contenteditableParagraphId"
		);
	}
	@Ignore(
		"Searches where the element is in an iframe different from the label " +
		"are not yet implemented."
	)
	@Test
	public void testTextfieldInIframe() {
		assertFindsEltWithId(
				TextField("TextField in iframe"), "textfieldInIframeId"
		);
	}

	// ComboBox tests:
	@Test
	public void testComboBoxExists() {
		assertTrue(ComboBox("Drop Down List").exists());
	}
	@Test
	public void testComboBoxExistsLowerCase() {
		assertTrue(ComboBox("drop down list").exists());
	}
	@Test
	public void testDropDownListIsEditableFalse() {
		assertFalse(ComboBox("Drop Down List").isEditable());
	}
	@Test
	public void testEditableComboBoxIsEditable() {
		assertTrue(ComboBox("Editable ComboBox").isEditable());
	}
	@Test
	public void testComboBoxOptions() {
		List<String> options = ComboBox("Drop Down List").getOptions();
		assertEquals(
			options, Arrays.asList("Option One", "Option Two", "Option Three")
		);
	}
	@Test
	public void testReadsValueOfComboBox() {
		assertEquals("Option One", ComboBox("Drop Down List").getValue());
	}
	@Test
	public void testSelectValueFromComboBox() {
		assertEquals("Option One", ComboBox("Drop Down List").getValue());
		select("Drop Down List", "Option Two");
		assertEquals("Option Two", ComboBox("Drop Down List").getValue());
		select(ComboBox("Drop Down List"), "Option Three");
		assertEquals("Option Three", ComboBox("Drop Down List").getValue());
	}
	@Test
	public void testComboBoxIdentifiedByValue() {
		ComboBox comboBox = ComboBox("Select a value...");
		assertTrue(comboBox.exists());
		assertEquals("Select a value...", comboBox.getValue());
		assertFalse(comboBox.isEditable());
		assertEquals(
			Arrays.asList("Select a value...", "Value 1"), comboBox.getOptions()
		);
	}
	@Test
	public void testComboBoxPrecededByComboWithNameAsLabel() {
		assertEquals(
			"combo1", ComboBox("Combo1").getWebElement().getAttribute("id")
		);
	}

	// CheckBox tests:
	@Test
	public void testCheckBoxExists() {
		assertTrue(CheckBox("CheckBox").exists());
	}
	@Test
	public void testCheckBoxExistsLowerCase() {
		assertTrue(CheckBox("checkbox").exists());
	}
	@Test
	public void testLeftHandSideCheckBoxExists() {
		assertTrue(CheckBox("LHS CheckBox").exists());
	}
	@Test
	public void testCheckBoxNotExists() {
		assertFalse(CheckBox("Nonexistent CheckBox").exists());
	}
	@Test
	public void testTextFieldDoesNotExistAsCheckBox() {
		assertFalse(CheckBox("Empty Text Field").exists());
	}
	@Test
	public void testTickedCheckBoxExists() {
		assertTrue(CheckBox("Ticked CheckBox").exists());
	}
	@Test
	public void testTickedCheckBoxIsEnabled() {
		assertTrue(CheckBox("Ticked CheckBox").isEnabled());
	}
	@Test
	public void testRightLabelledCheckBoxExists() {
		assertTrue(CheckBox("Right Labeled CheckBox").exists());
	}
	@Test
	public void testLeftLabelledCheckBoxExists() {
		assertTrue(CheckBox("Left Labeled CheckBox").exists());
	}
	@Test
	public void testDisabledCheckBoxExists() {
		assertTrue(CheckBox("Disabled CheckBox").exists());
	}
	@Test
	public void testTickedCheckBoxIsChecked() {
		assertTrue(CheckBox("Ticked CheckBox").isChecked());
	}
	@Test
	public void testRightLabelledCheckBoxIsNotChecked() {
		assertFalse(CheckBox("Right Labeled CheckBox").isChecked());
	}
	@Test
	public void testLeftLabelledCheckBoxIsNotChecked() {
		assertFalse(CheckBox("Left Labeled CheckBox").isChecked());
	}
	@Test
	public void testDisabledCheckBoxIsNotChecked() {
		assertFalse(CheckBox("Disabled CheckBox").isChecked());
	}
	@Test
	public void testUntickCheckBox() {
		CheckBox tickedCheckBox = CheckBox("Ticked CheckBox");
		click(tickedCheckBox);
		assertFalse(tickedCheckBox.isChecked());
	}
	@Test
	public void testDisabledCheckBoxIsNotEnabled() {
		assertFalse(CheckBox("Disabled CheckBox").isEnabled());
	}
	@Test
	public void testCheckBoxEnclosedByLabel() {
		assertFindsEltWithId(
			CheckBox("CheckBox enclosed by label"), "checkBoxEnclosedByLabel"
		);
	}
	@Test
	public void testCheckboxesLabelledByFreeText() {
		assertTrue(CheckBox("unchecked").exists());
		assertTrue(CheckBox("checked").exists());
		assertTrue(CheckBox("checked").isChecked());
		assertFalse(CheckBox("unchecked").isChecked());
	}

	// RadioButton tests:
	@Test
	public void testFirstRadioButtonExists() {
		assertTrue(RadioButton("RadioButton 1").exists());
	}
	@Test
	public void testFirstRadioButtonExistsLowerCase() {
		assertTrue(RadioButton("radiobutton 1").exists());
	}
	@Test
	public void testSecondRadioButtonExists() {
		assertTrue(RadioButton("RadioButton 2").exists());
	}
	@Test
	public void testLeftLabelledRadioButtonOneExists() {
		assertTrue(RadioButton("Left Labeled RadioButton 1").exists());
	}
	@Test
	public void testLeftLabelledRadioButtonTwoExists() {
		assertTrue(RadioButton("Left Labeled RadioButton 2").exists());
	}
	@Test
	public void testFirstRadioButtonIsSelected() {
		assertTrue(RadioButton("RadioButton 1").isSelected());
	}
	@Test
	public void testSecondRadioButtonIsNotSelected() {
		assertFalse(RadioButton("RadioButton 2").isSelected());
	}
	@Test
	public void testSelectSecondRadioButton() {
		click(RadioButton("RadioButton 2"));
		assertFalse(RadioButton("RadioButton 1").isSelected());
		assertTrue(RadioButton("RadioButton 2").isSelected());
	}
	@Test
	public void testRadioButtonNotExists() {
		assertFalse(RadioButton("Nonexistent option").exists());
	}
	@Test
	public void testTextFieldIsNotARadioButton() {
		assertFalse(RadioButton("Empty Text Field").exists());
	}
	@Test
	public void testRadioButtonsLabelledByFreeText() {
		assertTrue(RadioButton("male").exists());
		assertTrue(RadioButton("female").exists());
		assertTrue(RadioButton("male").isSelected());
		assertFalse(RadioButton("female").isSelected());
	}

	// Text tests:
	@Test
	public void testTextExistsSubmitButton() {
		assertTrue(Text("Submit Button").exists());
	}
	@Test
	public void testTextExistsSubmitButtonLowerCase() {
		assertTrue(Text("submit button").exists());
	}
	@Test
	public void testTextExistsLinkWithTitle() {
		assertTrue(Text("Link with title").exists());
	}
	@Test
	public void testTextExistsLinkWithTitleLowerCase() {
		assertTrue(Text("link with title").exists());
	}
	@Test
	public void testTextWithLeadingNbspExists() {
		assertTrue(Text("Text with leading &nbsp;").exists());
	}
	@Test
	public void testReadTextValue() {
		assertEquals(Text(toRightOf(Text("EUR/USD"))).getValue(), "1.3487");
	}
	@Test
	public void testFreeTextNotSurroundedByTagsExists() {
		assertTrue(Text("Free text not surrounded by tags").exists());
	}
	@Test
	public void testTextWithApostrophe() {
		assertTrue(Text("Your email's been sent!").exists());
	}
	@Test
	public void testTextWithDoubleQuotes() {
		assertTrue(Text("He said \"double quotes\".").exists());
	}
	@Test
	public void testTextWithSingleAndDoubleQuotes() {
		assertTrue(Text("Single'quote. Double\"quote.").exists());
	}
	@Test
	public void testTextUppercaseUmlaut() {
		assertTrue(Text("VERÖFFENTLICHEN").exists());
	}

	// Link tests:
	@Test
	public void testLinkExists() {
		assertTrue(Link("Link").exists());
	}
	@Test
	public void testLinkWithTitleExists() {
		assertTrue(Link("Link with title").exists());
	}
	@Test
	public void testLinkNoText() {
		assertEquals(4, findAll(Link()).size());
	}
	@Test
	public void testSpanWithRoleLinkExistsAsLink() {
		assertTrue(Link("Span with role=link").exists());
	}
	@Test
	public void testLinkHref() {
		assertEquals("http://heliumhq.com/", Link("heliumhq.com").getHref());
	}
	@Test
	public void testLinkEmptyHref() {
		assertEquals("", Link("Link with empty href").getHref());
	}

	// ListItem tests:
	@Test
	public void testListItemNoText() {
		List<ListItem> allListItems = findAll(
			ListItem(below("HTML Unordered List"))
		);
		Set<String> texts = new HashSet<String>();
		for (ListItem listItem : allListItems)
			texts.add(listItem.getWebElement().getText());
		assertEquals(
			new HashSet<String>(Arrays.asList("ListItem 1", "ListItem 2")),
			texts
		);
	}

	// Image tests:
	@Test
	public void testImageNotExists() {
		assertFalse(Image("Non-existent").exists());
	}
	@Test
	public void testImageExists() {
		assertTrue(Image("Dolphin").exists());
	}

	// Misc tests:
	@Test
	public void testTextFieldComboBoxWithSameName() {
		TextField textField = TextField("Language");
		ComboBox comboBox = ComboBox("Language");
		assertNotEquals(textField.getY(), comboBox.getY());
	}
}