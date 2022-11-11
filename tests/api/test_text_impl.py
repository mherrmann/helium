from helium._impl import TextImpl
from helium._impl.selenium_wrappers import WebDriverWrapper
from tests.api import BrowserAT

class TextImplTest(BrowserAT):
	def get_page(self):
		return 'test_text_impl.html'
	def test_empty_search_text_xpath(self):
		xpath = TextImpl(WebDriverWrapper(self.driver))._get_search_text_xpath()
		text_elements = self.driver.find_elements(by='xpath', value=xpath)
		texts = [w.get_attribute('innerHTML') for w in text_elements]
		self.assertEqual(
			["A paragraph", "A paragraph inside a div",
			 "Another paragraph inside the div"],
			sorted(texts)
		)