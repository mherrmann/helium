from helium._impl.chromedriver import install_matching_chromedriver
from os import access, X_OK
from tempfile import TemporaryDirectory
from unittest import TestCase


class InstallMatchingChromeDriverTest(TestCase):
	def test_install_matching_chromedriver(self):
		driver_path = self._install_matching_chromedriver()
		self.assertTrue(access(driver_path, X_OK))
	# Would also like to test caching here; But webdriver-manager always
	# modifies the file.
	def _install_matching_chromedriver(self):
		return install_matching_chromedriver(self.temp_dir.name)
	def setUp(self):
		self.temp_dir = TemporaryDirectory()
	def tearDown(self):
		self.temp_dir.cleanup()
