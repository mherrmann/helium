package com.heliumhq.util;

import org.junit.Test;

import static com.heliumhq.util.Number.int2base;
import static junit.framework.Assert.assertEquals;

public class Int2baseTest {

	@Test
	public void testZero() {
		assertEquals("0", int2base(0, "01"));
	}

	@Test
	public void testOne() {
		assertEquals("1", int2base(1, "01"));
	}

	@Test
	public void testTwoDigits() {
		assertEquals("11", int2base(3, "01"));
	}

	@Test
	public void testHex() {
		assertEquals("FF", int2base(255, "0123456789ABCDEF"));
	}

	@Test
	public void testHexEightytwo() {
		assertEquals("52", int2base(82, "0123456789ABCDEF"));
	}

}