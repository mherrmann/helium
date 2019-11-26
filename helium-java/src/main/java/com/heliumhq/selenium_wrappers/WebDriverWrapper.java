package com.heliumhq.selenium_wrappers;

import com.heliumhq.util.geom.Rectangle;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebDriverWrapper extends Wrapper<WebDriver> {

	private WebElementWrapper lastManipulatedElement;

	public WebDriverWrapper(WebDriver target) {
		super(target);
	}

	public double getDistanceToLastManipulated(WebElementWrapper element) {
		if (lastManipulatedElement == null)
			return 0;
		Rectangle lastManipulatedLocation;
		try {
			lastManipulatedLocation = lastManipulatedElement.getLocation();
		} catch (StaleElementReferenceException e) {
			return 0;
		}
		return element.getLocation().getDistanceTo(lastManipulatedLocation);
	}

	public void setLastManipulatedElement(WebElementWrapper value) {
		lastManipulatedElement = value;
	}

	public Actions action() {
		return new Actions(target);
	}

	public List<org.openqa.selenium.WebElement> findElements(By by) {
		List<WebElement> result = target.findElements(by);
		// The Python implementation sometimes returns None instead of [] for
		// find_elements_by_xpath(...). Just in case this also occurs in
		// Selenium's Java bindings, we add the null-check here too:
		if (result == null)
			result = new ArrayList<WebElement>();
		return result;
	}

	public void get(String url) {
		target.get(url);
	}

	public Object executeScript(String script, Object... args) {
		return ((JavascriptExecutor) target).executeScript(script, args);
	}

	public String getBrowserName() {
		return ((RemoteWebDriver) target).getCapabilities().getBrowserName();
	}

	public boolean isFirefox() {
		return "firefox".equals(getBrowserName());
	}

	public boolean isIE() {
		return "internet explorer".equals(getBrowserName());
	}

	public WebDriver.TargetLocator switchTo() {
		return target.switchTo();
	}

	public String getWindowHandle() {
		return target.getWindowHandle();
	}

	public Set<String> getWindowHandles() {
		return target.getWindowHandles();
	}

}
