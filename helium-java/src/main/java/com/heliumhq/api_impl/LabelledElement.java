package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.selenium_wrappers.WebElementWrapper;
import com.heliumhq.util.Tuple;
import com.heliumhq.util.geom.Rectangle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

import static com.heliumhq.util.Collections.inverse;
import static com.heliumhq.util.XPath.predicate;
import static com.heliumhq.util.StringUtils.isEmpty;

abstract class LabelledElement extends HTMLElementImpl {

	private final static double SECONDARY_SEARCH_DIMENSION_PENALTY_FACTOR = 1.5;

	private final String label;

	LabelledElement(WebDriverWrapper driver, SearchRegion... searchRegions) {
		this(driver, null, searchRegions);
	}

	LabelledElement(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.label = label;
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {label};
	}

	@Override
	protected List<WebElementWrapper> findAllInCurrFrame() {
		List<WebElementWrapper> result;
		if (isEmpty(label))
			result = findElts();
		else {
			List<WebElementWrapper> labels =
					new TextImpl(driver, label, false).findAllInCurrFrame();
			if (! labels.isEmpty())
				result = filterEltsBelongingToLabels(findElts(), labels);
			else
				result = findEltsByFreeText();
		}
		Collections.sort(result, new Comparator<WebElementWrapper>() {
			@Override
			public int compare(WebElementWrapper e1, WebElementWrapper e2) {
				double s1 = driver.getDistanceToLastManipulated(e1);
				double s2 = driver.getDistanceToLastManipulated(e2);
				return (int) Math.round(s1 - s2);
			}
		});
		return result;
	}

	private List<WebElementWrapper> findElts() {
		return findElts(null);
	}

	private List<WebElementWrapper> findElts(String xpath) {
		if (xpath == null)
			xpath = getXPath();
		List<WebElementWrapper> result = new ArrayList<WebElementWrapper>();
		for (WebElement element : driver.findElements(By.xpath(xpath)))
			result.add(new WebElementWrapper(driver, element));
		return result;
	}

	private List<WebElementWrapper> findEltsByFreeText() {
		List<String> eltTypes = new ArrayList<String>();
		for (String xpath : getXPath().split(("\\|")))
			eltTypes.add(xpath.trim().replaceAll("^/+", ""));
		String labels = "//text()" + predicate(matches.xpath(".", label));
		StringBuilder xpath = new StringBuilder();
		boolean isFirst = true;
		for (String eltType : eltTypes) {
			if (! isFirst)
				xpath.append(" | ");
			xpath.append(labels);
			String axis;
			if (eltType.contains("checkbox") || eltType.contains("radio")) {
				axis = "preceding-sibling";
			} else {
				axis = "following";
			}
			xpath.append(String.format("/%s::", axis));
			xpath.append(eltType);
			xpath.append("[1]");
			isFirst = false;
		}
		return findElts(xpath.toString());
	}

	protected abstract String getXPath();

	protected SearchDirection getPrimarySearchDirection() {
		return SearchDirection.TO_RIGHT_OF;
	}

	protected SearchDirection getSecondarySearchDirection() {
		return SearchDirection.BELOW;
	}

	private List<WebElementWrapper> filterEltsBelongingToLabels(
			List<WebElementWrapper> allElts, List<WebElementWrapper> labels
	) {
		List<WebElementWrapper> result = new ArrayList<WebElementWrapper>();
		for (Tuple<WebElementWrapper, WebElementWrapper> tpl :
				getLabelsWithExplicitElts(allElts, labels)) {
			WebElementWrapper label = tpl.getFirst();
			WebElementWrapper elt = tpl.getSecond();
			result.add(elt);
			labels.remove(label);
			allElts.remove(elt);
		}
		Map<WebElementWrapper, Set<WebElementWrapper>> labelsToElts =
				getRelatedElts(allElts, labels);
		labelsToElts = ensureAtMostOneLabelPerElt(labelsToElts);
		retainClosest(labelsToElts);
		for (Set<WebElementWrapper> eltsForLabel : labelsToElts.values()) {
			assert eltsForLabel.size() <= 1;
			if (eltsForLabel.size() > 0)
				result.add(eltsForLabel.iterator().next());
		}
		return result;
	}

