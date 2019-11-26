package com.heliumhq.site;

import com.sun.javadoc.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HeliumAPIDoclet {

	/**
	 * We need to implement this method to tell the Doclet API about our custom
	 * options.
	 */
	public static int optionLength(String option) {
		if ("-apiclass".equals(option) || "-d".equals(option) ||
				"-f".equals(option))
			return 2;
		return 0;
	}

	/**
	 * Another method of the Doclet API which we can implement to verify that
	 * any required options are set properly.
	 */
	public static boolean validOptions(
		String[][] options, DocErrorReporter errorReporter
	) {
		return requireOption("d", options, errorReporter) &&
			   requireOption("apiclass", options, errorReporter);
	}

	private static boolean requireOption(
		String option, String[][] options, DocErrorReporter errorReporter
	) {
		String[] optionValue = readOption(options, option);
		if (optionValue == null) {
			errorReporter.printError("Option '-" + option + "' is required.");
			return false;
		}
		if (optionValue.length != 2) {
			errorReporter.printError(
				"Option '-" + option + "' requires exactly one argument."
			);
			return false;
		}
		return true;
	}

	public static boolean start(RootDoc rootDoc) throws FileNotFoundException {
		String apiclass = readOption(rootDoc.options(), "apiclass")[1];
		HeliumAPIDoc apiDoc = new HeliumAPIDoc(rootDoc, apiclass);
		String outputDir = readOption(rootDoc.options(), "d")[1];
		String fileName = "index.html";
		try {
			fileName = readOption(rootDoc.options(), "f")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (NullPointerException e) {}
		PrintStream out = new PrintStream(new File(outputDir, fileName));
		apiDoc.writeTo(new HeliumAPIDocHTMLWriter(out));
		return true;
	}

	private static String[] readOption(String[][] options, String option) {
		for (String[] candidateOption : options)
			if (("-" + option).equals(candidateOption[0]))
				return candidateOption;
		return null;
	}

	/**
	 * This is required to make 'MethodDoc#isVarArgs()' work.
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

}
