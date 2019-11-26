# -*- coding: utf-8 -*-
from helium.api import *
from . import GmailExampleTest

class GmailExampleHelium(GmailExampleTest):
	def test_chrome(self):
		start_chrome()
		self.check_attachment_sizes()
	def test_ie(self):
		start_ie()
		self.check_attachment_sizes()
	def test_firefox(self):
		start_firefox()
		self.check_attachment_sizes()
	def log_in_to_gmail(self, email_address, password):
		go_to("gmail.com")
		# Note: we need to check for existence of the link, not button!
		if Link("Sign in").exists():
			click("Sign in")
		write(email_address, into="Email")
		write(password, into="Password")
		stay_signed_in = CheckBox("Stay signed in")
		if stay_signed_in.is_checked():
			click(stay_signed_in)
		click("Sign in")
	def compose_new_message(self, to_email_address, subject):
		click(Button("COMPOSE"))
		write(to_email_address, into=TextField(to_right_of="To"))
		write(subject, into="Subject")
	def attach_file_to_message(self, file_path):
		drag_file(file_path, to="Drop files here")
	def get_uploaded_file_info(self):
		return Text(self.test_file_name + " (").value
	def send_message(self):
		click("Send")
		wait_until(Text("Your message has been sent.").exists, timeout_secs=120)
	def log_out_from_gmail(self):
		go_to('https://mail.google.com/mail/?logout')
	def delete_all_client_cookies(self):
		get_driver().delete_all_cookies()
	def open_sent_message(self):
		click(self.email_subject)
		wait_until(Text(self.test_file_name).exists)
		wait_until(lambda: not Text("Scanning for viruses...").exists())
	def assert_attachment_size_is(self, file_size_in_kb, file_size_in_mb):
		hover(self.test_file_name)
		file_size_in_kb_exists = Text(file_size_in_kb).exists()
		file_size_in_mb_exists = Text(file_size_in_mb).exists()
		self.assertTrue(file_size_in_kb_exists or file_size_in_mb_exists)
	def forward_message_to(self, email_address):
		click(Link("Forward"))
		write(email_address, into="To")
		click("Send")
		wait_until(Text("Your message has been sent.").exists)
	def tearDown(self):
		kill_browser()
		super(GmailExampleHelium, self).tearDown()