package com.heliumhq.selenium_wrappers;

public class Wrapper<T> {

	protected final T target;

	protected Wrapper(T target) {
		this.target = target;
	}

	public T unwrap() {
		return target;
	}

}
