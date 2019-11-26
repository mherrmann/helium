package com.heliumhq.site;

public class EnumerationHelper {
	private final String delimiter;
	private boolean isFirst = true;
	public EnumerationHelper() {
		this(", ");
	}
	public EnumerationHelper(String delimiter) {
		this.delimiter = delimiter;
	}
	public String next() {
		if (isFirst) {
			isFirst = false;
			return "";
		}
		return delimiter;
	}
}