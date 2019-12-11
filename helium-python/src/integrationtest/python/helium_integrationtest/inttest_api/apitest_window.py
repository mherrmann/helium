from helium.api import Window, click, go_to, get_driver, wait_until
from helium_integrationtest.environment import get_it_file_url
from helium_integrationtest.inttest_api import BrowserAT, should_run

class WindowIT(BrowserAT):
	def get_page(self):
		return 'inttest_window/inttest_window.html'
	def test_window_exists(self):
		self.assertTrue(Window('inttest_window').exists())
	def test_window_not_exists(self):
		self.assertFalse(Window('non-existent').exists())
	def test_no_arg_window_exists(self):
		self.assertTrue(Window().exists())
	def test_handle(self):
		self.assertTrue(Window('inttest_window').handle)
	def test_title(self):
		self.assertEqual('inttest_window', Window('inttest_window').title)

class MultipleWindowIT(WindowIT):
	"""
	The purpose of this IT is to run the same tests as WindowIT, but with an
	additional pop up window open.
	"""
	@classmethod
	def setUpClass(cls):
		super(MultipleWindowIT, cls).setUpClass()
		if should_run():
			go_to(get_it_file_url('inttest_window/inttest_window.html'))
			click("Click here to open a popup.")
			wait_until(Window('inttest_window - popup').exists)
	def test_popup_window_exists(self):
		self.assertTrue(Window('inttest_window - popup').exists())
	def setUp(self):
		# Don't let super go_to(...):
		pass
	@classmethod
	def tearDownClass(cls):
		popup_window_handle = Window("inttest_window - popup").handle
		main_window_handle = Window("inttest_window").handle
		get_driver().switch_to_window(popup_window_handle)
		get_driver().close()
		get_driver().switch_to_window(main_window_handle)
		super(MultipleWindowIT, cls).tearDownClass()