from html.parser import HTMLParser
import re

def strip_tags(html):
	s = TagStripper()
	s.feed(html)
	return s.get_data()

class TagStripper(HTMLParser):
	def __init__(self):
		HTMLParser.__init__(self)
		self.reset()
		self.fed = []
	def handle_data(self, d):
		self.fed.append(d)
	def get_data(self):
		return ''.join(self.fed)

def get_easily_readable_snippet(html):
	html = normalize_whitespace(html)
	try:
		inner_start = html.index('>') + 1
		inner_end = html.rindex('<', inner_start)
	except ValueError:
		return html
	opening_tag = html[:inner_start]
	closing_tag = html[inner_end:]
	inner = html[inner_start:inner_end]
	if '<' in inner or len(inner) > 60:
		return '%s...%s' % (opening_tag, closing_tag)
	else:
		return html

def normalize_whitespace(html):
	result = html.strip()
	# Remove multiple spaces:
	result = re.sub(r'\s+', ' ', result)
	# Remove spaces after opening or before closing tags:
	result = result.replace('> ', '>').replace(' <', '<')
	return result