from helium._impl import chromedriver
from helium._impl.chromedriver import install_matching_chromedriver
from os import access, X_OK
from os.path import getctime
from tempfile import TemporaryDirectory
from unittest import TestCase


class InstallMatchingChromeDriverTest(TestCase):
	def test_install_matching_chromedriver(self):
		driver_path = install_matching_chromedriver()
		self.assertTrue(access(driver_path, X_OK))
	def test_caching(self):
		driver_path = install_matching_chromedriver()
		ctime = getctime(driver_path)
		self.assertEqual(install_matching_chromedriver(), driver_path)
		self.assertEqual(ctime, getctime(driver_path))
	def setUp(self):
		self.temp_dir = TemporaryDirectory()
		self._original_get_cache_directory = chromedriver._get_cache_directory
		chromedriver._get_cache_directory = lambda: self.temp_dir.name
	def tearDown(self):
		chromedriver._get_cache_directory = self._original_get_cache_directory
		self.temp_dir.cleanup()
