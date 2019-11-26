import re
import sys

def get_stdout_encoding(default=None):
	try:
		return sys.stdout.encoding or default
	except AttributeError:
		return default

def repr_in_stdout_encoding(obj):
	# On Python > 2, repr(...) needs to be unicode so we only encode if we're on
	# Python 2:
	if isinstance(obj, unicode) and sys.version_info[0] == 2:
		subject_repr = repr(obj)
		quote_start = re.search('[\'"]', subject_repr).start()
		quotation_mark = subject_repr[quote_start]
		is_escaped = False
		quote_end = quote_start + 1
		while quote_end < len(subject_repr):
			char = subject_repr[quote_end]
			if char == '\\':
				is_escaped = not is_escaped
			else:
				if char == quotation_mark and not is_escaped:
					break
				is_escaped = False
			quote_end += 1
		stdout_encoding = get_stdout_encoding(default='unicode-escape')
		self_encoded = obj.encode(stdout_encoding)
		if stdout_encoding == 'unicode-escape':
			self_encoded = self_encoded.replace('\\\\', '\\')
		return quotation_mark + self_encoded + subject_repr[quote_end:]
	return repr(obj)