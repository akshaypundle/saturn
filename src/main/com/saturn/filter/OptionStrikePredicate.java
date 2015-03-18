package com.saturn.filter;

import com.google.common.base.Predicate;
import com.saturn.Configuration;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;

class OptionStrikePredicate implements Predicate<Timestamped<Option>> {
	private static final String END_PROPERTY = "saturn.filter.optionstrike.end";
	private static final String START_PROPERTY = "saturn.filter.optionstrike.start";
	private final float start;
	private final float end;

	OptionStrikePredicate(float start, float end) {
		this.start = start;
		this.end = end;
	}

	OptionStrikePredicate() {
		this(Float.parseFloat(Configuration.getProperties().getProperty(START_PROPERTY)), Float
				.parseFloat(Configuration.getProperties().getProperty(END_PROPERTY)));
	}

	@Override
	public boolean apply(Timestamped<Option> value) {
		final float strike = value.getObject().getStrike();
		final float underlying = value.getObject().getUnderlying().getBid();
		return strike >= underlying * this.start && strike <= underlying * this.end;
	}
}