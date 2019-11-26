package com.heliumhq.site;

import java.util.Iterator;

public class AdvancedEnumerationHelper <T> implements Iterable<T> {

	private final Iterator<T> source;
	private final String lastDelimiter;
	private final String delimiter;
	private int numTimesNextCalled;
	private boolean isLast;

	public AdvancedEnumerationHelper(
		Iterable<T> source, String delimiter, String lastDelimiter
	) {
		this.source = source.iterator();
		this.delimiter = delimiter;
		this.lastDelimiter = lastDelimiter;
		numTimesNextCalled = 0;
		isLast = false;
	}

	public String getDelimiter() {
		if (numTimesNextCalled < 2)
			return "";
		if (isLast)
			return lastDelimiter;
		return delimiter;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return source.hasNext();
			}

			@Override
			public T next() {
				T result = source.next();
				isLast = ! hasNext();
				numTimesNextCalled++;
				return result;
			}

			@Override
			public void remove() {
				source.remove();
			}
		};
	}

}
