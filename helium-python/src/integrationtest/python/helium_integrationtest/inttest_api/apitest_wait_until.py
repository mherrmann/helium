from helium.api import click, wait_until, Text
from helium_integrationtest.inttest_api import BrowserAT
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.expected_conditions import \
	presence_of_element_located
from time import time

class WaitUntilIT(BrowserAT):
	def get_page(self):
		return 'inttest_wait_until.html'
	def test_wait_until_text_exists(self):
		click("Click me!")
		start_time = time()
		wait_until(Text("Success!").exists)
		end_time = time()
		self.assertGreaterEqual(end_time - start_time, 0.9)
	def test_wait_until_presence_of_element_located(self):
		click("Click me!")
		start_time = time()
		wait_until(presence_of_element_located((By.ID, "result")))
		end_time = time()
		self.assertGreaterEqual(end_time - start_time, 0.9)
	def test_wait_until_lambda_expires(self):
		with self.assertRaises(TimeoutException):
			wait_until(lambda: False, timeout_secs=1)
	def test_wait_until_lambda_with_driver_expires(self):
		with self.assertRaises(TimeoutException):
			wait_until(lambda driver: False, timeout_secs=0.1)
