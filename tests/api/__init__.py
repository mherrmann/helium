from helium import start_chrome, start_firefox, go_to, set_driver, \
    kill_browser
from unittest import TestCase
from tests.api.util import get_data_file_url
from time import time, sleep

import os


def test_browser_name():
    try:
        browser_name = os.environ['TEST_BROWSER']
    except KeyError:
        return 'chrome'
    else:
        return browser_name


class BrowserAT(TestCase):
    @classmethod
    def setUpClass(cls):
        if _TEST_BROWSER is None:
            cls.driver = start_browser()
            cls.started_browser = True
        else:
            cls.driver = _TEST_BROWSER
            cls.started_browser = False
        set_driver(cls.driver)

    def setUp(self):
        go_to(self.get_url())

    def get_url(self):
        return get_data_file_url(self.get_page())

    def get_page(self):
        raise NotImplementedError()

    def read_result_from_browser(self, timeout_secs=3):
        start_time = time()
        while time() < start_time + timeout_secs:
            result = self.driver\
                .find_element_by_id('result').get_attribute('innerHTML')
            if result:
                return result
            sleep(0.2)
        return ''

    def assertFindsEltWithId(self, predicate, id_):
        self.assertEqual(id_, predicate.web_element.get_attribute('id'))

    @classmethod
    def tearDownClass(cls):
        if cls.started_browser:
            kill_browser()


_TEST_BROWSER = None


def setUpModule():
    global _TEST_BROWSER
    _TEST_BROWSER = start_browser()


def tearDownModule():
    global _TEST_BROWSER
    if _TEST_BROWSER is not None:
        kill_browser()
    _TEST_BROWSER = None


def start_browser(url=None):
    browser_name = test_browser_name()
    kwargs = {}
    if browser_name in ('chrome', 'firefox'):
        kwargs['headless'] = True
    return _TEST_BROWSERS[browser_name](url, **kwargs)


_TEST_BROWSERS = {
    'firefox': start_firefox,
    'chrome': start_chrome
}
