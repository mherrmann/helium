from helium_systemtest import run_commands_in_python, HeliumST
from helium_integrationtest.util import is_osx
from unittest import skipIf

class PythonVersionsST(HeliumST):
	TEST_COMMANDS = [
		'from helium.api import *', 'start_chrome()', 'kill_browser()', 'exit()'
	]
	@skipIf(is_osx(), "On OSX there is only one version of Python (64bit).")
	def test_python_26(self):
		self._test_python_version('26')
	def test_python_26x64(self):
		self._test_python_version('26x64')
	def test_python_27x64(self):
		self._test_python_version('27x64')
	@skipIf(is_osx(), "Currently don't have Python3 interpreter on OSX.")
	def test_python_33(self):
		self._test_python_version('33')
	@skipIf(is_osx(), "Currently don't have Python3 interpreter on OSX.")
	def test_python_33x64(self):
		self._test_python_version('33x64')
	def _test_python_version(self, version_no):
		code, output = \
			run_commands_in_python(self.TEST_COMMANDS, 'python' + version_no)
		self.assertEquals(0, code, msg=output)
