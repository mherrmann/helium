package com.heliumhq.util;

import org.junit.Test;

import static com.heliumhq.util.XPath.predicateOr;
import static org.junit.Assert.assertEquals;

public class XPathTest {
	@Test
	public void testPredicateOrNoArgs() {
		assertEquals("", predicateOr());
	}
	@Test
	public void testPredicateOrOneArg() {
		assertEquals("[a=b]", predicateOr("a=b"));
	}
	@Test
	public void testPredicateOrTwoArgs() {
		assertEquals("[a=b or c=d]", predicateOr("a=b", "c=d"));
	}
	@Test
	public void testPredicateOrOneEmptyArg() {
		assertEquals("", predicateOr(""));
	}
	@Test
	public void testPredicateOrEmptyArgAmongNormalArgs() {
		assertEquals("[a=b or c=d]", predicateOr("a=b", "", "c=d"));
	}
}
