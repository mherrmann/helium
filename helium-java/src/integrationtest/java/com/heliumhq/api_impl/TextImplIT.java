package com.heliumhq.api_impl;

import com.heliumhq.inttest_api.BrowserAT;
import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextImplIT extends BrowserAT {
	@Override
	protected String getPage() {
		return "inttest_text_impl.html";
	}
	@Test
	public void testEmptySearchTextXPath() {
		String xpath =
			new TextImpl(new WebDriverWrapper(driver)).getSearchTextXPath();
		List<WebElement> textElements = driver.findElements(By.xpath(xpath));
		List<String> texts = new ArrayList<String>();
		for (WebElement w : textElements)
			texts.add(w.getAttribute("innerHTML"));
		Collections.sort(texts);
		assertEquals(
			Arrays.asList("A paragraph", "A paragraph inside a div",
				"Another paragraph inside the div"),
			texts
		);
	}
}
