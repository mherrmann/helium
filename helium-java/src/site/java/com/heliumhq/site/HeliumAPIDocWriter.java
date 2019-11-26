package com.heliumhq.site;

import com.sun.javadoc.*;

import java.util.List;
import java.util.Set;

public interface HeliumAPIDocWriter {
	public HeliumAPIDocWriter writePreamblePrefix();
	public HeliumAPIDocWriter writePreamblePostfix();
	public HeliumAPIDocWriter writeFunctionHeaderPrefix(MethodDoc methodDoc);
	public HeliumAPIDocWriter writePredicateHeaderPrefix(MethodDoc methodDoc);
	public HeliumAPIDocWriter writeMethodHeaderPrefix(MethodDoc methodDoc);
	public HeliumAPIDocWriter writeFieldHeaderPrefix(FieldDoc fieldDoc);
	public HeliumAPIDocWriter writeClassHeaderPrefix(ClassDoc classDoc);
	public HeliumAPIDocWriter writeFunctionSignature(
		String name, List<ParamCombination> paramCombinations
	);
	public HeliumAPIDocWriter writeMemberPostfix();
	public HeliumAPIDocWriter writeFunctionDescriptionPrefix();
	public HeliumAPIDocWriter writeFunctionDescriptionPostfix();
	public HeliumAPIDocWriter write(Tag[] inlineTags);
	public HeliumAPIDocWriter writeParamDescriptionsPrefix();
	public HeliumAPIDocWriter writeParamDescription(
		ParamTag paramTag, boolean isOptional, Set<String> paramTypes
	);
	public HeliumAPIDocWriter writeParamDescriptionsPostfix();
	public HeliumAPIDocWriter writeReturnTag(Tag returnTag);
	public HeliumAPIDocWriter writeThrowsTagsPrefix();
	public HeliumAPIDocWriter writeThrowsTag(ThrowsTag throwsTag);
	public HeliumAPIDocWriter writeThrowsTagsPostfix();
	public HeliumAPIDocWriter writeMembersPrefix(ClassDoc classDoc);
	public HeliumAPIDocWriter writePostamble();
}
