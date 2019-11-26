from helium.api import start_chrome, start_firefox, start_ie, go_to, \
	set_driver, kill_browser
from helium_integrationtest.util import is_windows
from helium_integrationtest.environment import get_it_file_url
from unittest import TestCase, skipIf
from time import time, sleep

import os

def test_browser_name():
	try:
		browser_name = os.environ['TEST_BROWSER']
	except KeyError:
		return 'chrome'
	else:
		return browser_name

def should_run():
	return is_windows() or not test_browser_name() == 'ie'

@skipIf(not should_run(), 'IE on Windows only')
class BrowserAT(TestCase):
	@classmethod
	def setUpClass(cls):
		# We still need this check despite the @skipIf above because nose calls
		# this method even if @skipIf returns True.
		if should_run():
			if _TEST_BROWSER is None:
				cls.driver = start_browser()
				cls.started_browser = True
			else:
				cls.driver = _TEST_BROWSER
				cls.started_browser = False
			set_driver(cls.driver)
	def setUp(self):
		go_to(self.get_url())
	def get_url(self):
		return get_it_file_url(self.get_page())
	def get_page(self):
		raise NotImplementedError()
	def read_result_from_browser(self, timeout_secs=3):
		start_time = time()
		while time() < start_time + timeout_secs:
			result = self.driver\
				.find_element_by_id('result').get_attribute('innerHTML')
			if result:
				return result
			sleep(0.2)
		return ''
	def assertFindsEltWithId(self, predicate, id_):
		self.assertEquals(id_, predicate.web_element.get_attribute('id'))
	@classmethod
	def tearDownClass(cls):
		if cls.started_browser:
			kill_browser()

_TEST_BROWSER = None

def setUpModule():
	global _TEST_BROWSER
	_TEST_BROWSER = start_browser()

def tearDownModule():
	global _TEST_BROWSER
	if _TEST_BROWSER is not None:
		kill_browser()
	_TEST_BROWSER = None

def start_browser(url=None):
	browser_name = test_browser_name()
	kwargs = {}
	if browser_name == 'chrome':
		kwargs['headless'] = True
	return _TEST_BROWSERS[browser_name](url, **kwargs)

_TEST_BROWSERS = {
	'ie': start_ie,
	'firefox': start_firefox,
	'chrome': start_chrome
}