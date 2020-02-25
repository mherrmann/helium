class TemporaryAttrValue:
	def __init__(self, obj, attr, value):
		self.obj = obj
		self.attr = attr
		self.value = value
		self.value_before = None
	def __enter__(self):
		self.value_before = getattr(self.obj, self.attr)
		setattr(self.obj, self.attr, self.value)
	def __exit__(self, *_):
		setattr(self.obj, self.attr, self.value_before)
		self.value_before = None

def isbound(method_or_fn):
	try:
		return method_or_fn.__self__ is not None
	except AttributeError: # Python 3
		try:
			return method_or_fn.__self__ is not None
		except AttributeError:
			return False