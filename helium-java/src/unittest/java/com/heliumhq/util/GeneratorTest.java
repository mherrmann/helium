package com.heliumhq.util;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.util.*;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneratorTest {

	@Test
	public void testEmpty() {
		assertIterableEquals(Collections.EMPTY_LIST, generate());
	}

	@Test
	public void testOneItem() {
		assertIterableEquals(asList(1), generate(1));
	}

	@Test
	public void testTwoItems() {
		assertIterableEquals(asList(5, 3), generate(5, 3));
	}

	@Test
	public void testInfinite() {
		Generator<Integer> infinite = new Generator<Integer>() {
			@Override
			protected Integer generateNext() {
				return 1;
			}
		};
		Iterator<Integer> iterator = infinite.iterator();
		for (int i=0; i < 1000; i++) {
			assertTrue(iterator.hasNext());
			assertEquals(1, (int) iterator.next());
		}
	}

	@Test
	public void testGenerateNextNotCalledAfterExhausted() {
		Generator<Integer> generator = new Generator<Integer>() {

			boolean firstCall = true;

			@Override
			protected Integer generateNext() {
				if (firstCall) {
					firstCall = false;
					throw new NoSuchElementException();
				}
				throw new AssertionFailedError();
			}

		};
		Iterator<Integer> iterator = generator.iterator();
		assertFalse(iterator.hasNext());
		// This is the crucial second call that tests that generateNext is not
		// called again:
		assertFalse(iterator.hasNext());
	}

	private <T> Generator<T> generate(T... items) {
		final Iterator<T> iterator = asList(items).iterator();
		return new Generator<T>() {
			@Override
			protected T generateNext() {
				return iterator.next();
			}
		};
	}

	private <E> void assertIterableEquals(
		Iterable<E> expected, Iterable<E> actual
	) {
		List<E> expectedList = new ArrayList<E>();
		for (E item : expected)
			expectedList.add(item);
		List<E> actualList = new ArrayList<E>();
		for (E item : actual)
			actualList.add(item);
		assertEquals(expectedList, actualList);
	}

}