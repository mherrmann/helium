package com.heliumhq.site;

import com.heliumhq.util.Tuple;
import com.sun.javadoc.*;

import java.util.*;

import static com.heliumhq.util.StringUtils.isEmpty;

public class HeliumAPIMethodDoc implements HeliumAPIDocElement {
	private final String name;
	private final List<MethodDoc> impls;
	private final ClassDoc classDoc;
	private final boolean isFunction, isPredicate, isMethod;
	public HeliumAPIMethodDoc(String name, ClassDoc classDoc) {
		this(name, classDoc, classDoc == null, classDoc != null, false);
	}
	public HeliumAPIMethodDoc(MethodDoc methodDoc) {
		this(methodDoc.name(), null, false, false, true);
		addImpl(methodDoc);
	}
	private HeliumAPIMethodDoc(
		String name, ClassDoc classDoc, boolean isFunction, boolean isPredicate,
		boolean isMethod
	) {
		this.name = name;
		this.classDoc = classDoc;
		this.impls = new ArrayList<MethodDoc>();
		this.isFunction = isFunction;
		this.isPredicate = isPredicate;
		this.isMethod = isMethod;
	}
	public void addImpl(MethodDoc methodDoc) {
		assert methodDoc.name().equals(name);
		impls.add(methodDoc);
	}
	public void writeTo(HeliumAPIDocWriter writer) {
		writeHeaderTo(writer);
		writer.writeMemberPostfix();
		writeDescriptionTo(writer);
		writeParamDescriptionsTo(writer);
		writer.writeReturnTag(getReturnTag());
		writeThrowsDescriptionsTo(writer);
		writeMembersTo(writer);
		writer.writePostamble();
	}

	private void writeHeaderTo(HeliumAPIDocWriter writer) {
		MethodDoc firstImpl = impls.iterator().next();
		if (isFunction)
			writer.writeFunctionHeaderPrefix(firstImpl);
		else if (isPredicate)
			writer.writePredicateHeaderPrefix(firstImpl);
		else {
			assert isMethod;
			writer.writeMethodHeaderPrefix(firstImpl);
		}
		writer.writeFunctionSignature(name, getParamCombinations());
	}

	private List<ParamCombination> getParamCombinations() {
		List<ParamCombination> result = new ArrayList<ParamCombination>();
		List<Param> params = getParams();
		for (MethodDoc impl : impls) {
			ParamCombination current = new ParamCombination();
			for (Parameter parameter : impl.parameters()) {
				Param param = null;
				for (Param maybeParameter : params)
					if (maybeParameter.getName().equals(parameter.name())) {
						param = maybeParameter;
						break;
					}
				current.add(param);
			}
			Iterator<ParamCombination> resultIter = result.iterator();
			boolean shouldBeAdded = true;
			while (resultIter.hasNext()) {
				ParamCombination existing = resultIter.next();
				if (existing.join(current)) {
					shouldBeAdded = false;
					break;
				}
				if (current.join(existing))
					resultIter.remove();
			}
			if (shouldBeAdded)
				result.add(current);
		}
		return result;
	}

	private void writeDescriptionTo(HeliumAPIDocWriter writer) {
		writer.writeFunctionDescriptionPrefix();
		for (MethodDoc impl : impls)
			writer.write(impl.inlineTags());
		writer.writeFunctionDescriptionPostfix();
	}

	private void writeParamDescriptionsTo(HeliumAPIDocWriter writer) {
		List<Param> params = getParams();
		if (! params.isEmpty())
			writer.writeParamDescriptionsPrefix();
		for (Param param : params) {
			ParamTag paramTag = getParamTag(param.getName());
			writer.writeParamDescription(
				paramTag, param.isOptional(), param.getTypes()
			);
		}
		if (! params.isEmpty())
			writer.writeParamDescriptionsPostfix();
	}

