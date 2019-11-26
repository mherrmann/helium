package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;

import static com.heliumhq.util.XPath.lower;
import static com.heliumhq.util.XPath.predicate;

public class TextFieldImpl extends CompositeElement {

	private final String label;

	public TextFieldImpl(
			WebDriverWrapper driver, SearchRegion... searchRegions
	) {
		this(driver, null, searchRegions);
	}

	public TextFieldImpl(
			WebDriverWrapper driver, String label, SearchRegion... searchRegions
	) {
		super(driver, searchRegions);
		this.label = label;
	}

	@Override
	protected Object[] getConstructorArgsBeforeSearchRegions() {
		return new Object[] {label};
	}

	public String getValue() {
		return ((TextFieldType) getFirstElement()).getValue();
	}

	public boolean isEnabled() {
		return ((TextFieldType) getFirstElement()).isEnabled();
	}

	public boolean isEditable() {
		return ((TextFieldType) getFirstElement()).isEditable();
	}

	@Override
	protected HTMLElementImpl[] getElements() {
		return new HTMLElementImpl[] {
				new StandardTextFieldWithPlaceHolder(
						driver, label, searchRegions
				),
				new StandardTextFieldWithLabel(driver, label, searchRegions),
				new AriaTextFieldWithLabel(driver, label, searchRegions)
		};
	}

	private interface TextFieldType {
		String getValue();
		boolean isEnabled();
		boolean isEditable();
	}

	private class StandardTextFieldWithLabel extends LabelledElement
			implements TextFieldType {

		StandardTextFieldWithLabel(
				WebDriverWrapper driver, String label,
				SearchRegion... searchRegions
		) {
			super(driver, label, searchRegions);
		}

		@Override
		public String getValue() {
			String result = getFirstOccurrence().getAttribute("value");
			if (result == null)
				return "";
			return result;
		}

		@Override
		public boolean isEnabled() {
			return super.isEnabled();
		}

		@Override
		public boolean isEditable() {
			return getFirstOccurrence().getAttribute("readOnly") == null;
		}

		@Override
		protected String getXPath() {
			return String.format(
				"//input[%s='text' or %s='email' or %s='password'" +
				" or %s='number' or %s='tel' or string-length(@type)=0]",
				lower("@type"), lower("@type"), lower("@type"), lower("@type"),
				lower("@type")
			) +  " | //textarea | //*[@contenteditable='true']";
		}
	}

	private class AriaTextFieldWithLabel extends LabelledElement
			implements TextFieldType {

		AriaTextFieldWithLabel(
				WebDriverWrapper driver, String label,
				SearchRegion... searchRegions
		) {
			super(driver, label, searchRegions);
		}

		@Override
		public String getValue() {
			return getFirstOccurrence().getText();
		}

		@Override
		public boolean isEnabled() {
			return super.isEnabled();
		}

		@Override
		public boolean isEditable() {
			return getFirstOccurrence().getAttribute("readOnly") == null;
		}

		@Override
		protected String getXPath() {
			return "//*[@role='textbox']";
		}
	}

	private class StandardTextFieldWithPlaceHolder extends
			HTMLElementIdentifiedByXPath implements TextFieldType {

		private final String label;

		StandardTextFieldWithPlaceHolder(
				WebDriverWrapper driver, String label,
				SearchRegion... searchRegions
		) {
			super(driver, searchRegions);
			this.label = label;
		}

		@Override
		public String getValue() {
			String result = getFirstOccurrence().getAttribute("value");
			if (result == null)
				return "";
			return result;
		}

		@Override
		public boolean isEnabled() {
			return super.isEnabled();
		}

		@Override
		public boolean isEditable() {
			return getFirstOccurrence().getAttribute("readOnly") == null;
		}

		@Override
		protected Object[] getConstructorArgsBeforeSearchRegions() {
			return new Object[] {label};
		}

		@Override
		protected String getXPath() {
			return String.format(
					"(%s)%s",
					new StandardTextFieldWithLabel(driver, label).getXPath(),
					predicate(matches.xpath("@placeholder", label))
			);
		}
	}

}
