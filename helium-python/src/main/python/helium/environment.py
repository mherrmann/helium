from base64 import b64decode
from os.path import normpath, exists, join, abspath

def is_development():
	source_code_subpath_b64 = \
		'aGVsaXVtLXB5dGhvbi9zcmMvbWFpbi9weXRob24='.encode('ascii')
	source_code_subpath = b64decode(source_code_subpath_b64).decode('ascii')
	return normpath(source_code_subpath) in normpath(abspath(__file__))

class ResourceLocator(object):
	def __init__(self, *root_dirs):
		self.root_dirs = root_dirs
	def locate(self, *rel_path_components):
		for root_dir in self.root_dirs:
			location = self.construct_path(root_dir, *rel_path_components)
			if exists(location):
				return location
		return self.construct_path(self.root_dirs[0], *rel_path_components)
	def construct_path(self, *rel_path_components):
		return normpath(join(*rel_path_components))