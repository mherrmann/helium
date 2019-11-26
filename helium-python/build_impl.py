"""
Helper functions for build.py.
"""
from build_environment import get_target_path, get_proj_path
from ConfigParser import RawConfigParser
from datetime import date
from helium.util.path import ensure_exists, get_components
from os import listdir, walk, chmod, unlink, devnull, remove, path
from os.path import abspath, join, isfile, isdir, exists, dirname, splitext, \
	pardir, basename
from random import randint
from shutil import copyfile, rmtree
from subprocess import check_call, check_output, STDOUT, CalledProcessError
from StringIO import StringIO
from zipfile import ZipFile

import pip
import stat
import sys

class Platform(object):
	def __init__(self, name, classifier):
		self.name = name
		self.classifier = classifier
	@property
	def dist_dir(self):
		return get_target_path('dist-' + self.name)

def copy_py_files(source_dir, dest_dir):
	ensure_exists(dest_dir)
	for src_file_name in listdir(source_dir):
		src_file = abspath(join(source_dir, src_file_name))
		dest_file = join(dest_dir, src_file_name)
		if isfile(src_file) and src_file.endswith('.py'):
			copyfile(src_file, dest_file)
		elif isdir(src_file):
			copy_py_files(src_file, dest_file)

def generate_helium_ini(project_version, filter_path, output_directory):
	properties = _parse_properties_file(filter_path)
	properties['project_version'] = project_version
	properties['build_checksum'] = _generate_fake_checksum_encoding_build_date()
	copy_with_filtering(
		get_proj_path('../src/main/resources/base/Helium.ini'),
		output_directory, properties
	)

def _parse_properties_file(file_path):
	with open(file_path, 'r') as f:
		filter_contents = f.read()
	config = RawConfigParser()
	config.readfp(StringIO('[dummy]\n' + filter_contents))
	return dict(config.items('dummy'))

def _generate_fake_checksum_encoding_build_date():
	result = ['%x' % randint(0, 15) for _ in range(40)]
	today = date.today()
	year_str = str(today.year)
	result[13] = year_str[0]
	result[3] = year_str[1]
	result[18] = year_str[2]
	result[29] = year_str[3]
	month_str = '%02d' % today.month
	result[11] = month_str[0]
	result[2] = month_str[1]
	day_str = '%02d' % today.day
	result[17] = day_str[0]
	result[31] = day_str[1]
	return ''.join(result)

def run_2to3(dir_or_file):
	script_2to3 = join(sys.prefix, 'Tools', 'Scripts', '2to3.py')
	if exists(script_2to3):
		check_call(
			'python "%s" -n -w --add-suffix=3 "%s"' % (script_2to3, dir_or_file),
			stdout=open(devnull, 'w')
		)
	else:
		# on OSX 2to3 is an executable located next to the python executable
		check_call(
			'2to3 -n -w --add-suffix=3 "%s"' % dir_or_file,
			stdout=open(devnull, 'w'), shell=True
		)

def rmtree_safe(dir_path):
	def handle_rm_error(func, file_path, exc_info):
		# This gets called when rmtree cannot remove file with path file_path.
		# Assume the cause of the problem is that the file is readonly and
		# unlink it:
		chmod(file_path, stat.S_IWRITE)
		unlink(file_path)
	rmtree(dir_path, onerror=handle_rm_error)

def strip_export_keyword(api_module_path):
	with open(api_module_path,'r') as f:
		newlines = []
		for line in f.readlines():
			newlines.append(line.replace('EXPORT', ''))
	with open(api_module_path, 'w') as f:
		for line in newlines:
			f.write(line)

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
	with open(src_file, 'rb') as open_src_file:
		with open(dest_file, 'wb') as open_dest_file:
			for line in open_src_file:
				new_line = line
				for key, value in dict_.iteritems():
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
	pip.main(['install', '-U', dependency, '-q'])