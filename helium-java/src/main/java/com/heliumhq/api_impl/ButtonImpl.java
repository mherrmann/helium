package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import static com.heliumhq.util.XPath.predicate;
import static com.heliumhq.util.XPath.predicateOr;
import static com.heliumhq.util.StringUtils.isEmpty;

public class ButtonImpl extends HTMLElementContainingText {

	public ButtonImpl(WebDriverWrapper driver, SearchRegion... searchRegions) {
		super(driver, searchRegions);
	}

	public ButtonImpl(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
	}

	protected String getXPathNodeSelector() {
		return "button";
	}

	public boolean isEnabled() {
		String ariaDisabled =
				getFirstOccurrence().getAttribute("aria-disabled");
		return super.isEnabled() && (
				isEmpty(ariaDisabled) || ariaDisabled.equalsIgnoreCase("false")
		);
	}

	@Override
	protected String getXPath() {
		String hasAriaLabel = matches.xpath("@aria-label", searchText);
		String hasText = matches.xpath(".", searchText);
		String hasTextOrAriaLabel = predicateOr(hasAriaLabel, hasText);
		return super.getXPath() + " | " + getInputButtonXPath() + " | " +
				"//*[@role='button']" + hasTextOrAriaLabel + " | " +
				"//button" + predicate(hasAriaLabel);
	}

	String getInputButtonXPath() {
		String hasText;
		if (!isEmpty(searchText)) {
			String hasValue = matches.xpath("@value", searchText);
			String hasLabel = matches.xpath("@label", searchText);
			String hasAriaLabel = matches.xpath("@aria-label", searchText);
			String hasTitle = matches.xpath("@title", searchText);
			hasText = predicateOr(hasValue, hasLabel, hasAriaLabel, hasTitle);
		} else
			hasText = "";
		return "//input[@type='submit' or @type='button']" + hasText;
	}

}
