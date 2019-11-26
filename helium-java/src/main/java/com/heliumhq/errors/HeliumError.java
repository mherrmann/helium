package com.heliumhq.errors;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HeliumError extends Error {

	public HeliumError(String message) {
		super(message);
	}

	public HeliumError(Throwable cause) {
		super(cause);
	}

	public String toString() {
		return "- " + getMessage();
	}

	public void printStackTrace() {
		truncateStackTrace();
		super.printStackTrace();
	}

	private void truncateStackTrace() {
		StackTraceElement[] stackTrace = getStackTrace();
		List<StackTraceElement> newStackTrace =
				new ArrayList<StackTraceElement>();
		for (int i = stackTrace.length - 1; i >= 0; i--) {
			newStackTrace.add(0, stackTrace[i]);
			if (stackTrace[i].getClassName().startsWith("com.heliumhq.API"))
				break;
		}
		setStackTrace(newStackTrace.toArray(
				new StackTraceElement[newStackTrace.size()]
		));
	}

	@Override
	public void printStackTrace(PrintStream s) {
		truncateStackTrace();
		super.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		truncateStackTrace();
		super.printStackTrace(s);
	}

}
