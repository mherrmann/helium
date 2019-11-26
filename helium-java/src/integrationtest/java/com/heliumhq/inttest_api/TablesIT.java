package com.heliumhq.inttest_api;

import com.heliumhq.API;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.heliumhq.API.*;
import static org.junit.Assert.assertEquals;

public class TablesIT extends BrowserAT {

	@Override
	protected String getPage() {
		return "inttest_tables.html";
	}

	@Test
	public void test$BelowAbove() {
		List<API.$> secondTableCells = findAll(
			$("table > tbody > tr > td",
				below(Text("Table no. 2")),
				above(Text("Table no. 3"))
			)
		);
		assertEquals(9, secondTableCells.toArray().length);
		assertEquals(
			Arrays.asList(
				"T2R1C1", "T2R1C2", "T2R1C3",
				"T2R2C1", "T2R2C2", "T2R2C3",
				"T2R3C1", "T2R3C2", "T2R3C3"
			),
			convertToSortedStringArray(secondTableCells)
		);
	}

	@Test
	public void test$ReadTableColumn() {
		List<$> emailCells = findAll(
			$("table > tbody > tr > td", below("Email"))
		);
		assertEquals(3, emailCells.toArray().length);
		assertEquals(
			Arrays.asList(
				"email1@domain.com", "email2@domain.com", "email3@domain.com"
			),
			convertToSortedStringArray(emailCells)
		);
	}

	@Test
	public void testTextBelowToLeftOf() {
		assertEquals(
			"Abdul",
			Text(below("Name"), toLeftOf("email2@domain.com")).getValue()
		);
	}

	private List<String> convertToSortedStringArray(List<$> elements) {
		List<String> cellTexts = new ArrayList<String>();
		for ($ cell : elements)
			cellTexts.add(cell.getWebElement().getText());
		Collections.sort(cellTexts);
		return cellTexts;
	}

}
