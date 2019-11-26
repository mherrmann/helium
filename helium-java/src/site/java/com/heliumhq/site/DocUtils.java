package com.heliumhq.site;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import java.util.ArrayList;
import java.util.List;

import static com.heliumhq.util.StringUtils.join;

public class DocUtils {
	public static String getShortSignature(MethodDoc impl) {
		String result = impl.name() + "(";
		List<String> paramNames = new ArrayList<String>();
		for (Parameter parameter : impl.parameters())
			paramNames.add(parameter.name());
		result += join(", ", paramNames);
		if (impl.isVarArgs())
			result += "...";
		result += ")";
		return result;
	}
	public static String getSimpleName(String qualifiedClassName) {
		String[] nameComponents = qualifiedClassName.split("\\.");
		return nameComponents[nameComponents.length - 1];
	}
}
