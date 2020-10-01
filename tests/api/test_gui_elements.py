# -*- coding: utf-8 -*-
from helium import Button, TextField, ComboBox, CheckBox, click, \
    RadioButton, write, Text, find_all, Link, ListItem, Image, select, Config
from tests.api import BrowserAT


class GUIElementsTest(BrowserAT):
    def get_page(self):
        return 'test_gui_elements.html'

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        # If a test does fail, ensure it happens quickly:
        cls.implicit_wait_secs_before = Config.implicit_wait_secs
        Config.implicit_wait_secs = .5

    @classmethod
    def tearDownClass(cls):
        Config.implicit_wait_secs = cls.implicit_wait_secs_before
        super().tearDownClass()

    # Button tests:
    def test_button_exists(self):
        self.assertTrue(Button("Enabled Button").exists())

    def test_submit_button_exists(self):
        self.assertTrue(Button("Submit Button").exists())

    def test_submit_button_exists_lower_case(self):
        self.assertTrue(Button("submit button").exists())

    def test_input_button_exists(self):
        self.assertTrue(Button("Input Button").exists())

    def test_button_not_exists(self):
        self.assertFalse(Button("Nonexistent Button").exists())

    def test_text_field_does_not_exist_as_button(self):
        self.assertFalse(Button("Example Text Field").exists())

    def test_enabled_button(self):
        self.assertIs(True, Button("Enabled Button").is_enabled())

    def test_disabled_button(self):
        self.assertFalse(Button("Disabled Button").is_enabled())

    def test_button_no_text(self):
        self.assertEqual(2, len(find_all(Button(to_right_of='Row 1'))))

    def test_div_button_exists(self):
        self.assertTrue(Button("DIV with role=button").exists())

    def test_button_tag_button_exists(self):
        self.assertTrue(Button("Button tag without type").exists())

    def test_submit_button_can_be_found_by_title(self):
        self.assertTrue(Button("submitButtonTitle").exists())

    # TextField tests:
    def test_text_field_exists(self):
        self.assertIs(True, TextField("Example Text Field").exists())

    def test_text_field_lower_case_exists(self):
        self.assertIs(True, TextField("example text field").exists())

    def test_text_field_in_second_col_exists(self):
        self.assertIs(True, TextField("Another Text Field").exists())

    def test_text_field_not_exists(self):
        self.assertFalse(TextField("Nonexistent TextField").exists())

    def test_text_field_is_editable_false(self):
        self.assertIs(False, TextField("ReadOnly Text Field").is_editable())

    def test_text_field_is_editable(self):
        self.assertTrue(TextField("Example Text Field").is_editable())

    def test_text_field_is_enabled(self):
        self.assertIs(True, TextField("Example Text Field").is_enabled())

    def test_text_field_is_enabled_false(self):
        self.assertFalse(TextField("Disabled Text Field").is_enabled())

    def test_text_field_value(self):
        self.assertEqual("Lorem ipsum", TextField("Example Text Field").value)

    def test_text_field_with_placeholder_exists(self):
        self.assertIs(True, TextField("Placeholder Text Field").exists())

    def test_text_field_no_type_specified_with_placeholder_exists(self):
        self.assertIs(
            True, TextField("Placeholder Text Field without type").exists()
        )

    def test_empty_text_field_value(self):
        self.assertEqual('', TextField("Empty Text Field").value)

    def test_read_readonly_text_field(self):
        self.assertEqual(
            'This is read only', TextField("ReadOnly Text Field").value
        )

    def test_read_disabled_text_field(self):
        self.assertEqual(
            'This is disabled', TextField("Disabled Text Field").value
        )

    def test_read_german_text_field(self):
        self.assertEqual(
            'Heizölrückstoßabdämpfung', TextField("Deutsch").value
        )

    def test_text_field_input_type_upper_case_text(self):
        self.assertTrue(TextField('Input type=Text').exists())

    def test_write_into_labelled_text_field(self):
        write('Some text', into='Labelled Text Field')
        self.assertEqual('Some text', TextField('Labelled Text Field').value)

    def test_required_text_field_marked_with_asterisk_exists(self):
        self.assertIs(True, TextField("Required Text Field").exists())

    def test_text_field_labelled_by_free_text(self):
        self.assertEqual(
            "TF labelled by free text",
            TextField("Text field labelled by free text").value
        )

    def test_input_type_tel(self):
        self.assertFindsEltWithId(TextField("Input type=tel"), "inputTypeTel")

    def test_text_field_to_right_of_text_field(self):
        self.assertFindsEltWithId(
            TextField(to_right_of=TextField("Required Text Field")),
            "inputTypeTel"
        )

    def test_contenteditable_paragrapth(self):
        self.assertFindsEltWithId(
            TextField("contenteditable Paragraph"), "contenteditableParagraphId"
        )

    # ComboBox tests:
    def test_combo_box_exists(self):
        self.assertIs(True, ComboBox("Drop Down List").exists())

    def test_combo_box_exists_lower_case(self):
        self.assertIs(True, ComboBox("drop down list").exists())

    def test_drop_down_list_is_editable_false(self):
        self.assertIs(False, ComboBox("Drop Down List").is_editable())

    def test_editable_combo_box_is_editable(self):
        self.assertTrue(ComboBox("Editable ComboBox").is_editable())

    def test_combo_box_options(self):
        options = ComboBox("Drop Down List").options
        self.assertListEqual(
            options, ['Option One', 'Option Two', 'Option Three']
        )

    def test_reads_value_of_combo_box(self):
        self.assertEqual('Option One', ComboBox("Drop Down List").value)

    def test_select_value_from_combo_box(self):
        self.assertEqual('Option One', ComboBox("Drop Down List").value)
        select("Drop Down List", "Option Two")
        self.assertEqual('Option Two', ComboBox("Drop Down List").value)
        select(ComboBox("Drop Down List"), "Option Three")
        self.assertEqual('Option Three', ComboBox("Drop Down List").value)

    def test_combo_box_identified_by_value(self):
        combo_box = ComboBox("Select a value...")
        self.assertTrue(combo_box.exists())
        self.assertEqual("Select a value...", combo_box.value)
        self.assertFalse(combo_box.is_editable())
        self.assertEqual(
            ["Select a value...", "Value 1"], combo_box.options
        )

    def test_combo_box_preceded_by_combo_with_name_as_label(self):
        self.assertEqual(
            "combo1", ComboBox("Combo1").web_element.get_attribute("id")
        )

    # CheckBox tests:
    def test_check_box_exists(self):
        self.assertIs(True, CheckBox("CheckBox").exists())

    def test_check_box_exists_lower_case(self):
        self.assertIs(True, CheckBox("checkbox").exists())

    def test_left_hand_side_check_box_exists(self):
        self.assertIs(True, CheckBox("LHS CheckBox").exists())

    def test_check_box_not_exists(self):
        self.assertFalse(CheckBox("Nonexistent CheckBox").exists())

    def test_text_field_does_not_exist_as_check_box(self):
        self.assertFalse(CheckBox("Empty Text Field").exists())

    def test_ticked_check_box_exists(self):
        self.assertIs(True, CheckBox("Ticked CheckBox").exists())

    def test_ticked_check_box_is_enabled(self):
        self.assertIs(True, CheckBox("Ticked CheckBox").is_enabled())

    def test_right_labelled_check_box_exists(self):
        self.assertIs(True, CheckBox("Right Labeled CheckBox").exists())

    def test_left_labelled_check_box_exists(self):
        self.assertIs(True, CheckBox("Left Labeled CheckBox").exists())

    def test_disabled_check_box_exists(self):
        self.assertIs(True, CheckBox("Disabled CheckBox").exists())

    def test_ticked_check_box_is_checked(self):
        self.assertIs(True, CheckBox("Ticked CheckBox").is_checked())

    def test_right_labelled_check_box_is_not_checked(self):
        self.assertFalse(CheckBox("Right Labeled CheckBox").is_checked())

    def test_left_labelled_check_box_is_not_checked(self):
        self.assertIs(False, CheckBox("Left Labeled CheckBox").is_checked())

    def test_disabled_check_box_is_not_checked(self):
        self.assertIs(False, CheckBox("Disabled CheckBox").is_checked())

    def test_untick_check_box(self):
        ticked_check_box = CheckBox("Ticked CheckBox")
        click(ticked_check_box)
        self.assertIs(False, ticked_check_box.is_checked())

    def test_disabled_check_box_is_not_enabled(self):
        self.assertIs(False, CheckBox("Disabled CheckBox").is_enabled())

    def test_check_box_enclosed_by_label(self):
        self.assertFindsEltWithId(
            CheckBox("CheckBox enclosed by label"), "checkBoxEnclosedByLabel"
        )

    def test_checkboxes_labelled_by_free_text(self):
        self.assertTrue(CheckBox("unchecked").exists())
        self.assertTrue(CheckBox("checked").exists())
        self.assertTrue(CheckBox("checked").is_checked())
        self.assertFalse(CheckBox("unchecked").is_checked())

    # RadioButton tests:
    def test_first_radio_button_exists(self):
        self.assertIs(True, RadioButton("RadioButton 1").exists())

    def test_first_radio_button_exists_lower_case(self):
        self.assertIs(True, RadioButton("radiobutton 1").exists())

    def test_second_radio_button_exists(self):
        self.assertIs(True, RadioButton("RadioButton 2").exists())

    def test_left_labelled_radio_button_one_exists(self):
        self.assertIs(True, RadioButton("Left Labeled RadioButton 1").exists())

    def test_left_labelled_radio_button_two_exists(self):
        self.assertIs(True, RadioButton("Left Labeled RadioButton 2").exists())

    def test_first_radio_button_is_selected(self):
        self.assertIs(True, RadioButton("RadioButton 1").is_selected())

    def test_second_radio_button_is_not_selected(self):
        self.assertIs(False, RadioButton("RadioButton 2").is_selected())

    def test_select_second_radio_button(self):
        click(RadioButton("RadioButton 2"))
        self.assertIs(False, RadioButton("RadioButton 1").is_selected())
        self.assertIs(True, RadioButton("RadioButton 2").is_selected())

    def test_radio_button_not_exists(self):
        self.assertIs(False, RadioButton("Nonexistent option").exists())

    def test_text_field_is_not_a_radio_button(self):
        self.assertIs(False, RadioButton("Empty Text Field").exists())

    def test_radiobuttons_labelled_by_free_text(self):
        self.assertTrue(RadioButton("male").exists())
        self.assertTrue(RadioButton("female").exists())
        self.assertTrue(RadioButton("male").is_selected())
        self.assertFalse(RadioButton("female").is_selected())

    # Text tests:
    def test_text_exists_submit_button(self):
        self.assertTrue(Text("Submit Button").exists())

    def test_text_exists_submit_button_lower_case(self):
        self.assertTrue(Text("submit button").exists())

    def test_text_exists_link_with_title(self):
        self.assertTrue(Text("Link with title").exists())

    def test_text_exists_link_with_title_lower_case(self):
        self.assertTrue(Text("link with title").exists())

    def test_text_with_leading_nbsp_exists(self):
        self.assertTrue(Text("Text with leading &nbsp;").exists())

    def test_read_text_value(self):
        self.assertEqual(Text(to_right_of=Text("EUR/USD")).value, "1.3487")

    def test_free_text_not_surrounded_by_tags_exists(self):
        self.assertTrue(Text("Free text not surrounded by tags").exists())

    def test_text_with_apostrophe(self):
        self.assertTrue(Text("Your email's been sent!").exists())

    def test_text_with_double_quotes(self):
        self.assertTrue(Text('He said "double quotes".').exists())

    def test_text_with_single_and_double_quotes(self):
        self.assertTrue(Text("Single'quote. Double\"quote.").exists())

    def test_text_uppercase_umlaut(self):
        self.assertTrue(Text('VERÖFFENTLICHEN').exists())

    # Link tests:
    def test_link_exists(self):
        self.assertTrue(Link("Link").exists())

    def test_link_with_title_exists(self):
        self.assertTrue(Link('Link with title').exists())

    def test_link_no_text(self):
        self.assertEqual(4, len(find_all(Link())))

    def test_span_with_role_link_exists_as_link(self):
        self.assertTrue(Link("Span with role=link").exists())

    def test_link_href(self):
        self.assertEqual(Link("heliumhq.com").href, "http://heliumhq.com/")

    def test_link_empty_href(self):
        self.assertEqual(Link("Link with empty href").href, "")

    # ListItem tests:
    def test_list_item_no_text(self):
        all_list_items = find_all(ListItem(below="HTML Unordered List"))
        texts = set(list_item.web_element.text for list_item in all_list_items)
        self.assertEqual({'ListItem 1', 'ListItem 2'}, texts)

    # Image tests:
    def test_image_not_exists(self):
        self.assertFalse(Image("Non-existent").exists())

    def test_image_exists(self):
        self.assertTrue(Image("Dolphin").exists())

    # Misc tests:
    def test_text_field_combo_box_with_same_name(self):
        text_field = TextField("Language")
        combo_box = ComboBox("Language")
        self.assertNotEqual(text_field.y, combo_box.y)
