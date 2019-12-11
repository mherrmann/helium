from helium.api import hover, Config
from helium_integrationtest.inttest_api import BrowserAT, test_browser_name
from helium_integrationtest.util import TemporaryAttrValue, is_windows
from unittest import skipIf

class HoverIT(BrowserAT):
	def get_page(self):
		return 'inttest_hover.html'
	def setUp(self):
		# This test fails if the mouse cursor happens to be over one of the
		# links in inttest_hover.html. Move the mouse cursor to (0, 0) to
		# prevent spurious test failures:
		self._move_mouse_cursor_to_origin()
		super(HoverIT, self).setUp()
	def _move_mouse_cursor_to_origin(self):
		if is_windows():
			from win32api import SetCursorPos
			SetCursorPos((0, 0))
		# Feel free to add implementation for OSX/Linux here...
	def test_hover_one(self):
		hover('Dropdown 1')
		result = self.read_result_from_browser()
		self.assertEqual(
			'Dropdown 1', result,
			"Got unexpected result %r. Maybe the mouse cursor was over the "
			"browser window and interfered with the test?" % result
		)
	@skipIf(
		test_browser_name() != 'firefox',
		"This test fails on the CI server when no VNC viewer is watching. When "
		"recording a video and inspecting afterwards, it appears that the test "
		"fails because the dropdown elements 'Item A', etc. disappear too "
		"quickly after hovering 'Dropdown 2'."
	)
	def test_hover_two_consecutively(self):
		hover('Dropdown 2')
		hover('Item C')
		result = self.read_result_from_browser()
		self.assertEqual(
			'Dropdown 2 - Item C', result,
			"Got unexpected result %r. Maybe the mouse cursor was over the "
			"browser window and interfered with the test?" % result
		)
	def test_hover_hidden(self):
		with TemporaryAttrValue(Config, 'implicit_wait_secs', 1):
			try:
				hover("Item C")
			except LookupError:
				pass # Success!
			else:
				self.fail(
					"Didn't receive expected LookupError. Maybe the mouse "
					"cursor was over the browser window and interfered with "
					"the test?"
				)