from helium._impl import APIImpl

def get_application_context():
	global _APPLICATION_CONTEXT
	if _APPLICATION_CONTEXT is None:
		_APPLICATION_CONTEXT = ApplicationContext()
	return _APPLICATION_CONTEXT

_APPLICATION_CONTEXT = None

class ApplicationContext:
	def __init__(self):
		self.api_impl = None
	def APIImpl(self):
		if self.api_impl is None:
			self.api_impl = APIImpl()
		return self.api_impl