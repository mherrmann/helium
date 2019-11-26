from helium_integrationtest.util import InSubProcess
from helium.api import start_chrome
from helium_integrationtest.util import is_windows
from helium_integrationtest.inttest_api.apitest_kill_service_at_exit import \
	KillServiceAtExitAT
from unittest import skip, TestCase

@skip('This test fails on recent versions of Chrome')
class KillServiceAtExitChromeIT(KillServiceAtExitAT, TestCase):
	"""
	This test fails when run from PyCharm. The reason for this is that
	silent-chromedriver.exe assigns its subprocess chromedriver.exe to a Job
	Object via AssignProcessToJobObject in silent-chromedriver.cpp. This fails
	with error code 5 (access denied) when we run from PyCharm. On the command
	line (`mvn verify`) however, it works. It seems to be common that
	AssignProcessToJobObject fails depending on the process' environment. See:
		http://stackoverflow.com/questions/89588/assignprocesstojobobject-fails-
		with-access-denied-error-when-running-under-the
	"""
	def get_service_process_names(self):
		if is_windows():
			return ['silent-chromedriver.exe', 'chromedriver.exe']
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