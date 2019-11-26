package com.heliumhq.inttest_api;

public class ConfirmationDialogIT extends AlertAT {
	@Override
	public String getLinkToOpenAlert() {
		return "Ask for confirmation";
	}

	@Override
	public String getExpectedAlertText() {
		return "Proceed?";
	}

	@Override
	public String getExpectedAlertAcceptedResult() {
		return "Accepted";
	}

	@Override
	public String getExpectedAlertDismissedResult() {
		return "Dismissed";
	}
}
