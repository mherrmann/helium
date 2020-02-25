from helium.api import press, TextField, SHIFT
from tests.api import BrowserAT

class PressTest(BrowserAT):
	def get_page(self):
		return 'test_write.html'
	def test_press_single_character(self):
		press('a')
		self.assertEqual('a', TextField('Autofocus text field').value)
	def test_press_upper_case_character(self):
		press('A')
		self.assertEqual('A', TextField('Autofocus text field').value)
	def test_press_shift_plus_lower_case_character(self):
		press(SHIFT + 'a')
		self.assertEqual('A', TextField('Autofocus text field').value)