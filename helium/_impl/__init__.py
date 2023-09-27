from copy import copy
from helium._impl.chromedriver import install_matching_chromedriver
from helium._impl.match_type import PREFIX_IGNORE_CASE
from helium._impl.selenium_wrappers import WebElementWrapper, \
	WebDriverWrapper, FrameIterator, FramesChangedWhileIterating
from helium._impl.util.dictionary import inverse
from helium._impl.util.os_ import make_executable
from helium._impl.util.system import is_windows, get_canonical_os_name
from helium._impl.util.xpath import lower, predicate, predicate_or
from inspect import getfullargspec, ismethod, isfunction
from os import access, X_OK
from os.path import join, dirname
from selenium.common.exceptions import UnexpectedAlertPresentException, \
	ElementNotVisibleException, MoveTargetOutOfBoundsException, \
	WebDriverException, StaleElementReferenceException, \
	NoAlertPresentException, NoSuchWindowException
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver import Chrome, ChromeOptions, Firefox, FirefoxOptions, \
	FirefoxProfile
from time import sleep, time

import atexit
import re

def might_spawn_window(f):
	def f_decorated(self, *args, **kwargs):
		driver = self.require_driver()
		if driver.is_ie() and AlertImpl(driver).exists():
			# Accessing .window_handles in IE when an alert is present raises an
			# UnexpectedAlertPresentException. When DesiredCapability
			# 'unexpectedAlertBehaviour' is not 'ignore' (the default is
			# 'dismiss'), this leads to the alert being closed. Since we don't
			# want to unintentionally close alert dialogs, we therefore do not
			# access .window_handles in IE when an alert is present.
			return f(self, *args, **kwargs)
		window_handles_before = driver.window_handles[:]
		result = f(self, *args, **kwargs)
		# As above, don't access .window_handles in IE if an alert is present:
		if not (driver.is_ie() and AlertImpl(driver).exists()):
			if driver.is_firefox():
				# Unlike Chrome, Firefox does not wait for new windows to open.
				# Give it a little time to do so:
				sleep(.2)
			new_window_handles = [
				h for h in driver.window_handles
				if h not in window_handles_before
			]
			if new_window_handles:
				driver.switch_to.window(new_window_handles[0])
		return result
	return f_decorated

def handle_unexpected_alert(f):
	def f_decorated(*args, **kwargs):
		try:
			return f(*args, **kwargs)
		except UnexpectedAlertPresentException:
			raise UnexpectedAlertPresentException(
				"This command is not supported when an alert is present. To "
				"accept the alert (this usually corresponds to clicking 'OK') "
				"use `Alert().accept()`. To dismiss the alert (ie. 'cancel' "
				"it), use `Alert().dismiss()`. If the alert contains a text "
				"field, you can use write(...) to set its value. "
				"Eg.: `write('hi there!')`."
			)
	return f_decorated

