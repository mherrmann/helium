from helium import write, click, switch_to, TextField, Text, get_driver, \
	Link, wait_until
from selenium.webdriver.common.by import By
from tests.api import BrowserAT, test_browser_name
from unittest import skipIf

class WindowHandlingTest(BrowserAT):
	def get_page(self):
		return 'test_window_handling/main.html'
	def test_write_writes_in_active_window(self):
		write("Main window")
		self.assertEqual("Main window", self._get_value('mainTextField'))
		self._open_popup()
		write("Popup")
		self.assertEqual("Popup", self._get_value('popupTextField'))
	def test_write_searches_in_active_window(self):
		write("Main window", into="Text field")
		self.assertEqual("Main window", self._get_value('mainTextField'))
		self._open_popup()
		write("Popup", into="Text field")
		self.assertEqual("Popup", self._get_value('popupTextField'))
	def test_switch_to_search_text_field(self):
		write("Main window", into="Text field")
		self.assertEqual("Main window", TextField("Text field").value)
		self._open_popup()
		write("Popup", into="Text field")
		self.assertEqual("Popup", TextField("Text field").value)
		switch_to("test_window_handling - Main")
		self.assertEqual("Main window", TextField("Text field").value)
	def test_handles_closed_window_gracefully(self):
		self._open_popup()
		get_driver().close()
		is_back_in_main_window = Link("Open popup").exists()
		self.assertTrue(is_back_in_main_window)
	def test_switch_to_after_window_closed(self):
		self._open_popup()
		get_driver().close()
		switch_to('test_window_handling - Main')
	def setUp(self):
		super().setUp()
		self.main_window_handle = self.driver.current_window_handle
	def tearDown(self):
		for window_handle in self.driver.window_handles:
			if window_handle != self.main_window_handle:
				self.driver.switch_to.window(window_handle)
				self.driver.close()
		self.driver.switch_to.window(self.main_window_handle)
		super().tearDown()
	def _get_value(self, element_id):
		return self.driver.find_element(By.ID, element_id).get_attribute('value')
	def _open_popup(self):
		click("Open popup")
		wait_until(self._is_in_popup)
	def _is_in_popup(self):
		return get_driver().title == 'test_window_handling - Popup'

class WindowHandlingOnStartBrowserTest(BrowserAT):
	def get_page(self):
		return 'test_window_handling/main_immediate_popup.html'
	@skipIf(test_browser_name() == 'firefox', 'This test fails on Firefox')
	def test_switches_to_popup(self):
		self.assertTrue(Text("In popup.").exists())