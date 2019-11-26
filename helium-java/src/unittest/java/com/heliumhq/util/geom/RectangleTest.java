package com.heliumhq.util.geom;

import org.junit.Test;

import static org.junit.Assert.*;

public class RectangleTest {
	@Test
	public void testDistanceToOverlappingHorizontally() {
		Rectangle top = new Rectangle(989, 14, 47, 15);
		Rectangle bottom = new Rectangle(803, 194, 194, 41);
		assertEquals(165.0, top.getDistanceTo(bottom), 0.01);
	}
	@Test
	public void testDistanceToOverlappingVertically() {
		Rectangle left = new Rectangle(14, 989, 15, 47);
		Rectangle right = new Rectangle(194, 803, 41, 194);
		assertEquals(165.0, left.getDistanceTo(right), 0.01);
	}
	@Test
	public void testDistanceToNotOverlapping() {
		Rectangle topLeft = new Rectangle(0, 0, 1, 1);
		Rectangle bottomRight = new Rectangle(2, 2, 1, 1);
		assertEquals(Math.sqrt(2), topLeft.getDistanceTo(bottomRight), 0.01);
	}
	@Test
	public void testDistanceFullyOverlappingVertically() {
		Rectangle top = new Rectangle(989, 14, 1000, 15);
		Rectangle bottom = new Rectangle(803, 194, 194, 41);
		assertEquals(165.0, top.getDistanceTo(bottom), 0.01);
	}
	@Test
	public void testIntersectsSelf() {
		Rectangle rect = new Rectangle(1, 2, 3, 4);
		assertTrue(rect.intersects(rect));
	}
	@Test
	public void testIntersectsNonIntersecting() {
		Rectangle rect_1 = new Rectangle(1, 2, 3, 4);
		Rectangle rect_2 = new Rectangle(10, 20, 30, 40);
		assertFalse(rect_1.intersects(rect_2));
	}
	@Test
	public void testIntersectsContaining() {
		Rectangle rect_1 = new Rectangle(1, 2, 3, 4);
		Rectangle rect_2 = new Rectangle(2, 3, 1, 1);
		assertTrue(rect_2.intersects(rect_1));
	}
	@Test
	public void testIntersectsOverlapping() {
		Rectangle rect_1 = new Rectangle(0, 0, 3, 3);
		Rectangle rect_2 = new Rectangle(1, 2, 3, 4);
		assertTrue(rect_1.intersects(rect_2));
	}
	@Test
	public void testIsToLeftOf() {
		/**
		 *     0         10        20        30        40        50        60
		 *     01234567890123456789012345678901234567890123456789012345678901
		 *             ____                        ____
		 * 0          |_r0_|  ____                | r1 |
		 * 1    ___          | r2 |  ____   ____  |____|
		 * 2   |   |         |____| | r3 | |    |  ____
		 * 3   | r |                |____| | r4 | |_r5_|   ____
		 * 4   |___|                       |____|         | r6 |  ____
		 * 5	                                          |____| | r7 |  ____
		 * 6                                                     |____| |_r8_|
		 */
		Rectangle rectangle = new Rectangle(0, 2, 4, 3);
		Rectangle r0 = new Rectangle(7, 0, 5, 1);
		Rectangle r1 = new Rectangle(35, 0, 5, 2);
		Rectangle r2 = new Rectangle(14, 1, 5, 2);
		Rectangle r3 = new Rectangle(21, 2, 5, 2);
		Rectangle r4 = new Rectangle(28, 2, 5, 3);
		Rectangle r5 = new Rectangle(35, 3, 5, 1);
		Rectangle r6 = new Rectangle(42, 4, 5, 2);
		Rectangle r7 = new Rectangle(49, 5, 5, 2);
		Rectangle r8 = new Rectangle(56, 6, 5, 1);
		assertFalse(rectangle.isToLeftOf(r0));
		assertFalse(rectangle.isToLeftOf(r1));
		assertTrue(rectangle.isToLeftOf(r2));
		assertTrue(rectangle.isToLeftOf(r3));
		assertTrue(rectangle.isToLeftOf(r4));
		assertTrue(rectangle.isToLeftOf(r5));
		assertTrue(rectangle.isToLeftOf(r6));
		assertFalse(rectangle.isToLeftOf(r7));
		assertFalse(rectangle.isToLeftOf(r8));
	}
	@Test
	public void testIsToRightOf() {
		/**
		 *     0         10        20        30        40        50        60
		 *     01234567890123456789012345678901234567890123456789012345678901
		 *             ____                        ____
		 * 0          |_r0_|  ____                | r1 |
		 * 1    ___          | r2 |  ____   ____  |____|
		 * 2   |   |         |____| | r3 | |    |  ____
		 * 3   | r |                |____| | r4 | |_r5_|   ____
		 * 4   |___|                       |____|         | r6 |  ____
		 * 5	                                          |____| | r7 |  ____
		 * 6                                                     |____| |_r8_|
		 */
		Rectangle rectangle = new Rectangle(0, 2, 4, 3);
		Rectangle r0 = new Rectangle(7, 0, 5, 1);
		Rectangle r1 = new Rectangle(35, 0, 5, 2);
		Rectangle r2 = new Rectangle(14, 1, 5, 2);
		Rectangle r3 = new Rectangle(21, 2, 5, 2);
		Rectangle r4 = new Rectangle(28, 2, 5, 3);
		Rectangle r8 = new Rectangle(56, 6, 5, 1);
		assertFalse(r8.isToRightOf(r1));
		assertTrue(r4.isToRightOf(r3));
		assertTrue(r4.isToRightOf(r2));
		assertFalse(r4.isToRightOf(r0));
		assertTrue(r4.isToRightOf(rectangle));
	}
	@Test
	public void testIsAbove() {
		/**
		 *       0         10        20
		 *       0123456789012345678901
		 *               _____
		 *  0           |__r__|
		 *  1     ____   ____    ____
		 *  2    |_r0_| |_r3_|  |_r8_|
		 *  3       ____   __  ____
		 *  4      |_r1_| |r5||_r7_|
		 *  5          ____   ____
		 *  6         |_r2_| |_r6_|
		 *  7            _____
		 *  8           |_r4__|
		 */
		Rectangle rectangle = new Rectangle(7, 0, 6, 1);
		Rectangle r0 = new Rectangle(0, 2, 5, 1);
		Rectangle r1 = new Rectangle(2, 4, 5, 1);
		Rectangle r2 = new Rectangle(5, 6, 5, 1);
		Rectangle r3 = new Rectangle(7, 2, 5, 1);
		Rectangle r4 = new Rectangle(7, 8, 6, 1);
		Rectangle r5 = new Rectangle(9, 4, 3, 1);
		Rectangle r6 = new Rectangle(12, 6, 5, 1);
		Rectangle r7 = new Rectangle(13, 4, 5, 1);
		Rectangle r8 = new Rectangle(15, 2, 5, 1);
		assertFalse(rectangle.isAbove(r0));
		assertFalse(rectangle.isAbove(r1));
		assertTrue(rectangle.isAbove(r2));
		assertTrue(rectangle.isAbove(r3));
		assertTrue(rectangle.isAbove(r4));
		assertTrue(rectangle.isAbove(r5));
		assertTrue(rectangle.isAbove(r6));
		assertFalse(rectangle.isAbove(r7));
		assertFalse(rectangle.isAbove(r8));
	}
	@Test
	public void testEqualsNull() {
		Rectangle rectangle = new Rectangle(1, 2, 3, 4);
		assertFalse(rectangle.equals(null));
	}
	@Test
	public void testEqualsNonRectangle() {
		Rectangle rectangle = new Rectangle(1, 2, 3, 4);
		assertFalse(rectangle.equals("hi there"));
	}
	@Test
	public void testEqualsSelf() {
		Rectangle rectangle = new Rectangle(1, 2, 3, 4);
		assertTrue(rectangle.equals(rectangle));
	}
	@Test
	public void testEqualsEqual() {
		Rectangle r1 = new Rectangle(1, 2, 3, 4);
		Rectangle r2 = new Rectangle(1, 2, 3, 4);
		assertTrue(r1.equals(r2));
	}
	@Test
	public void testEqualsDifferent() {
		Rectangle r1 = new Rectangle(1, 2, 3, 4);
		Rectangle r2 = new Rectangle(4, 5, 6, 7);
		assertFalse(r1.equals(r2));
	}
	@Test
	public void testHashcodeDifferentForDifferentRectangles() {
		Rectangle r1 = new Rectangle(1, 2, 3, 4);
		Rectangle r2 = new Rectangle(6, 3, 3, 4);
		assertNotEquals(r1.hashCode(), r2.hashCode());
	}
	@Test
	public void testHashcodeSameForEqualRectangles() {
		Rectangle r1 = new Rectangle(1, 2, 3, 4);
		Rectangle r2 = new Rectangle(1, 2, 3, 4);
		assertEquals(r1.hashCode(), r2.hashCode());
	}
}
