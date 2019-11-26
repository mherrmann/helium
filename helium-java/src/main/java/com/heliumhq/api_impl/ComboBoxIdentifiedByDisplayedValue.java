package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ComboBoxIdentifiedByDisplayedValue
		extends HTMLElementContainingText {

	public ComboBoxIdentifiedByDisplayedValue(
		WebDriverWrapper driver, String text, SearchRegion... searchRegions
	) {
		super(driver, text, searchRegions);
	}

	@Override
	protected String getXPathNodeSelector() {
		return "option";
	}

	@Override
	protected String getXPath() {
		return super.getXPath() + "/ancestor::select[1]";
	}

	@Override
	protected List<WebElementWrapper> findAllInCurrFrame() {
		List<WebElementWrapper> allCbsWithAMatchingValue =
			super.findAllInCurrFrame();
		List<WebElementWrapper> result = new ArrayList<WebElementWrapper>();
		for (WebElementWrapper cb : allCbsWithAMatchingValue) {
			Select select = new Select(cb.unwrap());
			for (WebElement selectedOption : select.getAllSelectedOptions())
				if (matches.text(selectedOption.getText(), searchText)) {
					result.add(cb);
					break;
				}
		}
		return result;
	}
}