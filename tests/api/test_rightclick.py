from helium import click, hover, rightclick
from tests.api import BrowserAT


class RightclickTest(BrowserAT):
	def get_page(self):
		return 'test_rightclick.html'

	def test_simple_rightclick(self):
		rightclick("Perform a normal rightclick here.")
		self.assertEqual(
			"Normal rightclick performed.", self.read_result_from_browser()
		)

	def test_rightclick_select_normal_item(self):
		rightclick("Rightclick here for context menu.")
		click("Normal item")
		self.assertEqual(
			"Normal item selected.", self.read_result_from_browser()
		)
