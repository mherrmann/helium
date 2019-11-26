package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import static com.heliumhq.util.XPath.predicate;

public class ImageImpl extends HTMLElementIdentifiedByXPath {

	private final String alt;

	public ImageImpl(WebDriverWrapper driver, SearchRegion... searchRegions) {
		this(driver, null, searchRegions);
	}

	public ImageImpl(
			WebDriverWrapper driver, String alt, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.alt = alt;
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {alt};
	}

	@Override
	protected String getXPath() {
		return "//img" + predicate(matches.xpath("@alt", alt));
	}

}