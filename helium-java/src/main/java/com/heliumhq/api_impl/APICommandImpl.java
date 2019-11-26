package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.openqa.selenium.UnhandledAlertException;

import java.util.HashSet;
import java.util.Set;

abstract class APICommandImpl implements Runnable {

	private final WebDriverWrapper driver;

	APICommandImpl(WebDriverWrapper driver) {
		this.driver = driver;
	}

	APICommandImpl handleUnexpectedAlert() {
		return new APICommandImpl(driver) {
			@Override
			public void run() {
				try {
					APICommandImpl.this.run();
				} catch (UnhandledAlertException e) {
					throwUnhandledAlertExceptionWithNiceMsg();
				}
			}
			private void throwUnhandledAlertExceptionWithNiceMsg() {
				throw new UnhandledAlertException(
					"This command is not supported when an alert is present. " +
					"To accept the alert (this usually corresponds to " +
					"clicking 'OK') use `Alert().accept()`. To dismiss the " +
					"alert (ie. 'cancel' it), use `Alert().dismiss()`. If " +
					"the alert contains a text field, you can use write(...) " +
					"to set its value. Eg.: `write('hi there!')`."
				);

			}
		};
	}

	APICommandImpl mightSpawnWindow() {
		return new APICommandImpl(driver) {
			@Override
			public void run() {
				if (driver.isIE() && new AlertImpl(driver).exists()) {
					// Accessing .window_handles in IE when an alert is present
					// raises an UnexpectedAlertPresentException. When
					// DesiredCapability 'unexpectedAlertBehaviour' is not
					// 'ignore' (the default is 'dismiss'), this leads to the
					// alert being closed. Since we don't want to
					// unintentionally close alert dialogs, we therefore do not
					// access .window_handles in IE when an alert is present.
					APICommandImpl.this.run();
					return;
				}
				Set<String> windowHandlesBefore =
						driver.unwrap().getWindowHandles();
				APICommandImpl.this.run();
				// As above, don't access .window_handles in IE if an alert is
				// present:
				if (!(driver.isIE() && new AlertImpl(driver).exists())) {
					Set<String> newWindowHandles = new HashSet<String>(
							driver.unwrap().getWindowHandles()
					);
					newWindowHandles.removeAll(windowHandlesBefore);
					if (!newWindowHandles.isEmpty())
						driver.switchTo().window(
							newWindowHandles.iterator().next()
						);
				}
			}
		};
	}

	void execute() {
		run();
	}

}
