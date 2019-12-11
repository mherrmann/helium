from helium.api import press, write, CONTROL, TAB, TextField, SHIFT
from helium_integrationtest.inttest_api import BrowserAT
from unittest import skip

class PressIT(BrowserAT):
	def get_page(self):
		return 'inttest_write.html'
	def test_press_single_character(self):
		press('a')
		self.assertEqual('a', TextField('Autofocus text field').value)
	def test_press_upper_case_character(self):
		press('A')
		self.assertEqual('A', TextField('Autofocus text field').value)
	def test_press_shift_plus_lower_case_character(self):
		press(SHIFT + 'a')
		self.assertEqual('A', TextField('Autofocus text field').value)
	@skip
	def test_copy_paste(self):
		# it is not possible to copy paste on OSX
		# OSX does not support native key events
		# https://code.google.com/p/selenium/issues/detail?id=3101
		write('One Two Three')
		press(CONTROL + 'a')
		press(CONTROL + 'c')
		press(TAB)
		press(CONTROL + 'v')
		self.assertEqual(
			'One Two Three', TextField("Normal text field").value
		)