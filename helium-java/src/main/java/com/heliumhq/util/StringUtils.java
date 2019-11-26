package com.heliumhq.util;

import java.util.Arrays;

public class StringUtils {
	public static String join(String delimiter, Object[] elements) {
		return join(delimiter, Arrays.asList(elements));
	}
	public static String join(String delimiter, Iterable elements) {
		StringBuilder result = new StringBuilder();
		boolean isFirst = true;
		for (Object element : elements) {
			if (!isFirst)
				result.append(delimiter);
			result.append(element.toString());
			isFirst = false;
		}
		return result.toString();
	}
	public static boolean isEmpty(String s) {
		return s == null || s.equals("");
	}
	public static String escape(String s) {
		if (s == null)
			return "";
		return "\"" + s + "\"";
	}
}
