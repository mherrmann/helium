package com.heliumhq.util;

import java.util.GregorianCalendar;

public class Date {

	public static GregorianCalendar createDate() {
		return new GregorianCalendar();
	}

	public static GregorianCalendar createDate(int year, int month, int day) {
		return new GregorianCalendar(year, month - 1, day);
	}

	public static GregorianCalendar createDate(long date) {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(date);
		return result;
	}

}
