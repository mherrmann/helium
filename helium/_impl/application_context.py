from helium._impl import APIImpl
from helium.environment import ResourceLocator
from helium.util.system import get_canonical_os_name
from os.path import join, dirname

import helium

def get_application_context():
	global _APPLICATION_CONTEXT
	if _APPLICATION_CONTEXT is None:
		_APPLICATION_CONTEXT = ApplicationContext()
	return _APPLICATION_CONTEXT

_APPLICATION_CONTEXT = None

class ApplicationContext:
	def __init__(self):
		self.api_impl = self.resource_locator = None
	def APIImpl(self):
		if self.api_impl is None:
			self.api_impl = APIImpl(self.ResourceLocator())
		return self.api_impl
	def ResourceLocator(self):
		if self.resource_locator is None:
			helium_pkg_dir = dirname(helium.__file__)
			data_dir = join(helium_pkg_dir, 'webdrivers', get_canonical_os_name())
			self.resource_locator = ResourceLocator(data_dir)
		return self.resource_locator