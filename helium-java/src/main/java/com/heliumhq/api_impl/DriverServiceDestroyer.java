package com.heliumhq.api_impl;

import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.remote.service.DriverService;

import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

class DriverServiceDestroyer extends Thread {

	private final DriverService driverService;

	DriverServiceDestroyer(DriverService driverService) {
		this.driverService = driverService;
	}

	public void run() {
		try {
			ReentrantLock lock = (ReentrantLock) getField("lock");
			try {
				lock.lock();
				CommandLine process = (CommandLine) getField("process");
				if (process == null)
					return;
				process.destroy();
				setField("process", null);
			} finally {
				lock.unlock();
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to shutdown " + driverService + ".", e
			);
		}
	}

	private Object getField(String fieldName) throws
			NoSuchFieldException, IllegalAccessException {
		return getFieldDefinition(fieldName).get(driverService);
	}
	private Field getFieldDefinition(String fieldName) throws
			NoSuchFieldException {
		Field field = DriverService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}
	private void setField(String fieldName, Object value) throws
			IllegalAccessException, NoSuchFieldException {
		getFieldDefinition(fieldName).set(driverService, value);
	}
}
