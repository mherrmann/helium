package com.heliumhq.inttest_api;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static com.heliumhq.inttest_api.BrowserAT.startBrowser;
import static com.heliumhq.API.killBrowser;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/*IT.class")
public class APITestSuite {

	static WebDriver TEST_BROWSER = null;

	@BeforeClass
	public static void setUpClass() {
		TEST_BROWSER = startBrowser();
	}

	@AfterClass
	public static void tearDownClass() {
		if (TEST_BROWSER != null)
			killBrowser();
		TEST_BROWSER = null;
	}

}
