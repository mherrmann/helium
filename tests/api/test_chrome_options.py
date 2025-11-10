from helium import start_chrome, kill_browser
from os.path import join
from tests.api import test_browser_name
from unittest import TestCase, skipIf
from selenium.webdriver.chrome.options import Options as ChromeOptions
from contextlib import contextmanager

import json

@skipIf(test_browser_name() != 'chrome', 'Only run this test for Chrome')
class ChromeOptionsTest(TestCase):
	def test_start_chrome_does_not_override_custom_prefs(self):
		test_value = 'download.default_directory'
		prefs = {'download.default_directory': test_value}
		with chrome_with_prefs(prefs) as actual_prefs:
			self.assertEqual(
				test_value,
				actual_prefs['download']['default_directory']
			)
	def test_start_chrome_respects_custom_password_leak_detection(self):
		prefs = {'profile.password_manager_leak_detection': True}
		with chrome_with_prefs(prefs) as actual_prefs:
			self.assertTrue(
				actual_prefs['profile']['password_manager_leak_detection']
			)

@contextmanager
def chrome_with_prefs(prefs):
	try:
		options = ChromeOptions()
		options.add_experimental_option('prefs', prefs)
		driver = start_chrome(headless=True, options=options)
		user_data_dir = driver.capabilities['chrome']['userDataDir']
		prefs_file = join(user_data_dir, 'Default', 'Preferences')
		with open(prefs_file, 'r') as f:
			yield json.load(f)
	finally:
		kill_browser()