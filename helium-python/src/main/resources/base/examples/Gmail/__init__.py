# -*- coding: utf-8 -*-
from ConfigParser import RawConfigParser
from os import path, urandom, remove
from random import randint
from unittest import TestCase
import re

MIN_FILE_SIZE_MB = 2
MAX_FILE_SIZE_MB = 5

class GmailExampleTest(TestCase):
	def check_attachment_sizes(self):
		# 2.:
		self.log_in_to_gmail(self.email_address_1, self.password_1)
		# 3.:
		self.compose_new_message(self.email_address_2, self.email_subject)
		# 4.:
		self.attach_file_to_message(self.test_file_path)
		# 5.:
		uploaded_file_info = self.get_uploaded_file_info()
		file_sizes = self.get_attachment_size(uploaded_file_info)
		# 6.:
		self.send_message()
		# 7.:
		self.log_out_from_gmail()
		# 8.:
		self.delete_all_client_cookies()
		# 9.:
		self.log_in_to_gmail(self.email_address_2, self.password_2)
		# 10.:
		self.open_sent_message()
		# 11.:
		self.assert_attachment_size_is(*file_sizes)
		# 12.:
		self.forward_message_to(self.email_address_1)
		# 13.:
		self.log_out_from_gmail()
		# 14.:
		self.delete_all_client_cookies()
		# 15.:
		self.log_in_to_gmail(self.email_address_1, self.password_1)
		# 16.:
		self.open_sent_message()
		# 17.:
		self.assert_attachment_size_is(*file_sizes)
	def log_in_to_gmail(self, email_address, password):
		raise NotImplementedError()
	def compose_new_message(self, to_email_address, subject):
		raise NotImplementedError()
	def attach_file_to_message(self, file_path):
		raise NotImplementedError()
	def get_uploaded_file_info(self):
		raise NotImplementedError()
	def send_message(self):
		raise NotImplementedError()
	def log_out_from_gmail(self):
		raise NotImplementedError()
	def delete_all_client_cookies(self):
		raise NotImplementedError()
	def open_sent_message(self):
		raise NotImplementedError()
	def assert_attachment_size_is(self, file_size_in_kb, file_size_in_mb):
		raise NotImplementedError()
	def forward_message_to(self, email_address):
		raise NotImplementedError()
	def open_forwarded_message(self):
		raise NotImplementedError()
	def get_attachment_size(self, uploaded_file_info, test_file_name=None):
		if test_file_name is None:
			test_file_name = self.test_file_name
		file_size_kb = \
			self._get_attachment_size_kb(uploaded_file_info, test_file_name)
		file_size_in_kb = '%dK' % file_size_kb
		file_size_mb = file_size_kb / 1024.0
		if int(file_size_mb) == file_size_mb:
			file_size_in_mb = '%d MB' % int(file_size_mb)
		else:
			file_size_in_mb = '%.1f MB' % file_size_mb
		return file_size_in_kb, file_size_in_mb
	def _get_attachment_size_kb(self, uploaded_file_info, test_file_name=None):
		if test_file_name is None:
			test_file_name = self.test_file_name
		file_info_re = test_file_name + r' \(([^\)]+)\)'
		file_size = re.match(file_info_re, uploaded_file_info).group(1)
		file_size_re = r'([0-9]+)[, ]?([0-9]*)(?:\.00)? ?KB?'
		file_size_match = re.match(file_size_re, file_size)
		return int(file_size_match.group(1) + file_size_match.group(2))
	def setUp(self):
		self.email_subject = 'TP1-Gmail attachment size reporting'
		self.parameters_file_name = 'parameters.ini'
		self._read_parameters_file()
		self.test_file_path = path.expanduser(
			r"~\Desktop\%s" % self.test_file_name
		)
		self._create_test_file()
		# Check pre-conditions:
		assert path.exists(self.test_file_path), \
			"Test file %s does not exist. Please create." % self.test_file_path
		MB = 1024 * 1024
		assert path.getsize(self.test_file_path) > MIN_FILE_SIZE_MB * MB, \
			"Test file %s does not have the required minimum size of %dMB" % (
				self.test_file_path, MIN_FILE_SIZE_MB
			)
	def _read_parameters_file(self):
		params_file_parser = RawConfigParser()
		params_file_parser.read(self.parameters_file_name)
		self.email_address_1 = params_file_parser.get(
			'parameters', 'email_address_1'
		)
		self.password_1 = params_file_parser.get('parameters', 'password_1')
		self.email_address_2 = params_file_parser.get(
			'parameters', 'email_address_2'
		)
		self.password_2 = params_file_parser.get('parameters', 'password_2')
		self.test_file_name = params_file_parser.get(
			'parameters', 'test_file_name'
		)
	def _create_test_file(self):
		MB = 1024 * 1024
		test_file_size = randint(MIN_FILE_SIZE_MB * MB, MAX_FILE_SIZE_MB * MB)
		with open(self.test_file_path, 'wb') as test_file:
			test_file.write(urandom(test_file_size))
	def tearDown(self):
		if path.exists(self.test_file_path):
			remove(self.test_file_path)

class GetAttachmentSizeKBTest(TestCase):
	def setUp(self):
		self.test_instance = GmailExampleTest('setUp')
		self.test_instance.setUp()
	def test_1(self):
		self.assertEquals(
			4254, self.test_instance._get_attachment_size_kb(
				'test_file.dat (4,254K)', 'test_file.dat'
			)
		)
	def test_2(self):
		self.assertEquals(
			4254, self.test_instance._get_attachment_size_kb(
				'test_file.dat (4,254.00K)', 'test_file.dat'
			)
		)
	def test_3(self):
		self.assertEquals(
			3650, self.test_instance._get_attachment_size_kb(
				'test_file.dat (3,650K)', 'test_file.dat'
			)
		)
	def test_get_attachment_size(self):
		self.assertEquals(
			('3650K', '3.6 MB'), self.test_instance.get_attachment_size(
				'test_file.dat (3,650K)', 'test_file.dat'
			)
		)
	def test_ie(self):
		self.assertEquals(
			2911, self.test_instance._get_attachment_size_kb(
				'chromedriver.zip (2,911.00K)', 'chromedriver.zip'
			)
		)
	def test_ff_chrome(self):
		self.assertEquals(
			2911, self.test_instance._get_attachment_size_kb(
				'chromedriver.zip (2,911K)', 'chromedriver.zip'
			)
		)
	def test_tytus_ff_chrome_1(self):
		self.assertEquals(
			5739, self.test_instance._get_attachment_size_kb(
				'helium.zip (5 739K)', test_file_name='helium.zip'
			)
		)
	def test_tytus_ie_1(self):
		self.assertEquals(
			5739, self.test_instance._get_attachment_size_kb(
				'helium.zip (5 739.00K)', test_file_name='helium.zip'
			)
		)
	def test_tytus_ff_chrome_2(self):
		self.assertEquals(
			5739, self.test_instance._get_attachment_size_kb(
				'helium.zip (5 739 KB)', test_file_name='helium.zip'
			)
		)
	def test_tytus_ie_2(self):
		self.assertEquals(
			5739, self.test_instance._get_attachment_size_kb(
				'helium.zip (5 739.00 KB)', test_file_name='helium.zip'
			)
		)
	def tearDown(self):
		self.test_instance.tearDown()