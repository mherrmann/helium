"""
Gives information about the current operating system.
"""
import sys

def is_windows():
	return sys.platform in ('win32', 'cygwin')

def is_mac():
	return sys.platform == 'darwin'

def is_linux():
	return sys.platform.startswith('linux')

def get_canonical_os_name():
	if is_windows():
		return 'windows'
	elif is_mac():
		return 'mac'
	elif is_linux():
		return 'linux'