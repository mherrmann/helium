from helium.util import system
from helium.util.path import ensure_exists
from build_environment import get_target_path, get_proj_path
from build_impl import copy_package_sources, rmtree_safe, Platform, \
	copy_with_filtering, unzip_replacing_top_lvl_dir, pip_install
from distutils.dir_util import copy_tree
from glob import glob
from os.path import exists, join
from setuptools import find_packages

import helium
import selenium
import sys

PLATFORMS = [
	Platform('linux', 'Operating System :: POSIX :: Linux'),
	Platform('macosx', 'Operating System :: MacOS :: MacOS X'),
	Platform('win', 'Operating System :: Microsoft :: Windows')
]

_SOURCES_DIR = get_target_path('sources')
_PIP_DIST_DIR = get_target_path('pip-dist')

def initialize(selenium_version, psutil_version):
	pip_install('selenium==' + selenium_version)
	pip_install('psutil==' + psutil_version)

def generate_sources(project_version, selenium_version, psutil_version):
	if exists(_SOURCES_DIR):
		rmtree_safe(_SOURCES_DIR)
	copy_package_sources(helium, to=_SOURCES_DIR)
	helium__init__ = join(_SOURCES_DIR, 'helium', '__init__.py')
	_add__version__(helium__init__, project_version)
	for platform in PLATFORMS:
		_generate_setup_py(
			platform.dist_dir, [platform], project_version, selenium_version,
			psutil_version
		)
	_generate_setup_py(
		_PIP_DIST_DIR, PLATFORMS, project_version, selenium_version,
		psutil_version
	)

def _add__version__(helium__init__, project_version):
	with open(helium__init__, 'r') as f:
		existing_contents = f.read()
	with open(helium__init__, 'w') as f:
		f.write("__version__ = %r\n\n%s" % (project_version, existing_contents))

def _generate_setup_py(
	dest_dir, platforms, project_version, selenium_version, psutil_version
):
	template = get_proj_path('src/main/resources/base/setup.py')
	classifiers = _DEFAULT_CLASSIFIERS[:]
	classifiers.extend([platform.classifier for platform in platforms])
	format_items = lambda items: "\n\t\t%s\n\t" % ",\n\t\t".join(items)
	classifiers_str = '[%s]' % format_items(list(map(repr, classifiers)))
	packages = find_packages(_get_sources_dir_to_distribute())
	packages_str = '[%s]' % format_items(list(map(repr, packages)))
	package_data = dict([(pkg, ['*.py3']) for pkg in packages])
	package_data['helium'].append('data/*.*')
	for platform in platforms:
		package_data['helium'].append('data/%s/webdrivers/*' % platform.name)
	package_data_items = \
		['%r: %r' % item for item in list(package_data.items())]
	package_data_str = '{%s}' % format_items(package_data_items)
	ensure_exists(dest_dir)
	copy_with_filtering(
		template, dest_dir,
		{
			'project.version': project_version,
			'classifiers': classifiers_str,
			'packages': packages_str,
			'package_data': package_data_str,
			'selenium.version': selenium_version,
			'psutil.version': psutil_version
		}
	)

_DEFAULT_CLASSIFIERS = [
	'Development Status :: 5 - Production/Stable',
	'Intended Audience :: Developers',
	'License :: Other/Proprietary License',
	'Topic :: Software Development :: Testing',
	'Topic :: Software Development :: Libraries',
	'Programming Language :: Python',
	'Programming Language :: Python :: 2.6',
	'Programming Language :: Python :: 2.7',
	'Programming Language :: Python :: 3.2',
	'Programming Language :: Python :: 3.3'
]

def _get_sources_dir_to_distribute():
	return _SOURCES_DIR

def process_resources(selenium_version):
	dependencies_dir = get_target_path('dependencies')
	assert selenium.__version__ == selenium_version, \
		'Please ensure you have Selenium version %s installed or notify ' \
		'the team if you want to update.' % selenium_version
	copy_package_sources(selenium, to=dependencies_dir)

def prepare_package():
	heliumlib_base = get_target_path('dist-base/heliumlib')
	copy_tree(_get_sources_dir_to_distribute(), heliumlib_base)
	copy_tree(get_target_path('dependencies'), heliumlib_base)

def pre_integration_test(project_version):
	test_dist_dir = get_target_path('test-dist')
	platform = system.get_canonical_os_name()
	helium_zip_path, = glob(get_target_path('helium-*.*.*-%s.zip' % platform))
	unzip_replacing_top_lvl_dir(helium_zip_path, test_dist_dir)

if __name__ == '__main__':
	globals()[sys.argv[1]](*sys.argv[2:])