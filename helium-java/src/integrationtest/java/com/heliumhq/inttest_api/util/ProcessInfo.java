package com.heliumhq.inttest_api.util;

import java.io.IOException;

import static com.heliumhq.util.System.isWindows;

public class ProcessInfo {

	private final int id;
	private final String name;

	public ProcessInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void kill() throws IOException {
		String killCmd;
		if (isWindows())
			killCmd = "taskkill /PID " + id;
		else
			killCmd = "kill " + id;
		Runtime.getRuntime().exec(killCmd);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProcessInfo that = (ProcessInfo) o;

		if (id != that.id) return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	public String toString() {
		return String.format("Process(%s, %s)", id, name);
	}

}
