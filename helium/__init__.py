"""
Helium's API is contained in module ``helium``. It is a simple Python API that
makes specifying web automation cases as simple as describing them to someone
looking over their shoulder at a screen.

The public functions and classes of Helium are listed below. If you wish to use
Helium functions in your Python scripts you can import them from the
``helium`` module::

	from helium import *
"""
from collections import namedtuple, OrderedDict
from copy import copy
from helium._impl import APIImpl
from helium._impl.util.html import get_easily_readable_snippet
from helium._impl.util.inspect_ import repr_args
from selenium.webdriver.common.keys import Keys

import helium._impl

def start_chrome(url=None, headless=False, maximize=False, options=None):
	"""
	:param url: URL to open.
	:type url: str
	:param headless: Whether to start Chrome in headless mode.
	:type headless: bool
	:param maximize: Whether to maximize the Chrome window.
	                 Ignored when `headless` is set to `True`.
	:type maximize: bool
	:param options: ChromeOptions to use for starting the browser
	:type options: :py:class:`selenium.webdriver.ChromeOptions`

	Starts an instance of Google Chrome::

		start_chrome()

	You can optionally open a URL::

		start_chrome("google.com")

	The `headless` switch lets you prevent the browser window from appearing on
	your screen::

		start_chrome(headless=True)
		start_chrome("google.com", headless=True)

	For more advanced configuration, use the `options` parameter::

		from selenium.webdriver import ChromeOptions
		options = ChromeOptions()
		options.add_argument('--proxy-server=1.2.3.4:5678')
		options.set_capability('goog:loggingPrefs', {'performance': 'ALL'})
		start_chrome(options=options)

	When no compatible ChromeDriver is found on your `PATH`, then `start_chrome`
	automatically downloads it using Selenium Manager.

	On shutdown of the Python interpreter, Helium terminates the ChromeDriver
	process but does not close the browser itself. If you want to close the
	browser at the end of your script, use the following command::

		kill_browser()
	"""
	return _get_api_impl().start_chrome_impl(url, headless, maximize, options)

def start_firefox(url=None, headless=False, options=None, profile=None):
	"""
	:param url: URL to open.
	:type url: str
	:param headless: Whether to start Firefox in headless mode.
	:type headless: bool
	:param options: FirefoxOptions to use for starting the browser.
	:type options: :py:class:`selenium.webdriver.FirefoxOptions`
	:param profile: FirefoxProfile to use for starting the browser.
	:type profile: :py:class:`selenium.webdriver.FirefoxProfile`

	Starts an instance of Firefox::

		start_firefox()

	If this doesn't work for you, then it may be that Helium's copy of
	geckodriver is not compatible with your version of Firefox. To fix this,
	place a copy of geckodriver on your `PATH`.

	You can optionally open a URL::

		start_firefox("google.com")

	The `headless` switch lets you prevent the browser window from appearing on
	your screen::

		start_firefox(headless=True)
		start_firefox("google.com", headless=True)

	For more advanced configuration, use the `options` parameter::

		from selenium.webdriver import FirefoxOptions
		options = FirefoxOptions()
		options.add_argument("--width=2560")
		options.add_argument("--height=1440")
		start_firefox(options=options)

	To set proxy, useragent, etc. (ie. things you find in about:config), use the
	`profile` parameter::

		from selenium.webdriver import FirefoxProfile
		profile = FirefoxProfile()
		SOCKS5_PROXY_HOST = "0.0.0.0"
		PROXY_PORT = 0
		profile.set_preference("network.proxy.type", 1)
		profile.set_preference("network.proxy.socks", SOCKS5_PROXY_HOST)
		profile.set_preference("network.proxy.socks_port", PROXY_PORT)
		profile.set_preference("network.proxy.socks_remote_dns", True)
		profile.set_preference("network.proxy.socks_version", 5)
		profile.set_preference("network.proxy.no_proxies_on", "localhost, 10.20.30.40")
		USER_AGENT = "Mozilla/5.0 ..."
		profile.set_preference("general.useragent.override", USER_AGENT)
		start_firefox(profile=profile)

	On shutdown of the Python interpreter, Helium cleans up all resources used
	for controlling the browser (such as the geckodriver process), but does
	not close the browser itself. If you want to terminate the browser at the
	end of your script, use the following command::

		kill_browser()
	"""
	return _get_api_impl().start_firefox_impl(url, headless, options, profile)

