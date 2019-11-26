package com.heliumhq.api_impl.application_context;

import com.heliumhq.errors.HeliumError;

public class StartupError extends HeliumError {
	public StartupError(String message) {
		super(message);
	}
}
