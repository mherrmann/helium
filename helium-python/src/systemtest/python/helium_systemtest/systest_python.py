from helium_systemtest import run_commands_in_python, HeliumST

class PythonST(HeliumST):
	TEST_COMMANDS = [
		'from helium.api import *', 'start_chrome(headless=True)',
		'kill_browser()', 'exit()'
	]
	def test_python(self):
		code, output = run_commands_in_python(self.TEST_COMMANDS)
		self.assertEqual(0, code, msg=output)