from helium._impl.util.geom import Rectangle
from selenium.common.exceptions import StaleElementReferenceException, \
	NoSuchFrameException, WebDriverException
from selenium.webdriver.common.action_chains import ActionChains
from urllib.error import URLError
import sys

class Wrapper:
	def __init__(self, target):
		self.target = target
	def __getattr__(self, item):
		return getattr(self.target, item)
	def unwrap(self):
		return self.target
	def __hash__(self):
		return hash(self.target)
	def __eq__(self, other):
		return self.target == other.target
	def __ne__(self, other):
		return not self == other

class WebDriverWrapper(Wrapper):
	def __init__(self, target):
		super(WebDriverWrapper, self).__init__(target)
		self.last_manipulated_element = None
	def action(self):
		return ActionChains(self.target)
	def get_distance_to_last_manipulated(self, web_element):
		if not self.last_manipulated_element:
			return 0
		try:
			if hasattr(self.last_manipulated_element, 'location'):
				last_location = self.last_manipulated_element.location
				return last_location.distance_to(web_element.location)
		except StaleElementReferenceException:
			return 0
		else:
			# No .location. This happens when last_manipulated_element is an
			# Alert or a Window.
			return 0
	def find_elements_by_name(self, name):
		# Selenium sometimes returns None. For robustness, we turn this into []:
		return self.target.find_elements_by_name(name) or []
	def find_elements_by_xpath(self, xpath):
		# Selenium sometimes returns None. For robustness, we turn this into []:
		return self.target.find_elements_by_xpath(xpath) or []
	def find_elements_by_css_selector(self, selector):
		# Selenium sometimes returns None. For robustness, we turn this into []:
		return self.target.find_elements_by_css_selector(selector) or []
	def is_firefox(self):
		return self.browser_name == 'firefox'
	@property
	def browser_name(self):
		return self.target.capabilities['browserName']
	def is_ie(self):
		return self.browser_name == 'internet explorer'

def _translate_url_errors_caused_by_server_shutdown(f):
	def f_decorated(*args, **kwargs):
		try:
			return f(*args, **kwargs)
		except URLError as url_error:
			if _is_caused_by_server_shutdown(url_error):
				raise StaleElementReferenceException(
					'The Selenium server this element belonged to is no longer '
					'available.'
				)
			else:
				raise
	return f_decorated

def _is_caused_by_server_shutdown(url_error):
	try:
		CONNECTION_REFUSED = 10061
		return url_error.args[0][0] == CONNECTION_REFUSED
	except (IndexError, TypeError):
		return False

def handle_element_being_in_other_frame(f):
	def f_decorated(self, *args, **kwargs):
		if not self.frame_index:
			return f(self, *args, **kwargs)
		try:
			return f(self, *args, **kwargs)
		except StaleElementReferenceException as original_exc:
			try:
				frame_iterator = FrameIterator(self.target.parent)
				frame_iterator.switch_to_frame(self.frame_index)
			except NoSuchFrameException:
				raise original_exc
			else:
				return f(self, *args, **kwargs)
	return f_decorated

class WebElementWrapper:
	def __init__(self, target, frame_index=None):
		self.target = target
		self.frame_index = frame_index
		self._cached_location = None
	@property
	@handle_element_being_in_other_frame
	@_translate_url_errors_caused_by_server_shutdown
	def location(self):
		if self._cached_location is None:
			# Cache access to web_element.location as it's expensive:
			location = self.target.location
			x, y = location['x'], location['y']
			# Cache access to web_element.size as it's expensive:
			size = self.target.size
			width, height = size['width'], size['height']
			self._cached_location = Rectangle(x, y, width, height)
		return self._cached_location
	def is_displayed(self):
		try:
			return self.target.is_displayed() and self.location.intersects(
				Rectangle(0, 0, sys.maxsize, sys.maxsize)
			)
		except StaleElementReferenceException:
			return False
	@handle_element_being_in_other_frame
	def get_attribute(self, attr_name):
		return self.target.get_attribute(attr_name)
	@property
	@handle_element_being_in_other_frame
	def text(self):
		return self.target.text
	@handle_element_being_in_other_frame
	def clear(self):
		self.target.clear()
	@handle_element_being_in_other_frame
	def send_keys(self, keys):
		self.target.send_keys(keys)
	@property
	@handle_element_being_in_other_frame
	def tag_name(self):
		return self.target.tag_name
	def unwrap(self):
		return self.target
	def __repr__(self):
		return '<%s>%s</%s>' % (self.tag_name, self.target.text, self.tag_name)

class FrameIterator:
	def __init__(self, driver, start_frame=None):
		if start_frame is None:
			start_frame = []
		self.driver = driver
		self.start_frame = start_frame
	def __iter__(self):
		yield []
		for new_frame in range(sys.maxsize):
			try:
				self.driver.switch_to.frame(new_frame)
			except WebDriverException:
				break
			else:
				new_start_frame = self.start_frame + [new_frame]
				for result in FrameIterator(self.driver, new_start_frame):
					yield [new_frame] + result
				try:
					self.switch_to_frame(self.start_frame)
				except NoSuchFrameException:
					raise FramesChangedWhileIterating()
	def switch_to_frame(self, frame_index_path):
		self.driver.switch_to.default_content()
		for frame_index in frame_index_path:
			self.driver.switch_to.frame(frame_index)

class FramesChangedWhileIterating(Exception):
	pass