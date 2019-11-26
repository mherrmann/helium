package com.heliumhq;

import static com.heliumhq.API.Config;

public class TemporaryImplicitWait {
	private double implicitWaitSecsBefore;
	public TemporaryImplicitWait(double value) {
		implicitWaitSecsBefore = Config.getImplicitWaitSecs();
		Config.setImplicitWaitSecs(value);
	}
	public void end() {
		Config.setImplicitWaitSecs(implicitWaitSecsBefore);
	}
}