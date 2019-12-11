from helium_integrationtest.environment import get_helium_package_source
from imp import load_source
from subprocess import Popen, PIPE, STDOUT

import os
import sys

util_system = load_source(
	'system',  get_helium_package_source('helium', 'util', 'system.py')
)
is_windows = util_system.is_windows
is_osx = util_system.is_osx

util_lang = load_source(
	'lang',  get_helium_package_source('helium', 'util', 'lang.py')
)
TemporaryAttrValue = util_lang.TemporaryAttrValue

environment = load_source(
	'environment',  get_helium_package_source('helium', 'environment.py')
)
ResourceLocator = environment.ResourceLocator

class InSubProcess(object):
	"""
	Important: You need to call `synchronize_with_parent_process()` in your sub-
	class's `main` method.
	"""
	def __init__(self):
		self.sub_process = None
	def __enter__(self):
		self.sub_process = Popen(
			['python', '-m', self.__class__.__module__],
			stdin=PIPE, stdout=PIPE, stderr=STDOUT, universal_newlines=True,
			cwd=os.getcwd(), env=os.environ
		)
		self.wait_for_sub_process()
	def wait_for_sub_process(self):
		line = self.sub_process.stdout.readline()
		assert 'Sub process started.\n' == line, \
			'Sub process invocation failed:\n' + \
			line + self.sub_process.stdout.read()
	@classmethod
	def synchronize_with_parent_process(cls):
		# Let parent process know we've started:
		sys.stdout.write('Sub process started.\n')
		sys.stdout.flush()
		# Wait until parent process is finished:
		input('')
	def __exit__(self, *args):
		self.sub_process.stdin.write('\n')
		self.sub_process.stdin.flush()
		self.sub_process.wait()
		assert self.sub_process.returncode == 0, \
			repr(self.sub_process.returncode)