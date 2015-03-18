package com.saturn.filter;

import org.apache.commons.lang3.Validate;
import org.joda.time.LocalDate;

import com.google.common.base.Predicate;
import com.saturn.Configuration;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;

class OptionExpiryPredicate implements Predicate<Timestamped<Option>> {
	private static final String END_PROPERTY = "saturn.filter.optionexpiry.end";
	private static final String START_PROPERTY = "saturn.filter.optionexpiry.start";

	private final LocalDate start;
	private final LocalDate end;

	OptionExpiryPredicate(LocalDate start, LocalDate end) {
		this.start = Validate.notNull(start);
		this.end = Validate.notNull(end);
	}

	OptionExpiryPredicate() {
		this(LocalDate.now().plusDays(Integer.parseInt(Configuration.getProperties().getProperty(START_PROPERTY))),
				LocalDate.now().plusDays(Integer.parseInt(Configuration.getProperties().getProperty(END_PROPERTY))));
	}

	@Override
	public boolean apply(Timestamped<Option> value) {
		final LocalDate expiry = value.getObject().getExpiry();
		if (expiry.isBefore(this.start)) {
			return false;
		} else if (expiry.isAfter(this.end)) {
			return false;
		}
		return true;
	}
}
