from os import path
from os.path import abspath, pardir

def get_integrationtest_resource(*rel_path_compontents):
	return abspath(path.join(
		__file__, pardir, pardir, pardir, pardir, pardir, pardir,
		'src', 'integrationtest', 'resources', *rel_path_compontents
	))

def get_helium_package_source(*rel_path_compontents):
	return abspath(path.join(
		__file__, pardir, pardir, pardir, pardir, pardir,
		'src', 'main', 'python', *rel_path_compontents
	))

def path_to_file_url(path):
	return 'file:///' + path.replace('\\', '/')

def get_it_file_url(page):
	return path_to_file_url(get_integrationtest_resource(page))