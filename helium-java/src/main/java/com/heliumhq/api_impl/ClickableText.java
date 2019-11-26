package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

class ClickableText extends CompositeElement {

	private final String searchText;

	ClickableText(WebDriverWrapper driver) {
		this(driver, null);
	}

	ClickableText(WebDriverWrapper driver, String searchText) {
		super(driver);
		this.searchText = searchText;
	}

	@Override
	protected HTMLElementImpl[] getElements() {
		return new HTMLElementImpl[] {
				new ButtonImpl(driver, searchText),
				new TextImpl(driver, searchText)
		};
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {searchText};
	}

}