class APIImpl:
	DRIVER_REQUIRED_MESSAGE = \
		"This operation requires a browser window. Please call one of " \
		"the following functions first:\n" \
		" * start_chrome()\n" \
		" * start_firefox()\n" \
		" * set_driver(...)"
	def __init__(self):
		self.driver = None
	def start_firefox_impl(
		self, url=None, headless=False, options=None, profile=None
	):
		firefox_driver = self._start_firefox_driver(headless, options, profile)
		return self._start(firefox_driver, url)
	def _start_firefox_driver(self, headless, options, profile):
		firefox_options = FirefoxOptions() if options is None else options
		firefox_profile = FirefoxProfile() if profile is None else profile
		if headless:
			firefox_options.headless = True
		kwargs = {
			'options': firefox_options,
			'firefox_profile': firefox_profile,
			'service_log_path': 'nul' if is_windows() else '/dev/null'
		}
		try:
			result = Firefox(**kwargs)
		except WebDriverException:
			# This usually happens when geckodriver is not on the PATH.
			driver_path = self._use_included_web_driver('geckodriver')
			result = Firefox(executable_path=driver_path, **kwargs)
		atexit.register(self._kill_service, result.service)
		return result
	def start_chrome_impl(
		self, url=None, headless=False, maximize=False, options=None,
		capabilities=None
	):
		chrome_driver = \
			self._start_chrome_driver(headless, maximize, options, capabilities)
		return self._start(chrome_driver, url)
	def _start_chrome_driver(self, headless, maximize, options, capabilities):
		chrome_options = self._get_chrome_options(headless, maximize, options)
		try:
			result = Chrome(
				options=chrome_options, desired_capabilities=capabilities
			)
		except WebDriverException:
			# This usually happens when chromedriver is not on the PATH.
			driver_path = install_matching_chromedriver()
			result = Chrome(
				options=chrome_options, desired_capabilities=capabilities,
				executable_path=driver_path
			)
		atexit.register(self._kill_service, result.service)
		return result
	def _get_chrome_options(self, headless, maximize, options):
		result = ChromeOptions() if options is None else options
		# Prevent Chrome's debug logs from appearing in our console window:
		result.add_experimental_option('excludeSwitches', ['enable-logging'])
		if headless:
			result.add_argument('--headless')
		elif maximize:
			result.add_argument('--start-maximized')
		return result
	def _use_included_web_driver(self, driver_name):
		if is_windows():
			driver_name += '.exe'
		driver_path = join(
			dirname(__file__), 'webdrivers', get_canonical_os_name(),
			driver_name
		)
		if not access(driver_path, X_OK):
			try:
				make_executable(driver_path)
			except Exception:
				raise RuntimeError(
					"The driver located at %s is not executable." % driver_path
				) from None
		return driver_path
	def _kill_service(self, service):
		old = service.send_remote_shutdown_command
		service.send_remote_shutdown_command = lambda: None
		try:
			service.stop()
		finally:
			service.send_remote_shutdown_command = old
	def _start(self, browser, url=None):
		self.set_driver_impl(browser)
		if url is not None:
			self.go_to_impl(url)
		return self.get_driver_impl()
	@might_spawn_window
	@handle_unexpected_alert
	def go_to_impl(self, url):
		if '://' not in url:
			url = 'http://' + url
		self.require_driver().get(url)
	def set_driver_impl(self, driver):
		self.driver = WebDriverWrapper(driver)
	def get_driver_impl(self):
		if self.driver is not None:
			return self.driver.unwrap()
	@might_spawn_window
	@handle_unexpected_alert
	def write_impl(self, text, into=None):
		if into is not None:
			from helium import GUIElement
			if isinstance(into, GUIElement):
				into = into._impl
		self._handle_alerts(
			self._write_no_alert, self._write_with_alert, text, into=into
		)
	def _write_no_alert(self, text, into=None):
		if into:
			if isinstance(into, str):
				into = TextFieldImpl(self.require_driver(), into)
			def _write(elt):
				if hasattr(elt, 'clear') and callable(elt.clear):
					elt.clear()
				elt.send_keys(text)
			self._manipulate(into, _write)
		else:
			self.require_driver().switch_to.active_element.send_keys(text)
	def _write_with_alert(self, text, into=None):
		if into is None:
			into = AlertImpl(self.require_driver())
		if not isinstance(into, AlertImpl):
			raise UnexpectedAlertPresentException(
				"into=%r is not allowed when an alert is present." % into
			)
		into._write(text)
	def _handle_alerts(self, no_alert, with_alert, *args, **kwargs):
		driver = self.require_driver()
		if not AlertImpl(driver).exists():
			return no_alert(*args, **kwargs)
		return with_alert(*args, **kwargs)
	@might_spawn_window
	@handle_unexpected_alert
	def press_impl(self, key):
		self.require_driver().switch_to.active_element.send_keys(key)
	def click_impl(self, element):
		self._perform_mouse_action(element, self._click)
	def doubleclick_impl(self, element):
		self._perform_mouse_action(element, self._doubleclick)
	def hover_impl(self, element):
		self._perform_mouse_action(element, self._hover)
	def rightclick_impl(self, element):
		self._perform_mouse_action(element, self._rightclick)
	def press_mouse_on_impl(self, element):
		self._perform_mouse_action(element, self._press_mouse_on)
	def release_mouse_over_impl(self, element):
		self._perform_mouse_action(element, self._release_mouse_over)
	def _click(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).click().perform()
	def _doubleclick(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).double_click().perform()
	def _hover(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).perform()
	def _rightclick(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).context_click().perform()
	def _press_mouse_on(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).click_and_hold().perform()
	def _release_mouse_over(self, selenium_elt, offset):
		self._move_to_element(selenium_elt, offset).release().perform()
	def _move_to_element(self, element, offset):
		result = self.require_driver().action()
		if offset is not None:
			result.move_to_element_with_offset(element, *offset)
		else:
			result.move_to_element(element)
		return result
	def drag_impl(self, element, to):
		with DragHelper(self) as drag_helper:
			self._perform_mouse_action(element, drag_helper.start_dragging)
			self._perform_mouse_action(to, drag_helper.drop_on_target)
	@might_spawn_window
	@handle_unexpected_alert
	def _perform_mouse_action(self, element, action):
		element, offset = self._unwrap_clickable_element(element)
		self._manipulate(element, lambda wew: action(wew.unwrap(), offset))
	def _unwrap_clickable_element(self, elt):
		from helium import HTMLElement, Point
		offset = None
		if isinstance(elt, str):
			elt = ClickableText(self.require_driver(), elt)
		elif isinstance(elt, HTMLElement):
			elt = elt._impl
		elif isinstance(elt, Point):
			elt, offset = self._point_to_element_and_offset(elt)
		return elt, offset
	def _point_to_element_and_offset(self, point):
		driver = self.require_driver()
		element = WebElementWrapper(driver.execute_script(
			'return document.elementFromPoint(%r, %r);' % (point.x, point.y)
		))
		offset = point - (element.location.left, element.location.top)
		if offset == (0, 0) and driver.is_firefox():
			# In some CSS settings (eg. test_point.html), the (0, 0) point of
			# buttons in Firefox is not clickable! The reason for this is that
			# Firefox styles buttons to not be perfect squares, but have an
			# indent in the corners. This workaround makes `click(btn.top_left)`
			# work even when this happens:
			offset = (1, 1)
		return element, offset
	@handle_unexpected_alert
	def find_all_impl(self, predicate):
		return [
			predicate.with_impl(bound_gui_elt_impl)
			for bound_gui_elt_impl in predicate._impl.find_all()
		]
	def scroll_down_impl(self, num_pixels):
		self._scroll_by(0, num_pixels)
	def scroll_up_impl(self, num_pixels):
		self._scroll_by(0, -num_pixels)
	def scroll_right_impl(self, num_pixels):
		self._scroll_by(num_pixels, 0)
	def scroll_left_impl(self, num_pixels):
		self._scroll_by(-num_pixels, 0)
	@handle_unexpected_alert
	def _scroll_by(self, dx_pixels, dy_pixels):
		self.require_driver().execute_script(
			'window.scrollBy(arguments[0], arguments[1]);', dx_pixels, dy_pixels
		)
	@might_spawn_window
	@handle_unexpected_alert
	def select_impl(self, combo_box, value):
		from helium import ComboBox
		if isinstance(combo_box, str):
			combo_box = ComboBoxImpl(self.require_driver(), combo_box)
		elif isinstance(combo_box, ComboBox):
			combo_box = combo_box._impl
		def _select(web_element):
			if isinstance(web_element, WebElementWrapper):
				web_element = web_element.unwrap()
			Select(web_element).select_by_visible_text(value)
		self._manipulate(combo_box, _select)
	def _manipulate(self, gui_or_web_elt, action):
		driver = self.require_driver()
		if hasattr(gui_or_web_elt, 'perform') \
			and callable(gui_or_web_elt.perform):
			driver.last_manipulated_element = gui_or_web_elt.perform(action)
		else:
			if isinstance(gui_or_web_elt, WebElement):
				gui_or_web_elt = WebElementWrapper(gui_or_web_elt)
			action(gui_or_web_elt)
			driver.last_manipulated_element = gui_or_web_elt
	@handle_unexpected_alert
	def drag_file_impl(self, file_path, to):
		to, _ = self._unwrap_clickable_element(to)
		drag_and_drop = DragAndDropFile(self.require_driver(), file_path)
		drag_and_drop.begin()
		try:
			# Some web apps (Gmail in particular) only register for the 'drop'
			# event when user has dragged the file over the document. We
			# therefore simulate this dragging over the document first:
			drag_and_drop.drag_over_document()
			self._manipulate(to, lambda elt: drag_and_drop.drop_on(elt))
		finally:
			drag_and_drop.end()
	@might_spawn_window
	@handle_unexpected_alert
	def attach_file_impl(self, file_path, to=None):
		from helium import Point
		driver = self.require_driver()
		if to is None:
			to = FileInput(driver)
		elif isinstance(to, str):
			to = FileInput(driver, to)
		elif isinstance(to, Point):
			to, _ = self._point_to_element_and_offset(to)
		self._manipulate(to, lambda elt: elt.send_keys(file_path))
	def refresh_impl(self):
		self._handle_alerts(
			self._refresh_no_alert, self._refresh_with_alert
		)
	def _refresh_no_alert(self):
		self.require_driver().refresh()
	def _refresh_with_alert(self):
		AlertImpl(self.require_driver()).accept()
		self._refresh_no_alert()
	def wait_until_impl(self, condition_fn, timeout_secs=10, interval_secs=0.5):
		if ismethod(condition_fn):
			is_bound = condition_fn.__self__ is not None
			args_spec = getfullargspec(condition_fn).args
			unfilled_args = len(args_spec) - (1 if is_bound else 0)
		else:
			if not isfunction(condition_fn):
				condition_fn = condition_fn.__call__
			args_spec = getfullargspec(condition_fn).args
			unfilled_args = len(args_spec)
		condition = \
			condition_fn if unfilled_args else lambda driver: condition_fn()
		wait = WebDriverWait(
			self.require_driver().unwrap(), timeout_secs,
			poll_frequency=interval_secs
		)
		wait.until(condition)
	@handle_unexpected_alert
	def switch_to_impl(self, window):
		driver = self.require_driver()
		from helium import Window
		if isinstance(window, str):
			window = WindowImpl(driver, window)
		elif isinstance(window, Window):
			window = window._impl
		driver.switch_to.window(window.handle)
	def kill_browser_impl(self):
		self.require_driver().quit()
		self.driver = None
	@handle_unexpected_alert
	def highlight_impl(self, element):
		driver = self.require_driver()
		from helium import HTMLElement, Text
		if isinstance(element, str):
			element = Text(element)
		if isinstance(element, HTMLElement):
			element = element._impl
		try:
			element = element.first_occurrence
		except AttributeError:
			pass
		previous_style = element.get_attribute("style")
		if isinstance(element, WebElementWrapper):
			element = element.unwrap()
		driver.execute_script(
			"arguments[0].setAttribute("
				"'style', 'border: 2px solid red; font-weight: bold;'"
			");", element
		)
		driver.execute_script(
			"var target = arguments[0];"
			"var previousStyle = arguments[1];"
			"setTimeout("
				"function() {"
					"target.setAttribute('style', previousStyle);"
				"}, 2000"
			");", element, previous_style
		)
	def require_driver(self):
		if not self.driver:
			raise RuntimeError(self.DRIVER_REQUIRED_MESSAGE)
		return self.driver

