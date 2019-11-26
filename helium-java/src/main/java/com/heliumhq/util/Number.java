package com.heliumhq.util;

public class Number {

	public static String int2base(int x, String base) {
		if (x == 0)
			return String.valueOf(base.charAt(0));
		StringBuilder digits = new StringBuilder();
		while (x != 0) {
			digits.append(base.charAt((int) (x % base.length())));
			x /= base.length();
		}
		return digits.reverse().toString();
	}

	public static int base2int(String x, String base) {
		int result = 0;
		int exponent = 0;
		for (int i = x.length() - 1; i >= 0; i--) {
			char digit = x.charAt(i);
			int digitVal = base.indexOf(digit);
			result += digitVal * Math.pow(base.length(), exponent);
			exponent++;
		}
		return result;
	}

}
