package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.ArrayList;
import java.util.List;

abstract class HTMLElementIdentifiedByXPath extends HTMLElementImpl {

	HTMLElementIdentifiedByXPath(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
	}

	@Override
	protected List<WebElementWrapper> findAllInCurrFrame() {
		String xpath = getXPath();
		List<WebElementWrapper> result = new ArrayList<WebElementWrapper>();
		for (org.openqa.selenium.WebElement element :
				driver.findElements(By.xpath(xpath)))
			result.add(new WebElementWrapper(driver, element));
		return sortSearchResult(result);
	}

	private List<WebElementWrapper> sortSearchResult(
			List<WebElementWrapper> result
	) {
		List<ResultScore<WebElementWrapper>> resultScores =
				new ArrayList<ResultScore<WebElementWrapper>>();
		for (WebElementWrapper element : result) {
			try {
				double score = getSortIndex(element);
				resultScores.add(
						new ResultScore<WebElementWrapper>(element, score)
				);
			} catch (StaleElementReferenceException e) {
				// Simply ignore the element.
			}
		}
		return ResultScore.getSortedResults(resultScores);
	}

	protected abstract String getXPath();

	protected double getSortIndex(WebElementWrapper webElement) {
		return driver.getDistanceToLastManipulated(webElement) + 1;
	}
}
