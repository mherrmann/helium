package com.heliumhq.inttest_api.util;

public class ProcessResult {
	private final int returnCode;
	private final String output;
	public ProcessResult(int returnCode, String output) {
		this.returnCode = returnCode;
		this.output = output;
	}
	public int getReturnCode() {
		return returnCode;
	}
	public String getOutput() {
		return output;
	}
}