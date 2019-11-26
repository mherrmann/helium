package com.heliumhq.site;

import com.sun.javadoc.*;

import java.io.PrintStream;
import java.util.*;

import static com.heliumhq.site.DocUtils.getSimpleName;
import static com.heliumhq.util.StringUtils.join;
import static com.heliumhq.site.StringUtils.stripFirstWhitespaceOfEachLine;
import static com.heliumhq.util.StringUtils.isEmpty;

public class HeliumAPIDocHTMLWriter implements HeliumAPIDocWriter {

	private final static String PRETTY_PRINT_CLASS = "prettyprint lang-java";

	private final PrintStream out;

	public HeliumAPIDocHTMLWriter(PrintStream out) {
		this.out = out;
	}

	private HeliumAPIDocHTMLWriter write(String text) {
		out.print(text);
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writePreamblePrefix() {
		write("<span class=\"target\" id=\"module-helium.api\"></span><p>");
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writePreamblePostfix() {
		return write("</p>");
	}

	@Override
	public HeliumAPIDocHTMLWriter writeFunctionHeaderPrefix(
		MethodDoc methodDoc
	) {
		return writeMemberPrefix(methodDoc, "function");
	}

	private HeliumAPIDocHTMLWriter writeMemberPrefix(
		MemberDoc memberDoc, String dlClassName
	) {
		String memberId =
			memberDoc.containingClass().qualifiedName() +
			"." + memberDoc.name();
		write("<dl class=\"" + dlClassName + "\">\n");
		write("<dt id=\"" + memberId + "\">\n");
		write("<tt class=\"descname\">");
		return this;
	}

	@Override
	public HeliumAPIDocWriter writePredicateHeaderPrefix(MethodDoc methodDoc) {
		return writeMemberPrefix(methodDoc, "class");
	}

	@Override
	public HeliumAPIDocWriter writeMethodHeaderPrefix(MethodDoc methodDoc) {
		return writeMemberPrefix(methodDoc, "method");
	}

	@Override
	public HeliumAPIDocWriter writeFieldHeaderPrefix(FieldDoc fieldDoc) {
		return writeMemberPrefix(fieldDoc, "attribute").write(fieldDoc.name());
	}

	@Override
	public HeliumAPIDocWriter writeClassHeaderPrefix(ClassDoc classDoc) {
		write("<dl class=\"class\">\n");
		write("<dt id=\"" + classDoc.qualifiedName() + "\">\n");
		write("<em class=\"property\">class </em>");
		write("<tt class=\"descname\">");
		write(classDoc.simpleTypeName());
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeFunctionSignature(
		String name, List<ParamCombination> paramCombinations
	) {
		write(name);
		write("<big>(</big><em>");
		EnumerationHelper paramCombEnum =
			new EnumerationHelper("</em> or <em>");
		for (ParamCombination paramCombination : paramCombinations) {
			write(paramCombEnum.next());
			EnumerationHelper paramEnum = new EnumerationHelper();
			EnumerationHelper optionalEnum = new EnumerationHelper(" ");
			int optionalsDepth = 0;
			for (List<Param> paramAlternatives : paramCombination) {
				EnumerationHelper paramAltsEnum = new EnumerationHelper(" or ");
				Param firstAlternative = paramAlternatives.get(0);
				if (firstAlternative.isVarargs())
					for (; optionalsDepth > 0; optionalsDepth--)
						write("]");
				if (firstAlternative.isOptional()) {
					write(optionalEnum.next());
					write("[");
					optionalsDepth++;
				} else
					optionalEnum.next();
				write(paramEnum.next());
				for (Param paramAlternative : paramAlternatives) {
					write(paramAltsEnum.next());
					write(paramAlternative.getName());
					if (paramAlternative.isVarargs())
						write("...");
				}
			}
			for (; optionalsDepth > 0; optionalsDepth--)
				write("]");
		}
		write("</em><big>)</big>");
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeMemberPostfix() {
		return write("</tt></dt>\n<dd>");
	}

	@Override
	public HeliumAPIDocHTMLWriter writeFunctionDescriptionPrefix() {
		return write("<p>");
	}

	@Override
	public HeliumAPIDocHTMLWriter writeFunctionDescriptionPostfix() {
		return write("</p>");
	}

	@Override
	public HeliumAPIDocHTMLWriter write(Tag[] inlineTags) {
		for (Tag inlineTag : inlineTags) {
			if (inlineTag.name().equals("@link"))
				writeLink((SeeTag) inlineTag);
			else {
				String text = stripFirstWhitespaceOfEachLine(inlineTag.text());
				if (inlineTag.name().equals("@code"))
					writeCode(text);
				else
					write(text
						.replace("<p>", "</p>\n<p>")
						.replace(
							"<code>",
							"<code class='" + PRETTY_PRINT_CLASS + "'>"
						)
					);
				}
		}
		return this;
	}

	private HeliumAPIDocHTMLWriter writeCode(String code) {
		write("</p>\n");
		write("<pre class='" + PRETTY_PRINT_CLASS + "'>");
		write(code);
		write("</pre>\n");
		write("<p>");
		return this;
	}

	private HeliumAPIDocHTMLWriter writeLink(SeeTag linkTag) {
		return write(" ").writeLink(
			linkTag.referencedClassName(), linkTag.referencedMemberName()
		);
	}

	private HeliumAPIDocHTMLWriter writeLink(String className) {
		return writeLink(className, "");
	}

	private HeliumAPIDocHTMLWriter writeLink(String typeName, String member) {
		return write(getAPIDocLink(typeName, member));
	}

	private String getAPIDocLink(String typeName, String member) {
		String nameToDisplay = getNameToDisplay(typeName, member);
		if (typeName.startsWith("org.openqa.selenium")) {
			String url = getExternalLinkURL(
				typeName, member,
				"http://selenium.googlecode.com/svn/trunk/docs/api/java/"
			);
			return "<a href=\"" + url + "\">" + nameToDisplay + "</a>";
		} else if (
			typeName.startsWith("com.heliumhq") &&
			! typeName.startsWith("com.heliumhq.api_impl")
		) {
			String reference = getInternalLinkReference(typeName, member);
			return "<a class=\"reference internal\" href=\"#" + reference
					+ "\" title=\"" + reference + "\">" + nameToDisplay +
					"</a>";
		}
		return nameToDisplay;
	}

	private String getExternalLinkURL(
		String typeName, String member, String linkBase
	) {
		String result =
			linkBase + stripGenerics(typeName).replace('.', '/') + ".html";
		if (! isEmpty(member))
			result += "#" + member;
		return result;
	}

	private String getInternalLinkReference(String typeName, String member) {
		if (isEmpty(member)) {
			// We want to convert a link to com.heliumhq.Point to
			// com.heliumhq.API.Point, but not com.heliumhq.Point.withOffset
			// to com.heliumhq.API.Point.withOffset. That's why we only
			// perform a change if 'member' is empty (see if-stmt above).
			List<String> pathParts = new ArrayList<String>(
					Arrays.asList(typeName.split("\\."))
			);
			if (! pathParts.contains("API"))
				pathParts.add(pathParts.size() - 1, "API");
			typeName = join(".", pathParts);
		}
		String result = typeName;
		if (! isEmpty(member))
			result += "." + stripParentheses(member);
		return result;
	}

	private String getNameToDisplay(String className, String referencedMember) {
		String classNameWithGenerics;
		if (className.startsWith("java.lang."))
			classNameWithGenerics = className.substring("java.lang.".length());
		else if (className.equals("com.heliumhq.API"))
			classNameWithGenerics = "";
		else
			classNameWithGenerics = className;
		String result = getSimpleName(stripGenerics(classNameWithGenerics));
		if (! isEmpty(referencedMember)) {
			if (! isEmpty(classNameWithGenerics))
				result += "#";
			result += stripParentheses(referencedMember) + "(...)";
		}
		return result;
	}

	private String stripGenerics(String typeName) {
		if (typeName.contains("<"))
			return typeName.substring(0, typeName.indexOf('<')).trim();
		return typeName;
	}

	private String stripParentheses(String memberName) {
		if (memberName.contains("("))
			return memberName.substring(0, memberName.indexOf('(')).trim();
		return memberName;
	}


	@Override
	public HeliumAPIDocHTMLWriter writeParamDescriptionsPrefix() {
		return writeParamDescriptionsPrefix("Parameters:");
	}

	public HeliumAPIDocHTMLWriter writeParamDescriptionsPrefix(String title) {
		write(
			"<table class=\"docutils field-list\" frame=\"void\" " +
			"rules=\"none\">\n"
		);
		write("<col class=\"field-name\" />\n");
		write("<col class=\"field-body\" />\n");
		write("<tbody valign=\"top\">\n");
		write(
			"<tr class=\"field-odd field\"><th class=\"field-name\">" + title +
			"</th><td class=\"field-body\"><ul class=\"first last simple\">"
		);
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeParamDescription(
		ParamTag paramTag, boolean isOptional, Set<String> paramTypes
	) {
		write("<li><strong>");
		write(paramTag.parameterName());
		write("</strong>");
		if (isOptional)
			write(" (optional)");
		write(" - <em>");
		AdvancedEnumerationHelper<String> enumHelper =
			new AdvancedEnumerationHelper<String>(paramTypes, ", ", " or ");
		for (String paramType : enumHelper) {
			write(enumHelper.getDelimiter());
			writeLink(paramType);
		}
		write("</em>");
		if (paramTag != null) {
			write(": ");
			write(paramTag.inlineTags());
		} else
			write(".");
		write("</li>");
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeParamDescriptionsPostfix() {
		write("\n</ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeReturnTag(Tag returnTag) {
		if (returnTag != null) {
			write("<strong>Returns:</strong> ");
			write(returnTag.inlineTags());
		}
		return this;
	}

	@Override
	public HeliumAPIDocHTMLWriter writeThrowsTagsPrefix() {
		return writeParamDescriptionsPrefix("Throws:");
	}

	@Override
	public HeliumAPIDocHTMLWriter writeThrowsTag(ThrowsTag throwsTag) {
		write("<li><strong>");
		writeLink(throwsTag.exceptionName());
		write("</strong>");
		write(" - ");
		write(throwsTag.inlineTags());
		write("</li>");
		return this;
	}


	@Override
	public HeliumAPIDocHTMLWriter writeThrowsTagsPostfix() {
		return writeParamDescriptionsPostfix();
	}

	@Override
	public HeliumAPIDocWriter writeMembersPrefix(ClassDoc classDoc) {
		write("<strong>Returns: </strong>");
		write("an object of class ");
		write("<code class='" + PRETTY_PRINT_CLASS + "'>");
		write(classDoc.qualifiedName());
		write("</code>");
		write(", with the following properties:</em></p>");
		writeParamDescriptionsPostfix();
		return this;
	}

	@Override
	public HeliumAPIDocWriter writePostamble() {
		write("</dd></dl>\n\n");
		return this;
	}

}