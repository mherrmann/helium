package com.heliumhq.api_impl;

abstract class Action<T> {
	abstract void performOn(T element);
}
