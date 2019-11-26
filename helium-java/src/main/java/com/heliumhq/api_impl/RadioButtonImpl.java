package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

public class RadioButtonImpl extends LabelledElement {

	public RadioButtonImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
	}

	public RadioButtonImpl(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, label, searchRegions);
	}

	public boolean isSelected() {
		return getFirstOccurrence().getAttribute("checked") != null;
	}

	@Override
	protected String getXPath() {
		return "//input[@type='radio']";
	}

	@Override
	protected SearchDirection getPrimarySearchDirection() {
		return SearchDirection.TO_LEFT_OF;
	}

	@Override
	protected SearchDirection getSecondarySearchDirection() {
		return SearchDirection.TO_RIGHT_OF;
	}

}
