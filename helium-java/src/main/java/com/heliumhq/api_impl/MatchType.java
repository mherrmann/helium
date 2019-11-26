package com.heliumhq.api_impl;

import static com.heliumhq.util.XPath.lower;
import static com.heliumhq.util.XPath.replaceNbsp;
import static com.heliumhq.util.StringUtils.isEmpty;
import static com.heliumhq.util.StringUtils.join;

enum MatchType {
	PREFIX_IGNORE_CASE() {
		@Override
		public String xpath(String value, String text) {
			if (isEmpty(text))
				return "";
			// Asterisks '*' are sometimes used to mark required fields. Eg.:
			// <label for="title"><span class="red-txt">*</span> Title:</label>
			// The starts-with filter below would be too strict to include such
			// matches. To get around this, we ignore asterisks unless the
			// searched text itself contains one.
			String stripAsterisks;
			if (text.contains("*")) {
				stripAsterisks = value;
			} else {
				stripAsterisks = String.format("translate(%s, '*', '')", value);
			}
			// if text contains apostrophes (single quotes) then they need to be
			// treated with care
			if (text.contains("'")) {
				text = String.format(
					"concat('%s')", join("',\"'\",'", text.split("'"))
				);
			} else {
				text = String.format("'%s'", text);
			}

			return String.format(
				"starts-with(normalize-space(%s), %s)",
				lower(replaceNbsp(stripAsterisks)), text.toLowerCase()
			);
		}
		@Override
		public boolean text(String value, String text) {
			if (isEmpty(text))
				return true;
			return value.toLowerCase().trim().startsWith(text.toLowerCase());
		}
	};
	public abstract String xpath(String value, String text);
	public abstract boolean text(String value, String text);
}
