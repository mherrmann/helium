package com.heliumhq.util;

import org.junit.Test;

import static com.heliumhq.util.Number.base2int;
import static org.junit.Assert.assertEquals;

public class Base2intTest {

	@Test
	public void testZero() {
		assertEquals(0, base2int("0", "01"));
	}

	@Test
	public void testOne() {
		assertEquals(1, base2int("1", "01"));
	}

	@Test
	public void testTwoDigits() {
		assertEquals(3, base2int("11", "01"));
	}

	@Test
	public void testHex() {
		assertEquals(255, base2int("FF", "0123456789ABCDEF"));
	}

}