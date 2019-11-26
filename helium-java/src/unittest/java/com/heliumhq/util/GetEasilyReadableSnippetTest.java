package com.heliumhq.util;

import org.junit.Test;

import static com.google.common.base.Strings.repeat;
import static com.heliumhq.util.HtmlUtils.getEasilyReadableSnippet;
import static org.junit.Assert.assertEquals;

public class GetEasilyReadableSnippetTest {

	@Test
	public void testNoTag() {
		assertEquals(
			"Hello World!", getEasilyReadableSnippet("Hello World!")
		);
	}

	@Test
	public void testCompletelyEmptyTag() {
		assertEquals("<>", getEasilyReadableSnippet("<>"));
	}

	@Test
	public void testEmptyTagWithAttributes() {
		String emptyTagWithAttrs =
			"<input type=\"checkbox\" id=\"checkBoxId\" name=\"checkBoxName\"" +
				" class=\"checkBoxClass\">";
		assertEquals(
			emptyTagWithAttrs,
			getEasilyReadableSnippet(emptyTagWithAttrs)
		);
	}

	@Test
	public void testTagWithNestedTags() {
		assertEquals(
			"<body>...</body>",
			getEasilyReadableSnippet("<body><p>Hello World!</p></body>")
		);
	}

	@Test
	public void testTagWithLongContent() {
		String tagWithLongContent = String.format(
			"<body>%s</body>", repeat("x", 100)
		);
		assertEquals(
			"<body>...</body>",
			getEasilyReadableSnippet(tagWithLongContent)
		);
	}

}