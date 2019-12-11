from setuptools import setup

setup(
	name = 'helium',
	version = '${project.version}',
	author = 'Michael Herrmann',
	author_email = 'michael+removethisifyouarehuman@herrmann.io',
	description = 'Simple web automation based on Selenium.',
	keywords = 'selenium web automation',
	url = 'http://heliumhq.com',
	classifiers = ${classifiers},
	packages = ${packages},
	package_dir = {'helium': 'heliumlib/helium'},
	install_requires = [
		'selenium==${selenium.version}', 'psutil>=${psutil.version}'
	],
	package_data = ${package_data},
	zip_safe = False
)