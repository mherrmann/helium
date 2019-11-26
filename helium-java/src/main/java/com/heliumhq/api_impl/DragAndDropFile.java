package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.Tuple;
import org.openqa.selenium.WebElement;

class DragAndDropFile {

	private final WebDriverWrapper driver;
	private final String filePath;
	private WebElement fileInputElement;
	private JavaScriptInterval dragoverEvent;

	DragAndDropFile(WebDriverWrapper driver, String filePath) {
		this.driver = driver;
		this.filePath = filePath;
	}

	void begin() {
		createFileInputElement();
		try {
			fileInputElement.sendKeys(filePath);
		} catch (RuntimeException e) {
			end();
			throw e;
		}
	}

	private void createFileInputElement() {
		// The input needs to be visible to Selenium to allow sending keys to it
		// in Firefox and IE.
		// According to http://stackoverflow.com/questions/6101461/
		// Selenium criteria whether an element is visible or not are the
		// following:
		//  - visibility != hidden
		//  - display != none (is also checked against every parent element)
		//  - opacity != 0
		//  - height and width are both > 0
		//  - for an input, the attribute type != hidden
		// So let's make sure its all good!
		fileInputElement = (WebElement) driver.executeScript(
			"var input = document.createElement('input');" +
			"input.type = 'file';" +
			"input.style.display = 'block';" +
			"input.style.opacity = '1';" +
			"input.style.visibility = 'visible';" +
			"input.style.height = '1px';" +
			"input.style.width = '1px';" +
			"if (document.body.childElementCount > 0) { " +
			"  document.body.insertBefore(input, document.body.childNodes[0]);"+
			"} else { " +
			"  document.body.appendChild(input);" +
			"}" +
			"return input;"
		);
	}

	void dragOverDocument() {
		// According to the HTML5 spec, we need to dispatch the dragenter event
		// once, and then the dragover event continuously, every 350+-200ms:
		// http://www.w3.org/html/wg/drafts/html/master/editing.html#current-
		// drag-operation
		// Especially IE implements this spec very tightly, and considers the
		// dragging to be over if no dragover event occurs for more than ~1sec.
		// We thus need to ensure that we keep dispatching the dragover event.

		// This line used to read `dispatchEvent(..., "document");`. However,
		// this doesn't work when adding a photo to a tweet on Twitter.
		// Dispatching the event to document.body fixes this, and also works for
		// Gmail:
		dispatchEvent("dragenter", "document.body");
		dragoverEvent = prepareContinuousEvent("dragover", "document", 300);
		dragoverEvent.start();
	}

	private void dispatchEvent(String eventName, String to) {
		Tuple<String, Object[]> scriptArgs =
				prepareDispatchEvent(eventName, to);
		driver.executeScript(scriptArgs.getFirst(), scriptArgs.getSecond());
	}
	private void dispatchEvent(String eventName, WebElementWrapper to) {
		Tuple<String, Object[]> scriptArgs =
				prepareDispatchEvent(eventName, to);
		driver.executeScript(scriptArgs.getFirst(), scriptArgs.getSecond());
	}

	private JavaScriptInterval prepareContinuousEvent(
			String eventName, String to, long intervalMsecs
	) {
		Tuple<String, Object[]> scriptArgs =
				prepareDispatchEvent(eventName, to);
		return new JavaScriptInterval(
				driver, scriptArgs.getFirst(), scriptArgs.getSecond(),
				intervalMsecs
		);
	}

	private Tuple<String, Object[]> prepareDispatchEvent(
			String eventName, String to
	) {
		String script = DISPATCH_EVENT_SCRIPT.replace("arguments[2]", to);
		return new Tuple<String, Object[]>(
				script, new Object[] {fileInputElement, eventName}
		);
	}

	private final String DISPATCH_EVENT_SCRIPT =
			"var files = arguments[0].files;" +
			"var items = [];" +
			"var types = [];" +
			"for (var i = 0; i < files.length; i++) {" +
			"   items[i] = {kind: 'file', type: files[i].type};" +
			"   types[i] = 'Files';" +
			"}" +
			"var event = document.createEvent('CustomEvent');" +
			"event.initCustomEvent(arguments[1], true, true, 0);" +
			"event.dataTransfer = {" +
			"	files: files," +
			"	items: items," +
			"	types: types" +
			"};" +
			"arguments[2].dispatchEvent(event);";

	private Tuple<String, Object[]> prepareDispatchEvent(
			String eventName, WebElementWrapper to
	) {
		return new Tuple<String, Object[]>(
				DISPATCH_EVENT_SCRIPT,
				new Object[] {fileInputElement, eventName, to.unwrap()}
		);
	}

	void dropOn(WebElementWrapper element) {
		dragoverEvent.stop();
		dispatchEvent("drop", element);
	}

	void end() {
		if (fileInputElement != null)
			driver.executeScript(
				"arguments[0].parentNode.removeChild(arguments[0]);",
				fileInputElement
			);
		fileInputElement = null;
	}

	private class JavaScriptInterval {

		private WebDriverWrapper driver;
		private String script;
		private Object[] args;
		private long intervalMsecs;
		private long intervalId;

		private JavaScriptInterval(
				WebDriverWrapper driver, String script, Object[] args,
				long intervalMsecs
		) {
			this.driver = driver;
			this.script = script;
			this.args = args;
			this.intervalMsecs = intervalMsecs;
		}

		private void start() {
			String setintervalScript = String.format(
					"var originalArguments = arguments;" +
					"return setInterval(function() {" +
					"	arguments = originalArguments;" +
					"	%s" +
					"}, %d);", script, intervalMsecs
			);
			intervalId = (Long) driver.executeScript(setintervalScript, args);
		}

		private void stop() {
			driver.executeScript("clearInterval(arguments[0]);", intervalId);
			intervalId = 0L;
		}

	}

}
