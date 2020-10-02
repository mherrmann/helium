from errno import EEXIST
from os.path import split, isdir
from os import makedirs

def get_components(path):
	folders = []
	while True:
		path, folder = split(path)
		if folder != "":
			folders.append(folder)
		else:
			if path != "":
				folders.append(path)
			break
	return list(reversed(folders))

def ensure_exists(path):
	"""http://stackoverflow.com/a/600612/190597 (tzot)"""
	try:
		makedirs(path, exist_ok=True)  # Python>3.2
	except TypeError:
		try:
			makedirs(path)
		except OSError as exc: # Python >2.5
			if exc.errno == EEXIST and isdir(path):
				pass
			else: raise