	private List<Param> getParams() {
		Map<String, Tuple<Set<String>, Boolean>> pTypes =
				new LinkedHashMap<String, Tuple<Set<String>, Boolean>>();
		for (MethodDoc impl : impls) {
			for (int i = 0; i < impl.parameters().length; i++) {
				Parameter param = impl.parameters()[i];
				String paramName = param.name();
				String paramType = param.type().qualifiedTypeName();
				boolean isLastParam = i == impl.parameters().length - 1;
				boolean isVarArgs = impl.isVarArgs() && isLastParam;
				if (! pTypes.containsKey(paramName))
					pTypes.put(paramName, new Tuple<Set<String>, Boolean>(
						new LinkedHashSet<String>(), isVarArgs
					));
				pTypes.get(paramName).getFirst().add(paramType);
			}
		}
		List<Param> result = new ArrayList<Param>();
		for (Map.Entry<String, Tuple<Set<String>, Boolean>> e :
				pTypes.entrySet()) {
			String paramName = e.getKey();
			Set<String> paramTypes = e.getValue().getFirst();
			boolean isOptional = isOptional(paramName);
			boolean isVarArgs = e.getValue().getSecond();
			result.add(new Param(paramName, paramTypes, isOptional, isVarArgs));
		}
		return result;
	}
	private boolean isOptional(String parameter) {
		MethodDoc implWithParam = getImplWithParameter(parameter);
		List<String> otherParameters = new ArrayList<String>();
		for (Parameter param : implWithParam.parameters())
			if (! param.name().equals(parameter))
				otherParameters.add(param.name());
		return getImplWithParameters(otherParameters) != null;
	}
	private MethodDoc getImplWithParameter(String parameter) {
		List<MethodDoc> implsWithParameter = getImplsWithParameter(parameter);
		if (implsWithParameter.isEmpty())
			return null;
		return implsWithParameter.get(0);
	}
	private List<MethodDoc> getImplsWithParameter(String parameter) {
		List<MethodDoc> result = new ArrayList<MethodDoc>();
		for (MethodDoc impl : impls)
			for (Parameter param : impl.parameters())
				if (param.name().equals(parameter))
					result.add(impl);
		return result;
	}
	private MethodDoc getImplWithParameters(List<String> parameters) {
		for (MethodDoc impl : impls) {
			if (impl.parameters().length != parameters.size())
				continue;
			boolean found = true;
			for (int i = 0; i < parameters.size(); i++)
				if (! impl.parameters()[i].name().equals(parameters.get(i))) {
					found = false;
					break;
				}
			if (found)
				return impl;
		}
		return null;
	}
	private ParamTag getParamTag(String parameterName) {
		for (MethodDoc implWithParam : getImplsWithParameter(parameterName))
			for (ParamTag paramTag : implWithParam.paramTags())
				if (paramTag.parameterName().equals(parameterName))
					return paramTag;
		return null;
	}
	private Tag getReturnTag() {
		for (MethodDoc impl : impls) {
			Tag[] returnTags = impl.tags("return");
			if (returnTags.length > 0)
				return returnTags[0];
		}
		return null;
	}
	private void writeThrowsDescriptionsTo(HeliumAPIDocWriter writer) {
		List<ThrowsTag> throwsTags = getThrowsTags();
		if (! throwsTags.isEmpty()) {
			writer.writeThrowsTagsPrefix();
			for (ThrowsTag throwsTag : throwsTags)
				writer.writeThrowsTag(throwsTag);
			writer.writeThrowsTagsPostfix();
		}
	}
	private List<ThrowsTag> getThrowsTags() {
		List<ThrowsTag> result = new ArrayList<ThrowsTag>();
		for (MethodDoc impl : impls) {
			Tag[] throwsTags = impl.tags("throws");
			if (throwsTags.length > 0)
				for (Tag tag : throwsTags) {
					result.add((ThrowsTag) tag);
				}
		}
		return result;
	}
	private void writeMembersTo(HeliumAPIDocWriter writer) {
		if (classDoc != null)
			writer.writeMembersPrefix(classDoc);
		List<HeliumAPIDocElement> members = getMembersSortedByName();
		for (HeliumAPIDocElement member : members)
			member.writeTo(writer);
	}
	private List<HeliumAPIDocElement> getMembersSortedByName() {
		// We're using a Map to sort by name, but it can happen that there are
		// two members with the same name (eg. Text(...).exists and
		// Text(...).exists()). We thus append a unique id to the keys used for
		// the map:
		int uniqueId = 0;
		Map<String, HeliumAPIDocElement> result =
			new TreeMap<String, HeliumAPIDocElement>();
		ClassDoc superclassDoc = classDoc;
		while (superclassDoc != null) {
			for (MethodDoc methodDoc : superclassDoc.methods()) {
				String commentText = methodDoc.commentText();
				if (isEmpty(commentText))
					continue;
				String uniqueName = methodDoc.name() + uniqueId;
				uniqueId ++;
				result.put(uniqueName, new HeliumAPIMethodDoc(methodDoc));
			}
			for (FieldDoc fieldDoc : superclassDoc.fields()) {
				String commentText = fieldDoc.commentText();
				if (isEmpty(commentText))
					continue;
				String uniqueName = fieldDoc.name() + uniqueId;
				uniqueId ++;
				result.put(uniqueName, new HeliumAPIFieldDoc(fieldDoc));
			}
			superclassDoc = superclassDoc.superclass();
		}
		return new ArrayList<HeliumAPIDocElement>(result.values());
	}
}