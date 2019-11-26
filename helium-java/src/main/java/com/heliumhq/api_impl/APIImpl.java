package com.heliumhq.api_impl;

import com.google.common.collect.ImmutableList;
import com.heliumhq.environment.ResourceLocator;
import com.heliumhq.errors.HeliumError;
import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.Tuple;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.heliumhq.API.Point;
import static com.heliumhq.util.System.*;

public class APIImpl {

	public static final String DRIVER_REQUIRED_MESSAGE =
		"This operation requires a browser window. Please call one of " +
		"the following functions first:\n" +
		" * startChrome()\n" +
		" * startFirefox()\n" +
		" * startIE()\n" +
		" * setDriver(...)";

	private final ResourceLocator resourceLocator;
	private WebDriverWrapper driver;

	public APIImpl(ResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
	}

	public WebDriver startFirefoxImpl() {
		return startFirefoxImpl(null);
	}
	public WebDriver startFirefoxImpl(String url) {
		DesiredCapabilities desiredCapabilities =
				new DesiredCapabilities(DesiredCapabilities.firefox());
		desiredCapabilities.setCapability(
				CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
				UnexpectedAlertBehaviour.IGNORE
		);
		FirefoxDriver firefox;
		try {
			firefox = new FirefoxDriver(desiredCapabilities);
		} catch (WebDriverException e) {
			throw new WebDriverException(
				"Could not start Firefox. Please note that Firefox versions " +
				"greater than 47.0.1 are currently not supported."
			);
		}
		return start(firefox, url);
	}

	public WebDriver startChromeImpl() {
		return startChromeImpl(null);
	}
	public WebDriver startChromeImpl(String url) {
		return startChromeImpl(url, false);
	}
	public WebDriver startChromeImpl(boolean headless) {
		return startChromeImpl(null, headless);
	}
	public WebDriver startChromeImpl(String url, boolean headless) {
		ChromeDriver chromeDriver = startChromeDriver(headless);
		return start(chromeDriver, url);
	}

	private ChromeDriver startChromeDriver(boolean headless) {
		ChromeOptions chromeOptions = getChromeOptions(headless);
		ChromeDriverService service = getChromeDriverService();
		Runtime.getRuntime().addShutdownHook(
			new DriverServiceDestroyer(service)
		);
		return new ChromeDriver(service, chromeOptions);
	}

	private ChromeOptions getChromeOptions(boolean headless) {
		ChromeOptions result = new ChromeOptions();
		// ChromeDriver uses the flag --ignore-certificate-errors, which as of
		// Chrome 36 has been added to the "bad flags" list. This results in the
		// following warning being shown in later versions of the browser:
		//    You are using an unsupported command-line flag:
		//     --ignore-certificate-errors. Stability and security will suffer.
		// Adding the command-line flag --test-type suppresses this warning
		// while supposedly not affecting the browser in any other noticeable
		// way. (Source: http://stackoverflow.com/a/23816922/751938)
		result.addArguments("--test-type");
		// Disable alert / warning "Disable Developer Extensions":
		result.addArguments("--disable-extensions");
		if (headless)
			result.addArguments("--headless");
		return result;
	}

	private ChromeDriverService getChromeDriverService() {
		File driver = new File(getChromeDriverPath());
		if (driver.exists()) {
			if (! driver.canExecute())
				if (! driver.setExecutable(true))
					throw new HeliumError(String.format(
						"The Chrome driver located at %s is not executable.",
						driver.getAbsolutePath()
					));
			ChromeDriverService.Builder serviceBuilder;
			if (isWindows()) {
				serviceBuilder = new SilentChromeDriverServiceBuilder(driver);
				driver = new File(locateWebDriver("silent-chromedriver.exe"));
			} else
				serviceBuilder = new ChromeDriverService.Builder();
			return serviceBuilder
				// Prevent verbose messages 'ChromeDriver started' on stderr:
				.withSilent(true)
				.usingAnyFreePort()
				.usingDriverExecutable(driver)
				.build();
		} else
			return ChromeDriverService.createDefaultService();
	}

