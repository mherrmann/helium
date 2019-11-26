package com.heliumhq.inttest_api;

import com.heliumhq.TemporaryImplicitWait;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class PromptIT extends AlertAT {

	@Override
	public String getLinkToOpenAlert() {
		return "Prompt for value";
	}

	@Override
	public String getExpectedAlertText() {
		return "Please enter a value";
	}

	@Override
	public String getExpectedAlertAcceptedResult() {
		return "Value entered: ";
	}

	@Test
	public void testWriteValue() throws InterruptedException {
		write("1");
		Alert().accept();
		expectResult("Value entered: 1");
	}

	@Test
	public void testWriteIntoLabelRaisesException() {
		expectUnhandledAlertException();
		write("3", into("Please enter a value"));
	}

	@Test
	public void testWriteIntoTextFieldRaisesException() {
		expectUnhandledAlertException();
		write("4", into(TextField("Please enter a value")));
	}

	@Test
	public void testWriteIntoNonExistentLabelRaisesException() {
		expectUnhandledAlertException();
		write("5", into("Please enter a value"));
	}

	@Test
	public void testWriteIntoAlert() throws InterruptedException {
		write("7", into(Alert()));
		Alert().accept();
		expectResult("Value entered: 7");
	}

	@Test
	public void testWriteIntoLabelledAlert() throws InterruptedException {
		write("8", into(Alert(getExpectedAlertText())));
		Alert().accept();
		expectResult("Value entered: 8");
	}

	@Test(expected = NoSuchElementException.class)
	public void testWriteIntoNonExistentAlert() {
		TemporaryImplicitWait temporaryWait = new TemporaryImplicitWait(1);
		try {
			write("8", into(Alert("Non-existent")));
		} finally {
			temporaryWait.end();
		}
	}

}
