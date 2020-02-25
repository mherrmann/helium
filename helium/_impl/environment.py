from os.path import normpath, exists, join

class ResourceLocator:
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