	private String getChromeDriverPath() {
		String driverName;
		if (isWindows())
			driverName = "chromedriver.exe";
		else if (isLinux())
			driverName = "chromedriver" + (is64bit() ? "_x64" : "");
		else {
			assert isOSX();
			driverName = "chromedriver";
		}
		return locateWebDriver(driverName);
	}

	private String locateWebDriver(String driverName) {
		return resourceLocator.locate("webdrivers", driverName);
	}

	private class SilentChromeDriverServiceBuilder
		extends ChromeDriverService.Builder {

		private final File originalDriverExecutable;

		private SilentChromeDriverServiceBuilder(File origDriverExecutable) {
			this.originalDriverExecutable = origDriverExecutable;
		}

		@Override
		protected ImmutableList<String> createArgs() {
			ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
			for (String superArg : super.createArgs())
				argsBuilder.add(superArg);
			argsBuilder.add(String.format(
				"--chromedriver-path=%s",
				originalDriverExecutable.getAbsolutePath()
			));
			return argsBuilder.build();
		}

	}

	public WebDriver startIEImpl() {
		return startIEImpl(null);
	}
	public WebDriver startIEImpl(String url) {
		String driverPath = locateWebDriver("IEDriverServer.exe");
		File driverExe = new File(driverPath);
		InternetExplorerDriverService.Builder serviceBuilder =
				new InternetExplorerDriverService.Builder()
				.usingAnyFreePort().
				// Prevent verbose messages 'IEDriver started', 'IEDriver
				// listening', 'IEDriver stopped' on stderr:
				withSilent(true);
		if (driverExe.exists())
			serviceBuilder = serviceBuilder.usingDriverExecutable(driverExe);
		InternetExplorerDriverService service = serviceBuilder.build();
		Runtime.getRuntime().addShutdownHook(
				new DriverServiceDestroyer(service)
		);
		InternetExplorerDriver ieBrowser;
		try {
			ieBrowser = new InternetExplorerDriver(service);
		} catch (WebDriverException e) {
			WebDriverException excToRaise = e;
			if (e.getMessage().contains(
				"Protected Mode settings are not the same for all zones."
			))
				excToRaise = new WebDriverException(
					"Error launching IE: Protected Mode settings are not the " +
					"same for all zones. Please follow these steps: " +
					"http://heliumhq.com/docs/internet_explorer#protected_mode"
				);
			throw excToRaise;
		}
		return start(ieBrowser, url);
	}

	private WebDriver start(WebDriver browser, String url) {
		setDriverImpl(browser);
		if (url != null)
			goToImpl(url);
		return getDriverImpl();
	}

