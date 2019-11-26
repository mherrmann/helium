from os import listdir
from os.path import basename, dirname, join, splitext
import imp
import sys

def initialize():
	global _INITIALIZED
	if not _INITIALIZED:
		if sys.version_info[0] == 3:
			sys.meta_path.insert(0, Py3Finder())
		_INITIALIZED = True

_INITIALIZED = False

def terminate():
	global _INITIALIZED
	if _INITIALIZED:
		py3_finders = [f for f in sys.meta_path if isinstance(f, Py3Finder)]
		for py3_finder in py3_finders:
			sys.meta_path.remove(py3_finder)
		_INITIALIZED = False

class Py3Finder(object):
	def find_module(self, mod_name, path=None):
		mod_file, mod_path, mod_info = find_module_rec(mod_name, path)
		try:
			is_package = mod_info[2] == imp.PKG_DIRECTORY
			if is_package:
				assert mod_path
				mod_path = join(mod_path, '__init__.py')
			if mod_path:
				mod_dir = dirname(mod_path) or '.'
				if basename(mod_path) + '3' in listdir(mod_dir):
					return Py3Loader(mod_name, mod_path + '3', mod_info)
		finally:
			if mod_file:
				mod_file.close()

def find_module_rec(name, path=None):
	module_hierarchy = list(name.split('.'))
	simple_name = module_hierarchy[-1]
	parent_modules = module_hierarchy[:-1]
	parent_mod_path = path
	for i, parent_mod_name in enumerate(parent_modules):
		full_parent_mod_name = '.'.join(parent_modules[:i+1])
		try:
			parent_module = sys.modules[full_parent_mod_name]
		except KeyError:
			module_desc = imp.find_module(parent_mod_name, parent_mod_path)
			parent_module = imp.load_module(full_parent_mod_name, *module_desc)
		parent_mod_path = parent_module.__path__
	return imp.find_module(simple_name, parent_mod_path)

if sys.version_info < (3, 1):
	class Py3Loader(object):
		def __init__(self, module_name, py3_file_name, module_description):
			self.module_name = module_name
			self.py3_file_name = py3_file_name
			self.module_description = module_description
		def load_module(self, module_name):
			if module_name != self.module_name:
				raise ImportError(
					"This loader cannot load module %s." % module_name
				)
			file_mode = self.module_description[1] or 'r'
			open_file = open(self.py3_file_name, file_mode)
			return imp.load_module(
				module_name, open_file, self.py3_file_name,
				self.module_description
			)
else:
	try:
		from importlib.abc import SourceLoader
	except ImportError:
		# Python 3.1
		from importlib.abc import PyLoader as SourceLoader

	class Py3Loader(SourceLoader):
		def __init__(self, module_name, py3_file_name, module_description):
			super(Py3Loader, self).__init__()
			self.module_name = module_name
			self.py3_file_name = py3_file_name
			self.module_description = module_description
		def get_data(self, file_path):
			return open(file_path, 'rb').read()
		def get_filename(self, module_name):
			if module_name != self.module_name:
				raise ImportError(
					'This loader cannot load module %s.' % module_name
				)
			return self.py3_file_name
		def source_path(self, module_name):
			# Copied from http://docs.python.org/3.3/library/importlib.html#
			# importlib.abc.PyLoader, for Python 3.1 compatibility.
			try:
				return self.get_filename(module_name)
			except ImportError:
				return None
		def is_package(self, module_name):
			# Copied from http://docs.python.org/3.3/library/importlib.html#
			# importlib.abc.PyLoader, for Python 3.1 compatibility.
			file_name = basename(self.get_filename(module_name))
			return splitext(file_name)[0] == '__init__'
		@classmethod
		def module_repr(cls, module):
			# Need to implement this for Python 3.3.
			try:
				from_file = ' from %r' % module.__file__
			except AttributeError:
				from_file = ''
			return '<module %r%s>' % (module.__name__, from_file)