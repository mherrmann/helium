from helium.api_impl import APIImpl
from helium.environment import ResourceLocator, is_development
from helium.util.system import get_canonical_os_name
from os.path import pardir, join, exists, dirname, normpath

import helium

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
		self.api_impl = None
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
		self.resource_locator = None
		super(StandaloneAPIConfig, self).__init__()
	def ResourceLocator(self):
		if self.resource_locator is None:
			helium_pkg_dir = dirname(helium.__file__)
			data_dir = join(helium_pkg_dir, 'data')
			helium_home = data_dir if exists(data_dir) else helium_pkg_dir
			self.resource_locator = ResourceLocator(
				helium_home, join(helium_home, get_canonical_os_name())
			)
		return self.resource_locator