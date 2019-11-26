package com.heliumhq.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Generator<T> implements Iterable<T> {

	private boolean needToGenerate = true;
	private T next;
	private boolean exhausted = false;

	protected abstract T generateNext();

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				if (exhausted)
					return false;
				if (needToGenerate) {
					try {
						next = generateNext();
					} catch (NoSuchElementException e) {
						exhausted = true;
						return false;
					}
					needToGenerate = false;
				}
				return true;
			}

			@Override
			public T next() {
				if (!hasNext())
					throw new NoSuchElementException();
				// Calling next() 'consumes' the item:
				needToGenerate = true;
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}