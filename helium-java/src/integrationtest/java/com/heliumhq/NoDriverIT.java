package com.heliumhq;

import com.heliumhq.api_impl.APIImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.heliumhq.API.*;

public class NoDriverIT {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testGoToRequiresDriver() {
		checkRequiresDriver();
		goTo("google.com");
	}

	@Test
	public void testWriteRequiresDriver() {
		checkRequiresDriver();
		write("foo");
	}

	@Test
	public void testPressRequiresDriver() {
		checkRequiresDriver();
		press(ENTER);
	}

	@Test
	public void testClickRequiresDriver() {
		checkRequiresDriver();
		click("Sign in");
	}

	@Test
	public void testDoubleclickRequiresDriver() {
		checkRequiresDriver();
		doubleclick("Sign in");
	}

	@Test
	public void testDragRequiresDriver() {
		checkRequiresDriver();
		drag("Drag me", to("Drop here"));
	}

	@Test
	public void testFindAllRequiresDriver() {
		checkRequiresDriver();
		findAll(Button());
	}

	@Test
	public void testScrollDownRequiresDriver() {
		checkRequiresDriver();
		scrollDown();
	}

	@Test
	public void testScrollUpRequiresDriver() {
		checkRequiresDriver();
		scrollUp();
	}

	@Test
	public void testScrollRightRequiresDriver() {
		checkRequiresDriver();
		scrollRight();
	}

	@Test
	public void testScrollLeftRequiresDriver() {
		checkRequiresDriver();
		scrollLeft();
	}

	@Test
	public void testHoverRequiresDriver() {
		checkRequiresDriver();
		hover("Hi there!");
	}

	@Test
	public void testRightclickRequiresDriver() {
		checkRequiresDriver();
		rightclick("Hi there!");
	}

	@Test
	public void testSelectRequiresDriver() {
		checkRequiresDriver();
		select("Language", "English");
	}

	@Test
	public void testDragFileRequiresDriver() {
		checkRequiresDriver();
		dragFile("C\\test.txt", to("Here"));
	}

	@Test
	public void testAttachFileRequiresDriver() {
		checkRequiresDriver();
		attachFile("C\\test.txt");
	}

	@Test
	public void testRefreshRequiresDriver() {
		checkRequiresDriver();
		refresh();
	}

	@Test
	public void testWaitUntilRequiresDriver() {
		checkRequiresDriver();
		waitUntil(null);
	}

	@Test
	public void testSwitchToRequiresDriver() {
		checkRequiresDriver();
		switchTo("Popup");
	}

	@Test
	public void testKillBrowserRequiresDriver() {
		checkRequiresDriver();
		switchTo("Popup");
	}

	@Test
	public void testHighlightRequiresDriver() {
		checkRequiresDriver();
		switchTo("Popup");
	}

	@Test
	public void test$RequiresDriver() {
		checkRequiresDriver();
		$("#home");
	}

	@Test
	public void testTextRequiresDriver() {
		checkRequiresDriver();
		Text("Home");
	}

	@Test
	public void testLinkRequiresDriver() {
		checkRequiresDriver();
		Link("Home");
	}

	@Test
	public void testListItemRequiresDriver() {
		checkRequiresDriver();
		ListItem("Home");
	}

	@Test
	public void testButtonRequiresDriver() {
		checkRequiresDriver();
		Button("Home");
	}

	@Test
	public void testImageRequiresDriver() {
		checkRequiresDriver();
		Image("Logo");
	}

	@Test
	public void testTextFieldRequiresDriver() {
		checkRequiresDriver();
		TextField("File name");
	}

	@Test
	public void testComboBoxRequiresDriver() {
		checkRequiresDriver();
		ComboBox("Language");
	}

	@Test
	public void testCheckBoxRequiresDriver() {
		checkRequiresDriver();
		CheckBox("True?");
	}

	@Test
	public void testRadioButtonRequiresDriver() {
		checkRequiresDriver();
		RadioButton("Option A");
	}

	@Test
	public void testWindowRequiresDriver() {
		checkRequiresDriver();
		Window("Main");
	}

	@Test
	public void testAlertRequiresDriver() {
		checkRequiresDriver();
		Alert();
	}

	private void checkRequiresDriver() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage(APIImpl.DRIVER_REQUIRED_MESSAGE);
	}

}