package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.heliumhq.util.XPath.predicate;

public class LinkImpl extends HTMLElementContainingText {

	public LinkImpl(WebDriverWrapper driver, SearchRegion... searchRegions) {
		super(driver, searchRegions);
	}

	public LinkImpl(
			WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
	}

	public String getHref() {
		return this.getWebElement().getAttribute("href");
	}

	@Override
	protected String getXPathNodeSelector() {
		return "a";
	}

	@Override
	protected String getXPath() {
		return super.getXPath() + " | " + "//a" +
				predicate(matches.xpath("@title", searchText)) + " | " +
				"//*[@role='link']" + predicate(matches.xpath(".", searchText));
	}
}
