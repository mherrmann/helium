from helium.api import *
from helium.api import HTMLElement
from helium_integrationtest.inttest_api import BrowserAT

import re

class UnboundReprIT(BrowserAT):
	def get_page(self):
		return 'inttest_gui_elements.html'
	def test_unbound_s_repr(self):
		self.assertEquals(
			"S('.cssClass')", repr(S('.cssClass'))
		)
	def test_unbound_s_repr_below(self):
		self.assertEquals(
			"S('.cssClass', below='Home')", repr(S('.cssClass', below='Home'))
		)
	def test_unbound_text_repr(self):
		self.assertEquals(
			"Text('Hello World!')", repr(Text('Hello World!'))
		)
	def test_unbound_link_repr(self):
		self.assertEquals(
			"Link('Download')", repr(Link('Download'))
		)
	def test_unbound_list_item_repr(self):
		self.assertEquals(
			"ListItem('Home')", repr(ListItem('Home'))
		)
	def test_unbound_button_repr(self):
		self.assertEquals(
			"Button('Home')", repr(Button('Home'))
		)
	def test_unbound_image_repr(self):
		self.assertEquals(
			"Image('Logo')", repr(Image('Logo'))
		)
	def test_unbound_text_field_repr(self):
		self.assertEquals(
			"TextField('File name')", repr(TextField('File name'))
		)
	def test_unbound_combo_box_repr(self):
		self.assertEquals(
			"ComboBox('Language')", repr(ComboBox('Language'))
		)
	def test_unbound_check_box_repr(self):
		self.assertEquals(
			"CheckBox('True?')", repr(CheckBox('True?'))
		)
	def test_unbound_radio_button_repr(self):
		self.assertEquals(
			"RadioButton('Option A')", repr(RadioButton('Option A'))
		)
	def test_unbound_window_repr(self):
		self.assertEquals(
			"Window('Main')", repr(Window('Main'))
		)
	def test_unbound_alert_repr(self):
		self.assertEquals(
			"Alert()", repr(Alert())
		)
	def test_unbound_alert_repr_with_search_text(self):
		self.assertEquals(
			"Alert('Hello World')", repr(Alert('Hello World'))
		)

class BoundReprIT(BrowserAT):
	def get_page(self):
		return 'inttest_gui_elements.html'
	def test_bound_s_repr(self):
		bound_s = self._bind(S("#checkBoxId"))
		self._assertHtmlEltWithMultipleAttributesEquals(
			'<input type="checkbox" id="checkBoxId" name="checkBoxName" '
			'class="checkBoxClass">',
			repr(bound_s)
		)
	def test_bound_s_repr_long_content(self):
		body = self._bind(S("body"))
		self.assertEquals("<body>...</body>", repr(body))
	def test_bound_button_repr(self):
		bound_button = self._bind(Button('Enabled Button'))
		self.assertEquals(
			'<button type="button">Enabled Button</button>', repr(bound_button)
		)
	def test_bound_link_repr_nested_tag(self):
		link = self._bind(Link("Link with title"))
		self._assertHtmlEltWithMultipleAttributesEquals(
			'<a href="#" title="Link with title">...</a>', repr(link)
		)
	def test_bound_repr_duplicate_button(self):
		self.assertEquals(
			'[<button type="button">Duplicate Button</button>,'
			' <button type="button">Duplicate Button</button>,'
			' <button type="button">Duplicate Button</button>,'
			' <button type="button">Duplicate Button</button>]',
			repr(find_all(Button("Duplicate Button")))
		)
	def test_bound_window_repr(self):
		bound_window = self._bind(Window())
		self.assertEquals(
			"Window('Test page for browser system tests')", repr(bound_window)
		)
	def test_bound_window_repr_with_search_text(self):
		bound_window = self._bind(Window('Test page for'))
		self.assertEquals(
			"Window('Test page for browser system tests')", repr(bound_window)
		)
	def _bind(self, predicate):
		if isinstance(predicate, HTMLElement):
			# Reading a property such as web_element waits for the element to
			# exist and binds the predicate to it:
			predicate.web_element
		else:
			assert isinstance(predicate, Window)
			# Reading a property such as handle waits for the element to
			# exist and binds the predicate to it:
			predicate.handle
		return predicate
	def _assertHtmlEltWithMultipleAttributesEquals(self, expected, actual):
		start_tag_exp, remainder_exp = expected.split('>', 1)
		start_tag_act, remainder_act = actual.split('>', 1)
		attributes_re = '[a-zA-Z]+="[^"]+"'
		attributes_exp = re.findall(attributes_re, start_tag_exp)
		attributes_act = re.findall(attributes_re, start_tag_act)
		self.assertEquals(set(attributes_exp), set(attributes_act))
		self.assertEquals(remainder_exp, remainder_act)

class BoundAlertReprIT(BrowserAT):
	def get_page(self):
		return 'inttest_alert.html'
	def setUp(self):
		super(BoundAlertReprIT, self).setUp()
		click("Display alert")
	def test_bound_alert_repr(self):
		alert = Alert()
		# Bind alert:
		alert.text
		self.assertEquals("Alert('Hello World!')", repr(alert))
	def test_bound_alert_repr_with_partial_search_text(self):
		alert = Alert('Hello')
		# Bind alert:
		alert.text
		self.assertEquals("Alert('Hello World!')", repr(alert))
	def tearDown(self):
		Alert().accept()
		super(BoundAlertReprIT, self).tearDown()