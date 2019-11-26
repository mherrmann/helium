package com.heliumhq.selenium_wrappers;

import com.heliumhq.util.geom.Rectangle;
import org.openqa.selenium.*;

public class WebElementWrapper extends Wrapper<WebElement> {

	private WebDriverWrapper driver;

	private Rectangle cachedLocation;

	private int[] frameIndex;

	public WebElementWrapper(WebDriverWrapper driver, WebElement target) {
		super(target);
		this.driver = driver;
	}

	public void setFrameIndex(int[] frameIndex) {
		this.frameIndex = frameIndex;
	}

	public Rectangle getLocation() {
		if (cachedLocation == null)
			cacheLocation();
		return cachedLocation;
	}

	private void cacheLocation() {
		if (frameIndex == null)
			cacheLocationWithoutHandlingErrors();
		else {
			try {
				cacheLocationWithoutHandlingErrors();
			} catch (StaleElementReferenceException originalExc) {
				try {
					new FrameIterator(driver).switchToFrame(frameIndex);
					cacheLocationWithoutHandlingErrors();
				} catch (NoSuchFrameException e2) {
					throw originalExc;
				}
			}
		}
	}

	// In the Python implementation of Selenium, when the browser is closed and
	// a WebElement's .location is accessed, an ugly socket.error is raised:
	// "No connection could be made because the target machine actively refused
	// it". The Python implementation of Helium catches this exception and
	// raises a more meaningful StaleElementReferenceException in this case (see
	// _translate_url_errors_caused_by_server_shutdown).
	// The Java implementation of Selenium does not have the problem described
	// above (it throws a descriptive error in the aforementioned situation).
	// Hence we don't need to do anything special here.
	private void cacheLocationWithoutHandlingErrors() {
		Point location = target.getLocation();
		Dimension size = target.getSize();
		cachedLocation = new Rectangle(
			location.getX(), location.getY(),
			size.getWidth(), size.getHeight()
		);
	}

	public boolean isDisplayed() {
		try {
			return target.isDisplayed() && getLocation().intersects(
				new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE)
			);
		} catch (StaleElementReferenceException e) {
			return false;
		}
	}

	public String getAttribute(String name) {
		if (frameIndex == null)
			return target.getAttribute(name);
		try {
			return target.getAttribute(name);
		} catch (StaleElementReferenceException originalExc) {
			try {
				new FrameIterator(driver).switchToFrame(frameIndex);
				return target.getAttribute(name);
			} catch (NoSuchFrameException e) {
				throw originalExc;
			}
		}
	}

	public String getText() {
		if (frameIndex == null)
			return target.getText();
		try {
			return target.getText();
		} catch (StaleElementReferenceException originalExc) {
			try {
				new FrameIterator(driver).switchToFrame(frameIndex);
				return target.getText();
			} catch (NoSuchFrameException e) {
				throw originalExc;
			}
		}
	}

	public String getTagName() {
		if (frameIndex == null)
			return target.getTagName();
		try {
			return target.getTagName();
		} catch (StaleElementReferenceException originalExc) {
			try {
				new FrameIterator(driver).switchToFrame(frameIndex);
				return target.getTagName();
			} catch (NoSuchFrameException e) {
				throw originalExc;
			}
		}
	}

	public String toString() {
		return String.format(
			"<%s>%s</%s>", getTagName(), getText(), getTagName()
		);
	}

}
