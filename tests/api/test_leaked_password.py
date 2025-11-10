from helium import write, click, Text, wait_until
from tests.api import BrowserAT

class LeakedPasswordTest(BrowserAT):
	def get_page(self):
		return 'test_leaked_password.html'
	def test_submit_leaked_password(self):
		# Chrome 140.0.7339.185 or earlier introduced password leak detection.
		# Writing leaked credentials into an input field sometimes brings up a
		# browser notification "The password you just used was found in a data
		# breach". Test that Helium prevents this:
		write('testuser', into='Username')
		write('testpassword', into='Password')
		click('Submit')
		wait_until(Text('You logged in with testuser:testpassword').exists)