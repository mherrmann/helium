package com.heliumhq.inttest_api;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static com.heliumhq.API.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.
		presenceOfElementLocated;

public class WaitUntilIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_wait_until.html";
	}

	@Test
	public void testWaitUntilTextExists() {
		click("Click me!");
		long startTime = System.currentTimeMillis();
		waitUntil(Text("Success!").exists);
		long endTime = System.currentTimeMillis();
		assertThat(endTime - startTime, is(greaterThan(900L)));
	}

	@Test
	public void testWaitUntilPresenceOfElementLocated() {
		click("Click me!");
		long startTime = System.currentTimeMillis();
		waitUntil(presenceOfElementLocated(By.id("result")));
		long endTime = System.currentTimeMillis();
		assertThat(endTime - startTime, is(greaterThan(900L)));
	}

	@Test(expected = TimeoutException.class)
	public void testWaitUntilLambdaExpires() {
		ExpectedCondition<Boolean> FALSE = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				return false;
			}
		};
		waitUntil(FALSE, timeoutSecs(1));
	}

}