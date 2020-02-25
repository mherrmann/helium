from helium.api import write, TextField
from tests.api import BrowserAT

class WriteTest(BrowserAT):
	def get_page(self):
		return 'test_write.html'
	def test_write(self):
		write("Hello World!")
		self.assertEqual(
			"Hello World!", TextField('Autofocus text field').value
		)
	def test_write_into(self):
		write("Hi there!", into='Normal text field')
		self.assertEqual("Hi there!", TextField('Normal text field').value)
	def test_write_into_text_field_to_right_of(self):
		write("Hi there!", into=(TextField(to_right_of='Normal text field')))
		self.assertEqual("Hi there!", TextField('Normal text field').value)