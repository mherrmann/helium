from helium._impl.util.xpath import lower, replace_nbsp

class MatchType:
	def xpath(self, value, text):
		raise NotImplementedError()
	def text(self, value, text):
		raise NotImplementedError()

class PREFIX_IGNORE_CASE(MatchType):
	def xpath(self, value, text):
		if not text:
			return ''
		# Asterisks '*' are sometimes used to mark required fields. Eg.:
		# <label for="title"><span class="red-txt">*</span> Title:</label>
		# The starts-with filter below would be too strict to include such
		# matches. To get around this, we ignore asterisks unless the searched
		# text itself contains one.
		if '*' in text:
			strip_asterisks = value
		else:
			strip_asterisks = "translate(%s, '*', '')" % value

		# if text contains apostrophes (single quotes) then they need to be
		# treated with care
		if "'" in text:
			text = "concat('%s')" % ("',\"'\",'".join(text.split("'")))
		else:
			text = "'%s'" % text

		return "starts-with(normalize-space(%s), %s)" % (
			lower(replace_nbsp(strip_asterisks)), text.lower()
		)
	def text(self, value, text):
		if not text:
			return True
		return value.lower().lstrip().startswith(text.lower())