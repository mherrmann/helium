package com.heliumhq.util;

import org.junit.Test;

import java.util.*;

import static com.heliumhq.util.Collections.inverse;
import static org.junit.Assert.assertEquals;

public class CollectionsTest {
	@Test
	public void testInverseEmpty() {
		Map<Integer, Set<String>> intToStr =
				new HashMap<Integer, Set<String>>();
		Map<String, Set<Integer>> strToInt =
				new HashMap<String, Set<Integer>>();
		assertEquals(strToInt, inverse(intToStr));
	}
	@Test
	public void testInverse() {
		Map<Integer, Set<String>> namesForInts =
				new HashMap<Integer, Set<String>>();
		Set<String> namesForZero = new HashSet<String>();
		namesForZero.add("zero");
		namesForZero.add("naught");
		Set<String> namesForOne = new HashSet<String>();
		namesForOne.add("one");
		namesForInts.put(0, namesForZero);
		namesForInts.put(1, namesForOne);

		Map<String, Set<Integer>> intsForNames =
				new HashMap<String, Set<Integer>>();
		intsForNames.put("zero", new HashSet<Integer>(Arrays.asList(0)));
		intsForNames.put("naught", new HashSet<Integer>(Arrays.asList(0)));
		intsForNames.put("one", new HashSet<Integer>(Arrays.asList(1)));
		assertEquals(intsForNames, inverse(namesForInts));
	}
}
