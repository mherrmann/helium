from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.core.driver_cache import DriverCacheManager


def install_matching_chromedriver(cache_dir=None):
	manager = ChromeDriverManager(cache_manager=DriverCacheManager(cache_dir))
	return manager.install()