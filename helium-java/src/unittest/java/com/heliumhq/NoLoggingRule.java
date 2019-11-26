package com.heliumhq;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoLoggingRule extends TestWatcher {
	@Override
	protected void starting(Description description) {
		Logger heliumLogger = Logger.getLogger("com.heliumhq");
		heliumLogger.setLevel(Level.WARNING);
	}
}
