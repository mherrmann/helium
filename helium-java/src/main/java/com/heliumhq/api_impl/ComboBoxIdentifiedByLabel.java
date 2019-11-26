package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

public class ComboBoxIdentifiedByLabel extends LabelledElement {

	ComboBoxIdentifiedByLabel(
		WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, label, searchRegions);
	}

	@Override
	protected String getXPath() {
		return "//select | //input[@list]";
	}

}
