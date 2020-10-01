from helium import scroll_down, scroll_left, scroll_right, scroll_up
from tests.api import BrowserAT


class ScrollTest(BrowserAT):
    def get_page(self):
        return 'test_scroll.html'

    def test_scroll_up_when_at_top_of_page(self):
        scroll_up()
        self.assert_scroll_position_equals(0, 0)

    def test_scroll_down(self):
        scroll_down()
        self.assert_scroll_position_equals(0, 100)

    def test_scroll_down_then_up(self):
        scroll_down()
        scroll_up()
        self.assert_scroll_position_equals(0, 0)

    def test_scroll_down_then_up_pixels(self):
        scroll_down(175)
        scroll_up(100)
        self.assert_scroll_position_equals(0, 75)

    def test_scroll_left_when_at_start_of_page(self):
        scroll_left()
        self.assert_scroll_position_equals(0, 0)

    def test_scroll_right(self):
        scroll_right()
        self.assert_scroll_position_equals(100, 0)

    def test_scroll_right_then_left(self):
        scroll_right()
        scroll_left()
        self.assert_scroll_position_equals(0, 0)

    def test_scroll_right_then_left_pixels(self):
        scroll_right(175)
        scroll_left(100)
        self.assert_scroll_position_equals(75, 0)

    def tearDown(self):
        # Recent versions of Chrome(Driver) don't reset the scroll position when
        # reloading the page. Force-reset it:
        self.driver.execute_script('window.scrollTo(0, 0);')
        super().tearDown()

    def assert_scroll_position_equals(self, x, y):
        scroll_position_x = self.driver.execute_script(
            'return window.pageXOffset || document.documentElement.scrollLeft '
            '|| document.body.scrollLeft'
        )
        self.assertEqual(x, scroll_position_x)
        scroll_position_y = self.driver.execute_script(
            'return window.pageYOffset || document.documentElement.scrollTop '
            '|| document.body.scrollTop'
        )
        self.assertEqual(y, scroll_position_y)
