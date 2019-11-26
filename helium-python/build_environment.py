from os.path import dirname, join

def get_proj_path(*rel_path):
	rel_path = '/'.join(rel_path)
	return join(dirname(__file__), *rel_path.split('/'))

def get_target_path(*rel_path):
	return get_proj_path('target/' + '/'.join(rel_path))