package com.heliumhq.inttest_api;

import com.heliumhq.inttest_api.util.ProcessInfo;
import com.heliumhq.inttest_api.util.Processes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.heliumhq.inttest_api.util.Processes.waitForProcessOutput;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public abstract class KillServiceAtExitAT {

	private final static String BROWSER_STARTED_MSG =
			KillServiceAtExitAT.class.getSimpleName() +
			" successfully started the browser.";

	private List<ProcessInfo> runningServicesBefore;
	private List<ProcessInfo> runningBrowsersBefore;

	@Test
	public void testKillServiceAtExit() throws IOException,
			InterruptedException {
		startBrowserInSubProcess();
		assertEquals(Collections.EMPTY_LIST, getNewRunningServices());
	}

	/**
	 * Starts public static void main(...) below in a new process.
	 */
	private void startBrowserInSubProcess() throws IOException,
			InterruptedException {
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder pb = new ProcessBuilder(
				"java", "-cp", classpath, getClass().getCanonicalName(),
				// Supply test class to let main(...) below access the
				// implementation of startBrowser().
				getClass().getCanonicalName()
		);
		pb.redirectErrorStream(true);
		Process subProcess = pb.start();
		waitForProcessOutput(subProcess, BROWSER_STARTED_MSG);
		subProcess.waitFor();
	}

	private List<ProcessInfo> getNewRunningServices() throws IOException {
		List<ProcessInfo> result = getRunningServices();
		result.removeAll(runningServicesBefore);
		return result;
	}

	@Before
	public void setUp() throws IOException {
		runningServicesBefore = getRunningServices();
		runningBrowsersBefore = getRunningBrowsers();
	}

	@After
	public void tearDown() throws IOException {
		for (ProcessInfo service : getNewRunningServices())
			service.kill();
		for (ProcessInfo browser : getNewRunningBrowsers())
			browser.kill();
	}

	private List<ProcessInfo> getNewRunningBrowsers() throws IOException {
		List<ProcessInfo> result = getRunningBrowsers();
		result.removeAll(runningBrowsersBefore);
		return result;
	}

	private List<ProcessInfo> getRunningServices() throws IOException {
		return getRunningProcesses(getServiceProcessNames());
	}

	private List<ProcessInfo> getRunningBrowsers() throws IOException {
		return getRunningProcesses(asList(getBrowserProcessName()));
	}

	private List<ProcessInfo> getRunningProcesses(List<String> imageNames)
		throws IOException {
		List<ProcessInfo> result = new ArrayList<ProcessInfo>();
		for (ProcessInfo process : Processes.getRunningProcesses())
			if (imageNames.contains(process.getName()))
				result.add(process);
		return result;
	}

	protected abstract List<String> getServiceProcessNames();

	protected abstract String getBrowserProcessName();

	public static void main(String[] args) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Class testClass = Class.forName(args[0]);
		KillServiceAtExitAT testInstance =
				(KillServiceAtExitAT) testClass.getConstructor().newInstance();
		testInstance.startBrowser();
		System.out.println(BROWSER_STARTED_MSG);
		System.out.flush();
		System.exit(0);
	}

	protected abstract void startBrowser();

}
