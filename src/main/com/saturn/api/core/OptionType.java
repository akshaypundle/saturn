package com.saturn.api.core;

public enum OptionType {
	CALL, PUT;

	public static OptionType fromString(String type) {
		if ("CALL".equalsIgnoreCase(type)) {
			return CALL;
		} else if ("PUT".equalsIgnoreCase(type)) {
			return PUT;
		}
		throw new IllegalArgumentException("Unknown option type " + type);
	}
}
