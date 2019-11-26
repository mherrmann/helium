package com.heliumhq;

import java.io.IOException;

import static com.heliumhq.api_impl.application_context.DevelopmentAPIConfig.
		getHeliumDir;
import static com.heliumhq.api_impl.application_context.DevelopmentAPIConfig.
		getHeliumFile;
import static com.heliumhq.inttest_api.util.Processes.waitForProcessOutput;

public class PythonServer {

	private final String pyFile;
	private final String serverStartedMessage;
	private Process pythonServer;

	public PythonServer(String pyFile, String serverStartedMessage) {
		this.pyFile = pyFile;
		this.serverStartedMessage = serverStartedMessage;
	}

	public void start() throws IOException {
		String pyFilePath = getHeliumFile("helium-python/" + pyFile);
		ProcessBuilder pb = new ProcessBuilder("python", pyFilePath);
		String pythonpath = System.getenv("PYTHONPATH");
		pb.environment().put("PYTHONPATH", pythonpath);
		pb.directory(getHeliumDir());
		pb.redirectErrorStream(true);
		pythonServer = pb.start();
		waitForProcessOutput(pythonServer, serverStartedMessage);
	}

	public void stop() {
		pythonServer.destroy();
	}

}