from build_impl import unzip_replacing_top_lvl_dir
from helium.util.system import get_canonical_os_name as get_platform, \
	is_windows
from os import chmod
from os.path import join, dirname
import sys

def pre_integration_test(project_version, artifactFinalName):
	zip_file = _get_target_path(
		'%s-%s.zip' % (artifactFinalName, get_platform())
	)
	unzip_replacing_top_lvl_dir(zip_file, _get_target_path('test-dist'))
	# we need to change the execution rights to make this work on OSX
	if not is_windows():
		chmod(_get_target_path('test-dist/webdrivers/chromedriver'), 0o755)

def _get_proj_path(*rel_path):
	rel_path = '/'.join(rel_path)
	return join(dirname(__file__), *rel_path.split('/'))

def _get_target_path(*rel_path):
	return _get_proj_path('target/' + '/'.join(rel_path))

if __name__ == '__main__':
	globals()[sys.argv[1]](*sys.argv[2:])