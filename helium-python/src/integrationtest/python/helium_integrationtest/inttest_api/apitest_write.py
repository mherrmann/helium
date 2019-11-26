from helium.api import write, TextField
from helium_integrationtest.inttest_api import BrowserAT

class WriteIT(BrowserAT):
	def get_page(self):
		return 'inttest_write.html'
	def test_write(self):
		write("Hello World!")
		self.assertEquals(
			"Hello World!", TextField('Autofocus text field').value
		)
	def test_write_into(self):
		write("Hi there!", into='Normal text field')
		self.assertEquals("Hi there!", TextField('Normal text field').value)
	def test_write_into_text_field_to_right_of(self):
		write("Hi there!", into=(TextField(to_right_of='Normal text field')))
		self.assertEquals("Hi there!", TextField('Normal text field').value)