def go_to(url):
	"""
	:param url: URL to open.
	:type url: str

	Opens the specified URL in the current web browser window. For instance::

		go_to("google.com")
	"""
	_get_api_impl().go_to_impl(url)

def set_driver(driver):
	"""
	Sets the Selenium WebDriver used to execute Helium commands. See also
	:py:func:`get_driver`.
	"""
	_get_api_impl().set_driver_impl(driver)

def get_driver():
	"""
	Returns the Selenium WebDriver currently used by Helium to execute all
	commands. Each Helium command such as ``click("Login")`` is translated to a
	sequence of Selenium commands that are issued to this driver.
	"""
	return _get_api_impl().get_driver_impl()

def write(text, into=None):
	"""
	:param text: The text to be written.
	:type text: one of str, unicode
	:param into: The element to write into.
	:type into: one of str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement`, :py:class:`Alert`

	Types the given text into the active window. If parameter 'into' is given,
	writes the text into the text field or element identified by that parameter.
	Common examples of 'write' are::

		write("Hello World!")
		write("user12345", into="Username:")
		write("Michael", into=Alert("Please enter your name"))
	"""
	_get_api_impl().write_impl(text, into)

def press(key):
	"""
	:param key: Key or combination of keys to be pressed.

	Presses the given key or key combination. To press a normal letter key such
	as 'a' simply call `press` for it::

		press('a')

	You can also simulate the pressing of upper case characters that way::

		press('A')

	The special keys you can press are those given by Selenium's class
	:py:class:`selenium.webdriver.common.keys.Keys`. Helium makes all those keys
	available through its namespace, so you can just use them without having to
	refer to :py:class:`selenium.webdriver.common.keys.Keys`. For instance, to
	press the Enter key::

		press(ENTER)

	To press multiple keys at the same time, concatenate them with `+`. For
	example, to press Control + a, call::

		press(CONTROL + 'a')
	"""
	_get_api_impl().press_impl(key)

NULL         = Keys.NULL
CANCEL       = Keys.CANCEL
HELP         = Keys.HELP
BACK_SPACE   = Keys.BACK_SPACE
TAB          = Keys.TAB
CLEAR        = Keys.CLEAR
RETURN       = Keys.RETURN
ENTER        = Keys.ENTER
SHIFT        = Keys.SHIFT
LEFT_SHIFT   = Keys.LEFT_SHIFT
CONTROL      = Keys.CONTROL
LEFT_CONTROL = Keys.LEFT_CONTROL
ALT          = Keys.ALT
LEFT_ALT     = Keys.LEFT_ALT
PAUSE        = Keys.PAUSE
ESCAPE       = Keys.ESCAPE
SPACE        = Keys.SPACE
PAGE_UP      = Keys.PAGE_UP
PAGE_DOWN    = Keys.PAGE_DOWN
END          = Keys.END
HOME         = Keys.HOME
LEFT         = Keys.LEFT
ARROW_LEFT   = Keys.ARROW_LEFT
UP           = Keys.UP
ARROW_UP     = Keys.ARROW_UP
RIGHT        = Keys.RIGHT
ARROW_RIGHT  = Keys.ARROW_RIGHT
DOWN         = Keys.DOWN
ARROW_DOWN   = Keys.ARROW_DOWN
INSERT       = Keys.INSERT
DELETE       = Keys.DELETE
SEMICOLON    = Keys.SEMICOLON
EQUALS       = Keys.EQUALS
NUMPAD0      = Keys.NUMPAD0
NUMPAD1      = Keys.NUMPAD1
NUMPAD2      = Keys.NUMPAD2
NUMPAD3      = Keys.NUMPAD3
NUMPAD4      = Keys.NUMPAD4
NUMPAD5      = Keys.NUMPAD5
NUMPAD6      = Keys.NUMPAD6
NUMPAD7      = Keys.NUMPAD7
NUMPAD8      = Keys.NUMPAD8
NUMPAD9      = Keys.NUMPAD9
MULTIPLY     = Keys.MULTIPLY
ADD          = Keys.ADD
SEPARATOR    = Keys.SEPARATOR
SUBTRACT     = Keys.SUBTRACT
DECIMAL      = Keys.DECIMAL
DIVIDE       = Keys.DIVIDE
F1           = Keys.F1
F2           = Keys.F2
F3           = Keys.F3
F4           = Keys.F4
F5           = Keys.F5
F6           = Keys.F6
F7           = Keys.F7
F8           = Keys.F8
F9           = Keys.F9
F10          = Keys.F10
F11          = Keys.F11
F12          = Keys.F12
META         = Keys.META
COMMAND      = Keys.COMMAND

