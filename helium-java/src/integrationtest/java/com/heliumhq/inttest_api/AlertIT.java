package com.heliumhq.inttest_api;

public class AlertIT extends AlertAT {

	@Override
	public String getLinkToOpenAlert() {
		return "Display alert";
	}

	@Override
	public String getExpectedAlertText() {
		return "Hello World!";
	}

	@Override
	public String getExpectedAlertAcceptedResult() {
		return "Alert displayed";
	}

}
