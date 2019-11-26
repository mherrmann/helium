// Copied from https://github.com/lyncode/jtwig/blob/d540f2160467146a7dde0bdf9ac
// 979687f78f194/jtwig-functions/src/main/java/com/lyncode/jtwig/functions/util/
// HtmlUtils.java
/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copyFile of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.heliumhq.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.*;

public class HtmlUtils {

	private static final String START_COMMENT = "<!--";
	private static final String END_COMMENT = "-->";

	public static String stripTags(String input, String allowedTags) {
		return removeUnknownTags(removeHtmlComments(input), allowedTags);
	}

	public static String stripTags(String input) {
		return removeUnknownTags(removeHtmlComments(input), "");
	}

	private static String removeUnknownTags(String input, String knownTags) {
		List<String> knownTagList = asList(
			knownTags.replaceAll("^<", "").replaceAll(">$", "").split("><")
		);
		return removeTags(input, knownTagList);
	}

	private static String removeTags(String input, List<String> knownTagList) {
		Pattern tag = compile("</?([^\\s>]*)\\s*[^>]*>", CASE_INSENSITIVE);
		Matcher matches = tag.matcher(input);
		while (matches.find()) {
			if (!knownTagList.contains(matches.group(1))) {
				input = input.replaceAll(quote(matches.group()), "");
			}
		}
		return input;
	}

	private static String removeTags(
		String input, String startTag, String endTag
	) {
		while (input.contains(startTag)) {
			int start = input.indexOf(startTag);
			int end =
				input.substring(start + startTag.length()).indexOf(endTag);
			if (end == -1) input = input.substring(0, start);
			else
				input =
					input.substring(0, start) +
					input.substring(start + startTag.length() +
					end + endTag.length());
		}
		return input;
	}

	private static String removeHtmlComments(String input) {
		return removeTags(input, START_COMMENT, END_COMMENT);
	}

	public static String getEasilyReadableSnippet(String html) {
		html = normalizeWhitespace(html);
		int innerStart = html.indexOf('>') + 1;
		int innerEnd = html.substring(innerStart).lastIndexOf('<');
		if (innerStart == -1 || innerEnd == -1)
			return html;
		// We computed innerEnd inside `html.substring(innerStart)`. To get the
		// index inside just `html`, we thus need to add the length of the
		// string which we skipped (`innerStart`):
		innerEnd += innerStart;
		String openingTag = html.substring(0, innerStart);
		String closingTag = html.substring(innerEnd);
		String inner = html.substring(innerStart, innerEnd);
		if (inner.contains("<") || inner.length() > 60)
			return String.format("%s...%s", openingTag, closingTag);
		else
			return html;
	}

	public static String normalizeWhitespace(String html) {
		String result = html.trim();
		// Remove multiple spaces:
		result = result.replaceAll("\\s+", " ");
		// Remove spaces after opening or before closing tags:
		result = result.replaceAll("> ", ">").replaceAll(" <", "<");
		return result;
	}

}