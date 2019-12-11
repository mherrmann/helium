from helium_integrationtest.util import InSubProcess
from helium.api import start_chrome
from helium_integrationtest.util import is_windows
from helium_integrationtest.inttest_api.apitest_kill_service_at_exit import \
	KillServiceAtExitAT
from unittest import skip, TestCase

@skip('This test fails on recent versions of Chrome')
class KillServiceAtExitChromeIT(KillServiceAtExitAT, TestCase):
	def get_service_process_names(self):
		if is_windows():
			return ['chromedriver.exe']
		return ['chromedriver']
	def get_browser_process_name(self):
		return 'chrome' + ('.exe' if is_windows() else '')
	def start_browser_in_sub_process(self):
		with ChromeInSubProcess():
			pass

class ChromeInSubProcess(InSubProcess):
	@classmethod
	def main(cls):
		start_chrome()
		cls.synchronize_with_parent_process()

if __name__ == '__main__':
	ChromeInSubProcess.main()