package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

class FileInput extends LabelledElement {

	FileInput(WebDriverWrapper driver, SearchRegion... searchRegions) {
		super(driver, searchRegions);
	}

	FileInput(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, label, searchRegions);
	}

	@Override
	protected String getXPath() {
		return "//input[@type='file']";
	}

}
