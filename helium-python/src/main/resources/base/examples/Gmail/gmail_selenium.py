from . import GmailExampleTest
from selenium.common.exceptions import NoAlertPresentException, \
	WebDriverException
from selenium.webdriver import *
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions

class GmailExampleSelenium(GmailExampleTest):
	def test_chrome(self):
		self.driver = Chrome()
		self.driver.implicitly_wait(10)
		self.check_attachment_sizes()
	def test_ie(self):
		self.driver = Ie()
		self.driver.implicitly_wait(10)
		self.check_attachment_sizes()
	def test_firefox(self):
		self.driver = Firefox()
		self.driver.implicitly_wait(10)
		self.check_attachment_sizes()
	def log_in_to_gmail(self, email_address, password):
		# Note: Have to provide full URL here to avoid WebDriverException
		self.driver.get("http://www.gmail.com")
		sign_in_links = \
			self.driver.find_elements_by_link_text('Sign in')
		if sign_in_links:
			sign_in_links[0].click()
		self.driver.find_element_by_id('Email').send_keys(email_address)
		self.driver.find_element_by_id('Passwd').send_keys(password)
		stay_signed_in = self.driver.find_element_by_id('PersistentCookie')
		if stay_signed_in.is_selected():
			stay_signed_in.click()
		self.driver.find_element_by_id('signIn').click()
	def compose_new_message(self, to_email_address, subject):
		self.driver.find_element_by_xpath(
			"//div[@role='button' and text()='COMPOSE']"
		).click()
		self.driver.find_element_by_name('to').send_keys(to_email_address)
		self.driver.find_element_by_name('subjectbox').send_keys(subject)
	def attach_file_to_message(self, file_path):
		# find_elements_by_xpath("//input[@type='file']") returns []
		# We also can't click on "Attach files", as this launches a file dialog
		# that we can't close with Selenium. We therefore drag and drop.
		file_input = self._create_file_input_element()
		file_input.send_keys(file_path)
		self._dispatch_file_drag_event('dragenter', 'document', file_input)
		self._dispatch_file_drag_event('dragover', 'document', file_input)
		drag_target = \
			self.driver.find_element_by_xpath("//div[text()='Drop files here']")
		self._dispatch_file_drag_event('drop', drag_target, file_input)
		self.driver.execute_script(
			"arguments[0].parentNode.removeChild(arguments[0]);",
			file_input
		)
	def _create_file_input_element(self):
		# The input needs to be visible to Selenium to allow sending keys to it
		# in Firefox and IE.
		# According to http://stackoverflow.com/questions/6101461/
		# Selenium criteria whether an element is visible or not are the
		# following:
		#  - visibility != hidden
		#  - display != none (is also checked against every parent element)
		#  - opacity != 0
		#  - height and width are both > 0
		#  - for an input, the attribute type != hidden
		# So let's make sure its all good!
		return self.driver.execute_script(
			"var input = document.createElement('input');"
			"input.type = 'file';"
			"input.style.display = 'block';"
			"input.style.opacity = '1';"
			"input.style.visibility = 'visible';"
			"input.style.height = '1px';"
			"input.style.width = '1px';"
			"if (document.body.childElementCount > 0) { "
			"   document.body.insertBefore(input, document.body.childNodes[0]);"
			"} else { "
			"   document.body.appendChild(input);"
			"}"
			"return input;"
		)
	def _dispatch_file_drag_event(self, event_name, to, file_input_element):
		script = \
			"var files = arguments[0].files;" \
			"var items = [];" \
			"var types = [];" \
			"for (var i = 0; i < files.length; i++) {" \
			"   items[i] = {kind: 'file', type: files[i].type};" \
			"   types[i] = 'Files';" \
			"}" \
			"var event = document.createEvent('CustomEvent');" \
			"event.initCustomEvent(arguments[1], true, true, 0);" \
			"event.dataTransfer = {" \
			"	files: files," \
			"	items: items," \
			"	types: types" \
			"};" \
			"arguments[2].dispatchEvent(event);"
		if isinstance(to, basestring):
			script = script.replace('arguments[2]', to)
			args = file_input_element, event_name,
		else:
			args = file_input_element, event_name, to
		self.driver.execute_script(script, *args)
	def get_uploaded_file_info(self):
		return self.driver.find_element_by_xpath(
			"//div[starts-with(@aria-label, 'Uploading attachment: %s')]" %
			self.test_file_name
		).text
	def send_message(self):
		self.driver.find_element_by_xpath("//div[text()='Send']").click()
		wait = WebDriverWait(self.driver, 120)
		xpath = "//div[starts-with(text(), 'Your message has been sent.')]"
		wait.until(
			expected_conditions.presence_of_element_located((By.XPATH, xpath))
		)
	def log_out_from_gmail(self):
		self.driver.get('https://mail.google.com/mail/?logout')
		try:
			self.driver.switch_to_alert().accept()
		except (NoAlertPresentException, WebDriverException):
			pass
		finally:
			self.driver.switch_to.default_content()
	def delete_all_client_cookies(self):
		self.driver.delete_all_cookies()
	def open_sent_message(self):
		self.driver.find_element_by_xpath(
			"//span/b[text()='%s']" % self.email_subject
		).click()
		WebDriverWait(self.driver, 10).until(
			expected_conditions.presence_of_element_located((
				By.XPATH, "//*[text()='%s']" % self.test_file_name
			))
		)
		# Without this wait, Selenium types the recipient's address for the
		# forwarding too quickly, and Gmail misses some characters:
		def virus_scan_complete(driver):
			return not driver.find_elements_by_xpath(
				"//*[starts-with(., 'Scanning for viruses')]"
			)
		self.driver.implicitly_wait(0)
		WebDriverWait(self.driver, 10).until(virus_scan_complete)
		self.driver.implicitly_wait(10)
	def assert_attachment_size_is(self, file_size_in_kb, file_size_in_mb):
		# With or without Google Drive:
		test_file = self.driver.find_element_by_xpath(
			"//td/b[starts-with(text(), '%s')]" % self.test_file_name + ' | ' +\
			"//span[text()='%s']" % self.test_file_name
		)
		ActionChains(self.driver).move_to_element(test_file).perform()
		self.driver.implicitly_wait(0)
		file_size_in_kb_exists = len(self.driver.find_elements_by_xpath(
			"//td[contains(., '%s')]" % file_size_in_kb
		)) > 0
		file_size_in_mb_exists = len(self.driver.find_elements_by_xpath(
			"//td[contains(., '%s')]" % file_size_in_mb
		)) > 0
		self.driver.implicitly_wait(10)
		self.assertTrue(file_size_in_kb_exists or file_size_in_mb_exists)
	def forward_message_to(self, email_address):
		# Got ElementNotVisibleException before adding "@role='link'" here.
		self.driver.find_element_by_xpath(
			"//span[text()='Forward' and @role='link']"
		).click()
		self.driver.find_element_by_name('to').send_keys(email_address)
		self.driver.find_element_by_xpath("//div[text()='Send']").click()
		wait = WebDriverWait(self.driver, 10)
		xpath = "//div[starts-with(text(), 'Your message has been sent.')]"
		wait.until(
			expected_conditions.presence_of_element_located((By.XPATH, xpath))
		)
	def tearDown(self):
		self.driver.quit()
		super(GmailExampleSelenium, self).tearDown()