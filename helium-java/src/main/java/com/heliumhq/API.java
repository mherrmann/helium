package com.heliumhq;

import com.heliumhq.api_impl.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static com.heliumhq.api_impl.application_context.ApplicationContext.
		getApplicationContext;

/**
 * Helium is a library that makes web automation as simple as giving
 * instructions to a colleague, looking over his/her shoulder at a screen.
 * <p>
 * The public functions and classes of Helium are listed below. If you wish to
 * use them in your scripts, all that is required is the following import:
 *
 * {@code
 * import static com.heliumhq.API.*;}
 */
public class API {

	public static WebDriver startFirefox() {
		return getAPIImpl().startFirefoxImpl();
	}
	public static WebDriver startFirefox(boolean headless) {
		return getAPIImpl().startFirefoxImpl(headless);
	}
	public static WebDriver startFirefox(String url) {
		return getAPIImpl().startFirefoxImpl(url);
	}

	/**
	 * Starts an instance of Firefox. You can optionally open a URL and/or start
	 * Firefox in headless mode. For instance:
	 *
	 * {@code
	 * startFirefox();
	 * startFirefox("google.com");
	 * startFirefox(true);
	 * startFirefox("google.com", true);}
	 *
	 * Helium does not automatically close the browser when Java shuts down. To
	 * terminate the browser at the end of your script, use the following
	 * command:
	 *
	 * {@code
	 * killBrowser();}
	 *
	 * @param url The URL to open.
	 * @return a Selenium {@link org.openqa.selenium.WebDriver} object
	 * representing the newly opened browser.
	 */
	public static WebDriver startFirefox(String url, boolean headless) {
		return getAPIImpl().startFirefoxImpl(url, headless);
	}

	public static WebDriver startChrome() {
		return getAPIImpl().startChromeImpl();
	}
	public static WebDriver startChrome(boolean headless) {
		return getAPIImpl().startChromeImpl(headless);
	}
	public static WebDriver startChrome(String url) {
		return getAPIImpl().startChromeImpl(url);
	}

	/**
	 * Starts an instance of Google Chrome. You can optionally open a URL and/or
	 * start Chrome in headless mode. For instance:
	 *
	 * {@code
	 * startChrome();
	 * startChrome("google.com");
	 * startChrome(true);
	 * startChrome("google.com", true);}
	 *
	 * When Java shuts down, Helium cleans up all resources used for controlling
	 * the browser (such as the ChromeDriver process), but does not close the
	 * browser itself. To terminate the browser at the end of your script, use
	 * the following command:
	 *
	 * {@code
	 * killBrowser();}
	 *
	 * @param url The URL to open.
	 * @param headless Whether Chrome should be started in headless mode.
	 * Defaults to <code>false</code>.
	 * @return a Selenium {@link org.openqa.selenium.WebDriver} object
	 * representing the newly opened browser.
	 */
	public static WebDriver startChrome(String url, boolean headless) {
		return getAPIImpl().startChromeImpl(url, headless);
	}

	public static WebDriver startIE() {
		return getAPIImpl().startIEImpl();
	}

	/**
	 * (Windows only) Starts an instance of Internet Explorer, optionally
	 * opening the specified URL. For instance:
	 *
	 * {@code
	 * startIE();
	 * startIE("google.com");}
	 *
	 * When Java shuts down, Helium cleans up all resources used for controlling
	 * the browser (such as the IEDriverServer process), but does not close the
	 * browser itself. To terminate the browser at the end of your script, use
	 * the following command:
	 *
	 * {@code
	 * killBrowser();}
	 *
	 * @param url The URL to open.
	 * @return a Selenium {@link org.openqa.selenium.WebDriver} object
	 * representing the newly opened browser.
	 */
	public static WebDriver startIE(String url) {
		return getAPIImpl().startIEImpl(url);
	}

	/**
	 * Opens the specified URL in the current web browser window. For instance:
	 *
	 * {@code
	 * goTo("google.com");}
	 *
	 * @param url The URL to open.
	 */
	public static void goTo(String url) {
		getAPIImpl().goToImpl(url);
	}

	/**
	 * Sets the Selenium {@link org.openqa.selenium.WebDriver} used to execute
	 * Helium commands. See also {@link API#getDriver()}.
	 *
	 * @param driver The new WebDriver against which all future Helium commands
	 *               will be issued.
	 */
	public static void setDriver(WebDriver driver) {
		getAPIImpl().setDriverImpl(driver);
	}

	/**
	 * Returns the Selenium {@link org.openqa.selenium.WebDriver} currently used
	 * by Helium to execute all commands. Each Helium command such as
	 * <code>click("Login");</code> is translated to a sequence of Selenium
	 * commands that are issued to this driver.
	 *
	 * @return A {@link org.openqa.selenium.WebDriver} object.
	 */
	public static WebDriver getDriver() {
		return getAPIImpl().getDriverImpl();
	}

	public static void write(String text) {
		getAPIImpl().writeImpl(text);
	}

	/**
	 * Types the given text into the active window. If parameter 'into' is
	 * given, writes the text into the text field or element identified by that
	 * parameter. Common examples of 'write' are:
	 *
	 * {@code
	 * write("Hello World!");
	 * write("user12345", into("Username:"));
	 * write("Michael", into(Alert("Please enter your name")));}
	 *
	 * As you can see in the above examples, the function
	 * {@link API#into} is used to make the script more readable.
	 *
	 * @param text    The text to be written.
	 * @param into    The element to write into.
	 */
	public static void write(String text, String into) {
		getAPIImpl().writeImpl(text, into);
	}
	public static void write(String text, WebElement into) {
		getAPIImpl().writeImpl(text, into);
	}
	public static void write(String text, HTMLElement into) {
		getAPIImpl().writeImpl(text, into.getImpl());
	}
	public static void write(String text, Alert into) {
		getAPIImpl().writeImpl(text, into.getImpl());
	}

	/**
	 * The purpose of the function 'into' is to make Helium scripts more
	 * readable. It simply returns the parameter passed into it. It lets you
	 * write:
	 *
	 * {@code
	 * write("user12345", into("Username:"));}
	 *
	 * instead of the much less readable:
	 *
	 * {@code
	 * write("user12345", "Username:");}
	 *
	 * @param element    The element to return.
	 * @return The <code>element</code> parameter.
	 * @see com.heliumhq.API#to
	 */
	public static String into(String element) {
		return element;
	}
	public static WebElement into(WebElement element) {
		return element;
	}
	public static HTMLElement into(HTMLElement element) {
		return element;
	}
	public static Alert into(Alert element) {
		return element;
	}

	/**
	 * Presses the given key or key combination. To press a normal letter key
	 * such as "a" simply call <code>press</code> for it:
	 *
	 * {@code
	 * press("a");}
	 *
	 * You can also simulate the pressing of upper case characters that way::
	 *
	 * {@code
	 * press("A");}
	 *
	 * The special keys you can press are those given by Selenium's enum
	 * {@link org.openqa.selenium.Keys}. Helium makes all those keys available
	 * through its namespace, so you can just use them without having to refer
	 * to {@link org.openqa.selenium.Keys}. For instance, to press the Enter
	 * key:
	 *
	 * {@code
	 * press(ENTER);}
	 *
	 * To press several keys at the same time, concatenate them with
	 * <code>+</code>. For example, to press Control + a, call:
	 *
	 * {@code
	 * press(CONTROL + "a");}
	 *
	 * @param key	The key or combination of keys to press.
	 */
	public static void press(CharSequence key) {
		getAPIImpl().pressImpl(key);
	}

