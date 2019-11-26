package com.heliumhq.util;

import static com.heliumhq.util.StringUtils.isEmpty;
import static java.lang.String.format;

public class XPath {
	public static String lower(String text) {
		String alphabet =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝ";
		return format(
			"translate(%s, '%s', '%s')", text, alphabet, alphabet.toLowerCase()
		);
	}
	public static String replaceNbsp(String text) {
		return replaceNbsp(text, " ");
	}
	public static String replaceNbsp(String text, String by) {
		return format("translate(%s, '\u00a0', '%s')", text, by);
	}
	public static String predicate(String condition) {
		if (isEmpty(condition))
			return "";
		else
			return format("[%s]", condition);
	}
	public static String predicateOr(String... conditions) {
		String resultCondition = "";
		for (String condition : conditions) {
			if (!isEmpty(condition)) {
				if (!isEmpty(resultCondition))
					resultCondition += " or ";
				resultCondition += condition;
			}
		}
		return predicate(resultCondition);
	}
}