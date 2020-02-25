from helium.api import click, Alert, press, ENTER, write, TextField, Config, \
	wait_until
from helium.util.lang import TemporaryAttrValue
from helium.util.system import is_osx
from tests.api import BrowserAT, test_browser_name
from selenium.common.exceptions import UnexpectedAlertPresentException
from time import time, sleep
from unittest import skipIf

import selenium

class AlertAT():

	UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG = \
		"This command is not supported when an alert is present. To accept " \
		"the alert (this usually corresponds to clicking 'OK') use `Alert()." \
		"accept()`. To dismiss the alert (ie. 'cancel' it), use `Alert()." \
		"dismiss()`. If the alert contains a text field, you can use " \
		"write(...) to set its value. Eg.: `write('hi there!')`."

	def get_page(self):
		return 'test_alert.html'
	def get_link_to_open_alert(self):
		raise NotImplementedError()
	def get_expected_alert_text(self):
		raise NotImplementedError()
	def get_expected_alert_accepted_result(self):
		raise NotImplementedError()
	def get_expected_alert_dismissed_result(self):
		return self.get_expected_alert_accepted_result()
	def setUp(self):
		super(AlertAT, self).setUp()
		click(self.get_link_to_open_alert())
		wait_until(Alert().exists)
	def tearDown(self):
		if Alert().exists():
			# We need to call .accept() instead of .dismiss() here to work
			# around ChromeDriver bug 764:
			# https://code.google.com/p/chromedriver/issues/detail?id=764
			Alert().accept()
		super(AlertAT, self).tearDown()
	def test_alert_exists(self):
		self.assertTrue(Alert().exists())
	def test_alert_text_exists(self):
		self.assertTrue(Alert(self.get_expected_alert_text()).exists())
	def test_alert_text_not_exists(self):
		self.assertFalse(Alert('Wrong text').exists())
	def test_alert_text(self):
		self.assertEqual(self.get_expected_alert_text(), Alert().text)
	def test_alert_accept(self):
		Alert().accept()
		self._expect_result(self.get_expected_alert_accepted_result())
	@skipIf(
		is_osx() and test_browser_name() == 'chrome',
		"Chrome driver on OSX does not support dismissing JS alerts. " +
		"See: https://code.google.com/p/chromedriver/issues/detail?id=764"
	)
	def test_alert_dismiss(self):
		Alert().dismiss()
		self._expect_result(self.get_expected_alert_dismissed_result())
	def test_click_with_open_alert_raises_exception(self):
		with self.assertRaises(UnexpectedAlertPresentException) as cm:
			click("OK")
		msg = self._get_unhandled_alert_exception_msg(cm.exception)
		self.assertEqual(
			self.UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG, msg
		)
	def test_press_with_open_alert_raises_exception(self):
		with self.assertRaises(UnexpectedAlertPresentException) as cm:
			press(ENTER)
		msg = self._get_unhandled_alert_exception_msg(cm.exception)
		self.assertEqual(
			self.UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG, msg
		)
	"""
	This method waits up to one second for the given result to appear. It
	should not be needed but Chrome sometimes returns from .accept()/.dismiss()
	before the JavaScript in test_alert.html has set the corresponding
	result.
	"""
	def _expect_result(self, expected_result, timeout_secs=1):
		start_time = time()
		while time() < start_time + timeout_secs:
			actual_result = self.read_result_from_browser(timeout_secs=.3)
			if actual_result == expected_result:
				return
			sleep(0.2)
		self.assertEqual(expected_result, actual_result)
	def _get_unhandled_alert_exception_msg(self, e):
		if selenium.__version__ == '2.43.0':
			# Selenium 2.43.0 has a regression where accessing the .msg field
			# of an UnexpectedAlertPresentException raises an AttributeError -
			# See: https://code.google.com/p/selenium/issues/detail?id=7886
			return e.args[0]
		else:
			return e.msg

class AlertTest(AlertAT, BrowserAT):
	def get_link_to_open_alert(self):
		return 'Display alert'
	def get_expected_alert_text(self):
		return 'Hello World!'
	def get_expected_alert_accepted_result(self):
		return 'Alert displayed'

class ConfirmationDialogTest(AlertAT, BrowserAT):
	def get_link_to_open_alert(self):
		return 'Ask for confirmation'
	def get_expected_alert_text(self):
		return 'Proceed?'
	def get_expected_alert_accepted_result(self):
		return 'Accepted'
	def get_expected_alert_dismissed_result(self):
		return 'Dismissed'

class PromptTest(AlertAT, BrowserAT):
	def get_link_to_open_alert(self):
		return 'Prompt for value'
	def get_expected_alert_text(self):
		return 'Please enter a value'
	def get_expected_alert_accepted_result(self):
		return 'Value entered: '
	def test_write_value(self):
		write("1")
		Alert().accept()
		self._expect_result('Value entered: 1')
	def test_write_into_label_raises_exception(self):
		with self.assertRaises(UnexpectedAlertPresentException) as cm:
			write("3", into="Please enter a value")
		msg = self._get_unhandled_alert_exception_msg(cm.exception)
		self.assertEqual(
			self.UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG, msg
		)
	def test_write_into_text_field_raises_exception(self):
		with self.assertRaises(UnexpectedAlertPresentException) as cm:
			write("4", into=TextField("Please enter a value"))
		msg = self._get_unhandled_alert_exception_msg(cm.exception)
		self.assertEqual(
			self.UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG, msg
		)
	def test_write_into_non_existent_label_raises_exception(self):
		with self.assertRaises(UnexpectedAlertPresentException) as cm:
			write("5", into="Please enter a value")
		msg = self._get_unhandled_alert_exception_msg(cm.exception)
		self.assertEqual(
			self.UNEXPECTED_ALERT_PRESENT_EXCEPTION_MSG, msg
		)
	def test_write_into_alert(self):
		write("7", into=Alert())
		Alert().accept()
		self._expect_result("Value entered: 7")
	def test_write_into_labelled_alert(self):
		write("8", into=Alert(self.get_expected_alert_text()))
		Alert().accept()
		self._expect_result("Value entered: 8")
	def test_write_into_non_existent_alert(self):
		with TemporaryAttrValue(Config, 'implicit_wait_secs', 1):
			with self.assertRaises(LookupError):
				write("8", into=Alert("Non-existent"))