def click(element):
	"""
	:param element: The element or point to click.
	:type element: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`

	Clicks on the given element or point. Common examples are::

		click("Sign in")
		click(Button("OK"))
		click(Point(200, 300))
		click(ComboBox("File type").top_left + (50, 0))
	"""
	_get_api_impl().click_impl(element)

def doubleclick(element):
	"""
	:param element: The element or point to click.
	:type element: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`

	Performs a double-click on the given element or point. For example::

		doubleclick("Double click here")
		doubleclick(Image("Directories"))
		doubleclick(Point(200, 300))
		doubleclick(TextField("Username").top_left - (0, 20))
	"""
	_get_api_impl().doubleclick_impl(element)

def drag(element, to):
	"""
	:param element: The element or point to drag.
	:type element: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`
	:param to: The element or point to drag to.
	:type to: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`

	Drags the given element or point to the given location. For example::

		drag("Drag me!", to="Drop here.")

	The dragging is performed by hovering the mouse cursor over ``element``,
	pressing and holding the left mouse button, moving the mouse cursor over
	``to``, and then releasing the left mouse button again.

	This function is exclusively used for dragging elements inside one web page.
	If you wish to drag a file from the hard disk onto the browser window (eg.
	to initiate a file upload), use function :py:func:`drag_file`.
	"""
	_get_api_impl().drag_impl(element, to)

def press_mouse_on(element):
	_get_api_impl().press_mouse_on_impl(element)

def release_mouse_over(element):
	_get_api_impl().release_mouse_over_impl(element)

def find_all(predicate):
	"""
	Lets you find all occurrences of the given GUI element predicate. For
	instance, the following statement returns a list of all buttons with label
	"Open"::

		find_all(Button("Open"))

	Other examples are::

		find_all(Window())
		find_all(TextField("Address line 1"))

	The function returns a list of elements of the same type as the passed-in
	parameter. For instance, ``find_all(Button(...))`` yields a list whose
	elements are of type :py:class:`Button`.

	In a typical usage scenario, you want to pick out one of the occurrences
	returned by :py:func:`find_all`. In such cases, :py:func:`list.sort` can
	be very useful. For example, to find the leftmost "Open" button, you can
	write::

		buttons = find_all(Button("Open"))
		leftmost_button = sorted(buttons, key=lambda button: button.x)[0]
	"""
	return _get_api_impl().find_all_impl(predicate)

def scroll_down(num_pixels=100):
	"""
	Scrolls down the page the given number of pixels.
	"""
	_get_api_impl().scroll_down_impl(num_pixels)

def scroll_up(num_pixels=100):
	"""
	Scrolls the the page up the given number of pixels.
	"""
	_get_api_impl().scroll_up_impl(num_pixels)

def scroll_right(num_pixels=100):
	"""
	Scrolls the page to the right the given number of pixels.
	"""
	_get_api_impl().scroll_right_impl(num_pixels)

def scroll_left(num_pixels=100):
	"""
	Scrolls the page to the left the given number of pixels.
	"""
	_get_api_impl().scroll_left_impl(num_pixels)