	public void goToImpl(final String url) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.get((!url.contains("://") ? "http://" : "") + url);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}

	public void setDriverImpl(WebDriver driver) {
		this.driver = new WebDriverWrapper(driver);
	}

	public WebDriver getDriverImpl() {
		return driver == null ? null : driver.unwrap();
	}

	public void writeImpl(final String text) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				AlertHandling alertHandling = new AlertHandling(driver);
				if (alertHandling.shouldAttemptActionAsIfNoAlertPresent())
					try {
						driver.switchTo().activeElement().sendKeys(text);
						return;
					} catch (UnhandledAlertException e) {
						alertHandling.handleUnhandledAlertException(e);
					}
				writeImpl(text, new AlertImpl(driver));
			}
		}).mightSpawnWindow().execute();
	}
	public void writeImpl(String text, String into) {
		writeImpl(text, new TextFieldImpl(requireDriver(), into));
	}
	public void writeImpl(String text, WebElement into) {
		writeImpl(text, new WebElementWrapper(driver, into));
	}
	private void writeImpl(final String text, final WebElementWrapper into) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				into.unwrap().clear();
				into.unwrap().sendKeys(text);
				driver.setLastManipulatedElement(into);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void writeImpl(final String text, final HTMLElementImpl into) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				into.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementWrapper) {
						writeImpl(text, elementWrapper);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void writeImpl(String text, AlertImpl into) {
		into.write(text);
	}

	public void pressImpl(final CharSequence key) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.switchTo().activeElement().sendKeys(key);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}

	public final CharSequence NULL         = Keys.NULL;
	public final CharSequence CANCEL       = Keys.CANCEL;
	public final CharSequence HELP         = Keys.HELP;
	public final CharSequence BACK_SPACE   = Keys.BACK_SPACE;
	public final CharSequence TAB          = Keys.TAB;
	public final CharSequence CLEAR        = Keys.CLEAR;
	public final CharSequence RETURN       = Keys.RETURN;
	public final CharSequence ENTER        = Keys.ENTER;
	public final CharSequence SHIFT        = Keys.SHIFT;
	public final CharSequence LEFT_SHIFT   = Keys.LEFT_SHIFT;
	public final CharSequence CONTROL      = Keys.CONTROL;
	public final CharSequence LEFT_CONTROL = Keys.LEFT_CONTROL;
	public final CharSequence ALT          = Keys.ALT;
	public final CharSequence LEFT_ALT     = Keys.LEFT_ALT;
	public final CharSequence PAUSE        = Keys.PAUSE;
	public final CharSequence ESCAPE       = Keys.ESCAPE;
	public final CharSequence SPACE        = Keys.SPACE;
	public final CharSequence PAGE_UP      = Keys.PAGE_UP;
	public final CharSequence PAGE_DOWN    = Keys.PAGE_DOWN;
	public final CharSequence END          = Keys.END;
	public final CharSequence HOME         = Keys.HOME;
	public final CharSequence LEFT         = Keys.LEFT;
	public final CharSequence ARROW_LEFT   = Keys.ARROW_LEFT;
	public final CharSequence UP           = Keys.UP;
	public final CharSequence ARROW_UP     = Keys.ARROW_UP;
	public final CharSequence RIGHT        = Keys.RIGHT;
	public final CharSequence ARROW_RIGHT  = Keys.ARROW_RIGHT;
	public final CharSequence DOWN         = Keys.DOWN;
	public final CharSequence ARROW_DOWN   = Keys.ARROW_DOWN;
	public final CharSequence INSERT       = Keys.INSERT;
	public final CharSequence DELETE       = Keys.DELETE;
	public final CharSequence SEMICOLON    = Keys.SEMICOLON;
	public final CharSequence EQUALS       = Keys.EQUALS;
	public final CharSequence NUMPAD0      = Keys.NUMPAD0;
	public final CharSequence NUMPAD1      = Keys.NUMPAD1;
	public final CharSequence NUMPAD2      = Keys.NUMPAD2;
	public final CharSequence NUMPAD3      = Keys.NUMPAD3;
	public final CharSequence NUMPAD4      = Keys.NUMPAD4;
	public final CharSequence NUMPAD5      = Keys.NUMPAD5;
	public final CharSequence NUMPAD6      = Keys.NUMPAD6;
	public final CharSequence NUMPAD7      = Keys.NUMPAD7;
	public final CharSequence NUMPAD8      = Keys.NUMPAD8;
	public final CharSequence NUMPAD9      = Keys.NUMPAD9;
	public final CharSequence MULTIPLY     = Keys.MULTIPLY;
	public final CharSequence ADD          = Keys.ADD;
	public final CharSequence SEPARATOR    = Keys.SEPARATOR;
	public final CharSequence SUBTRACT     = Keys.SUBTRACT;
	public final CharSequence DECIMAL      = Keys.DECIMAL;
	public final CharSequence DIVIDE       = Keys.DIVIDE;
	public final CharSequence F1           = Keys.F1;
	public final CharSequence F2           = Keys.F2;
	public final CharSequence F3           = Keys.F3;
	public final CharSequence F4           = Keys.F4;
	public final CharSequence F5           = Keys.F5;
	public final CharSequence F6           = Keys.F6;
	public final CharSequence F7           = Keys.F7;
	public final CharSequence F8           = Keys.F8;
	public final CharSequence F9           = Keys.F9;
	public final CharSequence F10          = Keys.F10;
	public final CharSequence F11          = Keys.F11;
	public final CharSequence F12          = Keys.F12;
	public final CharSequence META         = Keys.META;
	public final CharSequence COMMAND      = Keys.COMMAND;

	public void clickImpl(String element) {
		clickImpl(toClickableText(element));
	}
	private ClickableText toClickableText(String element) {
		return new ClickableText(requireDriver(), element);
	}
	public void clickImpl(WebElement element) {
		clickImpl(new WebElementWrapper(driver, element));
	}
	private void clickImpl(final WebElementWrapper elementWrapper) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				elementWrapper.unwrap().click();
				driver.setLastManipulatedElement(elementWrapper);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void clickImpl(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper element) {
						clickImpl(element);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void clickImpl(final Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				actions.click();
			}
		});
	}
	private void handleOffset(final Point point, final Action<Actions> action) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				Tuple<WebElementWrapper, Point> eltAndOffset =
						pointToElementAndOffset(point);
				WebElementWrapper element = eltAndOffset.getFirst();
				Point offset = eltAndOffset.getSecond();
				Actions actions = driver.action().moveToElement(
						element.unwrap(), offset.getX(), offset.getY()
				);
				action.performOn(actions);
				actions.perform();
				driver.setLastManipulatedElement(element);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	private Tuple<WebElementWrapper, Point> pointToElementAndOffset(
			Point point
	) {
		final WebDriverWrapper driver = requireDriver();
		WebElementWrapper element = new WebElementWrapper(
			driver,
			(WebElement) driver.executeScript(String.format(
				"return document.elementFromPoint(%s, %s);",
				point.getX(), point.getY()
			))
		);
		Point offset = point.withOffset(
				-element.getLocation().getLeft(),
				-element.getLocation().getTop()
		);
		if (offset.equals(Point(0, 0)) && driver.isFirefox())
			// In some CSS settings (eg. inttest_point.html), the (0, 0) point
			// of buttons in Firefox is not clickable! The reason for this is
			// that Firefox styles buttons to not be perfect squares, but have
			// an indent in the corners. This workaround makes
			// click(btn.getTopLeft()) work even when this happens:
			offset = Point(1, 1);
		return new Tuple<WebElementWrapper, Point>(element, offset);
	}
	private WebElementWrapper pointToElement(Point to) {
		return pointToElementAndOffset(to).getFirst();
	}

	public void doubleclickImpl(String element) {
		doubleclickImpl(toClickableText(element));
	}
	public void doubleclickImpl(WebElement element) {
		doubleclickImpl(new WebElementWrapper(driver, element));
	}
	private void doubleclickImpl(final WebElementWrapper elementWrapper) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.action().doubleClick(elementWrapper.unwrap()).perform();
				driver.setLastManipulatedElement(elementWrapper);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void doubleclickImpl(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper element) {
						doubleclickImpl(element);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void doubleclickImpl(final Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				actions.doubleClick();
			}
		});
	}

	public void dragImpl(String element, String to) {
		dragImpl(toClickableText(element), toClickableText(to));
	}
	public void dragImpl(String element, WebElement to) {
		dragImpl(toClickableText(element), new WebElementWrapper(driver, to));
	}
	public void dragImpl(String element, HTMLElementImpl to) {
		dragImpl(toClickableText(element), to);
	}
	public void dragImpl(String element, Point to) {
		dragImpl(toClickableText(element), pointToElement(to));
	}
	public void dragImpl(WebElement element, String to) {
		dragImpl(new WebElementWrapper(driver, element), toClickableText(to));
	}
	public void dragImpl(WebElement element, WebElement to) {
		dragImpl(
			new WebElementWrapper(driver, element),
			new WebElementWrapper(driver, to)
		);
	}
	private void dragImpl(WebElementWrapper element, WebElementWrapper to) {
		DragHelper dragHelper = new DragHelper(this);
		dragHelper.begin();
		try {
			dragHelper.startDragging(element);
			driver.setLastManipulatedElement(element);
			dragHelper.dropOnTarget(to);
			driver.setLastManipulatedElement(to);
		} finally {
			dragHelper.end();
		}
	}
	private void dragImpl(WebElementWrapper element, HTMLElementImpl to) {
		final DragHelper dragHelper = new DragHelper(this);
		dragHelper.begin();
		try {
			dragHelper.startDragging(element);
			driver.setLastManipulatedElement(element);
			to.perform(
				new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper target) {
						dragHelper.dropOnTarget(target);
						driver.setLastManipulatedElement(target);
					}
				}
			);
		} finally {
			dragHelper.end();
		}
	}
	private void dragImpl(HTMLElementImpl element, WebElementWrapper to) {
		final DragHelper dragHelper = new DragHelper(this);
		dragHelper.begin();
		try {
			element.perform(
				new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementToDrag) {
						dragHelper.startDragging(elementToDrag);
						driver.setLastManipulatedElement(elementToDrag);
					}
				}
			);
			dragHelper.dropOnTarget(to);
			driver.setLastManipulatedElement(to);
		} finally {
			dragHelper.end();
		}
	}
	public void dragImpl(HTMLElementImpl element, HTMLElementImpl to) {
		final DragHelper dragHelper = new DragHelper(this);
		dragHelper.begin();
		try {
			element.perform(
				new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementToDrag) {
						dragHelper.startDragging(elementToDrag);
						driver.setLastManipulatedElement(elementToDrag);
					}
				}
			);
			to.perform(
				new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper target) {
						dragHelper.dropOnTarget(target);
						driver.setLastManipulatedElement(target);
					}
				}
			);
		} finally {
			dragHelper.end();
		}
	}
	public void dragImpl(WebElement element, HTMLElementImpl to) {
		dragImpl(new WebElementWrapper(driver, element), to);
	}
	public void dragImpl(WebElement element, Point to) {
		dragImpl(new WebElementWrapper(driver, element), pointToElement(to));
	}
	public void dragImpl(HTMLElementImpl element, String to) {
		dragImpl(element, toClickableText(to));
	}
	public void dragImpl(HTMLElementImpl element, WebElement to) {
		dragImpl(element, new WebElementWrapper(driver, to));
	}
	public void dragImpl(HTMLElementImpl element, Point to) {
		dragImpl(element, pointToElement(to));
	}
	public void dragImpl(Point element, String to) {
		dragImpl(pointToElement(element), toClickableText(to));
	}
	public void dragImpl(Point element, WebElement to) {
		dragImpl(pointToElement(element), new WebElementWrapper(driver, to));
	}
	public void dragImpl(Point element, HTMLElementImpl to) {
		dragImpl(pointToElement(element), to);
	}
	public void dragImpl(Point element, Point to) {
		dragImpl(pointToElement(element), pointToElement(to));
	}
	private void pressMouseOn(String element) {
		pressMouseOn(toClickableText(element));
	}
	private void pressMouseOn(WebElement element) {
		pressMouseOn(new WebElementWrapper(driver, element));
	}
	void pressMouseOn(final WebElementWrapper elementWrapper) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.action().clickAndHold(elementWrapper.unwrap()).perform();
				driver.setLastManipulatedElement(elementWrapper);
			}
		}).handleUnexpectedAlert().execute();
	}
	private void pressMouseOn(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementWrapper) {
						pressMouseOn(elementWrapper);
					}
				});
			}
		}).handleUnexpectedAlert().execute();
	}
	private void pressMouseOn(Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				actions.clickAndHold();
			}
		});
	}
	private void releaseMouseOver(String element) {
		releaseMouseOver(toClickableText(element));
	}
	private void releaseMouseOver(WebElement element) {
		releaseMouseOver(new WebElementWrapper(driver, element));
	}
	void releaseMouseOver(final WebElementWrapper elementWrapper) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.action().moveToElement(elementWrapper.unwrap())
						.release().perform();
				driver.setLastManipulatedElement(elementWrapper);
			}
		}).handleUnexpectedAlert().execute();
	}
	private void releaseMouseOver(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementWrapper) {
						releaseMouseOver(elementWrapper);
					}
				});
			}
		}).handleUnexpectedAlert().execute();
	}
	private void releaseMouseOver(Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				actions.release();
			}
		});
	}

	public <T, G extends GUIElementImpl<T>> List<GUIElementImpl<T>> findAllImpl(
			final G predicateImpl
	) {
		final WebDriverWrapper driver = requireDriver();
		final List<GUIElementImpl<T>> result =
			new ArrayList<GUIElementImpl<T>>();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				for (GUIElementImpl<T> boundGuiElementImpl :
						predicateImpl.findAll())
					result.add(boundGuiElementImpl);
			}
		}).handleUnexpectedAlert().execute();
		return result;
	}

	public void scrollDownImpl() {
		scrollDownImpl(100);
	}
	public void scrollDownImpl(int numPixels) {
		scrollBy(0, numPixels);
	}

	public void scrollUpImpl() {
		scrollUpImpl(100);
	}
	public void scrollUpImpl(int numPixels) {
		scrollBy(0, -numPixels);
	}

	public void scrollRightImpl() {
		scrollRightImpl(100);
	}
	public void scrollRightImpl(int numPixels) {
		scrollBy(numPixels, 0);
	}

	public void scrollLeftImpl() {
		scrollLeftImpl(100);
	}
	public void scrollLeftImpl(int numPixels) {
		scrollBy(-numPixels, 0);
	}

	private void scrollBy(final int dxPixels, final int dyPixels) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.executeScript(
						"window.scrollBy(arguments[0], arguments[1]);",
						dxPixels, dyPixels
				);
			}
		}).handleUnexpectedAlert().execute();
	}

	public void hoverImpl(String element) {
		hoverImpl(toClickableText(element));
	}
	public void hoverImpl(WebElement element) {
		hoverImpl(new WebElementWrapper(driver, element));
	}
	private void hoverImpl(final WebElementWrapper element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.action().moveToElement(element.unwrap()).perform();
				driver.setLastManipulatedElement(element);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void hoverImpl(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementWrapper) {
						hoverImpl(elementWrapper);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void hoverImpl(Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				// Nothing to do here. At this point, the mouse will have
				// already been hovered over the correct position.
			}
		});
	}

	public void rightclickImpl(String element) {
		rightclickImpl(toClickableText(element));
	}
	public void rightclickImpl(WebElement element) {
		rightclickImpl(new WebElementWrapper(driver, element));
	}
	private void rightclickImpl(final WebElementWrapper wrapper) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.action().contextClick(wrapper.unwrap()).perform();
				driver.setLastManipulatedElement(wrapper);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void rightclickImpl(final HTMLElementImpl element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				element.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper elementWrapper) {
						rightclickImpl(elementWrapper);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void rightclickImpl(Point point) {
		handleOffset(point, new Action<Actions>() {
			@Override
			void performOn(Actions actions) {
				actions.contextClick();
			}
		});
	}
	
	public void selectImpl(String comboBox, String value) {
		this.selectImpl(ComboBoxImpl(comboBox), value);
	}
	public void selectImpl(ComboBoxImpl comboBox, String value) {
		new Select(comboBox.getWebElement()).selectByVisibleText(value);
	}	

	public void dragFileImpl(String filePath, String to) {
		dragFileImpl(filePath, toClickableText(to));
	}
	public void dragFileImpl(String filePath, WebElement to) {
		dragFileImpl(filePath, new WebElementWrapper(driver, to));
	}
	private void dragFileImpl(
		final String filePath, final WebElementWrapper to
	) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				DragAndDropFile dragAndDrop =
						new DragAndDropFile(driver, filePath);
				dragAndDrop.begin();
				try {
					// Some web apps (Gmail in particular) only register for the
					// 'drop' event when user has dragged the file over the
					// document. We therefore simulate this dragging over the
					// document first:
					dragAndDrop.dragOverDocument();
					dragAndDrop.dropOn(to);
					driver.setLastManipulatedElement(to);
				} finally {
					dragAndDrop.end();
				}
			}
		}).handleUnexpectedAlert().execute();
	}
	public void dragFileImpl(final String filePath, final HTMLElementImpl to) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				final DragAndDropFile dragAndDrop =
						new DragAndDropFile(driver, filePath);
				dragAndDrop.begin();
				try {
					// Some web apps (Gmail in particular) only register for the
					// 'drop' event when user has dragged the file over the
					// document. We therefore simulate this dragging over the
					// document first:
					dragAndDrop.dragOverDocument();
					to.perform(new Action<WebElementWrapper>() {
						@Override
						void performOn(WebElementWrapper element) {
							dragAndDrop.dropOn(element);
							driver.setLastManipulatedElement(element);
						}
					});
				} finally {
					dragAndDrop.end();
				}
			}
		}).handleUnexpectedAlert().execute();
	}

	public void attachFileImpl(String filePath) {
		attachFileImpl(filePath, new FileInput(requireDriver()));
	}
	public void attachFileImpl(String filePath, String to) {
		attachFileImpl(filePath, new FileInput(requireDriver(), to));
	}
	public void attachFileImpl(String filePath, WebElement to) {
		attachFileImpl(filePath, new WebElementWrapper(driver, to));
	}
	private void attachFileImpl(
		final String filePath, final WebElementWrapper to
	) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				to.unwrap().sendKeys(filePath);
				driver.setLastManipulatedElement(to);
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void attachFileImpl(
		final String filePath, final HTMLElementImpl to
	) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				to.perform(new Action<WebElementWrapper>() {
					@Override
					void performOn(WebElementWrapper element) {
						attachFileImpl(filePath, element);
					}
				});
			}
		}).handleUnexpectedAlert().mightSpawnWindow().execute();
	}
	public void attachFileImpl(final String filePath, Point to) {
		attachFileImpl(filePath, pointToElement(to));
	}

	public void refreshImpl() {
		final WebDriverWrapper driver = requireDriver();
		AlertHandling alertHandling = new AlertHandling(driver);
		if (alertHandling.shouldAttemptActionAsIfNoAlertPresent())
			try {
				refreshNoAlert();
				return;
			} catch (UnhandledAlertException e) {
				alertHandling.handleUnhandledAlertException(e);
			}
		refreshWithAlert();
	}
	private void refreshNoAlert() {
		requireDriver().unwrap().navigate().refresh();
	}
	private void refreshWithAlert() {
		new AlertImpl(requireDriver()).accept();
		refreshNoAlert();
	}

	public void waitUntilImpl(ExpectedCondition<?> condition) {
		waitUntilImpl(condition, 10);
	}
	public void waitUntilImpl(
			ExpectedCondition<?> condition, long timeoutSecs
	) {
		waitUntilImpl(condition, timeoutSecs, 0.5);
	}
	public void waitUntilImpl(
			ExpectedCondition<?> condition, long timeoutSecs,
			double intervalSecs
	) {
		final WebDriverWrapper driver = requireDriver();
		long intervalMillis = (long) Math.ceil(intervalSecs / 1000);
		WebDriverWait wait =
				new WebDriverWait(driver.unwrap(), timeoutSecs, intervalMillis);
		wait.until(condition);
	}

	public void switchToImpl(String windowTitle) {
		switchToImpl(new WindowImpl(requireDriver(), windowTitle));
	}
	public void switchToImpl(final WindowImpl window) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				driver.switchTo().window(window.getHandle());
			}
		}).handleUnexpectedAlert().execute();
	}

	public void killBrowserImpl() {
		requireDriver().unwrap().quit();
		driver = null;
	}

	public void highlightImpl(String element) {
		highlightImpl(new TextImpl(requireDriver(), element));
	}
	public void highlightImpl(HTMLElementImpl element) {
		highlightImpl(element.getFirstOccurrence());
	}
	private void highlightImpl(WebElementWrapper elementWrapper) {
		highlightImpl(elementWrapper.unwrap());
	}
	public void highlightImpl(final WebElement element) {
		final WebDriverWrapper driver = requireDriver();
		(new APICommandImpl(driver) {
			@Override
			public void run() {
				String previousStyle = element.getAttribute("style");
				driver.executeScript(
					"arguments[0].setAttribute(" +
						"'style', 'border: 2px solid red; font-weight: bold;'" +
					");", element
				);
				driver.executeScript(
					"var target = arguments[0];" +
					"var previousStyle = arguments[1];" +
					"setTimeout(" +
						"function() {" +
							"target.setAttribute('style', previousStyle);" +
						"}, 2000" +
					");", element, previousStyle
				);
			}
		}).handleUnexpectedAlert().execute();
	}

	public $Impl $Impl(String selector, SearchRegion... searchRegions) {
		return new $Impl(requireDriver(), selector, searchRegions);
	}

	public TextImpl TextImpl(SearchRegion... searchRegions) {
		return new TextImpl(requireDriver(), searchRegions);
	}
	public TextImpl TextImpl(
			String text, SearchRegion... searchRegions
	) {
		return new TextImpl(requireDriver(), text, searchRegions);
	}

	public LinkImpl LinkImpl(SearchRegion... searchRegions) {
		return new LinkImpl(requireDriver(), searchRegions);
	}
	public LinkImpl LinkImpl(
			String text, SearchRegion... searchRegions
	) {
		return new LinkImpl(requireDriver(), text, searchRegions);
	}

	public ListItemImpl ListItemImpl(SearchRegion... searchRegions) {
		return new ListItemImpl(requireDriver(), searchRegions);
	}
	public ListItemImpl ListItemImpl(
			String text, SearchRegion... searchRegions
	) {
		return new ListItemImpl(requireDriver(), text, searchRegions);
	}

	public ButtonImpl ButtonImpl(SearchRegion... searchRegions) {
		return new ButtonImpl(requireDriver(), searchRegions);
	}
	public ButtonImpl ButtonImpl(
			String text, SearchRegion... searchRegions
	) {
		return new ButtonImpl(requireDriver(), text, searchRegions);
	}

	public ImageImpl ImageImpl(SearchRegion... searchRegions) {
		return new ImageImpl(requireDriver(), searchRegions);
	}
	public ImageImpl ImageImpl(
			String alt, SearchRegion... searchRegions
	) {
		return new ImageImpl(requireDriver(), alt, searchRegions);
	}

	public TextFieldImpl TextFieldImpl(SearchRegion... searchRegions) {
		return new TextFieldImpl(requireDriver(), searchRegions);
	}
	public TextFieldImpl TextFieldImpl(
			String label, SearchRegion... searchRegions
	) {
		return new TextFieldImpl(requireDriver(), label, searchRegions);
	}

	public ComboBoxImpl ComboBoxImpl(SearchRegion... searchRegions) {
		return new ComboBoxImpl(requireDriver(), searchRegions);
	}
	public ComboBoxImpl ComboBoxImpl(
			String label, SearchRegion... searchRegions
	) {
		return new ComboBoxImpl(requireDriver(), label, searchRegions);
	}

	public CheckBoxImpl CheckBoxImpl(SearchRegion... searchRegions) {
		return new CheckBoxImpl(requireDriver(), searchRegions);
	}
	public CheckBoxImpl CheckBoxImpl(
			String label, SearchRegion... searchRegions
	) {
		return new CheckBoxImpl(requireDriver(), label, searchRegions);
	}

	public RadioButtonImpl RadioButtonImpl(
			SearchRegion... searchRegions
	) {
		return new RadioButtonImpl(requireDriver(), searchRegions);
	}
	public RadioButtonImpl RadioButtonImpl(
			String label, SearchRegion... searchRegions
	) {
		return new RadioButtonImpl(requireDriver(), label, searchRegions);
	}

	public WindowImpl WindowImpl() {
		return new WindowImpl(requireDriver());
	}
	public WindowImpl WindowImpl(String title) {
		return new WindowImpl(requireDriver(), title);
	}

	public AlertImpl AlertImpl() {
		return new AlertImpl(requireDriver());
	}

	public AlertImpl AlertImpl(String text) {
		return new AlertImpl(requireDriver(), text);
	}

	public SearchRegion toLeftOf(HTMLElementImpl element) {
		return new SearchRegion(SearchDirection.TO_LEFT_OF, element);
	}
	public SearchRegion toRightOf(HTMLElementImpl element) {
		return new SearchRegion(SearchDirection.TO_RIGHT_OF, element);
	}
	public SearchRegion above(HTMLElementImpl element) {
		return new SearchRegion(SearchDirection.ABOVE, element);
	}
	public SearchRegion below(HTMLElementImpl element) {
		return new SearchRegion(SearchDirection.BELOW, element);
	}

	WebDriverWrapper requireDriver() {
		if (driver == null)
			throw new RuntimeException(DRIVER_REQUIRED_MESSAGE);
		return driver;
	}

}