	private List<Tuple<WebElementWrapper, WebElementWrapper>>
	getLabelsWithExplicitElts(
			List<WebElementWrapper> allElts, List<WebElementWrapper> labels
	) {
		List<Tuple<WebElementWrapper, WebElementWrapper>> result =
				new ArrayList<Tuple<WebElementWrapper, WebElementWrapper>>();
		for (WebElementWrapper label : labels)
			if ("label".equals(label.getTagName())) {
				String labelTarget = label.getAttribute("for");
				if (!isEmpty(labelTarget))
					for (WebElementWrapper elt : allElts) {
						String eltId = elt.getAttribute("id");
						if (labelTarget.equalsIgnoreCase(eltId))
							result.add(
									new Tuple<
										WebElementWrapper, WebElementWrapper
									>(label, elt)
							);
					}
			}
		return result;
	}

	private Map<WebElementWrapper, Set<WebElementWrapper>>
	getRelatedElts(
			List<WebElementWrapper> allElts, List<WebElementWrapper> labels
	) {
		Map<WebElementWrapper, Set<WebElementWrapper>> result =
				new HashMap<WebElementWrapper, Set<WebElementWrapper>>();
		for (WebElementWrapper label : labels)
			for (WebElementWrapper elt : allElts)
				if (areRelated(elt, label)) {
					if (!result.containsKey(label))
						result.put(label, new HashSet<WebElementWrapper>());
					result.get(label).add(elt);
				}
		return result;
	}

	private boolean areRelated(
			WebElementWrapper elt, WebElementWrapper label
	) {
		Rectangle eltLoc = elt.getLocation();
		Rectangle labelLoc = label.getLocation();
		if (eltLoc.intersects(labelLoc))
			return true;
		SearchDirection primSearchDir = getPrimarySearchDirection();
		SearchDirection secSearchDir = getSecondarySearchDirection();
		return labelLoc.getDistanceTo(eltLoc) <= 150 && (
				primSearchDir.isInDirection(eltLoc, labelLoc) ||
				secSearchDir.isInDirection(eltLoc, labelLoc)
		);
	}

	private Map<WebElementWrapper, Set<WebElementWrapper>>
	ensureAtMostOneLabelPerElt(
			Map<WebElementWrapper, Set<WebElementWrapper>> labelsToElts
	) {
		Map<WebElementWrapper, Set<WebElementWrapper>> eltsToLabels =
				inverse(labelsToElts);
		retainClosest(eltsToLabels);
		return inverse(eltsToLabels);
	}

	private void retainClosest(
			Map<WebElementWrapper, Set<WebElementWrapper>> pivotsToElts
	) {
		for (WebElementWrapper pivot : pivotsToElts.keySet()) {
			Set<WebElementWrapper> elts = pivotsToElts.get(pivot);
			if (! elts.isEmpty())
				pivotsToElts.put(
						pivot,
						new HashSet<WebElementWrapper>(
								Arrays.asList(findClosest(pivot, elts))
						)
				);
		}
	}

	private WebElementWrapper findClosest(
			WebElementWrapper toPivot, Set<WebElementWrapper> amongElts
	) {
		Iterator<WebElementWrapper> remainingElts = amongElts.iterator();
		WebElementWrapper result = remainingElts.next();
		double resultDistance = computeDistance(result, toPivot);
		while (remainingElts.hasNext()) {
			WebElementWrapper element = remainingElts.next();
			double elementDistance = computeDistance(element, toPivot);
			if (elementDistance < resultDistance) {
				result = element;
				resultDistance = elementDistance;
			}
		}
		return result;
	}

	private double computeDistance(
			WebElementWrapper elt1, WebElementWrapper elt2
	) {
		Rectangle loc1 = elt1.getLocation();
		Rectangle loc2 = elt2.getLocation();
		double factor;
		if (getSecondarySearchDirection().isInDirection(loc1, loc2))
			factor = SECONDARY_SEARCH_DIMENSION_PENALTY_FACTOR;
		else
			factor = 1;
		return factor * loc1.getDistanceTo(loc2);
	}

}
