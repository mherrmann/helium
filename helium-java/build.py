from build_impl import unzip_replacing_top_lvl_dir, generate_helium_ini
from glob import glob
from helium.util.path import ensure_exists
from helium.util.system import get_canonical_os_name as get_platform, \
	is_windows
from os import chmod, listdir
from os.path import join, dirname, splitext, basename
import sys

def generate_resources(project_version, build_type):
	_generate_example_run_scripts()
	filter_path = '../src/main/filters/filter-%s.properties' % build_type
	generate_helium_ini(
		project_version, filter_path, _get_target_path('dist-base/runtime')
	)

def _generate_example_run_scripts():
	examples_dir = _get_proj_path('src/main/resources/base/examples')
	for example in listdir(examples_dir):
		java_file_path, = glob(_get_proj_path(examples_dir, example, '*.java'))
		java_file_name = basename(java_file_path)
		dest_dir_win = _get_target_path('dist-win/examples/' + example)
		_generate_run_bat(java_file_name, dest_dir_win)
		dest_dir_linux = _get_target_path('dist-linux/examples/' + example)
		_generate_run_sh(java_file_name, dest_dir_linux)
		dest_dir_macosx = _get_target_path('dist-macosx/examples/' + example)
		_generate_run_sh(java_file_name, dest_dir_macosx)

def _generate_run_bat(java_file, dest_dir):
	ensure_exists(dest_dir)
	with open(join(dest_dir, 'run.bat'), 'wb') as run_bat:
		run_bat.write(
			'javac -cp ".;../../heliumlib/*" %s\r\n'
			'java -cp ".;../../heliumlib/*" %s\r\n'
			'pause' % (java_file, splitext(java_file)[0])
		)

def _generate_run_sh(java_file, dest_dir):
	ensure_exists(dest_dir)
	with open(join(dest_dir, 'run.sh'), 'wb') as run_sh:
		run_sh.write(
			'#!/bin/sh\n'
			'javac -cp ".:../../heliumlib/*" %s\n'
			'java -cp ".:../../heliumlib/*" %s\n'
			'read -p "Press Enter to continue..." prompt' % (
				java_file, splitext(java_file)[0]
			)
		)

def pre_integration_test(project_version, artifactFinalName):
	zip_file = _get_target_path(
		'%s-%s.zip' % (artifactFinalName, get_platform())
	)
	unzip_replacing_top_lvl_dir(zip_file, _get_target_path('test-dist'))
	# we need to change the execution rights to make this work on OSX
	if not is_windows():
		chmod(_get_target_path('test-dist/webdrivers/chromedriver'), 0755)
	filter_path = '../src/systemtest/filters/filter-systemtest.properties'
	generate_helium_ini(
		project_version, filter_path, _get_target_path('test-dist/runtime')
	)

def _get_proj_path(*rel_path):
	rel_path = '/'.join(rel_path)
	return join(dirname(__file__), *rel_path.split('/'))

def _get_target_path(*rel_path):
	return _get_proj_path('target/' + '/'.join(rel_path))

if __name__ == '__main__':
	globals()[sys.argv[1]](*sys.argv[2:])