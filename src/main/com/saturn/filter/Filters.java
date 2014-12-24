package com.saturn.filter;

import org.joda.time.LocalDate;

import com.saturn.api.Filter;
import com.saturn.api.Option;

public class Filters {

	@SafeVarargs
	public static <T> Filter<T> and(Filter<T>... filters) {
		return new AndFilter<>(filters);
	}

	public static Filter<Option> expiry(LocalDate minExpiry, LocalDate maxExpiry) {
		return new OptionExpiryFilter(minExpiry, maxExpiry);
	}

	public static Filter<Option> expiry() {
		return new OptionExpiryFilter();
	}

	public static Filter<Option> strike(float min, float max) {
		return new OptionStrikeFilter(min, max);
	}

	public static Filter<Option> strike() {
		return new OptionStrikeFilter();
	}

	private Filters() {
		// nope
	}
}