class DragHelper:
	def __init__(self, api_impl):
		self.api_impl = api_impl
		self.is_html_5_drag = None
	def __enter__(self):
		self._execute_script(
			"window.helium = {};"
			"window.helium.dragHelper = {"
			"    createEvent: function(type) {"
			"        var event = document.createEvent('CustomEvent');"
			"        event.initCustomEvent(type, true, true, null);"
			"        event.dataTransfer = {"
			"            data: {},"
			"            setData: function(type, val) {"
			"                this.data[type] = val;"
			"            },"
			"            getData: function(type) {"
			"                return this.data[type];"
			"            }"
			"        };"
			"        return event;"
			"    }"
			"};"
		)
		return self
	def start_dragging(self, element, offset):
		if self._attempt_html_5_drag(element):
			self.is_html_5_drag = True
		else:
			self.api_impl._press_mouse_on(element, offset)
	def drop_on_target(self, target, offset):
		if self.is_html_5_drag:
			self._complete_html_5_drag(target)
		else:
			self.api_impl._release_mouse_over(target, offset)
	def _attempt_html_5_drag(self, element_to_drag):
		return self._execute_script(
			"var source = arguments[0];"
			"function getDraggableParent(element) {"
			"    var previousParent = null;"
			"    while (element != null && element != previousParent) {"
			"        previousParent = element;"
			"        if ('draggable' in element) {"
			"            var draggable = element.draggable;"
			"            if (draggable === true)"
			"                return element;"
			"            if (typeof draggable == 'string' "
			"                    || draggable instanceof String)"
			"                if (draggable.toLowerCase() == 'true')"
			"                    return element;"
			"        }"
			"        element = element.parentNode;"
			"    }"
			"    return null;"
			"}"
			"var draggableParent = getDraggableParent(source);"
			"if (draggableParent == null)"
			"    return false;"
			"window.helium.dragHelper.draggedElement = draggableParent;"
			"var dragStart = window.helium.dragHelper.createEvent('dragstart');"
			"source.dispatchEvent(dragStart);"
			"window.helium.dragHelper.dataTransfer = dragStart.dataTransfer;"
			"return true;",
			element_to_drag
		)
	def _complete_html_5_drag(self, on):
		self._execute_script(
			"var target = arguments[0];"
			"var drop = window.helium.dragHelper.createEvent('drop');"
			"drop.dataTransfer = window.helium.dragHelper.dataTransfer;"
			"target.dispatchEvent(drop);"
			"var dragEnd = window.helium.dragHelper.createEvent('dragend');"
			"dragEnd.dataTransfer = window.helium.dragHelper.dataTransfer;"
			"window.helium.dragHelper.draggedElement.dispatchEvent(dragEnd);",
			on
		)
	def __exit__(self, *_):
		self._execute_script("delete window.helium;")
	def _execute_script(self, script, *args):
		return self.api_impl.require_driver().execute_script(script, *args)

