from helium.api import *
from helium.api_impl import APIImpl
from unittest import TestCase

class NoDriverIT(TestCase):
	def test_go_to_requires_driver(self):
		self._check_requires_driver(lambda: go_to('google.com'))
	def test_write_requires_driver(self):
		self._check_requires_driver(lambda: write('foo'))
	def test_press_requires_driver(self):
		self._check_requires_driver(lambda: press(ENTER))
	def test_click_requires_driver(self):
		self._check_requires_driver(lambda: click("Sign in"))
	def test_doubleclick_requires_driver(self):
		self._check_requires_driver(lambda: doubleclick("Sign in"))
	def test_drag_requires_driver(self):
		self._check_requires_driver(lambda: drag("Drag me", to="Drop here"))
	def test_find_all_requires_driver(self):
		self._check_requires_driver(lambda: find_all(Button()))
	def test_scroll_down_requires_driver(self):
		self._check_requires_driver(lambda: scroll_down())
	def test_scroll_up_requires_driver(self):
		self._check_requires_driver(lambda: scroll_up())
	def test_scroll_right_requires_driver(self):
		self._check_requires_driver(lambda: scroll_right())
	def test_scroll_left_requires_driver(self):
		self._check_requires_driver(lambda: scroll_left())
	def test_hover_requires_driver(self):
		self._check_requires_driver(lambda: hover("Hi there!"))
	def test_rightclick_requires_driver(self):
		self._check_requires_driver(lambda: rightclick("Hi there!"))
	def test_select_requires_driver(self):
		self._check_requires_driver(lambda: select("Language", "English"))
	def test_drag_file_requires_driver(self):
		self._check_requires_driver(
			lambda: drag_file(r'C:\test.txt', to="Here")
		)
	def test_attach_file_requires_driver(self):
		self._check_requires_driver(lambda: attach_file(r'C:\test.txt'))
	def test_refresh_requires_driver(self):
		self._check_requires_driver(lambda: refresh())
	def test_wait_until_requires_driver(self):
		self._check_requires_driver(lambda: wait_until(lambda: True))
	def test_switch_to_requires_driver(self):
		self._check_requires_driver(lambda: switch_to('Popup'))
	def test_kill_browser_requires_driver(self):
		self._check_requires_driver(lambda: switch_to('Popup'))
	def test_highlight_requires_driver(self):
		self._check_requires_driver(lambda: switch_to('Popup'))
	def test_s_requires_driver(self):
		self._check_requires_driver(lambda: S('#home'))
	def test_text_requires_driver(self):
		self._check_requires_driver(lambda: Text('Home'))
	def test_link_requires_driver(self):
		self._check_requires_driver(lambda: Link('Home'))
	def test_list_item_requires_driver(self):
		self._check_requires_driver(lambda: ListItem('Home'))
	def test_button_requires_driver(self):
		self._check_requires_driver(lambda: Button('Home'))
	def test_image_requires_driver(self):
		self._check_requires_driver(lambda: Image('Logo'))
	def test_text_field_requires_driver(self):
		self._check_requires_driver(lambda: TextField('File name'))
	def test_combo_box_requires_driver(self):
		self._check_requires_driver(lambda: ComboBox('Language'))
	def test_check_box_requires_driver(self):
		self._check_requires_driver(lambda: CheckBox('True?'))
	def test_radio_button_requires_driver(self):
		self._check_requires_driver(lambda: RadioButton('Option A'))
	def test_window_requires_driver(self):
		self._check_requires_driver(lambda: Window('Main'))
	def test_alert_requires_driver(self):
		self._check_requires_driver(lambda: Alert())
	def _check_requires_driver(self, function):
		with self.assertRaises(RuntimeError) as cm:
			function()
		self.assertEqual(APIImpl.DRIVER_REQUIRED_MESSAGE, cm.exception.args[0])