from helium import highlight, Button, Text, Config
from helium._impl.util.lang import TemporaryAttrValue
from tests.api import BrowserAT

class HighlightTest(BrowserAT):
	def get_page(self):
		return 'test_gui_elements.html'
	def test_highlight(self):
		button = Button("Input Button")
		highlight(button)
		self._check_is_highlighted(button)
	def test_highlight_string(self):
		highlight("Text with id")
		self._check_is_highlighted(Text("Text with id"))
	def test_highlight_nonexistent(self):
		with TemporaryAttrValue(Config, 'implicit_wait_secs', .5):
			with self.assertRaises(LookupError):
				highlight(Button("foo"))
	def _check_is_highlighted(self, html_element):
		style = html_element.web_element.get_attribute("style")
		self.assertTrue("border: 2px solid red;" in style, style)
		self.assertTrue("font-weight: bold;" in style, style)