def hover(element):
	"""
	:param element: The element or point to hover.
	:type element: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`

	Hovers the mouse cursor over the given element or point. For example::

		hover("File size")
		hover(Button("OK"))
		hover(Link("Download"))
		hover(Point(200, 300))
		hover(ComboBox("File type").top_left + (50, 0))
	"""
	_get_api_impl().hover_impl(element)

def rightclick(element):
	"""
	:param element: The element or point to click.
	:type element: str, unicode, :py:class:`HTMLElement`, \
:py:class:`selenium.webdriver.remote.webelement.WebElement` or :py:class:`Point`

	Performs a right click on the given element or point. For example::

		rightclick("Something")
		rightclick(Point(200, 300))
		rightclick(Image("captcha"))
	"""
	_get_api_impl().rightclick_impl(element)

def select(combo_box, value):
	"""
	:param combo_box: The combo box whose value should be changed.
	:type combo_box: str, unicode or :py:class:`ComboBox`
	:param value: The visible value of the combo box to be selected.

	Selects a value from a combo box. For example::

		select("Language", "English")
		select(ComboBox("Language"), "English")
	"""
	_get_api_impl().select_impl(combo_box, value)

def drag_file(file_path, to):
	"""
	Simulates the dragging of a file from the computer over the browser window
	and dropping it over the given element. This allows, for example, to attach
	files to emails in Gmail::

		click("COMPOSE")
		write("example@gmail.com", into="To")
		write("Email subject", into="Subject")
		drag_file(r"C:\\Documents\\notes.txt", to="Drop files here")
	"""
	_get_api_impl().drag_file_impl(file_path, to)

def attach_file(file_path, to=None):
	"""
	:param file_path: The path of the file to be attached.
	:param to: The file input element to which the file should be attached.

	Allows attaching a file to a file input element. For instance::

		attach_file("c:/test.txt", to="Please select a file:")

	The file input element is identified by its label. If you omit the ``to=``
	parameter, then Helium attaches the file to the first file input element it
	finds on the page.
	"""
	_get_api_impl().attach_file_impl(file_path, to=to)

def refresh():
	"""
	Refreshes the current page. If an alert dialog is open, then Helium first
	closes it.
	"""
	_get_api_impl().refresh_impl()

def wait_until(condition_fn, timeout_secs=10, interval_secs=0.5):
	"""
	:param condition_fn: A function taking no arguments that represents the \
	condition to be waited for.
	:param timeout_secs: The timeout, in seconds, after which the condition is \
	deemed to have failed.
	:param interval_secs: The interval, in seconds, at which the condition \
	function is polled to determine whether the wait has succeeded.

	Waits until the given condition function evaluates to true. This is most
	commonly used to wait for an element to exist::

		wait_until(Text("Finished!").exists)

	More elaborate conditions are also possible using Python lambda
	expressions. For instance, to wait until a text no longer exists::

		wait_until(lambda: not Text("Uploading...").exists())

	``wait_until`` raises
	:py:class:`selenium.common.exceptions.TimeoutException` if the condition is
	not satisfied within the given number of seconds. The parameter
	``interval_secs`` specifies the number of seconds Helium waits between
	evaluating the condition function.
	"""
	_get_api_impl().wait_until_impl(condition_fn, timeout_secs, interval_secs)

class Config:
	"""
	This class contains Helium's run-time configuration. To modify Helium's
	behaviour, simply assign to the properties of this class. For instance::

		Config.implicit_wait_secs = 0
	"""
	implicit_wait_secs = 10
	"""
	``implicit_wait_secs`` is Helium's analogue to Selenium's
	``.implicitly_wait(secs)``. Suppose you have a script that executes the
	following command::

		>>> click("Download")

	If the "Download" element is not immediately available, then Helium waits up
	to ``implicit_wait_secs`` for it to appear before raising a
	``LookupError``. This is useful in situations where the page takes slightly
	longer to load, or a GUI element only appears after a certain time.

	To disable Helium's implicit waits, simply execute::

		Config.implicit_wait_secs = 0

	Helium's implicit waits do not affect commands :py:func:`find_all` or
	:py:func:`GUIElement.exists`. Note also that setting
	``implicit_wait_secs`` does not affect the underlying Selenium driver
	(see :py:func:`get_driver`).

	For the best results, it is recommended to not use Selenium's
	``.implicitly_wait(...)`` in conjunction with Helium.
	"""

