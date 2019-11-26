from setuptools import setup

setup(
	name = 'helium',
	version = '${project.version}',
	author = 'BugFree Software',
	author_email = 'contact@heliumhq.com',
	description = 'Simple web automation based on Selenium.',
	keywords = 'selenium web automation',
	url = 'http://heliumhq.com',
	classifiers = ${classifiers},
	packages = ${packages},
	package_dir = {'helium': 'heliumlib/helium'},
	install_requires = [
		'selenium==${selenium.version}', 'pkcs1>=${pkcs1.version}',
		'decorator>=${decorator.version}', 'psutil>=${psutil.version}'
	],
	package_data = ${package_data},
	zip_safe = False
)