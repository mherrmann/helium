from datetime import date
from helium.config_file import HeliumConfigFile, CONFIG_FILE_NAME
from helium.api_impl import APIImpl
from helium.environment import ResourceLocator, is_development
from helium.util.system import get_canonical_os_name
from logging import StreamHandler, Formatter, getLogger
from os.path import pardir, join, exists, dirname, normpath
from uuid import uuid1, UUID

import helium
import logging.config

def get_application_context():
	global _APPLICATION_CONTEXT
	if _APPLICATION_CONTEXT is None:
		if is_development():
			_APPLICATION_CONTEXT = DevelopmentAPIConfig()
		else:
			_APPLICATION_CONTEXT = StandaloneAPIConfig()
	return _APPLICATION_CONTEXT
_APPLICATION_CONTEXT = None

class APIConfig(object):
	def __init__(self):
		self.initialize_logging()
		self.api_impl = None
	def initialize_logging(self):
		raise NotImplementedError()
	def APIImpl(self):
		if self.api_impl is None:
			self.api_impl = APIImpl(self.ResourceLocator())
		return self.api_impl
	def ResourceLocator(self):
		raise NotImplementedError()

class DevelopmentAPIConfig(APIConfig):
	def __init__(self):
		super(DevelopmentAPIConfig, self).__init__()
		self.resource_locator = None
	def initialize_logging(self):
		handler = StreamHandler()
		formatter = Formatter('%(name)s %(levelname)s: %(message)s')
		handler.setFormatter(formatter)
		getLogger('helium').addHandler(handler)
	def ResourceLocator(self):
		if self.resource_locator is None:
			proj_dir = \
				join(dirname(__file__), pardir, pardir, pardir, pardir, pardir)
			def proj_file(rel_path):
				return normpath(join(proj_dir, *rel_path.split('/')))
			return ResourceLocator(
				proj_file('../src/main/resources/base'),
				proj_file('../src/main/resources/' + get_canonical_os_name()),
				proj_file('../target')
			)
		return self.resource_locator

class StandaloneAPIConfig(APIConfig):
	def __init__(self):
		self.is_first_run = self.system_info = self.uuid = \
			self.helium_config_file = self.time_service = \
			self.resource_locator = None
		super(StandaloneAPIConfig, self).__init__()
	def initialize_logging(self):
		# This import isn't actually explicitly used in the implementation of
		# this function. However, it is still required: The imported module is
		# not referenced anywhere else in the Python code, which results in
		# the obfuscator failing to pick it up. Having the import here fixes
		# this.
		import helium.logging_
		logging_conf_path = self.ResourceLocator().locate('logging.conf')
		if exists(logging_conf_path):
			logging.config.fileConfig(logging_conf_path)
	def _get_build_date(self, checksum):
		# We're actually not interested in a checksum. It is merely a means of
		# encoding the build date in a non-obvious form, to prevent the user
		# from tampering while trying to work around licensing restrictions.
		year1 = int(checksum[13])
		year2 = int(checksum[3])
		year3 = int(checksum[18])
		year4 = int(checksum[29])
		month1 = int(checksum[11])
		month2 = int(checksum[2])
		day1 = int(checksum[17])
		day2 = int(checksum[31])
		year = year1 * 1000 + year2 * 100 + year3 * 10 + year4
		month = month1 * 10 + month2
		day = day1 * 10 + day2
		return date(year, month, day)
	def UUID(self):
		self._set_uuid_and_first_run()
		return self.uuid
	def IsFirstRun(self):
		self._set_uuid_and_first_run()
		return self.is_first_run
	def _set_uuid_and_first_run(self):
		uuid_file = self.ResourceLocator().locate('uuid')
		if exists(uuid_file):
			with open(uuid_file) as open_uuid_file:
				self.uuid = UUID(open_uuid_file.read())
				self.is_first_run = False
		else:
			result = uuid1()
			with open(uuid_file, 'w') as open_uuid_file:
				open_uuid_file.write(str(result))
			self.uuid = uuid1()
			self.is_first_run = True
	def HeliumConfigFile(self):
		if self.helium_config_file is None:
			self.helium_config_file = HeliumConfigFile(
				self.ResourceLocator().locate(CONFIG_FILE_NAME)
			)
		return self.helium_config_file
	def ResourceLocator(self):
		if self.resource_locator is None:
			helium_pkg_dir = dirname(helium.__file__)
			data_dir = join(helium_pkg_dir, 'data')
			helium_home = data_dir if exists(data_dir) else helium_pkg_dir
			self.resource_locator = ResourceLocator(
				helium_home, join(helium_home, get_canonical_os_name())
			)
		return self.resource_locator