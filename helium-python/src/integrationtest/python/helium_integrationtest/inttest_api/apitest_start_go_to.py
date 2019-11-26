from helium.api import go_to
from helium_integrationtest.environment import get_it_file_url
from helium_integrationtest.inttest_api import start_browser, should_run
from os import path
from unittest import TestCase, skipIf

@skipIf(not should_run(), 'IE on Windows only')
class StartGoToIT(TestCase):
	def setUp(self):
		self.url = get_it_file_url('inttest_start_go_to.html')
		self.driver = None
	def test_go_to(self):
		self.driver = start_browser()
		go_to(self.url)
		self.assertUrlEquals(self.url, self.driver.current_url)
	def assertUrlEquals(self, expected, actual):
		expected = unicode(path.normpath(expected.lower().replace('\\', '/')))
		actual = unicode(path.normpath(actual.lower().replace('\\', '/')))
		self.assertEquals(expected, actual)
	def test_start_with_url(self):
		self.driver = start_browser(self.url)
		self.assertUrlEquals(self.url, self.driver.current_url)
	def tearDown(self):
		if self.driver is not None:
			self.driver.quit()