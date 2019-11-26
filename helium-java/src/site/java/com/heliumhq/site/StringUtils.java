package com.heliumhq.site;

import static com.heliumhq.util.StringUtils.*;

public class StringUtils {

	public static String stripFirstWhitespaceOfEachLine(String input) {
		return join("\n", input.split("\n ?"));
	}

}
