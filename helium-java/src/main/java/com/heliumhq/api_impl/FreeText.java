package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

public class FreeText extends HTMLElementContainingText {
	public FreeText(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
	}

	@Override
	protected String getXPathNodeSelector() {
		return "text()";
	}

	@Override
	protected String getXPath() {
		return super.getXPath() + "/..";
	}
}
