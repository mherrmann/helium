from helium.api import click, Config
from helium_integrationtest.inttest_api import BrowserAT
from helium_integrationtest.util import TemporaryAttrValue

class ClickIT(BrowserAT):
	def get_page(self):
		return 'inttest_click.html'
	def test_click(self):
		click("Click me!")
		self.assertEqual('Success!', self.read_result_from_browser())
	def test_click_non_existent_element(self):
		with TemporaryAttrValue(Config, 'implicit_wait_secs', 1):
			with self.assertRaises(LookupError):
				click("Non-existent")