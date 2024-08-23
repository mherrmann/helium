from helium import write, TextField
from tests.api import BrowserAT

class WriteTest(BrowserAT):
	def get_page(self):
		return 'test_write.html'
	def test_write(self):
		write('Hello World!')
		self.assertEqual(
			'Hello World!', TextField('Autofocus text field').value
		)
	def test_write_into(self):
		value = 'Hi there!'
		label = 'Normal text field'
		write(value, into=label)
		self.assertEqual(value, TextField(label).value)
	def test_write_into_text_field_to_right_of(self):
		value = 'Hi there!'
		label = 'Normal text field'
		write(value, into=TextField(to_right_of=label))
		self.assertEqual(value, TextField(label).value)
	def test_write_into_input_type_date(self):
		label = 'Input type=date'
		write('08/23/2024', into=TextField(to_right_of=label))
		self.assertEqual('2024-08-23', TextField(label).value)
	def test_write_into_input_type_time(self):
		label = 'Input type=time'
		write('1101PM', into=TextField(to_right_of=label))
		self.assertEqual('23:01', TextField(label).value)