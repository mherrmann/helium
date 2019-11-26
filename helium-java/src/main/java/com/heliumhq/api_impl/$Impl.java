package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class $Impl extends HTMLElementImpl {

	private final String selector;

	$Impl(
		WebDriverWrapper driver, String selector, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.selector = selector;
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {selector};
	}

	@Override
	protected List<WebElementWrapper> findAllInCurrFrame() {
		if (selector.startsWith("@"))
			return wrap(driver.findElements(By.name(selector.substring(1))));
		if (selector.startsWith("//"))
			return wrap(driver.findElements(By.xpath(selector)));
		return wrap(driver.findElements(By.cssSelector(selector)));
	}

	private List<WebElementWrapper> wrap(List<WebElement> elements) {
		List<WebElementWrapper> result = new ArrayList<WebElementWrapper>();
		for (WebElement element : elements)
			result.add(new WebElementWrapper(driver, element));
		return result;
	}

}
