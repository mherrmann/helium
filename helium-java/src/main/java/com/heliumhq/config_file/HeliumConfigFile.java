package com.heliumhq.config_file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HeliumConfigFile {

	public final static String CONFIG_FILE_NAME = "Helium.ini";

	private final String serverScheme;
	private final String serverHost;
	private final int serverPort;
	private final String serverPath;
	private final String buildVersion;
	private final String buildChecksum;

	public HeliumConfigFile(String configFilePath) throws IOException {
		BufferedReader reader =
				new BufferedReader(new FileReader(configFilePath));
		assertConfigFile(reader.readLine().equals("[build]"));
		buildVersion = readConfigFileProp(reader.readLine(), "version");
		buildChecksum = readConfigFileProp(reader.readLine(), "checksum");
		assertConfigFile(reader.readLine().equals(""));
		assertConfigFile(reader.readLine().equals("[server]"));
		serverScheme = readConfigFileProp(reader.readLine(), "scheme");
		serverHost = readConfigFileProp(reader.readLine(), "host");
		serverPort =
				Integer.parseInt(readConfigFileProp(reader.readLine(), "port"));
		serverPath = readConfigFileProp(reader.readLine(), "path");
		assertConfigFile(reader.readLine() == null);
	}

	public String getServerScheme() {
		return serverScheme;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getServerPath() {
		return serverPath;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public String getBuildChecksum() {
		return buildChecksum;
	}

	private String readConfigFileProp(String line, String property) {
		String equals = " = ";
		assertConfigFile(line.startsWith(property + equals));
		return line.substring(property.length() + equals.length());
	}

	private void assertConfigFile(boolean value) {
		assert value : "Malformed config file.";
	}

}