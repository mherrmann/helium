package com.heliumhq.inttest_api.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.heliumhq.util.System.isWindows;
import static org.junit.Assert.assertTrue;

public class Processes {

	public static List<ProcessInfo> getRunningProcesses() throws IOException {
		if (isWindows())
			return getRunningProcessesWindows();
		else
			return getRunningProcessesUnixLike();
	}

	private static List<ProcessInfo> getRunningProcessesWindows()
			throws IOException {
		List<ProcessInfo> result = new ArrayList<ProcessInfo>();
		Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
		BufferedReader input =
				new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = input.readLine()) != null) {
			String[] columns = line.split("\"");
			String name = columns[1];
			int pid = Integer.parseInt(columns[3]);
			result.add(new ProcessInfo(pid, name));
		}
		return result;
	}

	private static List<ProcessInfo> getRunningProcessesUnixLike()
			throws IOException {
		List<ProcessInfo> result = new ArrayList<ProcessInfo>();
		Process p = Runtime.getRuntime().exec("ps -e");
		BufferedReader input =
				new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		// Skip to below header line:
		while ((line = input.readLine()) != null)
			if (line.trim().startsWith("PID"))
				break;
		while ((line = input.readLine()) != null) {
			String[] columns = line.trim().split("\\s+");
			int pid = Integer.parseInt(columns[0]);
			String name = columns[columns.length - 1];
			result.add(new ProcessInfo(pid, name));
		}
		return result;
	}

	public static void waitForProcessOutput(Process process, String output)
			throws IOException {
		BufferedReader stderrReader = new BufferedReader(
				new InputStreamReader(process.getInputStream())
		);
		String actualOutput = "";
		int numLinesToRead = 10; // Don't wait forever
		String line;
		while (numLinesToRead > 0 && (line = stderrReader.readLine()) != null) {
			actualOutput += line + "\n";
			if (line.equals(output))
				numLinesToRead = 0;
			else
				numLinesToRead --;
		}
		assertTrue(actualOutput, actualOutput.contains(output));
	}

	public static ProcessResult execCapturingOutput(String command) throws
			IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		CommandLine commandLine = CommandLine.parse(command);
		DefaultExecutor exec = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		exec.setStreamHandler(streamHandler);
		// Don't throw exception if exit value != 0:
		exec.setExitValues(null);
		int returnCode = exec.execute(commandLine);
		return new ProcessResult(returnCode, outputStream.toString());
	}

}
