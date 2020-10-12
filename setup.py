from setuptools import setup, find_packages

setup(
	name = 'helium',
	# Also update docs/conf.py when you change this:
	version = '3.0.6-SNAPSHOT',
	author = 'Michael Herrmann',
	author_email = 'michael+removethisifyouarehuman@herrmann.io',
	description = 'Lighter browser automation based on Selenium.',
	keywords = 'helium selenium browser automation',
	url = 'https://github.com/mherrmann/selenium-python-helium',
	python_requires='>=3',
	packages = find_packages(exclude=['tests', 'tests.*']),
	install_requires = [
		'selenium==3.141.0'
	],
	package_data = {
		'helium._impl': ['webdrivers/**/*']
	},
	zip_safe = False,
	classifiers=[
		'Development Status :: 5 - Production/Stable',
		'Intended Audience :: Developers',
		'License :: OSI Approved :: MIT License',
		'Topic :: Software Development :: Testing',
		'Topic :: Software Development :: Libraries',
		'Programming Language :: Python',
		'Programming Language :: Python :: 3.5',
		'Programming Language :: Python :: 3.6',
		'Programming Language :: Python :: 3.7',
		'Programming Language :: Python :: 3.8',
		'Operating System :: Microsoft :: Windows',
		'Operating System :: POSIX :: Linux',
		'Operating System :: MacOS :: MacOS X'
	],
	test_suite='tests',
	tests_require=[
		'psutil==5.6.6',
		"pywin32 >= 227;platform_system=='Windows'"
	],
	extras_require={
        'docs': ['sphinx-rtd-theme==0.5.0', 'sphinx==3.2.1'],
    }
)