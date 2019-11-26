# -*- coding: utf-8 -*-
from helium.api import click, Text
from helium_integrationtest.environment import get_integrationtest_resource
from helium_integrationtest.inttest_api import test_browser_name, BrowserAT
from BaseHTTPServer import HTTPServer
from locale import getdefaultlocale
from os.path import abspath
from SimpleHTTPServer import SimpleHTTPRequestHandler
from threading import Thread
from unittest.case import skipIf
import ssl
import sys

@skipIf(
	test_browser_name() != 'ie',
	"This test only makes sense to be run on IE as other browsers don't display"
	" the warning page 'Invalid SSL certificate' when driven via Selenium."
)
class IESSLIT(BrowserAT):
	SSL_SERVER_PORT = 4443
	def setUp(self):
		self.ssl_server = SSLServer(self.SSL_SERVER_PORT, get_ssl_cert_file())
		self.ssl_server.start()
		super(IESSLIT, self).setUp()
	def get_url(self):
		return 'https://localhost:%d' % self.SSL_SERVER_PORT
	def test_continue(self):
		def_locale = getdefaultlocale()[0]
		if def_locale.startswith("pl"):
			click(
				u'Kontynuuj przeglądanie tej witryny sieci Web (niezalecane).'
			)
		else:
			click("Continue to this website (not recommended).")
		self.assertTrue(Text("Directory listing").exists())
	def tearDown(self):
		super(IESSLIT, self).tearDown()
		self.ssl_server.stop()

class SSLServer(object):
	def __init__(self, port, cert_file_path):
		self.cert_file_path = cert_file_path
		self.server = HTTPServer(('localhost', port), SimpleHTTPRequestHandler)
		self.server.socket = ssl.wrap_socket(
			self.server.socket, certfile=abspath(self.cert_file_path),
			server_side=True
		)
		self.server_thread = None
	def start(self):
		self.server_thread = Thread(target=self.server.serve_forever)
		self.server_thread.start()
	def stop(self):
		self.server.shutdown()
		self.server_thread.join()

def get_ssl_cert_file():
	return get_integrationtest_resource('inttest_ie_ssl', 'ssl_certificate.pem')

if __name__ == '__main__':
	ssl_server = SSLServer(IESSLIT.SSL_SERVER_PORT, get_ssl_cert_file())
	ssl_server.start()
	sys.stdout.write(
		'SSL server started on port %r.\n' % IESSLIT.SSL_SERVER_PORT
	)
	sys.stdout.flush()