package com.saturn.filter;

import org.apache.commons.lang3.Validate;
import org.joda.time.LocalDate;

import com.saturn.Configuration;
import com.saturn.api.Filter;
import com.saturn.api.Option;

class OptionExpiryFilter implements Filter<Option> {
	private static final String END_PROPERTY = "saturn.filter.optionexpiry.end";
	private static final String START_PROPERTY = "saturn.filter.optionexpiry.start";

	private final LocalDate start;
	private final LocalDate end;

	OptionExpiryFilter(LocalDate start, LocalDate end) {
		this.start = Validate.notNull(start);
		this.end = Validate.notNull(end);
	}

	OptionExpiryFilter() {
		this(LocalDate.now().plusDays(Integer.parseInt(Configuration.getProperties().getProperty(START_PROPERTY))),
				LocalDate.now().plusDays(Integer.parseInt(Configuration.getProperties().getProperty(END_PROPERTY))));
	}

	@Override
	public boolean retain(Option value) {
		final LocalDate expiry = value.getExpiry();
		if (expiry.isBefore(this.start)) {
			return false;
		} else if (expiry.isAfter(this.end)) {
			return false;
		}
		return true;
	}
}
