from unittest import TestCase
from helium._impl.util.html import normalize_whitespace, \
	get_easily_readable_snippet


class GetEasilyReadableSnippetTest(TestCase):
	def test_no_tag(self):
		self.assertEqual(
			'Hello World!', get_easily_readable_snippet('Hello World!')
		)

	def test_completely_empty_tag(self):
		self.assertEqual('<>', get_easily_readable_snippet('<>'))

	def test_empty_tag_with_attributes(self):
		empty_tag_with_attrs = \
			'<input type="checkbox" id="checkBoxId" name="checkBoxName" ' \
			'class="checkBoxClass">'
		self.assertEqual(
			empty_tag_with_attrs,
			get_easily_readable_snippet(empty_tag_with_attrs)
		)

	def test_tag_with_nested_tags(self):
		self.assertEqual(
			'<body>...</body>',
			get_easily_readable_snippet('<body><p>Hello World!</p></body>')
		)

	def test_tag_with_long_content(self):
		tag_with_long_content = '<body>%s</body>' % ('x' * 100)
		self.assertEqual(
			'<body>...</body>',
			get_easily_readable_snippet(tag_with_long_content)
		)


class NormalizeWhitespaceTest(TestCase):
	def test_string_without_whitespace(self):
		self.assertEqual('Foo', normalize_whitespace('Foo'))

	def test_string_one_whitespace(self):
		self.assertEqual('Hello World!', normalize_whitespace('Hello World!'))

	def test_string_leading_whitespace(self):
		self.assertEqual('Hello World!', normalize_whitespace(' Hello World!'))

	def test_string_complex_whitespace(self):
		self.assertEqual(
			'Hello World!', normalize_whitespace('\n\t Hello\t\t    World!  \n')
		)

	def test_tag_with_spaces_around_inner_html(self):
		self.assertEqual(
			'<span>Hi there!</span>',
			normalize_whitespace('<span> Hi there! </span>')
		)
