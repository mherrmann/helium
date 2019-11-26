from helium_integrationtest.util import ResourceLocator
from helium_integrationtest.environment import get_integrationtest_resource
from os import path
from unittest import TestCase

class ResourceLocatorIT(TestCase):
	def setUp(self):
		self.root_dir = get_integrationtest_resource('inttest_environment')
	def test_locate_file(self):
		resource_locator = ResourceLocator(self.root_dir)
		result = resource_locator.locate('file.txt')
		self.assertIsNotNone(result)
		self.assertTrue(path.exists(result))
	def test_locate_file_in_dir(self):
		resource_locator = ResourceLocator(self.root_dir)
		result = resource_locator.locate('dir', 'file_in_dir.txt')
		self.assertIsNotNone(result)
		self.assertTrue(path.exists(result))
	def test_locate_non_existent_file(self):
		resource_locator = ResourceLocator(self.root_dir)
		result = resource_locator.locate('non-existent file')
		self.assertIsNotNone(result)
	def test_multiple_root_directories(self):
		resource_locator = ResourceLocator(
			self.root_dir,
			get_integrationtest_resource('inttest_environment', 'dir')
		)
		result = resource_locator.locate('file_in_dir.txt')
		self.assertIsNotNone(result)
		self.assertTrue(path.exists(result))