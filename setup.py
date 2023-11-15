from setuptools import setup, find_packages

setup(
	name = 'helium',
	# Also update docs/conf.py when you change this:
	version = '4.0.0',
	author = 'Michael Herrmann',
	author_email = 'michael+removethisifyouarehuman@herrmann.io',
	description = 'Lighter browser automation based on Selenium.',
	keywords = 'helium selenium browser automation',
	url = 'https://github.com/mherrmann/selenium-python-helium',
	python_requires='>=3',
	packages = find_packages(exclude=['tests', 'tests.*']),
	install_requires = [
		# Also update requirements/base.txt when you make changes here.
		'selenium>=4.9.0,<4.10',
		# Freeze webdriver-manager for security; It downloads and runs binaries
		# from the internet. If it becomes malicious in the future, very bad
		# things can happen.
		'webdriver-manager==4.0.1'
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
		'Programming Language :: Python :: 3.9',
		'Programming Language :: Python :: 3.10',
		'Programming Language :: Python :: 3.11',
		'Operating System :: Microsoft :: Windows',
		'Operating System :: POSIX :: Linux',
		'Operating System :: MacOS :: MacOS X'
	],
	test_suite='tests'
)