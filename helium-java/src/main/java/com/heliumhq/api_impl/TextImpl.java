package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import static com.heliumhq.util.StringUtils.isEmpty;

public class TextImpl extends HTMLElementContainingText {

	private final boolean includeFreeTexts;

	public TextImpl(WebDriverWrapper driver, SearchRegion... searchRegions) {
		super(driver, searchRegions);
		this.includeFreeTexts = true;
	}

	public TextImpl(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
		this.includeFreeTexts = true;
	}

	public TextImpl(
			WebDriverWrapper driver, String label, boolean includeFreeTexts
	) {
		super(driver, label);
		this.includeFreeTexts = includeFreeTexts;
	}

	public String getValue() {
		return getFirstOccurrence().getText();
	}

	@Override
	protected String getXPath() {
		ButtonImpl buttonImpl = new ButtonImpl(driver, searchText);
		LinkImpl linkImpl = new LinkImpl(driver, searchText);
		String result = getSearchTextXPath();
		result += " | " + buttonImpl.getInputButtonXPath();
		result += " | " + linkImpl.getXPath();
		if (! isEmpty(searchText) && includeFreeTexts)
			result += " | " + new FreeText(driver, searchText).getXPath();
		return result;
	}

	String getSearchTextXPath() {
		String result;
		if (! isEmpty(searchText))
			result = super.getXPath();
		else {
			String noDescendantWithSameText =
				"not(.//*[normalize-space(.)=normalize-space(self::*)])";
			result =
				String.format("//*[text() and %s]", noDescendantWithSameText);
		}
		return result + "[not(self::option)]"
				+ (this.includeFreeTexts ? "" : "[count(*) <= 1]");
	}

}