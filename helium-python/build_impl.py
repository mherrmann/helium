from build_environment import get_target_path, get_proj_path
from helium.util.path import ensure_exists, get_components
from os import walk, chmod, unlink, path
from os.path import abspath, join, dirname, splitext, pardir, basename
from shutil import copyfile, rmtree
from subprocess import check_call
from zipfile import ZipFile

import stat
import sys

class Platform(object):
	def __init__(self, name, classifier):
		self.name = name
		self.classifier = classifier
	@property
	def dist_dir(self):
		return get_target_path('dist-' + self.name)

def rmtree_safe(dir_path):
	def handle_rm_error(func, file_path, exc_info):
		# This gets called when rmtree cannot remove file with path file_path.
		# Assume the cause of the problem is that the file is readonly and
		# unlink it:
		chmod(file_path, stat.S_IWRITE)
		unlink(file_path)
	rmtree(dir_path, onerror=handle_rm_error)

def copy_package_sources(
		package, to, file_endings=('.py', '.so', '.json', '.xpi', '.js')
):
	copy_over(package.__path__[0], join(to, package.__name__), file_endings)

def copy_over(src_dir, dest_dir, file_endings=None):
	src_dir = abspath(src_dir)
	dest_dir = abspath(dest_dir)
	src_dir_parts = get_components(src_dir)
	for root, dirs, files in walk(src_dir):
		for file_name in files:
			if file_endings is None or splitext(file_name)[1] in file_endings:
				src_file_path = join(root, file_name)
				src_file_path_parts = get_components(src_file_path)
				dest_file_parts = src_file_path_parts[len(src_dir_parts):]
				dest_file_path = join(dest_dir, *dest_file_parts)
				ensure_exists(dirname(dest_file_path))
				copyfile(src_file_path, dest_file_path)

def abs_path(*rel_path):
	return abspath(join(__file__, pardir, *rel_path))

def copy_with_filtering(src_file, dest_dir, dict_, place_holder='${%s}'):
	ensure_exists(dest_dir)
	dest_file = join(dest_dir, basename(src_file))
	with open(src_file, 'r') as open_src_file:
		with open(dest_file, 'w') as open_dest_file:
			for line in open_src_file:
				new_line = line
				for key, value in dict_.items():
					new_line = new_line.replace(place_holder % key, value)
				open_dest_file.write(new_line)

def unzip_replacing_top_lvl_dir(zip_file_path, dest_dir):
	zip_file = ZipFile(zip_file_path, 'r')
	target_dir, _ = path.split(dest_dir)
	zip_file.extractall(target_dir)
	top_lvl_dir = sorted(zip_file.namelist(), key=lambda name: len(name))[0]
	unzipped_dir = join(target_dir, top_lvl_dir)
	copy_over(unzipped_dir, dest_dir)
	rmtree_safe(unzipped_dir)

def pip_install(dependency):
	check_call([sys.executable, '-m', 'pip', 'install', '-U', dependency, '-q'])