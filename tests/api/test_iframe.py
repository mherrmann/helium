from helium import Text, get_driver
from tests.api import BrowserAT

class IframeTest(BrowserAT):
	def get_page(self):
		return "test_iframe/main.html"
	def test_test_text_in_iframe_exists(self):
		self.assertTrue(Text("This text is inside an iframe.").exists())
	def test_text_in_nested_iframe_exists(self):
		self.assertTrue(Text("This text is inside a nested iframe.").exists())
	def test_finds_element_in_parent_iframe(self):
		self.test_text_in_nested_iframe_exists()
		# Now we're "focused" on the nested IFrame. Check that we can still
		# find the element an the parent IFrame:
		self.test_test_text_in_iframe_exists()
	def test_access_attributes_across_iframes(self):
		text = Text("This text is inside an iframe.")
		self.assertEqual("This text is inside an iframe.", text.value)
		get_driver().switch_to.default_content()
		self.assertEqual("This text is inside an iframe.", text.value)