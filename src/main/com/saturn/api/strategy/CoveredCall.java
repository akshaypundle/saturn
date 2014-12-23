package com.saturn.api.strategy;

import com.saturn.api.core.Option;

public class CoveredCall {
	private final Option option;
	private final CoveredCallType type;

	public CoveredCall(Option option, CoveredCallType type) {
		this.option = option;
		this.type = type;
	}

	public Option getOption() {
		return this.option;
	}

	public CoveredCallType getType() {
		return this.type;
	}
}
