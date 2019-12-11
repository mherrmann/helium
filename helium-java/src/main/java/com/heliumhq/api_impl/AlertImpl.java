package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

public class AlertImpl extends GUIElementImpl<org.openqa.selenium.Alert> {

	private final String searchText;

	public AlertImpl(WebDriverWrapper driver) {
		this(driver, null);
	}

	public AlertImpl(WebDriverWrapper driver, String searchText) {
		super(driver);
		this.searchText = searchText;
	}

	@Override
	protected Iterable<org.openqa.selenium.Alert> findAllOccurrences() {
		List<org.openqa.selenium.Alert> result =
				new ArrayList<org.openqa.selenium.Alert>();
		try {
			org.openqa.selenium.Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			if (searchText == null ||
					(text != null && text.startsWith(searchText)))
				result.add(alert);
		} catch (NoAlertPresentException e) {
			// alert hasn't been added to result so no need to do anything here.
		}
		return result;
	}

	public String getText() {
		return getFirstOccurrence().getText();
	}

	public void accept() {
		org.openqa.selenium.Alert firstOccurrence = getFirstOccurrence();
		try {
			firstOccurrence.accept();
		} catch (WebDriverException e) {
			// Attempt to work around Selenium issue 3544:
			// https://code.google.com/p/selenium/issues/detail?id=3544
			String msg = e.getMessage();
			if (msg != null && msg.matches(
					"a\\.document\\.getElementsByTagName\\([^\\)]*\\)\\[0\\] " +
					"is undefined")) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e2) {}
				firstOccurrence.accept();
			} else {
				throw e;
			}
		}
	}

	public void dismiss() {
		getFirstOccurrence().dismiss();
	}

	@Override
	protected Object[] getConstructorArgs() {
		String text = isBound() ? getText() : searchText;
		return new Object[] {text};
	}

	void write(String text) {
		getFirstOccurrence().sendKeys(text);
	}

}