class GUIElement:
	def __init__(self):
		self._driver = _get_api_impl().require_driver()
		self._args = []
		self._kwargs = OrderedDict()
		self._impl_cached = None
	def exists(self):
		"""
		Evaluates to true if this GUI element exists.
		"""
		return self._impl.exists()
	def with_impl(self, impl):
		result = copy(self)
		result._impl = impl
		return result
	@property
	def _impl(self):
		if self._impl_cached is None:
			impl_class = \
				getattr(helium._impl, self.__class__.__name__ + 'Impl')
			self._impl_cached = impl_class(
				self._driver, *self._args, **self._kwargs
			)
		return self._impl_cached
	@_impl.setter
	def _impl(self, value):
		self._impl_cached = value
	def __repr__(self):
		return self._repr_constructor_args(self._args, self._kwargs)
	def _repr_constructor_args(self, args=None, kwargs=None):
		if args is None:
			args = []
		if kwargs is None:
			kwargs = {}
		return '%s(%s)' % (
			self.__class__.__name__,
			repr_args(self.__init__, args, kwargs, repr)
		)
	def _is_bound(self):
		return self._impl_cached is not None and self._impl_cached._is_bound()

class HTMLElement(GUIElement):
	def __init__(
			self, below=None, to_right_of=None, above=None, to_left_of=None
	):
		super(HTMLElement, self).__init__()
		self._kwargs['below'] = below
		self._kwargs['to_right_of'] = to_right_of
		self._kwargs['above'] = above
		self._kwargs['to_left_of'] = to_left_of
	@property
	def width(self):
		"""
		The width of this HTML element, in pixels.
		"""
		return self._impl.width
	@property
	def height(self):
		"""
		The height of this HTML element, in pixels.
		"""
		return self._impl.height
	@property
	def x(self):
		"""
		The x-coordinate on the page of the top-left point of this HTML element.
		"""
		return self._impl.x
	@property
	def y(self):
		"""
		The y-coordinate on the page of the top-left point of this HTML element.
		"""
		return self._impl.y
	@property
	def top_left(self):
		"""
		The top left corner of this element, as a :py:class:`helium.Point`.
		This point has exactly the coordinates given by this element's `.x` and
		`.y` properties. `top_left` is for instance useful for clicking at an
		offset of an element::

			click(Button("OK").top_left + (30, 15))
		"""
		return self._impl.top_left
	@property
	def web_element(self):
		"""
		The Selenium WebElement corresponding to this element.
		"""
		return self._impl.web_element
	def __repr__(self):
		if self._is_bound():
			element_html = self.web_element.get_attribute('outerHTML')
			return get_easily_readable_snippet(element_html)
		else:
			return super(HTMLElement, self).__repr__()

