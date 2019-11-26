package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class SearchRegion {

	private final SearchDirection searchDirection;
	private final HTMLElementImpl pivotElement;

	public SearchRegion(
			SearchDirection searchDirection, HTMLElementImpl element
	) {
		this.searchDirection = searchDirection;
		this.pivotElement = element;
	}

	List<Occurrence> getOccurrencesInCurrFrame() {
		List<Occurrence> result = new ArrayList<Occurrence>();
		for (WebElementWrapper occurrence : pivotElement.findAllInCurrFrame())
			result.add(
				new Occurrence(searchDirection, occurrence.getLocation())
			);
		return result;
	}

	class Occurrence {
		private final SearchDirection searchDirection;
		private final Rectangle location;
		private Occurrence(
				SearchDirection searchDirection, Rectangle location
		) {
			this.searchDirection = searchDirection;
			this.location = location;
		}
		boolean contains(WebElementWrapper element) {
			return searchDirection.isInDirection(
					element.getLocation(), location
			);
		}
		public SearchDirection getSearchDirection() {
			return this.searchDirection;
		}
	}

	public String toString() {
		String pivotElementRepr;
		if (pivotElement instanceof TextImpl)
			// Strip surrounding 'Text('...')':
			pivotElementRepr = pivotElement.reprConstuctorArgs();
		else
			pivotElementRepr = pivotElement.toString();
		return String.format(
			"%s(%s)", makeCamelCase(searchDirection.name()), pivotElementRepr
		);
	}

	// "ABOVE" -> "above", "TO_LEFT_OF" -> "toLeftOf", etc.
	private String makeCamelCase(String enumName) {
		String[] components = enumName.split("_");
		StringBuilder result = new StringBuilder(components[0].toLowerCase());
		for (int i = 1; i < components.length; i++) {
			String component = components[i];
			result.append(component.charAt(0));
			result.append(component.toLowerCase().substring(1));
		}
		return result.toString();
	}

}
