package com.heliumhq.inttest_api;

import com.heliumhq.Environment;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public abstract class BrowserAT {

	protected static WebDriver driver;

	@BeforeClass
	public static void setUpClass() {
		if (APITestSuite.TEST_BROWSER != null) {
			driver = APITestSuite.TEST_BROWSER;
			setDriver(driver);
		} else
			driver = startBrowser();
	}

	public static WebDriver startBrowser() {
		return startBrowser(null);
	}

	public static WebDriver startBrowser(String url) {
		String testBrowserName = getTestBrowserName();
		if (testBrowserName.equals("chrome"))
			return startChrome(url, true);
		else if (testBrowserName.equals("ie")) {
			assumeTrue(System.getProperty("os.name").startsWith("Windows"));
			return startIE(url);
		} else {
			assert testBrowserName.equals("firefox");
			return startFirefox(url, true);
		}
	}

	protected static String getTestBrowserName() {
		String result = System.getenv("TEST_BROWSER");
		return result != null ? result : "chrome";
	}

	@Before
	public void setUp() {
		goTo(getURL());
	}

	protected String getURL() {
		return Environment.getITFileURL(getPage());
	}

	protected String getPage() {
		throw new UnsupportedOperationException(
			"To be overridden by subclasses."
		);
	}

	protected String readResultFromBrowser() throws InterruptedException {
		long THREE_SECONDS = 3000;
		return readResultFromBrowser(THREE_SECONDS);
	}
	protected String readResultFromBrowser(long timeout) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + timeout) {
			String result =
				driver.findElement(By.id("result")).getAttribute("innerHTML");
			if (! result.equals(""))
				return result;
			Thread.sleep(200);
		}
		return "";
	}

	protected void assertFindsEltWithId(HTMLElement predicate, String id) {
		assertEquals(id, predicate.getWebElement().getAttribute("id"));
	}

	@AfterClass
	public static void tearDownClass() {
		if (driver != APITestSuite.TEST_BROWSER)
			killBrowser();
	}

}
