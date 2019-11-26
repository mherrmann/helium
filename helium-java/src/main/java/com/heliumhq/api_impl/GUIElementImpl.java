package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebDriverWrapper;
import com.heliumhq.util.Generator;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.heliumhq.API.Config;
import static com.heliumhq.util.StringUtils.escape;
import static com.heliumhq.util.StringUtils.join;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

public abstract class GUIElementImpl<T> implements Cloneable {

	protected final WebDriverWrapper driver;
	private T boundOccurrence;

	protected GUIElementImpl(WebDriverWrapper driver) {
		this.driver = driver;
	}

	public boolean exists() {
		return findAll().iterator().hasNext();
	}

	public Iterable<GUIElementImpl<T>> findAll() {
		if (isBound())
			return asList(this);
		final Iterator<T> allOccurrences = findAllOccurrences().iterator();
		return new Generator<GUIElementImpl<T>>() {
			@Override
			protected GUIElementImpl<T> generateNext() {
				return boundToOccurrence(allOccurrences.next());
			}
		};
	}

	@Override
	public String toString() {
		return toString(getClass().getSimpleName());
	}

	public String toString(String className) {
		return String.format(
			"%s(%s)", className, reprConstuctorArgs()
		);
	}

	protected String reprConstuctorArgs() {
		Object[] constructorArgs = getConstructorArgs();
		List<String> constructorArgsReprs = new ArrayList<String>();
		for (Object arg : constructorArgs) {
			if (arg instanceof String)
				constructorArgsReprs.add(escape((String) arg));
			else if (arg != null)
				constructorArgsReprs.add(arg.toString());
		}
		return join(", ", constructorArgsReprs);
	}

	protected abstract Object[] getConstructorArgs();

	protected boolean isBound() {
		return boundOccurrence != null;
	}

	protected abstract Iterable<T> findAllOccurrences();

	GUIElementImpl<T> boundToOccurrence(T occurrence) {
		GUIElementImpl<T> result;
		try {
			result = (GUIElementImpl<T>) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		result.boundOccurrence = occurrence;
		return result;
	}

	protected T getFirstOccurrence() {
		if (! isBound())
			bindToFirstOccurrence();
		return boundOccurrence;
	}

	protected void bindToFirstOccurrence() {
		perform(new NoOpAction());
		// performNoWait(...) below now sets boundOccurrence.
	}

	T perform(Action<T> action) {
		long endTime = currentTimeMillis() +
				(long) (Config.getImplicitWaitSecs() * 1000);
		// Try to perform `action` at least once:
		T result = performNoWait(action);
		while (result == null && currentTimeMillis() < endTime)
			result = performNoWait(action);
		if (result != null)
			return result;
		throw new NoSuchElementException("Cannot find element " + this + ".");
	}

	private T performNoWait(Action<T> action) {
		for (GUIElementImpl<T> boundGUIElementImpl : findAll()) {
			T occurrence = boundGUIElementImpl.getFirstOccurrence();
			try {
				action.performOn(occurrence);
			} catch (RuntimeException e) {
				if (shouldIgnoreException(e))
					continue;
				else
					throw e;
			}
			boundOccurrence = occurrence;
			return occurrence;
		}
		return null;
	}

	private boolean shouldIgnoreException(RuntimeException e) {
		if (e instanceof ElementNotVisibleException)
			return true;
		if (e instanceof MoveTargetOutOfBoundsException)
			return true;
		if (e instanceof WebDriverException) {
			String msg = e.getMessage();
			if (msg != null && msg.contains("Element is not clickable at point")
				&& msg.contains("Other element would receive the click")
			)
				return true;
		}
		return false;
	}

	private class NoOpAction extends Action<T> {
		@Override
		public void performOn(T element) {}
	}

}
