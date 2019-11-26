from selenium.common.exceptions import StaleElementReferenceException
from helium.api import find_all, Button, TextField, write
from helium_integrationtest.inttest_api import BrowserAT

class FindAllIT(BrowserAT):
	def get_page(self):
		return 'inttest_gui_elements.html'
	def test_find_all_duplicate_button(self):
		self.assertEquals(4, len(find_all(Button("Duplicate Button"))))
	def test_find_all_duplicate_button_to_right_of(self):
		self.assertEquals(
			2, len(find_all(Button("Duplicate Button", to_right_of="Row 1")))
		)
	def test_find_all_duplicate_button_below_to_right_of(self):
		self.assertEquals(
			1, len(find_all(Button(
				"Duplicate Button", below="Column 1", to_right_of="Row 1"
			)))
		)
	def test_find_all_non_existent_button(self):
		self.assertEquals([], find_all(Button("Non-existent Button")))
	def test_find_all_yields_api_elements(self):
		self.assertIsInstance(
			find_all(TextField('Example Text Field'))[0], TextField
		)
	def test_interact_with_found_elements(self):
		all_tfs = find_all(TextField())
		example_tf = None
		for text_field in all_tfs:
			try:
				id_ = text_field.web_element.get_attribute('id')
			except StaleElementReferenceException:
				# This may happen for found web elements in different iframes.
				# TODO: Improve this, eg. by adding a .getId() property to
				# TextField (/HTMLElement) which handles this problem.
				pass
			else:
				if id_ == 'exampleTextFieldId':
					example_tf = text_field
		self.assertIsNotNone(example_tf)
		write("test_interact_with_found_elements", into=example_tf)
		self.assertEquals(
			"test_interact_with_found_elements",
			TextField("Example Text Field").value
		)