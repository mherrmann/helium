from os import chmod, stat
from stat import S_IEXEC

def make_executable(file_path):
	chmod(file_path, stat(file_path).st_mode | S_IEXEC)