class DragAndDropFile:
	def __init__(self, driver, file_path):
		self.driver = driver
		self.file_path = file_path
		self.file_input_element = None
		self.dragover_event = None
	def begin(self):
		self._create_file_input_element()
		try:
			self.file_input_element.send_keys(self.file_path)
		except:
			self.end()
			raise
	def _create_file_input_element(self):
		# The input needs to be visible to Selenium to allow sending keys to it
		# in Firefox and IE.
		# According to http://stackoverflow.com/questions/6101461/
		# Selenium criteria whether an element is visible or not are the
		# following:
		#  - visibility != hidden
		#  - display != none (is also checked against every parent element)
		#  - opacity != 0
		#  - height and width are both > 0
		#  - for an input, the attribute type != hidden
		# So let's make sure its all good!
		self.file_input_element = self.driver.execute_script(
			"var input = document.createElement('input');"
			"input.type = 'file';"
			"input.style.display = 'block';"
			"input.style.opacity = '1';"
			"input.style.visibility = 'visible';"
			"input.style.height = '1px';"
			"input.style.width = '1px';"
			"if (document.body.childElementCount > 0) { "
			"  document.body.insertBefore(input, document.body.childNodes[0]);"
			"} else { "
			"  document.body.appendChild(input);"
			"}"
			"return input;"
		)
	def drag_over_document(self):
		# According to the HTML5 spec, we need to dispatch the dragenter event
		# once, and then the dragover event continuously, every 350+-200ms:
		# http://www.w3.org/html/wg/drafts/html/master/editing.html#current-drag
		# -operation
		# Especially IE implements this spec very tightly, and considers the
		# dragging to be over if no dragover event occurs for more than ~1sec.
		# We thus need to ensure that we keep dispatching the dragover event.

		# This line used to read `_dispatch_event(..., to='document')`. However,
		# this doesn't work when adding a photo to a tweet on Twitter.
		# Dispatching the event to document.body fixes this, and also works for
		# Gmail:
		self._dispatch_event('dragenter', to='document.body')
		self.dragover_event = self._prepare_continuous_event(
			'dragover', 'document', interval_msecs=300
		)
		self.dragover_event.start()
	def _dispatch_event(self, event_name, to):
		script, args = self._prepare_dispatch_event(event_name, to)
		self.driver.execute_script(script, *args)
	def _prepare_continuous_event(self, event_name, to, interval_msecs):
		script, args = self._prepare_dispatch_event(event_name, to)
		return JavaScriptInterval(self.driver, script, args, interval_msecs)
	def _prepare_dispatch_event(self, event_name, to):
		script = \
			"var files = arguments[0].files;" \
			"var items = [];" \
			"var types = [];" \
			"for (var i = 0; i < files.length; i++) {" \
			"   items[i] = {kind: 'file', type: files[i].type};" \
			"   types[i] = 'Files';" \
			"}" \
			"var event = document.createEvent('CustomEvent');" \
			"event.initCustomEvent(arguments[1], true, true, 0);" \
			"event.dataTransfer = {" \
			"	files: files," \
			"	items: items," \
			"	types: types" \
			"};" \
			"arguments[2].dispatchEvent(event);"
		if isinstance(to, str):
			script = script.replace('arguments[2]', to)
			args = self.file_input_element, event_name,
		else:
			args = self.file_input_element, event_name, to.unwrap()
		return script, args
	def drop_on(self, target):
		self.dragover_event.stop()
		self._dispatch_event('drop', to=target)
	def end(self):
		if self.file_input_element is not None:
			self.driver.execute_script(
				"arguments[0].parentNode.removeChild(arguments[0]);",
				self.file_input_element
			)
		self.file_input_element = None

class JavaScriptInterval:
	def __init__(self, driver, script, args, interval_msecs):
		self.driver = driver
		self.script = script
		self.args = args
		self.interval_msecs = interval_msecs
		self._interval_id = None
	def start(self):
		setinterval_script = (
			"var originalArguments = arguments;"
			"return setInterval(function() {"
			"	arguments = originalArguments;"
			"	%s"
			"}, %d);"
		) % (self.script, self.interval_msecs)
		self._interval_id = \
			self.driver.execute_script(setinterval_script, *self.args)
	def stop(self):
		self.driver.execute_script(
			"clearInterval(arguments[0]);", self._interval_id
		)
		self._interval_id = None

