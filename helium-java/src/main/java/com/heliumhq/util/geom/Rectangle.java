package com.heliumhq.util.geom;

public class Rectangle {

	private int left, top, width, height;

	public Rectangle(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return left + width;
	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return top + height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getDistanceTo(Rectangle other) {
		Rectangle leftmost = left < other.left ? this : other;
		Rectangle rightmost = leftmost == other ? this : other;
		int distanceX = Math.max(0, rightmost.left - leftmost.getRight());
		Rectangle topmost = top < other.top ? this : other;
		Rectangle bottommost = topmost == other ? this : other;
		int distanceY = Math.max(0, bottommost.top - topmost.getBottom());
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
	}

	public boolean intersects(Rectangle other) {
		int left = Math.max(this.left, other.left);
		int top = Math.max(this.top, other.top);
		int right = Math.min(getRight(), other.getRight());
		int bottom = Math.min(getBottom(), other.getBottom());
		return left < right && top < bottom;
	}

	public boolean isAbove(Rectangle other) {
		boolean startsAboveOther = top < other.top;
		boolean overlapsOtherLeft =
				left <= other.left && other.left < getRight();
		boolean otherOverlapsLeft =
				other.left <= left && left < other.getRight();
		return startsAboveOther && (overlapsOtherLeft || otherOverlapsLeft);
	}

	public boolean isBelow(Rectangle other) {
		return other.isAbove(this);
	}

	public boolean isToRightOf(Rectangle other) {
		return other.isToLeftOf(this);
	}

	public boolean isToLeftOf(Rectangle other) {
		boolean thisStartsToLeftOfOther = left < other.left;
		boolean thisOverlapsOtherTop =
				top <= other.top && other.top < getBottom();
		boolean otherOverlapsThisTop =
				other.top <= top && top < other.getBottom();
		return thisStartsToLeftOfOther && (
				thisOverlapsOtherTop || otherOverlapsThisTop
		);
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (! (other instanceof Rectangle))
			return false;
		Rectangle otherRect = (Rectangle) other;
		return left == otherRect.left && top == otherRect.top &&
				width == otherRect.width && height == otherRect.height;
	}

	@Override
	public int hashCode() {
		return left + 7 * top + 11 * width + 13 * height;
	}
}
