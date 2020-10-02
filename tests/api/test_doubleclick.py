from helium import doubleclick
from tests.api import BrowserAT

class DoubleclickTest(BrowserAT):
	def get_page(self):
		return 'test_doubleclick.html'
	def test_double_click(self):
		doubleclick('Doubleclick here.')
		self.assertEqual('Success!', self.read_result_from_browser())