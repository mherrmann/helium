from helium.api import doubleclick
from helium_integrationtest.inttest_api import BrowserAT

class DoubleclickIT(BrowserAT):
	def get_page(self):
		return 'inttest_doubleclick.html'
	def test_double_click(self):
		doubleclick('Doubleclick here.')
		self.assertEqual('Success!', self.read_result_from_browser())