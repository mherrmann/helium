"""
Gives information about the current operating system.
"""
from os import popen

import os
import sys

def is_windows():
	return sys.platform in ('win32', 'cygwin')

def is_osx():
	return sys.platform == 'darwin'

def is_linux():
	return sys.platform.startswith('linux')

def get_canonical_os_name():
	if is_windows():
		return 'win'
	elif is_osx():
		return 'macosx'
	elif is_linux():
		return 'linux'

def is_32_bit():
	"""
	Determines whether the current OS is 32- or 64 bit. Note that this may be
	different from the bitness of the Python interpreter or the CPU. Eg.
	is_32_bit() returns True on a 32 bit Windows running on a 64 bit processor.
	"""
	if is_windows():
		return 'PROGRAMFILES(X86)' not in os.environ
	assert is_linux() or is_osx()
	return _uname_m() in ('i386', 'i686')

def _uname_m():
	return popen('uname -m').read().strip()

def is_64_bit():
	if is_windows():
		return 'PROGRAMFILES(X86)' in os.environ
	assert is_linux() or is_osx()
	return _uname_m() in ('x86_64', 'ia64', 'amd64')