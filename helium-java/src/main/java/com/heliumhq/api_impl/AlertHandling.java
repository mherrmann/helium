package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.openqa.selenium.UnhandledAlertException;

class AlertHandling {

	private final WebDriverWrapper driver;

	AlertHandling(WebDriverWrapper driver) {
		this.driver = driver;
	}

	boolean shouldAttemptActionAsIfNoAlertPresent() {
		// Selenium's check whether an alert is present takes 2(!) seconds in
		// Firefox - see
		// https://code.google.com/p/selenium/issues/detail?id=2438.
		// We do not want to incur this overhead so if we're in Firefox, we try
		// our luck with the normal implementation and trust the
		// UnhandledAlertException to tell us when an alert is present.
		// The reason we don't do it like this for all browsers is that by
		// default, occurring exceptions dismiss open alert dialogs in Selenium
		// (Desired-Capability 'unexpectedAlertBehaviour'). So, if we just
		// 'tried' no_alert and an alert were in fact present, then this alert
		// would unintentionally be closed. To prevent this from happening in
		// Firefox, we require unexpectedAlertBehaviour = 'ignore', but with the
		// present implementation this requirement is at least confined to
		// Firefox.
		return driver.isFirefox() || ! new AlertImpl(driver).exists();
	}

	void handleUnhandledAlertException(UnhandledAlertException e) {
		if (driver.isFirefox() && ! new AlertImpl(driver).exists())
			throw new RuntimeException(
					"An alert dialog was open but was closed by an " +
					"expected exception. This normally happens when you " +
					"called setDriver(...) with a Firefox driver you " +
					"instantiated yourself without setting CapabilityType." +
					"UNEXPECTED_ALERT_BEHAVIOUR to UnexpectedAlertBehaviour." +
					"IGNORE. If this is not the case, please file a bug " +
					"report at http://heliumhq.com."
			);
	}

}
