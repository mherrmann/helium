package com.heliumhq.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class System {

	// isWindows, isLinux, isOSX inspired by org.apache.commons.lang.SystemUtils
	public static boolean isWindows() {
		return osNameMatches("Windows");
	}
	public static boolean isLinux() {

		return osNameMatches("Linux") || osNameMatches("LINUX");
	}
	public static boolean isOSX() {
		return osNameMatches("Mac OS X") || osNameMatches("Darwin");
	}
	private static boolean osNameMatches(String expected) {
		String actual = java.lang.System.getProperty("os.name");
		return actual != null && actual.startsWith(expected);
	}

	/**
	 * Determines whether the current OS is 32- or 64 bit. Note that this may be
	 * different from the bitness of the Java runtime or the CPU. Eg. is32bit()
	 * returns true on a 32 bit Windows running on a 64 bit processor.
	 */
	public static boolean is32bit() {
		if (isWindows())
			return java.lang.System.getenv("PROGRAMFILES(X86)") == null;
		assert isLinux() || isOSX();
		String unameM = unameM();
		return "i386".equals(unameM) || "i686".equals(unameM);
	}

	private static String unameM() {
		try {
			Process uname = Runtime.getRuntime().exec("uname -m");
			uname.waitFor();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(uname.getInputStream())
			);
			return reader.readLine().trim();
		} catch (IOException e) {
			return null;
		} catch (InterruptedException e) {
			return null;
		}
	}

	public static boolean is64bit() {
		if (isWindows())
			return java.lang.System.getenv("PROGRAMFILES(X86)") != null;
		assert isLinux() || isOSX();
		String unameM = unameM();
		return "x86_64".equals(unameM) || "ia64".equals(unameM) ||
				"amd64".equals(unameM);
	}

}
