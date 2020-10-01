from helium import Window, click, go_to, get_driver, wait_until
from tests.api.util import get_data_file_url
from tests.api import BrowserAT


class WindowTest(BrowserAT):
	def get_page(self):
		return 'test_window/test_window.html'

	def test_window_exists(self):
		self.assertTrue(Window('test_window').exists())

	def test_window_not_exists(self):
		self.assertFalse(Window('non-existent').exists())

	def test_no_arg_window_exists(self):
		self.assertTrue(Window().exists())

	def test_handle(self):
		self.assertTrue(Window('test_window').handle)

	def test_title(self):
		self.assertEqual('test_window', Window('test_window').title)


class MultipleWindowTest(WindowTest):
	"""
	The purpose of this Test is to run the same tests as WindowTest, but with an
	additional pop up window open.
	"""
	@classmethod
	def setUpClass(cls):
		super().setUpClass()
		go_to(get_data_file_url('test_window/test_window.html'))
		click("Click here to open a popup.")
		wait_until(Window('test_window - popup').exists)

	def test_popup_window_exists(self):
		self.assertTrue(Window('test_window - popup').exists())

	def setUp(self):
		# Don't let super go_to(...):
		pass

	@classmethod
	def tearDownClass(cls):
		popup_window_handle = Window("test_window - popup").handle
		main_window_handle = Window("test_window").handle
		get_driver().switch_to.window(popup_window_handle)
		get_driver().close()
		get_driver().switch_to.window(main_window_handle)
		super().tearDownClass()
