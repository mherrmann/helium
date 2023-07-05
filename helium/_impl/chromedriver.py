from get_chrome_driver import GetChromeDriver
from os.path import join, expanduser

import os
import platform

def install_matching_chromedriver():
	get_driver = GetChromeDriver()
	chrome_version = get_driver._GetChromeDriver__get_installed_chrome_version()
	cache_directory = _get_cache_directory()
	cached_chrome_version = \
		StringStoredInFile(join(cache_directory, 'chrome.version'))
	if chrome_version != cached_chrome_version.read():
		get_driver.auto_download(cache_directory, True)
		cached_chrome_version.write(chrome_version)
	return join(
		cache_directory,
		'chromedriver' + ('.exe' if platform.system() == 'Windows' else '')
	)

def _get_cache_directory():
	system = platform.system()
	if system == 'Windows':
		return join(os.getenv('LOCALAPPDATA'), 'Cache', 'Helium')
	elif system == 'Darwin':
		return expanduser('~/Library/Caches/Helium')
	else:
		cache_home = os.getenv('XDG_CACHE_HOME', expanduser('~/.cache'))
		return join(cache_home, 'helium')

class StringStoredInFile:
	def __init__(self, path):
		self.path = path
	def read(self):
		try:
			with open(self.path) as f:
				return f.read()
		except FileNotFoundError:
			return None
	def write(self, value):
		with open(self.path, 'w') as f:
			f.write(value)
