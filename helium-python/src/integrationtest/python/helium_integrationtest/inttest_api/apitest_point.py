from helium.api import click, Point, Button, hover, rightclick, doubleclick
from helium_integrationtest.inttest_api import BrowserAT, test_browser_name
from re import search
from unittest import skipIf

@skipIf(
	test_browser_name() == 'firefox',
	'This test currently fails because of a bug with Firefox 27.0.1 and '
	'Selenium 2.39.0: https://code.google.com/p/selenium/issues/detail?id=7005'
)
class PointIT(BrowserAT):
	"""
	Tests helium.api.Point.

	The tests allow for a coordinate difference between browsers of up to +/- 1
	pixel. For instance: In Firefox, Button("Button 1").center is (39, 12), in
	IE and Chrome it is (39, 13). This really is because Firefox lays out the
	page slightly differently, so that the button is further up on the page.
	"""
	def get_page(self):
		return 'inttest_point.html'
	def test_top_left(self):
		self.assert_is_in_range(
			Point(2, 3), Button("Button 1").top_left, delta=(0, 1)
		)
	def assert_is_in_range(self, expected, point, delta):
		x, y = point
		expected_x, expected_y = expected
		delta_x, delta_y = delta
		self.assert_around(expected_x, x, delta_x)
		self.assert_around(expected_y, y, delta_y)
	def assert_around(self, expected, actual, delta, msg=None):
		self.assertIn(
			actual, range(expected - delta, expected + delta + 1), msg
		)
	def test_click_top_left(self):
		click(Button("Button 1").top_left)
		self.assert_result_is(
			"Button 1 clicked at offset (0, 0).", offset_delta=(1, 1)
		)
	def test_click_point(self):
		click(Point(39, 13))
		self.assert_result_is(
			"Button 1 clicked at offset (37, 10).", offset_delta=(0, 1)
		)
	def test_click_top_left_offset(self):
		click(Button("Button 3").top_left + (3, 4))
		self.assert_result_is("Button 3 clicked at offset (3, 4).")
	def test_hover_top_left(self):
		hover(Button("Button 1").top_left)
		self.assert_result_is(
			"Button 1 hovered at offset (0, 0).", offset_delta=(1, 1)
		)
	def test_hover_point(self):
		hover(Point(39, 13))
		self.assert_result_is(
			"Button 1 hovered at offset (37, 10).", offset_delta=(0, 1)
		)
	def test_hover_top_left_offset(self):
		hover(Button("Button 3").top_left + (3, 4))
		self.assert_result_is("Button 3 hovered at offset (3, 4).")
	def test_rightclick_top_left(self):
		rightclick(Button("Button 1").top_left)
		self.assert_result_is(
			"Button 1 rightclicked at offset (0, 0).", offset_delta=(1, 1)
		)
	def test_rightclick_point(self):
		rightclick(Point(39, 13))
		self.assert_result_is(
			"Button 1 rightclicked at offset (37, 10).", offset_delta=(0, 1)
		)
	def test_rightclick_top_left_offset(self):
		rightclick(Button("Button 3").top_left + (3, 4))
		self.assert_result_is(
			"Button 3 rightclicked at offset (3, 4)."
		)
	def test_doubleclick_top_left(self):
		doubleclick(Button("Button 1").top_left)
		self.assert_result_is(
			"Button 1 doubleclicked at offset (0, 0).", offset_delta=(1, 1)
		)
	def test_doubleclick_point(self):
		doubleclick(Point(39, 13))
		self.assert_result_is(
			"Button 1 doubleclicked at offset (37, 10).", offset_delta=(0, 1)
		)
	def test_doubleclick_top_left_offset(self):
		doubleclick(Button("Button 3").top_left + (3, 4))
		self.assert_result_is("Button 3 doubleclicked at offset (3, 4).")
	def assert_result_is(self, expected, offset_delta=(0, 0)):
		actual = self.read_result_from_browser()
		expected_offset = self._extract_offset(expected)
		actual_offset = self._extract_offset(actual)
		expected_x, expected_y = eval(expected_offset)
		actual_x, actual_y = eval(actual_offset)
		delta_x, delta_y = offset_delta
		self.assert_around(
			expected_x, actual_x, delta_x,
			"Offset (%r, %r) is not in expected range (%r+-%r, %r+-%r)." % (
				actual_x, actual_y, expected_x, delta_x, expected_y, delta_y
			)
		)
		self.assert_around(
			expected_x, actual_x, delta_x,
			"Offset (%r, %r) is not in expected range (%r+-%r, %r+-%r)." % (
				actual_x, actual_y, expected_x, delta_x, expected_y, delta_y
			)
		)
		expected_prefix, expected_suffix = expected.split(expected_offset)
		actual_prefix, actual_suffix = actual.split(actual_offset)
		self.assertEquals(expected_prefix, actual_prefix)
		self.assertEquals(expected_suffix, actual_suffix)
	def _extract_offset(self, result_in_browser):
		return search(r"(\([^,]+, [^\)]+\))", result_in_browser).group(1)