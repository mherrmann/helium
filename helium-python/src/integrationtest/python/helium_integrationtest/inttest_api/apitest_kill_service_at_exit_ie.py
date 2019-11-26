from helium_integrationtest.util import InSubProcess
from helium.api import start_ie
from helium_integrationtest.inttest_api import test_browser_name
from helium_integrationtest.inttest_api.apitest_kill_service_at_exit import \
	KillServiceAtExitAT
from unittest import skipIf, TestCase

@skipIf(
	test_browser_name() != 'ie', "Only run this test for TEST_BROWSER ie."
)
class KillServiceAtExitIEIT(KillServiceAtExitAT, TestCase):
	def get_service_process_names(self):
		return ["IEDriverServer.exe"]
	def get_browser_process_name(self):
		return "iexplore.exe"
	def start_browser_in_sub_process(self):
		with IeInSubProcess():
			pass

class IeInSubProcess(InSubProcess):
	@classmethod
	def main(cls):
		start_ie()
		cls.synchronize_with_parent_process()

if __name__ == '__main__':
	IeInSubProcess.main()