class GUIElementImpl:
	def __init__(self, driver):
		self._bound_occurrence = None
		self._driver = driver
	def find_all(self):
		if self._is_bound():
			yield self
		else:
			for occurrence in self.find_all_occurrences():
				yield self.bound_to_occurrence(occurrence)
	def _is_bound(self):
		return self._bound_occurrence is not None
	def find_all_occurrences(self):
		raise NotImplementedError()
	def bound_to_occurrence(self, occurrence):
		result = copy(self)
		result._bound_occurrence = occurrence
		return result
	def exists(self):
		try:
			next(self.find_all())
		except StopIteration:
			return False
		else:
			return True
	@property
	def first_occurrence(self):
		if not self._is_bound():
			self._bind_to_first_occurrence()
		return self._bound_occurrence
	def _bind_to_first_occurrence(self):
		self.perform(lambda _: None)
		# _perform_no_wait(...) below now sets _bound_occurrence.
	def perform(self, action):
		from helium import Config
		end_time = time() + Config.implicit_wait_secs
		# Try to perform `action` at least once:
		result = self._perform_no_wait(action)
		while result is None and time() < end_time:
			result = self._perform_no_wait(action)
		if result is not None:
			return result
		raise LookupError()
	def _perform_no_wait(self, action):
		for bound_gui_elt_impl in self.find_all():
			occurrence = bound_gui_elt_impl.first_occurrence
			try:
				action(occurrence)
			except Exception as e:
				if self.should_ignore_exception(e):
					continue
				else:
					raise
			else:
				self._bound_occurrence = occurrence
				return occurrence
	def should_ignore_exception(self, exception):
		if isinstance(exception, ElementNotVisibleException):
			return True
		if isinstance(exception, MoveTargetOutOfBoundsException):
			return True
		if isinstance(exception, StaleElementReferenceException):
			return True
		if isinstance(exception, WebDriverException):
			msg = exception.msg
			if 'is not clickable at point' in msg \
				and 'Other element would receive the click' in msg:
				# This can happen when the element has moved.
				return True
		return False

