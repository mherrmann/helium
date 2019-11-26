package com.heliumhq.site;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class HeliumAPIClassDoc implements HeliumAPIDocElement {
	private final ClassDoc classDoc;

	public HeliumAPIClassDoc(ClassDoc classDoc) {
		this.classDoc = classDoc;
	}

	public void writeTo(HeliumAPIDocWriter writer) {
		writer.writeClassHeaderPrefix(classDoc);
		writer.writeMemberPostfix();
		writer.write(classDoc.inlineTags());
		for (MethodDoc methodDoc : classDoc.methods())
			new HeliumAPIMethodDoc(methodDoc).writeTo(writer);
		writer.writePostamble();
	}
}
