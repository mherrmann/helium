from helium.api import Text, get_driver
from helium_integrationtest.inttest_api import BrowserAT

class IframeIT(BrowserAT):
	def get_page(self):
		return "inttest_iframe/main.html"
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
		self.assertEquals("This text is inside an iframe.", text.value)
		get_driver().switch_to.default_content()
		self.assertEquals("This text is inside an iframe.", text.value)