package com.heliumhq.site;

import java.util.Set;

public class Param {
	private final String name;
	private final Set<String> types;
	private final boolean isOptional;
	private final boolean isVarargs;
	public Param(
		String name, Set<String> types, boolean isOptional, boolean isVarargs
	) {
		this.name = name;
		this.types = types;
		this.isOptional = isOptional;
		this.isVarargs = isVarargs;
	}

	public String getName() {
		return name;
	}

	public Set<String> getTypes() {
		return types;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public boolean isVarargs() {
		return isVarargs;
	}
}