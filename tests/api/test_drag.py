from selenium.webdriver.common.by import By

from helium import *
from tests.api import BrowserAT

class DragTest(BrowserAT):
	def setUp(self):
		super().setUp()
		self.drag_target = self.driver.find_element(By.ID, 'target')
	def get_page(self):
		return 'test_drag/default.html'
	def test_drag(self):
		print(Text('Drag me.').exists())
		drag("Drag me.", to=self.drag_target)
		self.assertEqual('Success!', self.read_result_from_browser())
	def test_drag_to_point(self):
		target_loc = self.drag_target.location
		target_size = self.drag_target.size
		target_point = Point(
			target_loc['x'] + target_size['width'] / 2,
			target_loc['y'] + target_size['height'] / 2
		)
		self.assertTrue(Text('Drag me').exists())
		drag("Drag me.", to=target_point)
		self.assertEqual('Success!', self.read_result_from_browser())

class Html5DragIT(BrowserAT):
	def get_page(self):
		return 'test_drag/html5.html'
	def test_html5_drag(self):
		drag("Drag me.", to=self.driver.find_element(By.ID, 'target'))
		self.assertEqual('Success!', self.read_result_from_browser())