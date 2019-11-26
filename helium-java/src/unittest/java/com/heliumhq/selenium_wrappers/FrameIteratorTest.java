package com.heliumhq.selenium_wrappers;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;


public class FrameIteratorTest {

	@Test
	public void testOnlyMainFrame() {
		WebDriverWrapper driver = new WebDriverWrapper(new StubWebDriver());
		assertEquals(asList(new int[0]), list(new FrameIterator(driver)));
	}

	@Test
	public void testOneFrame() {
		WebDriverWrapper driver =
			new WebDriverWrapper(new StubWebDriver(new Frame()));
		assertEquals(
			asList(new int[0], new int[] {0}), list(new FrameIterator(driver))
		);
	}

	@Test
	public void testTwoFrames() {
		WebDriverWrapper driver =
			new WebDriverWrapper(new StubWebDriver(new Frame(), new Frame()));
		assertEquals(
			asList(new int[0], new int[] {0}, new int[] {1}),
			list(new FrameIterator(driver))
		);
	}

	@Test
	public void testNestedFrame() {
		WebDriverWrapper driver =
			new WebDriverWrapper(new StubWebDriver(new Frame(new Frame())));
		assertEquals(
			asList(new int[0], new int[] {0}, new int[] {0, 0}),
			list(new FrameIterator(driver))
		);
	}

	@Test
	public void testComplex() {
		WebDriverWrapper driver = new WebDriverWrapper(
			new StubWebDriver(new Frame(new Frame()), new Frame())
		);
		assertEquals(
			asList(new int[0], new int[] {0}, new int[] {0, 0}, new int[] {1}),
			list(new FrameIterator(driver))
		);
	}

	@Test(expected=FramesChangedWhileIterating.class)
	public void testDisappearingFrame() {
		Frame childFrame = new Frame();
		Frame firstFrame = new Frame(childFrame);
		StubWebDriver driver = new StubWebDriver(firstFrame);
		// We allow precisely 2 frame switches: One to firstFrame and one to
		// childFrame. After this, FrameIterator tries to switch back to
		// firstFrame, to see whether it has other children besides childFrame.
		// This is where we raise a NoSuchFrameException (by limiting the num.
		// of frame switches to 2). This simulates a situation where firstFrame
		// disappears during iteration.
		driver.switchTo =
			new TargetLocatorFailingAfterNFrameSwitches(driver, 2);
		list(new FrameIterator(new WebDriverWrapper(driver)));
	}

	private class StubWebDriver implements WebDriver {
		private final Frame[] frames;
		private StubTargetLocator switchTo;
		private Frame currentFrame;
		private StubWebDriver(Frame... frames) {
			this.frames = frames;
			switchTo = new StubTargetLocator(this);
			currentFrame = null;
		}
		@Override
		public TargetLocator switchTo() {
			return switchTo;
		}
		@Override
		public void get(String url) {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getCurrentUrl() {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getTitle() {
			throw new UnsupportedOperationException();
		}
		@Override
		public List<WebElement> findElements(By by) {
			throw new UnsupportedOperationException();
		}
		@Override
		public WebElement findElement(By by) {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getPageSource() {
			throw new UnsupportedOperationException();
		}
		@Override
		public void close() {
			throw new UnsupportedOperationException();
		}
		@Override
		public void quit() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Set<String> getWindowHandles() {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getWindowHandle() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Navigation navigate() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Options manage() {
			throw new UnsupportedOperationException();
		}
	}

	private class StubTargetLocator implements WebDriver.TargetLocator {
		private final StubWebDriver driver;
		protected StubTargetLocator(StubWebDriver driver) {
			this.driver = driver;
		}
		@Override
		public WebDriver defaultContent() {
			driver.currentFrame = null;
			return driver;
		}
		@Override
		public WebDriver frame(int index) {
			Frame[] children;
			if (driver.currentFrame == null)
				children = driver.frames;
			else
				children = driver.currentFrame.children;
			Frame newFrame;
			try {
				newFrame = children[index];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new NoSuchFrameException("Frame doesn't exist.");
			}
			driver.currentFrame = newFrame;
			return driver;
		}

		@Override
		public WebDriver frame(String nameOrId) {
			throw new UnsupportedOperationException();
		}
		@Override
		public WebDriver frame(WebElement frameElement) {
			throw new UnsupportedOperationException();
		}
		@Override
		public WebDriver parentFrame() {
			throw new UnsupportedOperationException();
		}
		@Override
		public WebDriver window(String nameOrHandle) {
			throw new UnsupportedOperationException();
		}
		@Override
		public WebElement activeElement() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Alert alert() {
			throw new UnsupportedOperationException();
		}
	}

	private class Frame {
		private final Frame[] children;
		private Frame(Frame... children) {
			this.children = children;
		}
	}

	private class TargetLocatorFailingAfterNFrameSwitches extends
		StubTargetLocator {
		private int numAllowedFrameSwitches;
		private TargetLocatorFailingAfterNFrameSwitches(
			StubWebDriver driver, int numAllowedFrameSwitches
		) {
			super(driver);
			this.numAllowedFrameSwitches = numAllowedFrameSwitches;
		}

		@Override
		public WebDriver frame(int index) {
			if (numAllowedFrameSwitches > 0) {
				numAllowedFrameSwitches--;
				return super.frame(index);
			}
			throw new NoSuchFrameException("Frame no longer available.");
		}
	}

	private <T> List<T> list(Iterable<T> iterable) {
		List<T> result = new ArrayList<T>();
		for (T item : iterable)
			result.add(item);
		return result;
	}

	private void assertEquals(List<int[]> expected, List<int[]> actual) {
		Assert.assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertArrayEquals(expected.get(i), actual.get(i));
	}

}
