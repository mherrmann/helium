from helium._impl.environment import ResourceLocator
from os.path import dirname, exists, basename
from unittest import TestCase

class ResourceLocatorTest(TestCase):
	def test_locate_file(self):
		resource_locator = ResourceLocator(dirname(__file__))
		result = resource_locator.locate(basename(__file__))
		self.assertIsNotNone(result)
		self.assertTrue(exists(result))
	def test_locate_file_in_dir(self):
		resource_locator = ResourceLocator(dirname(dirname(__file__)))
		result = resource_locator.locate(
			basename(dirname(__file__)), basename(__file__)
		)
		self.assertIsNotNone(result)
		self.assertTrue(exists(result))
	def test_locate_non_existent_file(self):
		resource_locator = ResourceLocator(dirname(__file__))
		result = resource_locator.locate('non-existent file')
		self.assertIsNotNone(result)
	def test_multiple_root_directories(self):
		resource_locator = ResourceLocator(
			dirname(__file__), dirname(dirname(__file__))
		)
		result = resource_locator.locate(basename(__file__))
		self.assertIsNotNone(result)
		self.assertTrue(exists(result))