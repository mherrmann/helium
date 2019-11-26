package com.heliumhq.util;

public class Tuple<F, S> {

	private final F first;
	private final S second;

	public Tuple(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

}
