from helium.api import Button, TextField
from helium_integrationtest.inttest_api import BrowserAT

class AriaIT(BrowserAT):
	def get_page(self):
		return 'inttest_aria.html'
	def test_aria_label_button_exists(self):
		self.assertTrue(Button("Close").exists())
	def test_aria_label_button_is_enabled(self):
		self.assertTrue(Button("Close").is_enabled())
	def test_aria_label_disabled_button_is_enabled(self):
		self.assertFalse(Button("Disabled Close").is_enabled())
	def test_aria_label_non_existent_button(self):
		self.assertFalse(Button("This doesnt exist").exists())
	def test_aria_label_div_button_exists(self):
		self.assertTrue(Button("Attach files").exists())
	def test_aria_label_div_button_is_enabled(self):
		self.assertTrue(Button("Attach files").is_enabled())
	def test_aria_label_div_disabled_button_is_enabled(self):
		self.assertFalse(Button("Disabled Attach files").is_enabled())
	def test_aria_label_submit_button_exists(self):
		self.assertTrue(Button("Submit").exists())
	def test_aria_textbox_exists(self):
		self.assertTrue(TextField("Textbox").exists())
	def test_aria_textbox_value(self):
		self.assertEqual("Textbox value", TextField("Textbox").value)