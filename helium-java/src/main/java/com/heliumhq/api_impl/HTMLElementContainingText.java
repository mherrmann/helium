package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import static com.heliumhq.util.XPath.predicate;

abstract class HTMLElementContainingText extends HTMLElementIdentifiedByXPath {

	protected final String searchText;

	public HTMLElementContainingText(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		this(driver, null, searchRegions);
	}

	public HTMLElementContainingText(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.searchText = text;
	}

	@Override
	protected String getXPath() {
		String xpathBase = "//" + getXPathNodeSelector() +
							predicate(matches.xpath(".", searchText));
		return String.format(
				"%s[not(self::script)][not(.%s)]", xpathBase, xpathBase
		);
	}

	protected String getXPathNodeSelector() {
		return "*";
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {searchText};
	}

}
