from helium import go_to
from tests.api import start_browser
from tests.api.util import get_data_file_url
from os import path
from unittest import TestCase


class StartGoToTest(TestCase):
    def setUp(self):
        self.url = get_data_file_url('test_start_go_to.html')
        self.driver = None

    def test_go_to(self):
        self.driver = start_browser()
        go_to(self.url)
        self.assertUrlEquals(self.url, self.driver.current_url)

    def assertUrlEquals(self, expected, actual):
        expected = str(path.normpath(expected.lower().replace('\\', '/')))
        actual = str(path.normpath(actual.lower().replace('\\', '/')))
        self.assertEqual(expected, actual)

    def test_start_with_url(self):
        self.driver = start_browser(self.url)
        self.assertUrlEquals(self.url, self.driver.current_url)

    def tearDown(self):
        if self.driver is not None:
            self.driver.quit()
