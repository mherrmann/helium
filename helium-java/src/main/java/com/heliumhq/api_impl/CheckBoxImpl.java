package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

public class CheckBoxImpl extends LabelledElement {

	public CheckBoxImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
	}

	public CheckBoxImpl(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, label, searchRegions);
	}

	public boolean isEnabled() {
		return super.isEnabled();
	}

	public boolean isChecked() {
		return getFirstOccurrence().getAttribute("checked") != null;
	}

	@Override
	protected String getXPath() {
		return "//input[@type='checkbox']";
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
