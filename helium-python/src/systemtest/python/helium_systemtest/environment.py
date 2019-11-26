from build_environment import get_target_path, get_proj_path
from os.path import pardir

def get_test_library():
	return get_target_path('test-dist/heliumlib')

def get_systemtest_resource(*rel_path):
	return get_proj_path(pardir, 'src', 'systemtest', 'resources', *rel_path)

def get_test_dist_dir():
	return get_target_path('test-dist')