package com.heliumhq.site;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.heliumhq.util.StringUtils.isEmpty;

public class HeliumAPIDoc implements HeliumAPIDocElement {

	private final RootDoc rootDoc;
	private final ClassDoc apiclassDoc;
	private final Map<String, HeliumAPIMethodDoc> methods;

	public HeliumAPIDoc(RootDoc rootDoc, String apiclass) {
		this.rootDoc = rootDoc;
		apiclassDoc = rootDoc.classNamed(apiclass);
		methods = new LinkedHashMap<String, HeliumAPIMethodDoc>();
		fillMethods();
	}

	private void fillMethods() {
		for (MethodDoc method : apiclassDoc.methods()) {
			String name = method.name();
			ClassDoc classDoc = getClassNamed(name);
			if (! methods.containsKey(name))
				methods.put(name, new HeliumAPIMethodDoc(name, classDoc));
			methods.get(name).addImpl(method);
		}
	}

	public void writeTo(HeliumAPIDocWriter writer) {
		writer.writePreamblePrefix();
		writer.write(apiclassDoc.inlineTags());
		writer.writePreamblePostfix();
		for (HeliumAPIMethodDoc methodDoc : methods.values())
			methodDoc.writeTo(writer);
		for (ClassDoc innerClass : apiclassDoc.innerClasses())
			if (! isEmpty(innerClass.commentText()))
				new HeliumAPIClassDoc(innerClass).writeTo(writer);
	}

	private ClassDoc getClassNamed(String simpleName) {
		for (ClassDoc classDoc : rootDoc.classes())
			if (classDoc.simpleTypeName().equals(simpleName))
				return classDoc;
		return null;
	}

}
