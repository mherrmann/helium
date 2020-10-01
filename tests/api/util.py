from os.path import dirname, join
from pathlib import Path
from subprocess import Popen, PIPE, STDOUT

import os
import sys


def get_data_file(*rel_path):
    return join(dirname(__file__), 'data', *rel_path)


def get_data_file_url(data_file):
    return Path(get_data_file(data_file)).as_uri()


class InSubProcess:
    """
    Important: You need to call `synchronize_with_parent_process()` in your sub-
    class's `main` method.
    """

    def __init__(self):
        self.sub_process = None

    def __enter__(self):
        self.sub_process = Popen(
            ['python', '-m', self.__class__.__module__],
            stdin=PIPE, stdout=PIPE, stderr=STDOUT, universal_newlines=True,
            cwd=os.getcwd(), env=os.environ
        )
        self.sub_process.__enter__()
        self.wait_for_sub_process()

    def wait_for_sub_process(self):
        line = self.sub_process.stdout.readline()
        assert 'Sub process started.\n' == line, \
            'Sub process invocation failed:\n' + \
            line + self.sub_process.stdout.read()

    @classmethod
    def synchronize_with_parent_process(cls):
        # Let parent process know we've started:
        sys.stdout.write('Sub process started.\n')
        sys.stdout.flush()
        # Wait until parent process is finished:
        input('')

    def __exit__(self, *args):
        self.sub_process.stdin.write('\n')
        self.sub_process.stdin.flush()
        self.sub_process.__exit__(*args)
        self.sub_process.wait()
        assert self.sub_process.returncode == 0, \
            repr(self.sub_process.returncode)
