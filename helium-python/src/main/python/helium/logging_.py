from helium.util.path import ensure_exists
from logging import FileHandler
from os.path import dirname, join, expanduser

class FileHandlerRelativeToPackageOrUserDir(FileHandler):
	def __init__(self, package_name_or_tilde, rel_path_cpts, mode='a'):
		if package_name_or_tilde == '~':
			log_file_path = join(
				expanduser(package_name_or_tilde), *rel_path_cpts
			)
			ensure_exists(dirname(log_file_path))
			FileHandler.__init__(self, log_file_path, mode=mode)
		else:
			package_dir = dirname(__import__(package_name_or_tilde).__file__)
			FileHandler.__init__(
				self, join(package_dir, *rel_path_cpts), mode=mode
			)