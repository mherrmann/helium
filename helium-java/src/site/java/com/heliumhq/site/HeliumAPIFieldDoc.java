package com.heliumhq.site;

import com.sun.javadoc.FieldDoc;

public class HeliumAPIFieldDoc implements HeliumAPIDocElement {
	private final FieldDoc fieldDoc;
	public HeliumAPIFieldDoc(FieldDoc fieldDoc) {
		this.fieldDoc = fieldDoc;
	}
	public void writeTo(HeliumAPIDocWriter writer) {
		writer.writeFieldHeaderPrefix(fieldDoc);
		writer.writeMemberPostfix();
		writer.write(fieldDoc.inlineTags());
		writer.writePostamble();
	}
}
