from helium.api import click, hover, rightclick
from helium_integrationtest.inttest_api import BrowserAT
from unittest import skip

class RightclickIT(BrowserAT):
	def get_page(self):
		return 'inttest_rightclick.html'
	def test_simple_rightclick(self):
		rightclick("Perform a normal rightclick here.")
		self.assertEquals(
			"Normal rightclick performed.", self.read_result_from_browser()
		)
	def test_rightclick_select_normal_item(self):
		rightclick("Rightclick here for context menu.")
		click("Normal item")
		self.assertEquals(
			"Normal item selected.", self.read_result_from_browser()
		)
	@skip("This test is too unstable.")
	def test_rightclick_select_sub_item(self):
		rightclick("Rightclick here for context menu.")
		hover("Item with sub items")
		click("Sub item 1")
		self.assertEquals(
			"Sub item 1 selected.", self.read_result_from_browser()
		)