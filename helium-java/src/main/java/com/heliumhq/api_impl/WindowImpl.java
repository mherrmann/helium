package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class WindowImpl extends GUIElementImpl<WindowImpl.SeleniumWindow> {

	private final String searchTitle;

	public WindowImpl(WebDriverWrapper driver) {
		this(driver, null);
	}

	public WindowImpl(WebDriverWrapper driver, String title) {
		super(driver);
		searchTitle = title;
	}

	@Override
	protected Iterable<SeleniumWindow> findAllOccurrences() {
		List<ResultScore<SeleniumWindow>> resultScores =
				new ArrayList<ResultScore<SeleniumWindow>>();
		for (String handle : driver.unwrap().getWindowHandles()) {
			SeleniumWindow window = new SeleniumWindow(driver.unwrap(), handle);
			if (searchTitle == null)
				resultScores.add(new ResultScore<SeleniumWindow>(window));
			else {
				String title = window.getTitle();
				if (title.startsWith(searchTitle)) {
					int score = title.length() - searchTitle.length();
					resultScores.add(
							new ResultScore<SeleniumWindow>(window, score)
					);
				}
			}
		}
		return ResultScore.getSortedResults(resultScores);
	}

	public String getTitle() {
		return getFirstOccurrence().getTitle();
	}

	public String getHandle() {
		return getFirstOccurrence().getHandle();
	}

	@Override
	protected Object[] getConstructorArgs() {
		String title = isBound() ? getTitle() : searchTitle;
		return new Object[] {title};
	}

	static class SeleniumWindow {
		private final WebDriver driver;
		private final String handle;
		private String windowHandleBefore;

		public SeleniumWindow(WebDriver driver, String handle) {
			this.driver = driver;
			this.handle = handle;
		}

		public String getHandle() {
			return handle;
		}

		public String getTitle() {
			activateTemporarily();
			try {
				return driver.getTitle();
			} finally {
				restorePreviousWindow();
			}
		}

		private void activateTemporarily() {
			boolean doSwitch;
			try {
				windowHandleBefore = driver.getWindowHandle();
				doSwitch = ! windowHandleBefore.equals(handle);
			} catch (NoSuchWindowException windowClosed) {
				doSwitch = true;
			}
			if (doSwitch)
				driver.switchTo().window(handle);
		}

		private void restorePreviousWindow() {
			if (
				windowHandleBefore != null &&
				!driver.getWindowHandle().equals(windowHandleBefore)
			)
				driver.switchTo().window(windowHandleBefore);
		}
	}

}