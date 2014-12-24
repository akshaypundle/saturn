package com.saturn.filter;

import com.saturn.Configuration;
import com.saturn.api.Filter;
import com.saturn.api.Option;

class OptionStrikeFilter implements Filter<Option> {
	private static final String END_PROPERTY = "saturn.filter.optionstrike.end";
	private static final String START_PROPERTY = "saturn.filter.optionstrike.start";
	private final float start;
	private final float end;

	OptionStrikeFilter(float start, float end) {
		this.start = start;
		this.end = end;
	}

	OptionStrikeFilter() {
		this(Float.parseFloat(Configuration.getProperties().getProperty(START_PROPERTY)), Float
				.parseFloat(Configuration.getProperties().getProperty(END_PROPERTY)));
	}

	@Override
	public boolean retain(Option value) {
		final float strike = value.getStrike();
		final float underlying = value.getUnderlying().getBid();
		return strike >= underlying * this.start && strike <= underlying * this.end;
	}
}