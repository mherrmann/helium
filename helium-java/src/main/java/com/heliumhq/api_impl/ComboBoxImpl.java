package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ComboBoxImpl extends CompositeElement {

	private final String label;

	public ComboBoxImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		this(driver, null, searchRegions);
	}

	public ComboBoxImpl(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.label = label;
	}

	@Override
	protected HTMLElementImpl[] getElements() {
		return new HTMLElementImpl[] {
			new ComboBoxIdentifiedByDisplayedValue(
				driver, label, searchRegions
			),
			new ComboBoxIdentifiedByLabel(driver, label, searchRegions)
		};
	}

	public boolean isEditable() {
		return ! "select".equals(getFirstOccurrence().getTagName());
	}
	
	public String getValue() {
		WebElement selectedOption = getSelectDriver().getFirstSelectedOption();
		if (selectedOption != null) {
			return selectedOption.getText();
		}
		return null;
	}

	public List<String> getOptions() {
		List<String> result = new ArrayList<String>();
		for (WebElement option : getSelectDriver().getOptions())
			result.add(option.getText());
		return result;
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {label};
	}

	private Select getSelectDriver() {
		return new Select(getWebElement());
	}

}