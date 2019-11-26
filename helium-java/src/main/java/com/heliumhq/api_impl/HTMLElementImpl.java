package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.FrameIterator;
import com.heliumhq.selenium_wrappers.FramesChangedWhileIterating;
import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.Generator;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.google.common.collect.ObjectArrays.concat;
import static com.heliumhq.API.Point;
import static com.heliumhq.util.HtmlUtils.getEasilyReadableSnippet;

public abstract class HTMLElementImpl extends
		GUIElementImpl<WebElementWrapper> {

	protected final SearchRegion[] searchRegions;
	protected final MatchType matches;

	HTMLElementImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		super(driver);
		this.searchRegions = searchRegions;
		matches = MatchType.PREFIX_IGNORE_CASE;
	}

	public int getWidth() {
		return getFirstOccurrence().getLocation().getWidth();
	}

	public int getHeight() {
		return getFirstOccurrence().getLocation().getHeight();
	}

	public int getX() {
		return getFirstOccurrence().getLocation().getLeft();
	}

	public int getY() {
		return getFirstOccurrence().getLocation().getTop();
	}

	public Point getTopLeft() {
		return Point(getX(), getY());
	}

	public WebElement getWebElement() {
		return getFirstOccurrence().unwrap();
	}

	@Override
	protected Object[] getConstructorArgs() {
		Object[] normalConstrArgs = getConstructorArgsBeforeSearchRegions();
		return concat(normalConstrArgs, searchRegions, Object.class);
	}

	protected abstract Object[] getConstructorArgsBeforeSearchRegions();

	@Override
	public String toString(String className) {
		if (isBound()) {
			String elementHtml = getWebElement().getAttribute("outerHTML");
			return getEasilyReadableSnippet(elementHtml);
		} else
			return super.toString(className);
	}

	@Override
	protected Iterable<WebElementWrapper> findAllOccurrences() {
		return new Generator<WebElementWrapper>() {

			private boolean firstCall = true;
			private Iterator<int[]> frameIndices;
			private int[] frameIndex;
			List<SearchRegion.Occurrence> searchRegions;
			Iterator<WebElementWrapper> occurrences;

			@Override
			protected WebElementWrapper generateNext() {
				if (firstCall) {
					firstCall = false;
					handleClosedWindow();
					driver.switchTo().defaultContent();
				}
				if (frameIndices == null)
					frameIndices = new FrameIterator(driver).iterator();
				while (true) {
					if (frameIndex == null)
						try {
							frameIndex = frameIndices.next();
						} catch (FramesChangedWhileIterating e) {
							// Abort this search:
							throw new NoSuchElementException();
						}
					if (searchRegions == null)
						searchRegions = getSearchRegionsInCurrFrame();
					if (occurrences == null)
						occurrences = findAllInCurrFrame().iterator();
					while (occurrences.hasNext()) {
						WebElementWrapper occ = occurrences.next();
						if (shouldYield(occ, searchRegions)) {
							occ.setFrameIndex(frameIndex);
							return occ;
						}
					}
					frameIndex = null;
					searchRegions = null;
					occurrences = null;
				}
			}

			private void handleClosedWindow() {
				boolean windowHasBeenClosed;
				Set<String> windowHandles = driver.getWindowHandles();
				try {
					windowHasBeenClosed =
						! windowHandles.contains(driver.getWindowHandle());
				} catch (NoSuchWindowException e) {
					windowHasBeenClosed = true;
				}
				if (windowHasBeenClosed)
					driver.switchTo().window(windowHandles.iterator().next());
			}

		};
	}

	private List<SearchRegion.Occurrence> getSearchRegionsInCurrFrame() {
		List<SearchRegion.Occurrence> result =
				new ArrayList<SearchRegion.Occurrence>();
		for (SearchRegion searchRegion : searchRegions)
			result.addAll(searchRegion.getOccurrencesInCurrFrame());
		return result;
	}

	private boolean shouldYield(
		WebElementWrapper occurrence,
		List<SearchRegion.Occurrence> searchRegions
	) {
		return occurrence.isDisplayed() &&
				isInAnySearchRegion(occurrence, searchRegions);
	}

	private boolean isInAnySearchRegion(
		WebElementWrapper element, List<SearchRegion.Occurrence> searchRegions
	) {
		// group by SearchDirection
		Map<SearchDirection, List<SearchRegion.Occurrence>> directionToRegion
				= new HashMap<SearchDirection, List<SearchRegion.Occurrence>>();
		for (SearchRegion.Occurrence searchRegion : searchRegions) {
			SearchDirection direction = searchRegion.getSearchDirection();
			if (!directionToRegion.containsKey(direction)) {
				directionToRegion.put(
					direction,
					new ArrayList<SearchRegion.Occurrence>()
				);
			}
			directionToRegion.get(direction).add(searchRegion);
		}

		for (SearchDirection direction : SearchDirection.values()) {
			if (directionToRegion.containsKey(direction)) {
				boolean found = false;
				for (SearchRegion.Occurrence searchRegion :
						directionToRegion.get(direction)) {
					if (searchRegion.contains(element)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}

		return true;
	}

	protected abstract Iterable<WebElementWrapper> findAllInCurrFrame();

	/**
	 * Useful for subclasses.
	 */
	protected boolean isEnabled() {
		return getFirstOccurrence().getAttribute("disabled") == null;
	}

}
