from helium.api import attach_file, drag_file, TextField, Text
from tests.api import BrowserAT
from tests.api.util import get_data_file

class FileUploadTest(BrowserAT):
	def get_page(self):
		return 'test_file_upload/test_file_upload.html'
	def setUp(self):
		super().setUp()
		self.file_to_upload = get_data_file(
			'test_file_upload', 'upload_this.png'
		)
	def test_normal_file_upload_is_not_text_field(self):
		self.assertFalse(TextField("Normal file upload").exists())
	def test_attach_file_to_normal_file_upload(self):
		attach_file(self.file_to_upload, to='Normal file upload')
		self.assertEqual('Success!', self.read_result_from_browser())
	def test_attach_file_no_to(self):
		attach_file(self.file_to_upload)
		self.assertEqual('Success!', self.read_result_from_browser())
	def test_attach_file_to_point(self):
		attach_file(
			self.file_to_upload, to=Text('Normal file upload').top_left + (200, 10)
		)
	def test_drag_file_to_appearing_drop_area(self):
		drag_file(self.file_to_upload, to='Drop the file here!')
		self.assertEqual('Success!', self.read_result_from_browser())