from helium._impl import APIImpl
from helium._impl.environment import ResourceLocator
from helium._impl.util.system import get_canonical_os_name
from os.path import join, dirname

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
			data_dir = join(
				dirname(__file__), 'webdrivers', get_canonical_os_name()
			)
			self.resource_locator = ResourceLocator(data_dir)
		return self.resource_locator