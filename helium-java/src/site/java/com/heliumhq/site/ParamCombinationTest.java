package com.heliumhq.site;

// Contents of this class uncommented because it is not possible (easily) to add
// JUnit to the class path of the site Java sources in Maven. We should really
// switch to another build tool...

//import org.junit.Test;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//
//import static java.util.Arrays.asList;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;

public class ParamCombinationTest {
//	@Test
//	public void testJoinSelf() {
//		ParamCombination paramCombination = new ParamCombination();
//		Assert.assertTrue(paramCombination.join(paramCombination));
//	}
//	@Test
//	public void testJoinEmpty() {
//		ParamCombination paramCombination = new ParamCombination();
//		Param param = new Param(
//			"element", new HashSet<String>(asList("String")), true, false
//		);
//		paramCombination.add(param);
//		ParamCombination empty = new ParamCombination();
//		Assert.assertTrue(paramCombination.join(empty));
//	}
//	@Test
//	public void testJoinSimpleSuffix() {
//		Param searchRegion = new Param(
//			"searchRegion", new HashSet<String>(asList("SearchRegion")), false,
//			true
//		);
//		Param text = new Param(
//			"text", new HashSet<String>(asList("String")), true, false
//		);
//		// Text([text], searchRegion...)
//		ParamCombination thisCombination = new ParamCombination();
//		thisCombination.add(text);
//		thisCombination.add(searchRegion);
//		// Text(searchRegion...)
//		ParamCombination otherCombination = new ParamCombination();
//		otherCombination.add(searchRegion);
//
//		Assert.assertTrue(thisCombination.join(otherCombination));
//	}
//	@Test
//	public void testJoinCommonSuffix() {
//		Param searchRegion = new Param(
//			"searchRegion", new HashSet<String>(asList("SearchRegion")), false,
//			true
//		);
//		Param text = new Param(
//			"text", new HashSet<String>(asList("String")), true, false
//		);
//		Param locator = new Param(
//			"locator", new HashSet<String>(asList("By")), true, false
//		);
//		// Text([text], searchRegion...)
//		ParamCombination thisCombination = new ParamCombination();
//		thisCombination.add(text);
//		thisCombination.add(searchRegion);
//		// Text([locator], searchRegion...)
//		ParamCombination otherCombination = new ParamCombination();
//		otherCombination.add(locator);
//		otherCombination.add(searchRegion);
//
//		Assert.assertTrue(thisCombination.join(otherCombination));
//		// Expect thisCombination to have become
//		// Text([text or locator], searchRegion...)
//		Iterator<List<Param>> iter = thisCombination.iterator();
//		Assert.assertEquals(asList(text, locator), iter.next());
//		Assert.assertEquals(asList(searchRegion), iter.next());
//	}
//	@Test
//	public void testJoinOptionalPrefix() {
//		Param text = new Param(
//			"text", new HashSet<String>(asList("String")), false, false
//		);
//		Param into = new Param(
//			"into", new HashSet<String>(asList("String")), true, false
//		);
//		// write(text [, into])
//		ParamCombination thisCombination = new ParamCombination();
//		thisCombination.add(text);
//		thisCombination.add(into);
//		// write(text)
//		ParamCombination otherCombination = new ParamCombination();
//		otherCombination.add(text);
//
//		Assert.assertTrue(thisCombination.join(otherCombination));
//	}
}