	public final static CharSequence NULL         = Keys.NULL;
	public final static CharSequence CANCEL       = Keys.CANCEL;
	public final static CharSequence HELP         = Keys.HELP;
	public final static CharSequence BACK_SPACE   = Keys.BACK_SPACE;
	public final static CharSequence TAB          = Keys.TAB;
	public final static CharSequence CLEAR        = Keys.CLEAR;
	public final static CharSequence RETURN       = Keys.RETURN;
	public final static CharSequence ENTER        = Keys.ENTER;
	public final static CharSequence SHIFT        = Keys.SHIFT;
	public final static CharSequence LEFT_SHIFT   = Keys.LEFT_SHIFT;
	public final static CharSequence CONTROL      = Keys.CONTROL;
	public final static CharSequence LEFT_CONTROL = Keys.LEFT_CONTROL;
	public final static CharSequence ALT          = Keys.ALT;
	public final static CharSequence LEFT_ALT     = Keys.LEFT_ALT;
	public final static CharSequence PAUSE        = Keys.PAUSE;
	public final static CharSequence ESCAPE       = Keys.ESCAPE;
	public final static CharSequence SPACE        = Keys.SPACE;
	public final static CharSequence PAGE_UP      = Keys.PAGE_UP;
	public final static CharSequence PAGE_DOWN    = Keys.PAGE_DOWN;
	public final static CharSequence END          = Keys.END;
	public final static CharSequence HOME         = Keys.HOME;
	public final static CharSequence LEFT         = Keys.LEFT;
	public final static CharSequence ARROW_LEFT   = Keys.ARROW_LEFT;
	public final static CharSequence UP           = Keys.UP;
	public final static CharSequence ARROW_UP     = Keys.ARROW_UP;
	public final static CharSequence RIGHT        = Keys.RIGHT;
	public final static CharSequence ARROW_RIGHT  = Keys.ARROW_RIGHT;
	public final static CharSequence DOWN         = Keys.DOWN;
	public final static CharSequence ARROW_DOWN   = Keys.ARROW_DOWN;
	public final static CharSequence INSERT       = Keys.INSERT;
	public final static CharSequence DELETE       = Keys.DELETE;
	public final static CharSequence SEMICOLON    = Keys.SEMICOLON;
	public final static CharSequence EQUALS       = Keys.EQUALS;
	public final static CharSequence NUMPAD0      = Keys.NUMPAD0;
	public final static CharSequence NUMPAD1      = Keys.NUMPAD1;
	public final static CharSequence NUMPAD2      = Keys.NUMPAD2;
	public final static CharSequence NUMPAD3      = Keys.NUMPAD3;
	public final static CharSequence NUMPAD4      = Keys.NUMPAD4;
	public final static CharSequence NUMPAD5      = Keys.NUMPAD5;
	public final static CharSequence NUMPAD6      = Keys.NUMPAD6;
	public final static CharSequence NUMPAD7      = Keys.NUMPAD7;
	public final static CharSequence NUMPAD8      = Keys.NUMPAD8;
	public final static CharSequence NUMPAD9      = Keys.NUMPAD9;
	public final static CharSequence MULTIPLY     = Keys.MULTIPLY;
	public final static CharSequence ADD          = Keys.ADD;
	public final static CharSequence SEPARATOR    = Keys.SEPARATOR;
	public final static CharSequence SUBTRACT     = Keys.SUBTRACT;
	public final static CharSequence DECIMAL      = Keys.DECIMAL;
	public final static CharSequence DIVIDE       = Keys.DIVIDE;
	public final static CharSequence F1           = Keys.F1;
	public final static CharSequence F2           = Keys.F2;
	public final static CharSequence F3           = Keys.F3;
	public final static CharSequence F4           = Keys.F4;
	public final static CharSequence F5           = Keys.F5;
	public final static CharSequence F6           = Keys.F6;
	public final static CharSequence F7           = Keys.F7;
	public final static CharSequence F8           = Keys.F8;
	public final static CharSequence F9           = Keys.F9;
	public final static CharSequence F10          = Keys.F10;
	public final static CharSequence F11          = Keys.F11;
	public final static CharSequence F12          = Keys.F12;
	public final static CharSequence META         = Keys.META;
	public final static CharSequence COMMAND      = Keys.COMMAND;

	/**
	 * Clicks on the given element or point. Common examples are:
	 *
	 * {@code
	 * click("Sign in");
	 * click(Button("OK"));
	 * click(Point(200, 300));
	 * click(ComboBox("File type").getTopLeft().withOffset(50, 0));}
	 *
	 * @param element    The element to click.
	 */
	public static void click(String element) {
		getAPIImpl().clickImpl(element);
	}
	public static void click(WebElement element) {
		getAPIImpl().clickImpl(element);
	}
	public static void click(HTMLElement element) {
		getAPIImpl().clickImpl(element.getImpl());
	}

	/**
	 * @param point    The point to click.
	 */
	public static void click(Point point) {
		getAPIImpl().clickImpl(point);
	}

	/**
	 * Performs a double click on the given element or point. Common examples
	 * are:
	 *
	 * {@code
	 * doubleclick("Double click here");
	 * doubleclick(Image("Directories"));
	 * doubleclick(Point(200, 300));
	 * click(ComboBox("File type").getTopLeft().withOffset(50, 0));}
	 *
	 * @param element    The element to double click.
	 */
	public static void doubleclick(String element) {
		getAPIImpl().doubleclickImpl(element);
	}
	public static void doubleclick(WebElement element) {
		getAPIImpl().doubleclickImpl(element);
	}
	public static void doubleclick(HTMLElement element) {
		getAPIImpl().doubleclickImpl(element.getImpl());
	}

	/**
	 * @param point    The point to double click.
	 */
	public static void doubleclick(Point point) {
		getAPIImpl().doubleclickImpl(point);
	}

