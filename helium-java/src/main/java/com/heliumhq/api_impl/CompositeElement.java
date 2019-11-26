package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.Generator;

import java.util.*;

import static java.util.Arrays.asList;

abstract class CompositeElement extends HTMLElementImpl {

	private GUIElementImpl<WebElementWrapper> firstElement;

	CompositeElement(WebDriverWrapper driver, SearchRegion... searchRegions) {
		super(driver, searchRegions);
	}

	protected GUIElementImpl<WebElementWrapper> getFirstElement() {
		if (firstElement == null) {
			bindToFirstOccurrence();
			// findAllInCurrFrame() below now sets firstElement.
		}
		return firstElement;
	}

	protected abstract HTMLElementImpl[] getElements();

	@Override
	protected Iterable<WebElementWrapper> findAllInCurrFrame() {
		return new Generator<WebElementWrapper>() {

			List<WebElementWrapper> alreadyYielded;
			Iterator<HTMLElementImpl> elements;
			HTMLElementImpl element;
			Iterator<WebElementWrapper> boundGuiEltImpls;

			@Override
			protected WebElementWrapper generateNext() {
				if (alreadyYielded == null)
					alreadyYielded = new ArrayList<WebElementWrapper>();
				if (elements == null)
					elements = asList(getElements()).iterator();
				while (true) {
					if (element == null)
						element = elements.next();
					if (boundGuiEltImpls == null)
						boundGuiEltImpls =
								element.findAllInCurrFrame().iterator();
					while (boundGuiEltImpls.hasNext()) {
						WebElementWrapper boundGuiEltImpl =
								boundGuiEltImpls.next();
						if (firstElement == null)
							firstElement = element;
						if (!alreadyYielded.contains(boundGuiEltImpl)) {
							alreadyYielded.add(boundGuiEltImpl);
							return boundGuiEltImpl;
						}
					}
					element = null;
					boundGuiEltImpls = null;
				}
			}

		};
	}

}