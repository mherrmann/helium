from helium_systemtest.environment import get_test_library
from subprocess import Popen, STDOUT
from tempfile import TemporaryFile
from unittest import TestCase

import os

def run_commands_in_python(commands, python_binary="python"):
	return run_python_with_helium_library(
		['-c', ';'.join(commands)], python_binary=python_binary
	)

def run_python_with_helium_library(args, python_binary="python", cwd=None):
	return run_python(
		args, python_binary=python_binary, cwd=cwd,
		env={'PYTHONPATH': get_test_library()}
	)

def run_python(args, python_binary="python", cwd=None, env=None):
	"""
	We would like to be able to use ApplicationConsoleTester or Popen with
	stdout=PIPE here, however if we do that, we never receive any output.
	This may be a Python bug.
	To avoid this problem, we redirect stdout and stderr to a normal file.
	"""
	if env is None:
		env = {}
	with TemporaryFile('r+') as open_stdout_file:
		full_env = os.environ.copy()
		for key in env:
			set_item_ignore_case(full_env, key, env[key])
		python = Popen(
			[python_binary] + list(args),
			stdout=open_stdout_file, stderr=STDOUT, cwd=cwd, env=full_env
		)
		return_code = python.wait()
		open_stdout_file.seek(0)
		output = open_stdout_file.read()
		return return_code, output

def set_item_ignore_case(dictionary, key, value):
	for actual_key in dictionary:
		if actual_key.lower() == key.lower():
			dictionary[actual_key] = value

class HeliumST(TestCase):
	pass