class S(HTMLElement):
	"""
	:param selector: The selector used to identify the HTML element(s).

	A jQuery-style selector for identifying HTML elements by ID, name, CSS
	class, CSS selector or XPath. For example: Say you have an element with
	ID "myId" on a web page, such as ``<div id="myId" .../>``.
	Then you can identify this element using ``S`` as follows::

		S("#myId")

	The parameter which you pass to ``S(...)`` is interpreted by Helium
	according to these rules:

	 * If it starts with an ``@``, then it identifies elements by HTML ``name``.
	   Eg. ``S("@btnName")`` identifies an element with ``name="btnName"``.
	 * If it starts with ``//``, then Helium interprets it as an XPath.
	 * Otherwise, Helium interprets it as a CSS selector. This in particular
	   lets you write ``S("#myId")`` to identify an element with ``id="myId"``,
	   or ``S(".myClass")`` to identify elements with ``class="myClass"``.

	``S`` also makes it possible to read plain text data from a web page. For
	example, suppose you have a table of people's email addresses. Then you
	can read the list of email addresses as follows::

		email_cells = find_all(S("table > tr > td", below="Email"))
		emails = [cell.web_element.text for cell in email_cells]

	Where ``email`` is the column header (``<th>Email</th>``). Similarly to
	``below`` and ``to_right_of``, the keyword parameters ``above`` and
	``to_left_of`` can be used to search for elements above and to the left
	of other web elements.
	"""
	def __init__(self, selector, below=None, to_right_of=None, above=None,
			to_left_of=None):
		super(S, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(selector)

class Text(HTMLElement):
	"""
	Lets you identify any text or label on a web page. This is most useful for
	checking whether a particular text exists::

		if Text("Do you want to proceed?").exists():
		    click("Yes")

	``Text`` also makes it possible to read plain text data from a web page. For
	example, suppose you have a table of people's email addresses. Then you
	can read John's email addresses as follows::

		Text(below="Email", to_right_of="John").value

	Similarly to ``below`` and ``to_right_of``, the keyword parameters ``above``
	and ``to_left_of`` can be used to search for texts above and to the left of
	other web elements.
	"""
	def __init__(
			self, text=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(Text, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(text)
	@property
	def value(self):
		"""
		Returns the current value of this Text object.
		"""
		return self._impl.value

class Link(HTMLElement):
	"""
	Lets you identify a link on a web page. A typical usage of ``Link`` is::

		click(Link("Sign in"))

	You can also read a ``Link``'s properties. This is most typically used to
	check for a link's existence before clicking on it::

		if Link("Sign in").exists():
		    click(Link("Sign in"))

	When there are multiple occurrences of a link on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(Link("Block User", to_right_of="John Doe"))
	"""
	def __init__(
			self, text=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(Link, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(text)
	@property
	def href(self):
		"""
		Returns the URL of the page the link goes to.
		"""
		return self._impl.href

class ListItem(HTMLElement):
	"""
	Lets you identify a list item (HTML ``<li>`` element) on a web page. This is
	often useful for interacting with elements of a navigation bar::

		click(ListItem("News Feed"))

	In other cases such as an automated test, you might want to query the
	properties of a ``ListItem``. For example, the following line checks whether
	a list item with text "List item 1" exists, and raises an error if not::

		assert ListItem("List item 1").exists()

	When there are multiple occurrences of a list item on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(ListItem("List item 1", below="My first list:"))
	"""
	def __init__(
			self, text=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(ListItem, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(text)

class Button(HTMLElement):
	"""
	Lets you identify a button on a web page. A typical usage of ``Button`` is::

		click(Button("Log In"))

	``Button`` also lets you read a button's properties. For example, the
	following snippet clicks button "OK" only if it exists::

		if Button("OK").exists():
		    click(Button("OK"))

	When there are multiple occurrences of a button on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(Button("Log In", below=TextField("Password")))
	"""
	def __init__(
			self, text=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(Button, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(text)
	def is_enabled(self):
		"""
		Returns true if this UI element can currently be interacted with.
		"""
		return self._impl.is_enabled()

class Image(HTMLElement):
	"""
	Lets you identify an image (HTML ``<img>`` element) on a web page.
	Typically, this is done via the image's alt text. For instance::

		click(Image(alt="Helium Logo"))

	You can also query an image's properties. For example, the following snippet
	clicks on the image with alt text "Helium Logo" only if it exists::

		if Image("Helium Logo").exists():
		    click(Image("Helium Logo"))

	When there are multiple occurrences of an image on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(Image("Helium Logo", to_left_of=ListItem("Download")))
	"""
	def __init__(
			self, alt=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(Image, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(alt)

class TextField(HTMLElement):
	"""
	Lets you identify a text field on a web page. This is most typically done to
	read the value of a text field. For example::

		TextField("First name").value

	This returns the value of the "First name" text field. If it is empty, the
	empty string "" is returned.

	When there are multiple occurrences of a text field on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		TextField("Address line 1", below="Billing Address:").value
	"""
	def __init__(
			self, label=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(TextField, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(label)
	@property
	def value(self):
		"""
		Returns the current value of this text field. '' if there is no value.
		"""
		return self._impl.value
	def is_enabled(self):
		"""
		Returns true if this UI element can currently be interacted with.

		The difference between a text field being 'enabled' and 'editable' is
		mostly visual: If a text field is not enabled, it is usually greyed out,
		whereas if it is not editable it looks normal. See also ``is_editable``.
		"""
		return self._impl.is_enabled()
	def is_editable(self):
		"""
		Returns true if the value of this UI element can be modified.

		The difference between a text field being 'enabled' and 'editable' is
		mostly visual: If a text field is not enabled, it is usually greyed out,
		whereas if it is not editable it looks normal. See also ``is_enabled``.
		"""
		return self._impl.is_editable()

class ComboBox(HTMLElement):
	"""
	Lets you identify a combo box on a web page. This can for instance be used
	to determine the current value of a combo box::

		ComboBox("Language").value

	A ComboBox may be *editable*, which means that it is possible to type in
	arbitrary values in addition to selecting from a predefined drop-down list
	of values. The property :py:func:`ComboBox.is_editable` can be used to
	determine whether this is the case for a particular combo box instance.

	When there are multiple occurrences of a combo box on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		select(ComboBox(to_right_of="John Doe", below="Status"), "Active")

	This sets the Status of John Doe to Active on the page.
	"""
	def __init__(
			self, label=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(ComboBox, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(label)
	def is_editable(self):
		"""
		Returns whether this combo box allows entering an arbitrary text in
		addition to selecting predefined values from a drop-down list.
		"""
		return self._impl.is_editable()
	@property
	def value(self):
		"""
		Returns the currently selected combo box value.
		"""
		return self._impl.value
	@property
	def options(self):
		"""
		Returns a list of all possible options available to choose from in the
		ComboBox.
		"""
		return self._impl.options

class CheckBox(HTMLElement):
	"""
	Lets you identify a check box on a web page. To tick a currently unselected
	check box, use::

		click(CheckBox("I agree"))

	``CheckBox`` also lets you read the properties of a check box. For example,
	the method :py:func:`CheckBox.is_checked` can be used to only click a check
	box if it isn't already checked::

		if not CheckBox("I agree").is_checked():
		    click(CheckBox("I agree"))

	When there are multiple occurrences of a check box on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(CheckBox("Stay signed in", below=Button("Sign in")))
	"""
	def __init__(
			self, label=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(CheckBox, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(label)
	def is_enabled(self):
		"""
		Returns True if this GUI element can currently be interacted with.
		"""
		return self._impl.is_enabled()
	def is_checked(self):
		"""
		Returns True if this GUI element is checked (selected).
		"""
		return self._impl.is_checked()

class RadioButton(HTMLElement):
	"""
	Lets you identify a radio button on a web page. To select a currently
	unselected radio button, use::

		click(RadioButton("Windows"))

	``RadioButton`` also lets you read the properties of a radio button. For
	example, the method :py:func:`RadioButton.is_selected` can be used to only
	click a radio button if it isn't already selected::

		if not RadioButton("Windows").is_selected():
		    click(RadioButton("Windows"))

	When there are multiple occurrences of a radio button on a page, you can
	disambiguate between them using the keyword parameters ``below``,
	``to_right_of``, ``above`` and ``to_left_of``. For instance::

		click(RadioButton("I accept", below="License Agreement"))
	"""
	def __init__(
			self, label=None, below=None, to_right_of=None, above=None,
			to_left_of=None
	):
		super(RadioButton, self).__init__(
			below=below, to_right_of=to_right_of, above=above,
			to_left_of=to_left_of
		)
		self._args.append(label)
	def is_selected(self):
		"""
		Returns true if this radio button is selected.
		"""
		return self._impl.is_selected()

class Window(GUIElement):
	"""
	Lets you identify individual windows of the currently open browser session.
	"""
	def __init__(self, title=None):
		super(Window, self).__init__()
		self._args.append(title)
	@property
	def title(self):
		"""
		Returns the title of this Window.
		"""
		return self._impl.title
	@property
	def handle(self):
		"""
		Returns the Selenium driver window handle assigned to this window. Note
		that this window handle is simply an abstract identifier and bears no
		relationship to the corresponding operating system handle (HWND on
		Windows).
		"""
		return self._impl.handle
	def __repr__(self):
		if self._is_bound():
			return self._repr_constructor_args([self.title])
		else:
			return super(Window, self).__repr__()

class Alert(GUIElement):
	"""
	Lets you identify and interact with JavaScript alert boxes.
	"""
	def __init__(self, search_text=None):
		super(Alert, self).__init__()
		self._args.append(search_text)
	@property
	def text(self):
		"""
		The text displayed in the alert box.
		"""
		return self._impl.text
	def accept(self):
		"""
		Accepts this alert. This typically corresponds to clicking the "OK"
		button inside the alert. The typical way to use this method is::

			>>> Alert().accept()

		This accepts the currently open alert.
		"""
		self._impl.accept()
	def dismiss(self):
		"""
		Dismisses this alert. This typically corresponds to clicking the
		"Cancel" or "Close" button of the alert. The typical way to use this
		method is::

			>>> Alert().dismiss()

		This dismisses the currently open alert.
		"""
		self._impl.dismiss()
	def __repr__(self):
		if self._is_bound():
			return self._repr_constructor_args([self.text])
		else:
			return super(Alert, self).__repr__()

class Point(namedtuple('Point', ['x', 'y'])):
	"""
	A clickable point. To create a ``Point`` at an offset of an existing point,
	use ``+`` and ``-``::

		>>> point = Point(x=10, y=25)
		>>> point + (10, 0)
		Point(x=20, y=25)
		>>> point - (0, 10)
		Point(x=10, y=15)
	"""
	def __new__(cls, x=0, y=0):
		return cls.__bases__[0].__new__(cls, x, y)
	def __init__(self, x=0, y=0):
		# tuple is immutable so we can't do anything here. The initialization
		# happens in __new__(...) above.
		pass
	@property
	def x(self):
		"""
		The x coordinate of the point.
		"""
		return self[0]
	@property
	def y(self):
		"""
		The y coordinate of the point.
		"""
		return self[1]
	def __eq__(self, other):
		return (self.x, self.y) == other
	def __ne__(self, other):
		return not self == other
	def __hash__(self):
		return self.x + 7 * self.y
	def __add__(self, delta):
		dx, dy = delta
		return Point(self.x + dx, self.y + dy)
	def __radd__(self, delta):
		return self.__add__(delta)
	def __sub__(self, delta):
		dx, dy = delta
		return Point(self.x - dx, self.y - dy)
	def __rsub__(self, delta):
		x, y = delta
		return Point(x - self.x, y - self.y)

def switch_to(window):
	"""
	:param window: The title (string) of a browser window or a \
:py:class:`Window` object

	Switches to the given browser window. For example::

		switch_to("Google")

	This searches for a browser window whose title contains "Google", and
	activates it.

	If there are multiple windows with the same title, then you can use
	:py:func:`find_all` to find all open windows, pick out the one you want and
	pass that to ``switch_to``. For example, the following snippet switches to
	the first window in the list of open windows::

		switch_to(find_all(Window())[0])
	"""
	_get_api_impl().switch_to_impl(window)

def kill_browser():
	"""
	Closes the current browser with all associated windows and potentially open
	dialogs. Dialogs opened as a response to the browser closing (eg. "Are you
	sure you want to leave this page?") are also ignored and closed.

	This function is most commonly used to close the browser at the end of an
	automation run::

		start_chrome()
		...
		# Close Chrome:
		kill_browser()
	"""
	_get_api_impl().kill_browser_impl()

def highlight(element):
	"""
	:param element: The element to highlight.

	Highlights the given element on the webpage by drawing a red rectangle
	around it. This is useful for debugging purposes. For example::

		highlight("Helium")
		highlight(Button("Sign in"))
	"""
	_get_api_impl().highlight_impl(element)

def _get_api_impl():
	global _API_IMPL
	if _API_IMPL is None:
		_API_IMPL = APIImpl()
	return _API_IMPL

_API_IMPL = None