from helium.api import write, click, switch_to, TextField, Text, get_driver, \
	Link, wait_until
from helium_integrationtest.inttest_api import BrowserAT

class WindowHandlingIT(BrowserAT):
	def get_page(self):
		return 'inttest_window_handling/main.html'
	def test_write_writes_in_active_window(self):
		write("Main window")
		self.assertEquals("Main window", self._get_value('mainTextField'))
		self._open_popup()
		write("Popup")
		self.assertEquals("Popup", self._get_value('popupTextField'))
	def test_write_searches_in_active_window(self):
		write("Main window", into="Text field")
		self.assertEquals("Main window", self._get_value('mainTextField'))
		self._open_popup()
		write("Popup", into="Text field")
		self.assertEquals("Popup", self._get_value('popupTextField'))
	def test_switch_to_search_text_field(self):
		write("Main window", into="Text field")
		self.assertEquals("Main window", TextField("Text field").value)
		self._open_popup()
		write("Popup", into="Text field")
		self.assertEquals("Popup", TextField("Text field").value)
		switch_to("inttest_window_handling - Main")
		self.assertEquals("Main window", TextField("Text field").value)
	def test_handles_closed_window_gracefully(self):
		self._open_popup()
		get_driver().close()
		is_back_in_main_window = Link("Open popup").exists()
		self.assertTrue(is_back_in_main_window)
	def test_switch_to_after_window_closed(self):
		self._open_popup()
		get_driver().close()
		switch_to('inttest_window_handling - Main')
	def setUp(self):
		super(WindowHandlingIT, self).setUp()
		self.main_window_handle = self.driver.current_window_handle
	def tearDown(self):
		for window_handle in self.driver.window_handles:
			if window_handle != self.main_window_handle:
				self.driver.switch_to_window(window_handle)
				self.driver.close()
		self.driver.switch_to_window(self.main_window_handle)
		super(WindowHandlingIT, self).tearDown()
	def _get_value(self, element_id):
		return self.driver.find_element_by_id(element_id).get_attribute('value')
	def _open_popup(self):
		click("Open popup")
		wait_until(self._is_in_popup)
	def _is_in_popup(self):
		return get_driver().title == 'inttest_window_handling - Popup'

class WindowHandlingOnStartBrowserIT(BrowserAT):
	def get_page(self):
		return 'inttest_window_handling/main_immediate_popup.html'
	def test_switches_to_popup(self):
		self.assertTrue(Text("In popup.").exists())