	/**
	 * Drags the given element or point to the given location. For example:
	 *
	 * {@code
	 * drag("Drag me!", to("Drop here."));}
	 *
	 * The dragging is performed by hovering the mouse cursor over
	 * <code>element</code>, pressing and holding the left mouse button, moving
	 * the mouse cursor over <code>to</code>, and then releasing the left mouse
	 * button again.
	 *
	 * @param element	The element or point to drag.
	 * @param to        The place to drop the dragged element.
	 */
	public static void drag(String element, String to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(String element, WebElement to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(String element, HTMLElement to) {
		getAPIImpl().dragImpl(element, to.getImpl());
	}
	public static void drag(String element, Point to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(WebElement element, String to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(WebElement element, WebElement to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(WebElement element, HTMLElement to) {
		getAPIImpl().dragImpl(element, to.getImpl());
	}
	public static void drag(WebElement element, Point to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(HTMLElement element, String to) {
		getAPIImpl().dragImpl(element.getImpl(), to);
	}
	public static void drag(HTMLElement element, WebElement to) {
		getAPIImpl().dragImpl(element.getImpl(), to);
	}
	public static void drag(HTMLElement element, HTMLElement to) {
		getAPIImpl().dragImpl(element.getImpl(), to.getImpl());
	}
	public static void drag(HTMLElement element, Point to) {
		getAPIImpl().dragImpl(element.getImpl(), to);
	}
	public static void drag(Point element, String to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(Point element, WebElement to) {
		getAPIImpl().dragImpl(element, to);
	}
	public static void drag(Point element, HTMLElement to) {
		getAPIImpl().dragImpl(element, to.getImpl());
	}
	public static void drag(Point element, Point to) {
		getAPIImpl().dragImpl(element, to);
	}

	/**
	 * Lets you find all occurrences of the given GUI element predicate. For
	 * instance, the following statement returns a list of all buttons with
	 * label "Open":
	 *
	 * {@code
	 * findAll(Button("Open"));}
	 *
	 * Other examples are:
	 *
	 * {@code
	 * findAll(Window());
	 * findAll(TextField("Address line 1"));}
	 *
	 * The function returns a list of elements of the same type as the passed-in
	 * parameter. For instance, <code>findAll(Button(...));</code> yields
	 * an object of type <code>List&lt;Button&gt;</code>.
	 *
	 * @param predicate    Any {@link com.heliumhq.API.GUIElement}.
	 * @return A list of occurrences of the given predicate on the current page.
	 */
	public static <G extends GUIElement> List<G> findAll(G predicate) {
		List<G> result = new ArrayList<G>();
		for (Object impl : getAPIImpl().findAllImpl(predicate.getImpl()))
			try {
				Constructor<G> constructor = (Constructor<G>) predicate.
						getClass().getDeclaredConstructor(impl.getClass());
				constructor.setAccessible(true);
				result.add(constructor.newInstance(impl));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		return result;
	}

	public static void scrollDown() {
		getAPIImpl().scrollDownImpl();
	}

	/**
	 * Scrolls down the page. The distance scrolled can be given by parameter
	 * <code>numPixels</code>, and defaults to <code>100</code>.
	 *
	 * @param numPixels    The distance to scroll in pixels.
	 */
	public static void scrollDown(int numPixels) {
		getAPIImpl().scrollDownImpl(numPixels);
	}

	public static void scrollUp() {
		getAPIImpl().scrollUpImpl();
	}

	/**
	 * Scrolls up the page. The distance scrolled can be given by parameter
	 * <code>numPixels</code>, and defaults to <code>100</code>.
	 *
	 * @param numPixels    The distance to scroll in pixels.
	 */
	public static void scrollUp(int numPixels) {
		getAPIImpl().scrollUpImpl(numPixels);
	}

	public static void scrollRight() {
		getAPIImpl().scrollRightImpl();
	}

	/**
	 * Scrolls the page to the right. The distance scrolled can be given by
	 * parameter <code>numPixels</code>, and defaults to <code>100</code>.
	 *
	 * @param numPixels    The distance to scroll in pixels.
	 */
	public static void scrollRight(int numPixels) {
		getAPIImpl().scrollRightImpl(numPixels);
	}

	public static void scrollLeft() {
		getAPIImpl().scrollLeftImpl();
	}

	/**
	 * Scrolls the page to the left. The distance scrolled can be given by
	 * parameter <code>numPixels</code>, and defaults to <code>100</code>.
	 *
	 * @param numPixels    The distance to scroll in pixels.
	 */
	public static void scrollLeft(int numPixels) {
		getAPIImpl().scrollLeftImpl(numPixels);
	}

	/**
	 * Hovers the mouse cursor over the given element or point. For example:
	 *
	 * {@code
	 * hover("File size");
	 * hover(Button("OK"));
	 * hover(Link("Download"));
	 * hover(Point(200, 300));
	 * hover(ComboBox("File type").getTopLeft().withOffset(50, 0));}
	 *
	 * @param element    The element to hover.
	 */
	public static void hover(String element) {
		getAPIImpl().hoverImpl(element);
	}
	public static void hover(WebElement element) {
		getAPIImpl().hoverImpl(element);
	}
	public static void hover(HTMLElement element) {
		getAPIImpl().hoverImpl(element.getImpl());
	}

	/**
	 * @param point    The point to hover.
	 */
	public static void hover(Point point) {
		getAPIImpl().hoverImpl(point);
	}

	/**
	 * Performs a right click on the given element or point. For example:
	 *
	 * {@code
	 * rightclick("Something");
	 * rightclick(Point(200, 300));
	 * rightclick(Image("captcha"));}
	 *
	 * @param element    The element to right click.
	 */
	public static void rightclick(String element) {
		getAPIImpl().rightclickImpl(element);
	}
	public static void rightclick(WebElement element) {
		getAPIImpl().rightclickImpl(element);
	}
	public static void rightclick(HTMLElement element) {
		getAPIImpl().rightclickImpl(element.getImpl());
	}

	/**
	 * @param point    The point to right click.
	 */
	public static void rightclick(Point point) {
		getAPIImpl().rightclickImpl(point);
	}

	/**
	 * Selects a value from a combo box. For example:
	 *
	 * {@code
	 * select("Language", "English");
	 * select(ComboBox("Language"), "English");}
	 *
	 * @param comboBox    The combo box whose value should be changed.
	 * @param value       The visible value of the combo box to be selected.
	 */
	public static void select(String comboBox, String value) {
		getAPIImpl().selectImpl(comboBox, value);
	}
	public static void select(ComboBox comboBox, String value) {
		getAPIImpl().selectImpl(comboBox.getImpl(), value);
	}

	/**
	 * Simulates the dragging of a file from the computer over the browser
	 * window and dropping it over the given element. This allows, for example,
	 * to attach files to emails in Gmail:
	 *
	 * {@code
	 * click("COMPOSE");
	 * write("example@gmail.com", into("To"));
	 * write("Email subject", into("Subject"));
	 * dragFile("C:\\Documents\\notes.txt", to("Drop files here"));}
	 *
	 * As you can see in the above example, the function {@link API#to} is used
	 * to make the script more readable.
	 *
	 * @param filePath    The path of the file to be attached.
	 * @param to          The element to drop the dragged file on.
	 */
	public static void dragFile(String filePath, String to) {
		getAPIImpl().dragFileImpl(filePath, to);
	}
	public static void dragFile(String filePath, WebElement to) {
		getAPIImpl().dragFileImpl(filePath, to);
	}
	public static void dragFile(String filePath, HTMLElement to) {
		getAPIImpl().dragFileImpl(filePath, to.getImpl());
	}

	/**
	 * The purpose of the function 'to' is to make Helium scripts more
	 * readable. It simply returns the parameter passed into it. It lets you
	 * write:
	 *
	 * {@code
	 * attachFile("c:/test.txt", to("Please select a file:"));}
	 *
	 * instead of the much less readable:
	 *
	 * {@code
	 * attachFile("c:/test.txt", "Please select a file:");}
	 *
	 * Similarly, <code>to</code> can be used with {@link API#dragFile}:
	 *
	 * {@code
	 * dragFile("C:\\Documents\\notes.txt", to("Drop files here"));}
	 *
	 * @param element    The element to return.
	 * @return The <code>element</code> parameter.
	 * @see com.heliumhq.API#into
	 */
	public static String to(String element) {
		return element;
	}
	public static WebElement to(WebElement element) {
		return element;
	}
	public static HTMLElement to(HTMLElement element) {
		return element;
	}
	public static Point to(Point element) {
		return element;
	}

	public static void attachFile(String filePath) {
		getAPIImpl().attachFileImpl(filePath);
	}

	/**
	 * Allows attaching a file to a file input element or point. For instance:
	 *
	 * {@code
	 * attachFile("c:/test.txt", to("Please select a file:"));}
	 *
	 * The file input element is identified by its label. If you omit the
	 * <code>to</code> parameter, then Helium attaches the file to the first
	 * file input element it finds on the page.
	 *
	 * As you can see in the above example, the function {@link API#to} is used
	 * to make the script more readable.
	 *
	 * @param filePath    The path of the file to be attached.
	 * @param to          The file input element to which to attach the file.
	 */
	public static void attachFile(String filePath, String to) {
		getAPIImpl().attachFileImpl(filePath, to);
	}
	public static void attachFile(String filePath, WebElement to) {
		getAPIImpl().attachFileImpl(filePath, to);
	}
	public static void attachFile(String filePath, HTMLElement to) {
		getAPIImpl().attachFileImpl(filePath, to.getImpl());
	}
	public static void attachFile(String filePath, Point to) {
		getAPIImpl().attachFileImpl(filePath, to);
	}

	/**
	 * Refreshes the current page. If an alert dialog is open, then Helium first
	 * closes it.
	 */
	public static void refresh() {
		getAPIImpl().refreshImpl();
	}

	public static void waitUntil(ExpectedCondition<?> condition) {
		getAPIImpl().waitUntilImpl(condition);
	}
	public static void waitUntil(
			ExpectedCondition<?> condition, long timeoutSecs
	) {
		getAPIImpl().waitUntilImpl(condition, timeoutSecs);
	}

	/**
	 * Waits until the given condition becomes true. This is most commonly used
	 * to wait for an element to exist:
	 *
	 * {@code
	 * waitUntil(Text("Finished!").exists);}
	 *
	 * You can wait for any boolean property exposed by Helium's GUI elements
	 * this way. Here are some further examples:
	 *
	 * {@code
	 * waitUntil(Button("Download").isEnabled);
	 * waitUntil(Link("Proceed").exists);
	 * waitUntil(TextField("Credit card number").isEditable);
	 * ...}
	 *
	 * In general, <code>waitUntil</code> accepts any Selenium
	 * {@link org.openqa.selenium.support.ui.ExpectedCondition}. For instance,
	 * you can use {@link org.openqa.selenium.support.ui.ExpectedConditions#not(
	 *org.openqa.selenium.support.ui.ExpectedCondition)}
	 * to negate a condition:
	 *
	 * {@code
	 * import static org.openqa.selenium.support.ui.ExpectedConditions.not;
	 * ...
	 * waitUntil(not(Text("Processing...").exists));}
	 *
	 * The parameter <code>timeoutSecs</code> controls how long Helium waits for
	 * the condition to become true. If it does not become true within the given
	 * number of seconds, then a {@link org.openqa.selenium.TimeoutException}
	 * is thrown.
	 *
	 * <code>intervalSecs</code> specifies how long Helium waits in between
	 * consecutive checks of the condition. The default of <code>0.5</code>
	 * makes Helium check every 500ms.
	 *
	 * It is recommended to use the functions {@link API#timeoutSecs(long)} and
	 * {@link API#intervalSecs(double)} when specifying the time parameters for
	 * improved readability. For example:
	 *
	 * {@code
	 * waitUntil(Text("Done!").exists, timeoutSecs(15), intervalSecs(1));}
	 *
	 * @param condition       The condition to wait for.
	 * @param timeoutSecs     The timeout after which Helium aborts the wait, in
	 *                        seconds. Default: 10.
	 * @param intervalSecs    The number of seconds Helium waits between
	 *                        consecutive checks of the given condition.
	 *                        Default: 0.5 (500 ms).
	 * @throws org.openqa.selenium.TimeoutException if the given condition isn't
	 * fulfilled within timeoutSecs.
	 */
	public static void waitUntil(
			ExpectedCondition<?> condition, long timeoutSecs,
			double intervalSecs
	) {
		getAPIImpl().waitUntilImpl(condition, timeoutSecs, intervalSecs);
	}

	/**
	 * This function is used to improve the readability of Helium scripts that
	 * use {@link API#waitUntil}. It simply returns the parameter passed into
	 * it. It lets you write:
	 *
	 * {@code
	 * waitUntil(Text(...).exists, timeoutSecs(10));}
	 *
	 * instead of the less readable:
	 *
	 * {@code
	 * waitUntil(Text(...).exists, 10);}
	 *
	 * @param timeoutSecs    The timeout to be passed into waitUntil.
	 * @return the timeoutSecs parameter passed when calling this function.
	 */
	public static long timeoutSecs(long timeoutSecs) {
		return timeoutSecs;
	}
	/**
	 * Similarly to {@link API#timeoutSecs(long)}, this function is used to
	 * improve the readability of Helium scripts that use {@link API#waitUntil}.
	 * It simply returns the parameter passed into it. It lets you write:
	 *
	 * {@code
	 * waitUntil(Text(...).exists, timeoutSecs(10), intervalSecs(1));}
	 *
	 * instead of the less readable:
	 *
	 * {@code
	 * waitUntil(Text(...).exists, timeoutSecs(10), 1);}
	 *
	 * @param intervalSecs    The wait interval to be passed into waitUntil.
	 * @return the intervalSecs parameter passed when calling this function.
	 */
	public static double intervalSecs(double intervalSecs) {
		return intervalSecs;
	}

	/**
	 * This class contains Helium's run-time configuration. To modify Helium's
	 * behaviour, simply set the properties of this class. For instance:
	 *
	 * {@code
	 * Config.setImplicitWaitSecs(0);}
	 */
	public static class Config {
		private Config() {}
		private static double implicitWaitSecs = 10;
		/**
		 * Suppose you have a script that executes the following command:
		 *
		 * {@code
		 * click("Download");}
		 *
		 * If the "Download" element is not immediately available, then Helium
		 * by default waits up to <code>10</code> seconds before throwing a
		 * {@link org.openqa.selenium.NoSuchElementException}. This is useful in
		 * situations where the page takes slightly longer to load, or a GUI
		 * element only appears after a certain time (eg. after an AJAX
		 * request).
		 * <p>
		 * The function <code>Config.setImplicitWaitSecs(...)</code> lets you
		 * configure the timeout before Helium throws a
		 * {@link org.openqa.selenium.NoSuchElementException} when an element
		 * cannot be found. For instance, to increase the timeout to 30 seconds,
		 * you can call:
		 *
		 * {@code
		 * Config.setImplicitWaitSecs(30);}
		 *
		 * To disable Helium's implicit waits entirely, you can call:
		 *
		 * {@code
		 * Config.setImplicitWaitSecs(0);}
		 *
		 * Helium's implicit waits do not affect commands {@link API#findAll} or
		 * {@link GUIElement#exists()}. Note also that calling
		 * <code>setImplicitWaitSecs(...)</code> does not affect the underlying
		 * Selenium driver (see {@link API#getDriver()}).
		 * <p>
		 * For the best results, it is recommended to not use Selenium's
		 * <code>.implicitlyWait(...)</code> in conjunction with Helium.
		 *
		 * @param value    The new timeout, in seconds, after which Helium
		 *                 throws
		 *                 {@link org.openqa.selenium.NoSuchElementException}
		 *                 when GUI elements cannot be found.
		 */
		public static void setImplicitWaitSecs(double value) {
			implicitWaitSecs = value;
		}
		/**
		 * Returns the number of seconds (as a <code>double</code>) Helium waits
		 * before throwing a {@link org.openqa.selenium.NoSuchElementException}
		 * when a GUI element cannot be found. Please see
		 * {@link com.heliumhq.API.Config#setImplicitWaitSecs(double)} for a
		 * more comprehensive description of this feature.
		 */
		public static double getImplicitWaitSecs() {
			return implicitWaitSecs;
		}
	}

	/**
	 * Abstract base class for all GUI elements identifiable by Helium:
	 * <p>
	 * <ul>
	 *     <li>All {@link com.heliumhq.API.HTMLElement}s</li>
	 *     <li>{@link API#Alert(java.lang.String)}</li>
	 *     <li>{@link API#Window(java.lang.String)}</li>
	 * </ul>
	 */
	public static abstract class GUIElement {
		private final GUIElementImpl impl;
		private GUIElement(GUIElementImpl impl) {
			this.impl = impl;
		}
		/**
		 * Returns <code>true</code> if this GUI element exists,
		 * <code>false</code> otherwise.
		 */
		public boolean exists() {
			return impl.exists();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * GUI element to exist. For example, to wait until a certain text is
		 * shown on the page, one can use:
		 *
		 * {@code
		 * waitUntil(Text("Email sent").exists);}
		 *
		 * The same pattern can be used for Helium's other GUI elements:
		 * <code>waitUntil(Button(...).exists);</code>,
		 * <code>waitUntil(CheckBox(...).exists);</code>,
		 * <code>waitUntil(Window(...).exists);</code> etc.
		 */
		public final ExpectedCondition<Boolean> exists =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return exists();
					}
				};
		@Override
		public String toString() {
			return impl.toString(getClass().getSimpleName());
		}
		GUIElementImpl getImpl() {
			return impl;
		}
	}

	/**
	 * Abstract base class for HTML elements identifiable by Helium on a web
	 * page:
	 * <p>
	 * <ul>
	 *     <li>{@link API#Text(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#Link(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#ListItem(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#Button(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#Image(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#TextField(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#ComboBox(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#CheckBox(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 *     <li>{@link API#RadioButton(java.lang.String,
	 *     com.heliumhq.api_impl.SearchRegion...)}</li>
	 * </ul>
	 */
	public static abstract class HTMLElement extends GUIElement {
		private HTMLElement(HTMLElementImpl impl) {
			super(impl);
		}
		/**
		 * Returns an <code>int</code> giving the width of this HTML element, in
		 * pixels.
		 */
		public int getWidth() {
			return getImpl().getWidth();
		}
		/**
		 * Returns an <code>int</code> giving the height of this HTML element,
		 * in pixels.
		 */
		public int getHeight() {
			return getImpl().getHeight();
		}
		/**
		 * Returns an <code>int</code> giving the x-coordinate of the top-left
		 * point of this UI element.
		 */
		public int getX() {
			return getImpl().getX();
		}
		/**
		 * Returns an <code>int</code> giving the y-coordinate of the top-left
		 * point of this UI element.
		 */
		public int getY() {
			return getImpl().getY();
		}
		/**
		 * Returns the top left corner of this element, as a
		 * {@link com.heliumhq.API.Point}. This point has exactly the
		 * coordinates given by this element's <code>.getX()</code> and
		 * <code>.getY()</code> properties. <code>getTopLeft()</code> is for
		 * instance useful for clicking at an offset of an element:
		 *
		 * {@code
		 * click(Button("OK").getTopLeft().withOffset(30, 15));}
		 */
		public Point getTopLeft() {
			return getImpl().getTopLeft();
		}
		/**
		 * Returns the Selenium {@link org.openqa.selenium.WebElement}
		 * corresponding to this element.
		 */
		public WebElement getWebElement() {
			return getImpl().getWebElement();
		}
		HTMLElementImpl getImpl() {
			return (HTMLElementImpl) super.getImpl();
		}
	}

	/**
	 * Switches to the given browser window. For example:
	 *
	 * {@code
	 * switchTo("Google");}
	 *
	 * This searches for a browser window whose title contains "Google", and
	 * activates it.
	 *
	 * If there are multiple windows with the same title, then you can use
	 * {@link API#findAll} to find all open windows, pick out the one you want
	 * and pass that to <code>switchTo</code>. For example, the following
	 * snippet switches to the first window in the list of open windows:
	 *
	 * {@code
	 * switchTo(findAll(Window()).get(0));}
	 *
	 * @param windowTitle    The title of the window to switch to.
	 */
	public static void switchTo(String windowTitle) {
		getAPIImpl().switchToImpl(windowTitle);
	}

	/**
	 * @param window    The window to switch to.
	 */
	public static void switchTo(Window window) {
		getAPIImpl().switchToImpl(window.getImpl());
	}

	/**
	 * Closes the current browser with all associated windows and potentially
	 * open dialogs. Dialogs opened as a response to the browser closing (eg.
	 * "Are you sure you want to leave this page?") are also ignored and closed.
	 * <p>
	 * This function is most commonly used to close the browser at the end of an
	 * automation run:
	 *
	 * {@code
	 * startChrome();
	 * ...
	 * // Close Chrome:
	 * killBrowser();}
	 */
	public static void killBrowser() {
		getAPIImpl().killBrowserImpl();
	}

	/**
	 * Highlights the given element on the page by drawing a red rectangle
	 * around it. This is useful for debugging purposes. For example:
	 *
	 * {@code
	 * highlight("Helium");
	 * highlight(Button("Sign in"));}
	 *
	 * @param element    The element to highlight.
	 */
	public static void highlight(String element) {
		getAPIImpl().highlightImpl(element);
	}
	public static void highlight(HTMLElement element) {
		getAPIImpl().highlightImpl(element.getImpl());
	}
	public static void highlight(WebElement element) {
		getAPIImpl().highlightImpl(element);
	}

	/**
	 * A jQuery-style selector for identifying HTML elements by ID, name, CSS
	 * class, CSS selector or XPath. For example: Say you have an element with
	 * ID "myId" on a web page, such as <code>&lt;div id="myId" .../&gt;</code>.
	 * Then you can identify this element using <code>$</code> as follows:
	 *
	 * {@code
	 * $("#myId")}
	 *
	 * The parameter which you pass to <code>$(...)</code> is interpreted by
	 * Helium according to these rules:
	 *
	 * <ul>
	 *     <li>If it starts with an <code>@</code>, then it identifies elements
	 *     by HTML <code>name</code>. Eg. <code>$("@btnName")</code> identifies
	 *     an element with <code>name="btnName"</code>.</li>
	 *     <li>If it starts with <code>//</code>, then Helium interprets it as
	 *     an XPath.</li>
	 *     <li>Otherwise, Helium interprets it as a CSS selector. This in
	 *     particular lets you write <code>$("#myId")</code> to identify an
	 *     element with <code>id="myId"</code>, or <code>$(".myClass")</code>
	 *     to identify elements with <code>class="myClass"</code>.</li>
	 * </ul>
	 * @param selector        The selector used to identify the HTML element(s).
	 * @param searchRegion    The search region to find the text in.
	 *                        See the documentation of {@link API#below}.*
	 */
	public static $ $(String selector, SearchRegion... searchRegion) {
		return new $(getAPIImpl().$Impl(selector, searchRegion));
	}

	public static class $ extends HTMLElement {
		private $($Impl impl) {
			super(impl);
		}
	}

	/**
	 * Lets you identify any text or label on a web page. This is most useful
	 * for checking whether a particular text exists:
	 *
	 * {@code
	 * if (Text("Do you want to proceed?").exists())
	 *     click("Yes");}
	 *
	 * {@link com.heliumhq.API.Text} also makes it possible to read plain text
	 * data from a web page. For example, suppose you have a table of people's
	 * email addresses. Then you can read John's email addresses as follows:
	 *
	 * {@code
	 * Text(below("Email"), toRightOf("John")).getValue()}
	 *
	 * Similarly to <code>below</code> and <code>toRightOf</code>, the keyword
	 * parameters {@link API#above} and {@link API#toLeftOf} can be used to
	 * search for texts above and to the left of other web elements.
	 *
	 * @param text            The text to identify.
	 * @param searchRegion    The search region to find the text in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static Text Text(String text, SearchRegion... searchRegion) {
		return new Text(getAPIImpl().TextImpl(text, searchRegion));
	}
	public static Text Text(SearchRegion... searchRegion) {
		return new Text(getAPIImpl().TextImpl(searchRegion));
	}
	public static class Text extends HTMLElement {
		private Text(TextImpl impl) {
			super(impl);
		}
		/**
		 * Returns the current value of this Text object, as a
		 * {@link java.lang.String}.
		 */
		public String getValue() {
			return getImpl().getValue();
		}
		TextImpl getImpl() {
			return (TextImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify a link on a web page. A typical usage of
	 * <code>Link</code> is:
	 *
	 * {@code
	 * click(Link("Sign in"));}
	 *
	 * You can also read a <code>Link</code>'s properties. This is most
	 * typically used to check for a link's existence before clicking on it:
	 *
	 * {@code
	 * if (Link("Sign in").exists())
	 *     click(Link("Sign in"));}
	 *
	 * When there are multiple occurrences of a link on a page, you can
	 * disambiguate between them using {@link API#below}, {@link API#toRightOf},
	 * {@link API#above} and {@link API#toLeftOf}. For instance:
	 *
	 * {@code
	 * click(Link("Block User", toRightOf("John Doe")));}
	 *
	 * @param text            The link text or title.
	 * @param searchRegion    The search region to find the link in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static Link Link(String text, SearchRegion... searchRegion) {
		return new Link(getAPIImpl().LinkImpl(text, searchRegion));
	}
	public static Link Link(SearchRegion... searchRegion) {
		return new Link(getAPIImpl().LinkImpl(searchRegion));
	}
	public static class Link extends HTMLElement {
		private Link(LinkImpl impl) {
			super(impl);
		}
		/**
		 * Returns the URL of the page the link goes to, as a
		 * {@link java.lang.String}. If there is no value, null is returned.
		 */
		public String getHref() {
			return ((LinkImpl)getImpl()).getHref();
		}
	}

	/**
	 * Lets you identify a list item (HTML <code>&lt;li&gt;</code> element) on a
	 * web page. This is often useful for interacting with elements of a
	 * navigation bar:
	 *
	 * {@code
	 * click(ListItem("News Feed"));}
	 *
	 * In other cases such as an automated test, you might want to query the
	 * properties of a <code>ListItem</code>. For example, the following snippet
	 * checks whether a list item with text "List item 1" exists, and raises an
	 * error if not:
	 *
	 * {@code
	 * if (! ListItem("List item 1").exists())
	 *     throw new AssertionError("List item 1 does not exist");}
	 *
	 * When there are multiple occurrences of a list item on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}.
	 * For instance:
	 * {@code
	 * click(ListItem("List item 1", below("My first list:")));}
	 *
	 * @param text            The text (label) of the list item.
	 * @param searchRegion    The search region to find the list item in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static ListItem ListItem(String text, SearchRegion... searchRegion) {
		return new ListItem(getAPIImpl().ListItemImpl(text, searchRegion));
	}
	public static ListItem ListItem(SearchRegion... searchRegion) {
		return new ListItem(getAPIImpl().ListItemImpl(searchRegion));
	}
	public static class ListItem extends HTMLElement {
		private ListItem(ListItemImpl impl) {
			super(impl);
		}
	}

	/**
	 * Lets you identify a button on a web page. A typical usage of
	 * <code>Button</code> is:
	 *
	 * {@code
	 * click(Button("Log In"));}
	 *
	 * <code>Button</code> also lets you read a button's properties. For
	 * example, the following snippet clicks button "OK" only if it exists:
	 *
	 * {@code
	 * if (Button("OK").exists())
	 *     click(Button("OK"));}
	 *
	 * When there are multiple occurrences of a button on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * click(Button("Log In", below(TextField("Password"))));}
	 *
	 * @param text            The button label.
	 * @param searchRegion    The search region to find the button in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static Button Button(String text, SearchRegion... searchRegion) {
		return new Button(getAPIImpl().ButtonImpl(text, searchRegion));
	}
	public static Button Button(SearchRegion... searchRegion) {
		return new Button(getAPIImpl().ButtonImpl(searchRegion));
	}
	public static class Button extends HTMLElement {
		private Button(ButtonImpl impl) {
			super(impl);
		}
		/**
		 * Returns <code>true</code> if this button can currently be interacted
		 * with, <code>false</code> otherwise.
		 */
		public boolean isEnabled() {
			return getImpl().isEnabled();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * Button to become enabled. For example:
		 *
		 * {@code
		 * waitUntil(Button("Submit").isEnabled);}
		 */
		public final ExpectedCondition<Boolean> isEnabled =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isEnabled();
					}
				};
		ButtonImpl getImpl() {
			return (ButtonImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify an image (HTML <code>&lt;img&gt;</code> element) on a
	 * web page. Typically, this is done via the image's alt text. For instance:
	 *
	 * {@code
	 * click(Image("Helium Logo"));}
	 *
	 * You can also query an image's properties. For example, the following
	 * snippet clicks on the image with alt text "Helium Logo" only if it
	 * exists:
	 *
	 * {@code
	 * if (Image("Helium Logo").exists())
	 *     click(Image("Helium Logo"));}
	 *
	 * When there are multiple occurrences of an image on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * click(Image("Helium Logo", toLeftOf(ListItem("Download"))));}
	 *
	 * @param alt             The image's alt text.
	 * @param searchRegion    The search region to find the image in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static Image Image(String alt, SearchRegion... searchRegion) {
		return new Image(getAPIImpl().ImageImpl(alt, searchRegion));
	}
	public static Image Image(SearchRegion... searchRegion) {
		return new Image(getAPIImpl().ImageImpl(searchRegion));
	}
	public static class Image extends HTMLElement {
		private Image(ImageImpl impl) {
			super(impl);
		}
	}

	/**
	 * Lets you identify a text field on a web page. This is most typically done
	 * to read the value of a text field. For example:
	 *
	 * {@code
	 * TextField("First name").getValue()}
	 *
	 * This returns the value of the "First name" text field. If it is empty,
	 * the empty string "" is returned.
	 *
	 * When there are multiple occurrences of a text field on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * TextField("Address line 1", below("Billing Address:")).getValue()}
	 *
	 * @param label           The label (human-visible name) of the text field.
	 * @param searchRegion    The search region to find the text field in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static TextField TextField(
			String label, SearchRegion... searchRegion
	) {
		return new TextField(getAPIImpl().TextFieldImpl(label, searchRegion));
	}
	public static TextField TextField(SearchRegion... searchRegion) {
		return new TextField(getAPIImpl().TextFieldImpl(searchRegion));
	}
	public static class TextField extends HTMLElement {
		private TextField(TextFieldImpl impl) {
			super(impl);
		}
		/**
		 * Returns the current value of this text field, as a
		 * {@link java.lang.String}. If there is no value, "" is returned.
		 */
		public String getValue() {
			return getImpl().getValue();
		}
		/**
		 * Returns a <code>boolean</code> indicating whether this text field can
		 * currently be interacted with.
		 * <p>
		 * The difference between a text field being 'enabled' and 'editable' is
		 * mostly visual: If a text field is not enabled, it is usually greyed
		 * out, whereas if it is not editable it looks normal. See also
		 * {@link TextField#isEditable()}.
		 */
		public boolean isEnabled() {
			return getImpl().isEnabled();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * TextField to become enabled. For example:
		 *
		 * {@code
		 * waitUntil(TextField("Subject").isEnabled);}
		 */
		public final ExpectedCondition<Boolean> isEnabled =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isEnabled();
					}
				};
		/**
		 * Returns <code>true</code> if the value of the text field can be
		 * modified, <code>false</code> otherwise.
		 * <p>
		 * The difference between a text field being 'enabled' and 'editable' is
		 * mostly visual: If a text field is not enabled, it is usually greyed
		 * out, whereas if it is not editable it looks normal. See also
		 * {@link TextField#isEnabled()}.
		 */
		public boolean isEditable() {
			return getImpl().isEditable();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * TextField to become editable. For example:
		 *
		 * {@code
		 * waitUntil(TextField("Is active").isEditable);}
		 */
		public final ExpectedCondition<Boolean> isEditable =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isEditable();
					}
				};
		TextFieldImpl getImpl() {
			return (TextFieldImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify a combo box on a web page. This can for instance be
	 * used to determine the current value of a combo box:
	 * {@code
	 * ComboBox("Language").getValue()}
	 *
	 * A ComboBox may be <emph>editable</emph>, which means that it is possible
	 * to type in arbitrary values in addition to selecting from a predefined
	 * drop-down list of values. The property
	 * {@link com.heliumhq.API.ComboBox#isEditable()} can be used to
	 * determine whether this is the case for a particular combo box instance.
	 *
	 * When there are multiple occurrences of a combo box on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * select(ComboBox(toRightOf("John Doe"), below("Status")), "Active")}
	 *
	 * This sets the Status of John Doe to Active on the page.
	 *
	 * @param label           The label (human-visible name) of the combo box.
	 * @param searchRegion    The search region to find the combo box in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static ComboBox ComboBox(
			String label, SearchRegion... searchRegion
	) {
		return new ComboBox(getAPIImpl().ComboBoxImpl(label, searchRegion));
	}
	public static ComboBox ComboBox(SearchRegion... searchRegion) {
		return new ComboBox(getAPIImpl().ComboBoxImpl(searchRegion));
	}
	public static class ComboBox extends HTMLElement {
		private ComboBox(ComboBoxImpl impl) {
			super(impl);
		}
		/**
		 * Returns <code>true</code> if this combo box allows entering an
		 * arbitrary text in addition to selecting predefined values from a
		 * drop-down list. Otherwise, returns <code>false</code>.
		 */
		public boolean isEditable() {
			return getImpl().isEditable();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * ComboBox to become editable. For example:
		 *
		 * {@code
		 * waitUntil(ComboBox("Region").isEditable);}
		 */
		public final ExpectedCondition<Boolean> isEditable =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isEditable();
					}
				};
		/**
		 * Returns a <code>List&lt;String&gt;</code> of all possible options
		 * available to choose from in the combo box.
		 */
		public List<String> getOptions() {
			return getImpl().getOptions();
		}
		/**
		 * Returns the currently selected combo box value, as a
		 * {@link java.lang.String}.
		 */
		public String getValue() {
			return getImpl().getValue();
		}
		ComboBoxImpl getImpl() {
			return (ComboBoxImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify a check box on a web page. To tick a currently
	 * unselected check box, use:
	 *
	 * {@code
	 * click(CheckBox("I agree"));}
	 *
	 * <code>CheckBox</code> also lets you read the properties of a check box.
	 * For example,
	 * the method {@link com.heliumhq.API.CheckBox#isChecked()} can be used to
	 * only click a check box if it isn't already checked:
	 *
	 * {@code
	 * if (! CheckBox("I agree").isChecked())
	 *     click(CheckBox("I agree"));}
	 *
	 * When there are multiple occurrences of a check box on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * click(CheckBox("Stay signed in", below(Button("Sign in"))));}
	 *
	 * @param label           The label (human-visible name) of the check box.
	 * @param searchRegion    The search region to find the check box in.
	 *                        See the documentation of {@link API#below}.
	 */
	public static CheckBox CheckBox(
			String label, SearchRegion... searchRegion
	) {
		return new CheckBox(getAPIImpl().CheckBoxImpl(label, searchRegion));
	}
	public static CheckBox CheckBox(SearchRegion... searchRegion) {
		return new CheckBox(getAPIImpl().CheckBoxImpl(searchRegion));
	}
	public static class CheckBox extends HTMLElement {
		private CheckBox(CheckBoxImpl impl) {
			super(impl);
		}
		/**
		 * Returns <code>true</code> if this check box can currently be
		 * interacted with, <code>false</code> otherwise.
		 */
		public boolean isEnabled() {
			return getImpl().isEnabled();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * CheckBox to become enabled. For example:
		 *
		 * {@code
		 * waitUntil(CheckBox("I agree").isEnabled);}
		 */
		public final ExpectedCondition<Boolean> isEnabled =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isEnabled();
					}
				};
		/**
		 * Returns <code>true</code> if this check box is checked (selected).
		 * <code>false</code> otherwise.
		 */
		public boolean isChecked() {
			return getImpl().isChecked();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * CheckBox to become checked. For example:
		 *
		 * {@code
		 * waitUntil(CheckBox("Is active").isChecked);}
		 */
		public final ExpectedCondition<Boolean> isChecked =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isChecked();
					}
				};
		CheckBoxImpl getImpl() {
			return (CheckBoxImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify a radio button on a web page. To select a currently
	 * unselected radio button, use:
	 *
	 * {@code
	 * click(RadioButton("Windows"))}
	 *
	 * <code>RadioButton</code> also lets you read the properties of a radio
	 * button. For example, the method
	 * {@link com.heliumhq.API.RadioButton#isSelected()} can be used to only
	 * click a radio button if it isn't already selected:
	 *
	 * {@code
	 * if (! RadioButton("Windows").is_selected())
	 *     click(RadioButton("Windows"));}
	 *
	 * When there are multiple occurrences of a radio button on a page, you can
	 * disambiguate between them using the functions {@link API#below},
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf}. For
	 * instance:
	 *
	 * {@code
	 * click(RadioButton("I accept", below("License Agreement")));}
	 *
	 * @param label          The label (human-visible name) of the radio button.
	 * @param searchRegion   The search region to find the radio button in.
	 *                       See the documentation of {@link API#below}.
	 */
	public static RadioButton RadioButton(
			String label, SearchRegion... searchRegion
	) {
		return new RadioButton(
				getAPIImpl().RadioButtonImpl(label, searchRegion)
		);
	}
	public static RadioButton RadioButton(SearchRegion... searchRegion) {
		return new RadioButton(getAPIImpl().RadioButtonImpl(searchRegion));
	}
	public static class RadioButton extends HTMLElement {
		private RadioButton(RadioButtonImpl impl) {
			super(impl);
		}
		/**
		 * Returns <code>true</code> if this radio button is selected,
		 * <code>false</code> otherwise.
		 */
		public boolean isSelected() {
			return getImpl().isSelected();
		}
		/**
		 * An {@link org.openqa.selenium.support.ui.ExpectedCondition} that can
		 * be used in conjunction with {@link com.heliumhq.API#waitUntil(
		 * org.openqa.selenium.support.ui.ExpectedCondition)} to wait for this
		 * RadioButton to become checked. For example:
		 *
		 * {@code
		 * waitUntil(RadioButton("Express shipping").isSelected);}
		 */
		public final ExpectedCondition<Boolean> isSelected =
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return isSelected();
					}
				};
		RadioButtonImpl getImpl() {
			return (RadioButtonImpl) super.getImpl();
		}
	}

	/**
	 * Lets you identify individual windows of the currently open browser
	 * session.
	 *
	 * @param title    The title of the window you wish to identify.
	 */
	public static Window Window(String title) {
		return new Window(getAPIImpl().WindowImpl(title));
	}
	public static Window Window() {
		return new Window(getAPIImpl().WindowImpl());
	}
	public static class Window extends GUIElement {
		private Window(WindowImpl impl) {
			super(impl);
		}
		/**
		 * Returns the title of this Window, as a {@link java.lang.String}.
		 */
		public String getTitle() {
			return getImpl().getTitle();
		}
		/**
		 * Returns the Selenium driver window handle assigned to this window (a
		 * {@link java.lang.String}). Note that this window handle is simply an
		 * abstract identifier and bears no relationship to the corresponding
		 * operating system handle (HWND on Windows).
		 */
		public String getHandle() {
			return getImpl().getHandle();
		}
		WindowImpl getImpl() {
			return (WindowImpl) super.getImpl();
		}
	}


	/**
	 * Lets you identify and interact with JavaScript alert boxes.
	 *
	 * @param text    The text displayed in the alert box you wish to identify.
	 */
	public static Alert Alert(String text) {
		return new Alert(getAPIImpl().AlertImpl(text));
	}
	public static Alert Alert() {
		return new Alert(getAPIImpl().AlertImpl());
	}
	public static class Alert extends GUIElement {
		private Alert(AlertImpl impl) {
			super(impl);
		}
		/**
		 * Returns the text displayed in this alert box, as a
		 * {@link java.lang.String}.
		 */
		public String getText() {
			return getImpl().getText();
		}
		/**
		 * Accepts this alert. This typically corresponds to clicking the "OK"
		 * button inside the alert. The typical way to use this method is:
		 *
		 * {@code
		 * Alert().accept();}
		 *
		 * This accepts the currently open alert.
		 */
		public void accept() {
			getImpl().accept();
		}
		/**
		 * Dismisses this alert. This typically corresponds to clicking the
		 * "Cancel" or "Close" button of the alert. The typical way to use this
		 * method is:
		 *
		 * {@code
		 * Alert().dismiss();}
		 *
		 * This dismisses the currently open alert.
		 */
		public void dismiss() {
			getImpl().dismiss();
		}
		AlertImpl getImpl() {
			return (AlertImpl) super.getImpl();
		}
	}

	/**
	 * A clickable point. To create a Point at on offset of an existing point,
	 * use {@link com.heliumhq.API.Point#withOffset(int, int)}:
	 *
	 * {@code
	 * Point point = Point(10, 25);
	 * point.withOffset(15, 5); // Point(25, 30);}
	 *
	 * @param x    The x coordinate of the point.
	 * @param y    The y coordinate of the point.
	 */
	public static Point Point(int x, int y) {
		return new Point(x, y);
	}
	public static class Point {
		private int x, y;
		private Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		/**
		 * Returns the x-coordinate of this point.
		 */
		public int getX() {
			return x;
		}
		/**
		 * Returns the y-coordinate of this point.
		 */
		public int getY() {
			return y;
		}
		/**
		 * Returns a new Point with coordinates relative to this Point's
		 * coordinates.
		 *
		 * @param dx    The distance of the new Point to this Point on the
		 *              horizontal axis.
		 * @param dy    The distance of the new Point to this Point on the
		 *              vertical axis.
		 * @return a new Point with coordinates (x + dx, y + dy) if this Point
		 * has coordinates (x, y).
		 */
		public Point withOffset(int dx, int dy) {
			return new Point(x + dx, y + dy);
		}
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Point point = (Point) o;

			if (x != point.x) return false;
			if (y != point.y) return false;

			return true;
		}
		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}

	/**
	 * This function is one of the four functions that can be used to perform
	 * relative HTML element searches. For a description of this feature, please
	 * consult {@link API#below}.
	 *
	 * @param text    The text to the left of which a search should be
	 *                performed.
	 */
	public static SearchRegion toLeftOf(String text) {
		return toLeftOf(Text(text));
	}

	/**
	 * @param element    The element to the left of which a search should be
	 *                   performed.
	 */
	public static SearchRegion toLeftOf(HTMLElement element) {
		return getAPIImpl().toLeftOf(element.getImpl());
	}

	/**
	 * This function is one of the four functions that can be used to perform
	 * relative HTML element searches. For a description of this feature, please
	 * consult {@link API#below}.
	 *
	 * @param text    The text to the right of which a search should be
	 *                performed.
	 */
	public static SearchRegion toRightOf(String text) {
		return toRightOf(Text(text));
	}

	/**
	 * @param element    The element to the left of which a search should be
	 *                   performed.
	 */
	public static SearchRegion toRightOf(HTMLElement element) {
		return getAPIImpl().toRightOf(element.getImpl());
	}

	/**
	 * This function is one of the four functions that can be used to perform
	 * relative HTML element searches. For a description of this feature, please
	 * consult {@link API#below}.
	 *
	 * @param text    The text above which a search should be performed.
	 */
	public static SearchRegion above(String text) {
		return above(Text(text));
	}

	/**
	 * @param element    The element above which a search should be performed.
	 */
	public static SearchRegion above(HTMLElement element) {
		return getAPIImpl().above(element.getImpl());
	}

	/**
	 * The function <code>below</code> and its sister functions
	 * {@link API#toRightOf}, {@link API#above} and {@link API#toLeftOf} make it
	 * possible to disambiguate multiple occurrences of a HTML element on a
	 * page. Most typically, this occurs when working with tables. For example,
	 * suppose you have a table of user accounts with buttons for blocking
	 * them on a page. Then you can use the following snippet to disable the
	 * user account of "John Doe":
	 *
	 * {@code
	 * click(Button("Block account", toRightOf("John Doe"));}
	 *
	 * Relative GUI element searches like this can be nested and combined
	 * arbitrarily with Helium's other functions. For example:
	 *
	 * {@code
	 * click(CheckBox(below("Has read permissions"), toRightOf("Bob")));}
	 *
	 * This clicks on the check box below text "Has read permissions" and to the
	 * right of text "Bob".
	 *
	 * @param text   The text below which a search should be performed.
	 */
	public static SearchRegion below(String text) {
		return below(Text(text));
	}

	/**
	 * @param element    The element below which a search should be performed.
	 */
	public static SearchRegion below(HTMLElement element) {
		return getAPIImpl().below(element.getImpl());
	}

	private static APIImpl getAPIImpl() {
		return getApplicationContext().getAPIImpl();
	}

}