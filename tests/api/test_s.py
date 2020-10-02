from helium import S
from tests.api import BrowserAT

class STest(BrowserAT):
	def get_page(self):
		return 'test_gui_elements.html'
	def test_find_by_id(self):
		self.assertFindsEltWithId(S("#checkBoxId"), 'checkBoxId')
	def test_find_by_name(self):
		self.assertFindsEltWithId(S("@checkBoxName"), 'checkBoxId')
	def test_find_by_class(self):
		self.assertFindsEltWithId(S(".checkBoxClass"), 'checkBoxId')
	def test_find_by_xpath(self):
		self.assertFindsEltWithId(
			S("//input[@type='checkbox' and @id='checkBoxId']"), 'checkBoxId'
		)
	def test_find_by_css_selector(self):
		self.assertFindsEltWithId(S('input.checkBoxClass'), 'checkBoxId')