class HTMLElementImpl(GUIElementImpl):
	def __init__(
			self, driver, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(HTMLElementImpl, self).__init__(driver)
		self.below = self._unwrap_element(below)
		self.to_right_of = self._unwrap_element(to_right_of)
		self.above = self._unwrap_element(above)
		self.to_left_of = self._unwrap_element(to_left_of)
		self.matches = PREFIX_IGNORE_CASE()
	def find_anywhere_in_curr_frame(self):
		raise NotImplementedError()
	@property
	def width(self):
		return self.first_occurrence.location.width
	@property
	def height(self):
		return self.first_occurrence.location.height
	@property
	def x(self):
		return self.first_occurrence.location.left
	@property
	def y(self):
		return self.first_occurrence.location.top
	@property
	def top_left(self):
		from helium import Point
		return Point(self.x, self.y)
	@property
	def web_element(self):
		return self.first_occurrence.unwrap()
	def find_all_occurrences(self):
		self._handle_closed_window()
		self._driver.switch_to.default_content()
		already_yielded = set()
		for frame_index in FrameIterator(self._driver):
			for occurrence in self._find_all_in_curr_frame():
				if occurrence.target in already_yielded:
					# We have seen this element before, but its frame had a
					# different index. This means that the frames have changed.
					# Abort:
					return
				occurrence.frame_index = frame_index
				yield occurrence
				already_yielded.add(occurrence.target)
	def _handle_closed_window(self):
		window_handles = self._driver.window_handles
		try:
			curr_window_handle = self._driver.current_window_handle
		except NoSuchWindowException:
			window_has_been_closed = True
		else:
			window_has_been_closed = curr_window_handle not in window_handles
		if window_has_been_closed:
			self._driver.switch_to.window(window_handles[0])
	def _find_all_in_curr_frame(self):
		search_regions = self._get_search_regions_in_curr_frame()
		for occurrence in self.find_anywhere_in_curr_frame():
			if not occurrence.is_displayed():
				continue
			if self._is_in_any_search_region(occurrence, search_regions):
				yield occurrence
	def _get_search_regions_in_curr_frame(self):
		result = []
		if self.below:
			result.append([
				elt.location.is_above
				for elt in self.below._find_all_in_curr_frame()
			])
		if self.to_right_of:
			result.append([
				elt.location.is_to_left_of
				for elt in self.to_right_of._find_all_in_curr_frame()
			])
		if self.above:
			result.append([
				elt.location.is_below
				for elt in self.above._find_all_in_curr_frame()
			])
		if self.to_left_of:
			result.append([
				elt.location.is_to_right_of
				for elt in self.to_left_of._find_all_in_curr_frame()
			])
		return result
	def _is_in_any_search_region(self, element, search_regions):
		for direction in search_regions:
			found = False
			for search_region in direction:
				if search_region(element.location):
					found = True
					break
			if not found:
				return False
		return True
	def _is_enabled(self):
		"""
		Useful for subclasses.
		"""
		return self.first_occurrence.get_attribute('disabled') is None
	def _unwrap_element(self, element):
		if isinstance(element, str):
			return TextImpl(self._driver, element)
		from helium import HTMLElement
		if isinstance(element, HTMLElement):
			return element._impl
		return element

class SImpl(HTMLElementImpl):
	def __init__(self, driver, selector, **kwargs):
		super(SImpl, self).__init__(driver, **kwargs)
		self.selector = selector
	def find_anywhere_in_curr_frame(self):
		wrap = lambda web_elements: list(map(WebElementWrapper, web_elements))
		if self.selector.startswith('@'):
			return wrap(self._driver.find_elements_by_name(self.selector[1:]))
		if self.selector.startswith('//'):
			return wrap(self._driver.find_elements_by_xpath(self.selector))
		return wrap(self._driver.find_elements_by_css_selector(self.selector))

class HTMLElementIdentifiedByXPath(HTMLElementImpl):
	def find_anywhere_in_curr_frame(self):
		x_path = self.get_xpath()
		return self._sort_search_result(
			list(map(
				WebElementWrapper, self._driver.find_elements_by_xpath(x_path)
			))
		)
	def _sort_search_result(self, search_result):
		keys_to_result_items = []
		for web_elt in search_result:
			try:
				key = self.get_sort_index(web_elt)
			except StaleElementReferenceException:
				pass
			else:
				keys_to_result_items.append((key, web_elt))
		sort_key = lambda tpl: tpl[0]
		keys_to_result_items.sort(key=sort_key)
		result_item = lambda tpl: tpl[1]
		return list(map(result_item, keys_to_result_items))
	def get_xpath(self):
		raise NotImplementedError()
	def get_sort_index(self, web_element):
		return self._driver.get_distance_to_last_manipulated(web_element) + 1

class HTMLElementContainingText(HTMLElementIdentifiedByXPath):
	def __init__(self, driver, text=None, **kwargs):
		super(HTMLElementContainingText, self).__init__(driver, **kwargs)
		self.search_text = text
	def get_xpath(self):
		xpath_base = "//" + self.get_xpath_node_selector() + \
					 predicate(self.matches.xpath('.', self.search_text))
		return '%s[not(self::script)][not(.%s)]' % (xpath_base, xpath_base)
	def get_xpath_node_selector(self):
		return '*'

class TextImpl(HTMLElementContainingText):
	def __init__(self, driver, text=None, include_free_text=True, **kwargs):
		super(TextImpl, self).__init__(driver, text, **kwargs)
		self.include_free_text = include_free_text
	@property
	def value(self):
		return self.first_occurrence.text
	def get_xpath(self):
		button_impl = ButtonImpl(self._driver, self.search_text)
		link_impl = LinkImpl(self._driver, self.search_text)
		components = [
			self._get_search_text_xpath(),
			button_impl.get_input_button_xpath(),
			link_impl.get_xpath()
		]
		if self.search_text and self.include_free_text:
			components.append(
				FreeText(self._driver, self.search_text).get_xpath()
			)
		return ' | '.join(components)
	def _get_search_text_xpath(self):
		if self.search_text:
			result = super(TextImpl, self).get_xpath()
		else:
			no_descendant_with_same_text = \
				"not(.//*[normalize-space(.)=normalize-space(self::*)])"
			result = '//*[text() and %s]' % no_descendant_with_same_text
		return result + "[not(self::option)]" + \
		       ("" if self.include_free_text else "[count(*) <= 1]")

class FreeText(HTMLElementContainingText):
	def get_xpath_node_selector(self):
		return 'text()'
	def get_xpath(self):
		return super(FreeText, self).get_xpath() + '/..'

class LinkImpl(HTMLElementContainingText):
	def get_xpath_node_selector(self):
		return 'a'
	def get_xpath(self):
		return super(LinkImpl, self).get_xpath() + ' | ' + \
			   "//a" + \
			   predicate(self.matches.xpath('@title', self.search_text)) + \
			   ' | ' + "//*[@role='link']" + \
			   predicate(self.matches.xpath('.', self.search_text))
	@property
	def href(self):
		return self.web_element.get_attribute('href')

class ListItemImpl(HTMLElementContainingText):
	def get_xpath_node_selector(self):
		return 'li'

class ButtonImpl(HTMLElementContainingText):
	def get_xpath_node_selector(self):
		return 'button'
	def is_enabled(self):
		aria_disabled = self.first_occurrence.get_attribute('aria-disabled')
		return self._is_enabled() \
			and (not aria_disabled or aria_disabled.lower() == 'false')
	def get_xpath(self):
		has_aria_label = self.matches.xpath('@aria-label', self.search_text)
		has_text = self.matches.xpath('.', self.search_text)
		has_text_or_aria_label = predicate_or(has_aria_label, has_text)
		return ' | '.join([
			super(ButtonImpl, self).get_xpath(), self.get_input_button_xpath(),
			"//*[@role='button']" + has_text_or_aria_label,
			"//button" + predicate(has_aria_label)
		])
	def get_input_button_xpath(self):
		if self.search_text:
			has_value = self.matches.xpath('@value', self.search_text)
			has_label = self.matches.xpath('@label', self.search_text)
			has_aria_label = self.matches.xpath('@aria-label', self.search_text)
			has_title = self.matches.xpath('@title', self.search_text)
			has_text = \
				predicate_or(has_value, has_label, has_aria_label, has_title)
		else:
			has_text = ''
		return "//input[@type='submit' or @type='button']" + has_text

class ImageImpl(HTMLElementIdentifiedByXPath):
	def __init__(self, driver, alt, **kwargs):
		super(ImageImpl, self).__init__(driver, **kwargs)
		self.alt = alt
	def get_xpath(self):
		return "//img" + predicate(self.matches.xpath('@alt', self.alt))

class LabelledElement(HTMLElementImpl):
	SECONDARY_SEARCH_DIMENSION_PENALTY_FACTOR = 1.5
	def __init__(self, driver, label=None, **kwargs):
		super(LabelledElement, self).__init__(driver, **kwargs)
		self.label = label
	def find_anywhere_in_curr_frame(self):
		if not self.label:
			result = self._find_elts()
		else:
			labels = TextImpl(
				self._driver, self.label, include_free_text=False
			).find_anywhere_in_curr_frame()
			if labels:
				result = list(self._filter_elts_belonging_to_labels(
					self._find_elts(), labels
				))
			else:
				result = self._find_elts_by_free_text()
		return sorted(result, key=self._driver.get_distance_to_last_manipulated)
	def _find_elts(self, xpath=None):
		if xpath is None:
			xpath = self.get_xpath()
		return list(map(
			WebElementWrapper, self._driver.find_elements_by_xpath(xpath)
		))
	def _find_elts_by_free_text(self):
		elt_types = [
			xpath.strip().lstrip('/') for xpath in self.get_xpath().split('|')
		]
		labels = '//text()' + predicate(self.matches.xpath('.', self.label))
		xpath = ' | '.join(
			[(labels + '/%s::' + elt_type + '[1]')
			 % ('preceding-sibling'
			    if 'checkbox' in elt_type or 'radio' in elt_type
			    else 'following')
			 for elt_type in elt_types]
		)
		return self._find_elts(xpath)
	def get_xpath(self):
		raise NotImplementedError()
	def get_primary_search_direction(self):
		return 'to_right_of'
	def get_secondary_search_direction(self):
		return 'below'
	def _filter_elts_belonging_to_labels(self, all_elts, labels):
		for label, elt in self._get_labels_with_explicit_elts(all_elts, labels):
			yield elt
			labels.remove(label)
			all_elts.remove(elt)
		labels_to_elts = self._get_related_elts(all_elts, labels)
		labels_to_elts = self._ensure_at_most_one_label_per_elt(labels_to_elts)
		self._retain_closest(labels_to_elts)
		for elts_for_label in list(labels_to_elts.values()):
			assert len(elts_for_label) <= 1
			if elts_for_label:
				yield next(iter(elts_for_label))
	def _get_labels_with_explicit_elts(self, all_elts, labels):
		for label in labels:
			if label.tag_name == 'label':
				label_target = label.get_attribute('for')
				if label_target:
					for elt in all_elts:
						elt_id = elt.get_attribute('id')
						if elt_id.lower() == label_target.lower():
							yield label, elt
	def _get_related_elts(self, all_elts, labels):
		result = {}
		for label in labels:
			for elt in all_elts:
				if self._are_related(elt, label):
					if label not in result:
						result[label] = set()
					result[label].add(elt)
		return result
	def _are_related(self, elt, label):
		if elt.location.intersects(label.location):
			return True
		prim_search_dir = self.get_primary_search_direction()
		sec_search_dir = self.get_secondary_search_direction()
		return label.location.distance_to(elt.location) <= 150 and (
			elt.location.is_in_direction(prim_search_dir, label.location) or
			elt.location.is_in_direction(sec_search_dir, label.location)
		)
	def _ensure_at_most_one_label_per_elt(self, labels_to_elts):
		elts_to_labels = inverse(labels_to_elts)
		self._retain_closest(elts_to_labels)
		return inverse(elts_to_labels)
	def _retain_closest(self, pivots_to_elts):
		for pivot, elts in list(pivots_to_elts.items()):
			if elts:
				# Would like to use a set literal {...} here, but this is not
				# supported in Python 2.6. Thus we need to use set([...]).
				pivots_to_elts[pivot] = set([self._find_closest(pivot, elts)])
	def _find_closest(self, to_pivot, among_elts):
		remaining_elts = iter(among_elts)
		result = next(remaining_elts)
		result_distance = self._compute_distance(result, to_pivot)
		for element in remaining_elts:
			element_distance = self._compute_distance(element, to_pivot)
			if element_distance < result_distance:
				result = element
				result_distance = element_distance
		return result
	def _compute_distance(self, elt_1, elt_2):
		loc_1 = elt_1.location
		loc_2 = elt_2.location
		if loc_1.is_in_direction(self.get_secondary_search_direction(), loc_2):
			factor = self.SECONDARY_SEARCH_DIMENSION_PENALTY_FACTOR
		else:
			factor = 1
		return factor * loc_1.distance_to(loc_2)

class CompositeElement(HTMLElementImpl):
	def __init__(self, driver, *args, **kwargs):
		super(CompositeElement, self).__init__(driver, **kwargs)
		self.args = [driver] + list(args)
		self.kwargs = kwargs
		self._first_element = None
	@property
	def first_element(self):
		if self._first_element is None:
			self._bind_to_first_occurrence()
			# find_anywhere_in_curr_frame() below now sets _first_element
		return self._first_element
	def find_anywhere_in_curr_frame(self):
		already_yielded = []
		for element in self.get_elements():
			for bound_gui_elt_impl in element.find_anywhere_in_curr_frame():
				if self._first_element is None:
					self._first_element = element
				if bound_gui_elt_impl not in already_yielded:
					yield bound_gui_elt_impl
					already_yielded.append(bound_gui_elt_impl)
	def get_elements(self):
		for element_type in self.get_element_types():
			yield element_type(*self.args, **self.kwargs)
	def get_element_types(self):
		raise NotImplementedError()

class ClickableText(CompositeElement):
	def get_element_types(self):
		return [ButtonImpl, TextImpl, ImageImpl]

class TextFieldImpl(CompositeElement):
	def get_element_types(self):
		return [
			StandardTextFieldWithPlaceholder, StandardTextFieldWithLabel,
			AriaTextFieldWithLabel
		]
	@property
	def value(self):
		return self.first_element.value
	def is_enabled(self):
		return self.first_element.is_enabled()
	def is_editable(self):
		return self.first_element.is_editable()

class StandardTextFieldWithLabel(LabelledElement):
	@property
	def value(self):
		return self.first_occurrence.get_attribute('value') or ''
	def is_enabled(self):
		return self._is_enabled()
	def is_editable(self):
		return self.first_occurrence.get_attribute('readOnly') is None
	def get_xpath(self):
		return \
			"//input[%s='text' or %s='email' or %s='password' or %s='number' " \
			"or %s='tel' or string-length(@type)=0]" % ((lower('@type'), ) * 5)\
			+ " | //textarea | //*[@contenteditable='true']"

class AriaTextFieldWithLabel(LabelledElement):
	@property
	def value(self):
		return self.first_occurrence.text
	def is_enabled(self):
		return self._is_enabled()
	def is_editable(self):
		return self.first_occurrence.get_attribute('readOnly') is None
	def get_xpath(self):
		return "//*[@role='textbox']"

class StandardTextFieldWithPlaceholder(HTMLElementIdentifiedByXPath):
	def __init__(self, driver, label, **kwargs):
		super(StandardTextFieldWithPlaceholder, self).__init__(driver, **kwargs)
		self.label = label
	@property
	def value(self):
		return self.first_occurrence.get_attribute('value') or ''
	def is_enabled(self):
		return self._is_enabled()
	def is_editable(self):
		return self.first_occurrence.get_attribute('readOnly') is None
	def get_xpath(self):
		return "(%s)%s" % (
			StandardTextFieldWithLabel(self.label).get_xpath(),
			predicate(self.matches.xpath('@placeholder', self.label))
		)

class FileInput(LabelledElement):
	def get_xpath(self):
		return "//input[@type='file']"

class ComboBoxImpl(CompositeElement):
	def get_element_types(self):
		return [ComboBoxIdentifiedByDisplayedValue, ComboBoxIdentifiedByLabel]
	def is_editable(self):
		return self.first_occurrence.tag_name != 'select'
	@property
	def value(self):
		selected_value = self._select_driver.first_selected_option
		if selected_value:
			return selected_value.text
		return None
	@property
	def options(self):
		return [option.text for option in self._select_driver.options]
	@property
	def _select_driver(self):
		return Select(self.web_element)

class ComboBoxIdentifiedByLabel(LabelledElement):
	def get_xpath(self):
		return "//select | //input[@list]"

class ComboBoxIdentifiedByDisplayedValue(HTMLElementContainingText):
	def get_xpath_node_selector(self):
		return 'option'
	def get_xpath(self):
		option_xpath = \
			super(ComboBoxIdentifiedByDisplayedValue, self).get_xpath()
		return option_xpath + '/ancestor::select[1]'
	def find_anywhere_in_curr_frame(self):
		all_cbs_with_a_matching_value = super(
			ComboBoxIdentifiedByDisplayedValue, self
		).find_anywhere_in_curr_frame()
		result = []
		for cb in all_cbs_with_a_matching_value:
			for selected_option in Select(cb.unwrap()).all_selected_options:
				if self.matches.text(selected_option.text, self.search_text):
					result.append(cb)
					break
		return result

class CheckBoxImpl(LabelledElement):
	def is_enabled(self):
		return self._is_enabled()
	def is_checked(self):
		return self.first_occurrence.get_attribute('checked') is not None
	def get_xpath(self):
		return "//input[@type='checkbox']"
	def get_primary_search_direction(self):
		return 'to_left_of'
	def get_secondary_search_direction(self):
		return 'to_right_of'

class RadioButtonImpl(LabelledElement):
	def is_selected(self):
		return self.first_occurrence.get_attribute('checked') is not None
	def get_xpath(self):
		return "//input[@type='radio']"
	def get_primary_search_direction(self):
		return 'to_left_of'
	def get_secondary_search_direction(self):
		return 'to_right_of'

class WindowImpl(GUIElementImpl):
	def __init__(self, driver, title=None):
		super(WindowImpl, self).__init__(driver)
		self.search_title = title
	def find_all_occurrences(self):
		result_scores = []
		for handle in self._driver.window_handles:
			window = WindowImpl.SeleniumWindow(self._driver, handle)
			if self.search_title is None:
				result_scores.append((0, window))
			else:
				title = window.title
				if title.startswith(self.search_title):
					score = len(title) - len(self.search_title)
					result_scores.append((score, window))
		score = lambda tpl: tpl[0]
		result_scores.sort(key=score)
		for score, window in result_scores:
			yield window
	@property
	def title(self):
		return self.first_occurrence.title
	@property
	def handle(self):
		return self.first_occurrence.handle
	class SeleniumWindow:
		def __init__(self, driver, handle):
			self.driver = driver
			self.handle = handle
			self._window_handle_before = None
		@property
		def title(self):
			with self:
				return self.driver.title
		def __enter__(self):
			try:
				self._window_handle_before = self.driver.current_window_handle
			except NoSuchWindowException as window_closed:
				do_switch = True
			else:
				do_switch = self._window_handle_before != self.handle
			if do_switch:
				self.driver.switch_to.window(self.handle)
		def __exit__(self, *_):
			if self._window_handle_before and \
				self.driver.current_window_handle != self._window_handle_before:
				self.driver.switch_to.window(self._window_handle_before)

class AlertImpl(GUIElementImpl):
	def __init__(self, driver, search_text=None):
		super(AlertImpl, self).__init__(driver)
		self.search_text = search_text
	def find_all_occurrences(self):
		try:
			result = self._driver.switch_to.alert
			text = result.text
			if self.search_text is None or text.startswith(self.search_text):
				yield result
		except NoAlertPresentException:
			pass
	@property
	def text(self):
		return self.first_occurrence.text
	def accept(self):
		first_occurrence = self.first_occurrence
		try:
			first_occurrence.accept()
		except WebDriverException as e:
			# Attempt to work around Selenium issue 3544:
			# https://code.google.com/p/selenium/issues/detail?id=3544
			msg = e.msg
			if msg and re.match(
					r"a\.document\.getElementsByTagName\([^\)]*\)\[0\] is "
					r"undefined", msg
			):
				sleep(0.25)
				first_occurrence.accept()
			else:
				raise
	def dismiss(self):
		self.first_occurrence.dismiss()
	def _write(self, text):
		self.first_occurrence.send_keys(text)
