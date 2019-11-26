from helium_integrationtest.samples.gmail.gmail_helium import GmailExampleHelium
from helium.api import *
from selenium.common.exceptions import TimeoutException

class DeleteTestEmails(GmailExampleHelium):
	def test_chrome(self):
		pass
	def test_firefox(self):
		pass
	def test_ie(self):
		pass
	def test_cleanup(self):
		start_chrome()
		self.clean_emails_for_account(self.email_address_1, self.password_1)
		self.clean_emails_for_account(self.email_address_2, self.password_2)
	def clean_emails_for_account(self, email_address, password):
		self.log_in_to_gmail(email_address, password)
		write(
			self.email_subject,
			into=TextField(to_left_of=Button('Google Search'))
		)
		click(Button("Google Search"))
		try:
			wait_until(
				Text('No messages matched your search.').exists, timeout_secs=3
			)
		except TimeoutException:
			click(Button("Select"))
			click(Button("Delete"))
		from time import sleep
		sleep(2)
		self.log_out_from_gmail()
		self.delete_all_client_cookies()