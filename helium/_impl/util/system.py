"""
Gives information about the current operating system.
"""
import sys
import subprocess


def is_windows():
    return sys.platform in ('win32', 'cygwin')


def is_mac():
    return sys.platform == 'darwin'


def is_linux():
    return sys.platform.startswith('linux')


def get_canonical_os_name():
    if is_windows():
        return 'windows'
    elif is_mac():
        # check apple cpu
        result = subprocess.run(["sysctl", "-a", "machdep.cpu.brand_string"], stdout=subprocess.PIPE)
        brand_string: str = result.stdout.decode('utf-8').strip().lower()
        if 'apple' in brand_string:
            return 'mac_m1'
        else:
            return 'mac'
    elif is_linux():
        return 'linux'
