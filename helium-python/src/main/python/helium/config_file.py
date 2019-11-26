from ConfigParser import RawConfigParser

CONFIG_FILE_NAME = 'Helium.ini'

class HeliumConfigFile(object):
	def __init__(self, config_file_path):
		self.config_parser = RawConfigParser()
		self.config_parser.read(config_file_path)
		self.build_version = self.config_parser.get('build', 'version')
		self.build_checksum = self.config_parser.get('build', 'checksum')
		self.server_scheme = self.config_parser.get('server', 'scheme')
		self.server_host = self.config_parser.get('server', 'host')
		self.server_port = self.config_parser.getint('server', 'port')
		self.server_path = self.config_parser.get('server', 'path')