package com.heliumhq.selenium_wrappers;

import com.heliumhq.util.Generator;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriverException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FrameIterator implements Iterable<int[]> {

	private final WebDriverWrapper driver;
	private final int[] startFrame;

	public FrameIterator(WebDriverWrapper driver) {
		this(driver, new int[0]);
	}

	public FrameIterator(WebDriverWrapper driver, int[] startFrame) {
		this.driver = driver;
		this.startFrame = startFrame;
	}

	@Override
	public Iterator<int[]> iterator() {
		return (new Generator<int[]>() {

			boolean firstCall = true;
			int newFrame = 0;
			boolean newFrameChanged = true;
			Iterator<int[]> results;

			protected int[] generateNext() {
				if (firstCall) {
					firstCall = false;
					return new int[0];
				}
				while (true) {
					if (newFrameChanged) {
						newFrameChanged = false;
						try {
							driver.switchTo().frame(newFrame);
						} catch (WebDriverException e) {
							throw new NoSuchElementException();
						}
						int[] newStartFrame =
							concat(startFrame, new int[]{newFrame});
						results =
							new FrameIterator(driver, newStartFrame).iterator();
					}
					if (results.hasNext())
						return concat(new int[]{newFrame}, results.next());
					try {
						switchToFrame(startFrame);
					} catch (NoSuchFrameException e) {
						throw new FramesChangedWhileIterating();
					}
					newFrame++;
					newFrameChanged = true;
				}
			}

		}).iterator();
	}

	public void switchToFrame(int[] frameIndexPath) {
		driver.switchTo().defaultContent();
		for (int frameIndex : frameIndexPath)
			driver.switchTo().frame(frameIndex);
	}

	int[] concat(int[] a, int[] b) {
		int aLen = a.length;
		int bLen = b.length;
		int[] result = new int[aLen + bLen];
		System.arraycopy(a, 0, result, 0, aLen);
		System.arraycopy(b, 0, result, aLen, bLen);
		return result;
	}

}
