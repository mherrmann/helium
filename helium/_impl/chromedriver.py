from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.core.driver_cache import DriverCacheManager

import webdriver_manager

# Work around a bug in webdriver_manager:
if webdriver_manager.__version__ == '4.0.0':
	class DriverCacheManager(DriverCacheManager):
		def __get_metadata_key(self, driver):
			super().__get_metadata_key(driver)
			return self._metadata_key

def install_matching_chromedriver(cache_dir=None):
	manager = ChromeDriverManager(cache_manager=DriverCacheManager(cache_dir))
	return manager.install()