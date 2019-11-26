package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

public class ListItemImpl extends HTMLElementContainingText {

	public ListItemImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
	}

	public ListItemImpl(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
	}

	@Override
	protected String getXPathNodeSelector() {
		return "li";
	}

}