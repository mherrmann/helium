from helium._impl.selenium_wrappers import FrameIterator, FramesChangedWhileIterating
from selenium.common.exceptions import NoSuchFrameException
from unittest import TestCase


class FrameIteratorTest(TestCase):
	def test_only_main_frame(self):
		self.assertEqual([[]], list(FrameIterator(StubWebDriver())))

	def test_one_frame(self):
		driver = StubWebDriver(Frame())
		self.assertEqual([[], [0]], list(FrameIterator(driver)))

	def test_two_frames(self):
		driver = StubWebDriver(Frame(), Frame())
		self.assertEqual([[], [0], [1]], list(FrameIterator(driver)))

	def test_nested_frame(self):
		driver = StubWebDriver(Frame(Frame()))
		self.assertEqual([[], [0], [0, 0]], list(FrameIterator(driver)))

	def test_complex(self):
		driver = StubWebDriver(Frame(Frame()), Frame())
		self.assertEqual([[], [0], [0, 0], [1]], list(FrameIterator(driver)))

	def test_disappearing_frame(self):
		child_frame = Frame()
		first_frame = Frame(child_frame)
		driver = StubWebDriver(first_frame)
		# We allow precisely 2 frame switches: One to first_frame and one to
		# child_frame. After this, FrameIterator tries to switch back to
		# first_frame, to see whether it has other children besides child_frame.
		# This is where we raise a NoSuchFrameException (by limiting the num.
		# of frame switches to 2). This simulates a situation where first_frame
		# disappears during iteration.
		driver.switch_to = TargetLocatorFailingAfterNFrameSwitches(driver, 2)
		with self.assertRaises(FramesChangedWhileIterating):
			list(FrameIterator(driver))


class StubWebDriver:
	def __init__(self, *frames):
		self.frames = list(frames)
		self.switch_to = StubTargetLocator(self)
		self.current_frame = None


class StubTargetLocator:
	def __init__(self, driver):
		self.driver = driver

	def default_content(self):
		self.driver.current_frame = None

	def frame(self, index):
		if self.driver.current_frame is None:
			children = self.driver.frames
		else:
			children = self.driver.current_frame.children
		try:
			new_frame = children[index]
		except IndexError:
			raise NoSuchFrameException()
		else:
			self.driver.current_frame = new_frame


class Frame:
	def __init__(self, *children):
		self.children = children


class TargetLocatorFailingAfterNFrameSwitches(StubTargetLocator):
	def __init__(self, driver, num_allowed_frame_switches):
		super(TargetLocatorFailingAfterNFrameSwitches, self).__init__(driver)
		self.num_allowed_frame_switches = num_allowed_frame_switches

	def frame(self, index):
		if self.num_allowed_frame_switches > 0:
			self.num_allowed_frame_switches -= 1
			return super(TargetLocatorFailingAfterNFrameSwitches, self)\
				.frame(index)
		raise NoSuchFrameException()
