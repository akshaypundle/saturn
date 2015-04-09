package com.saturn.filter;

import org.joda.time.LocalDate;

import com.google.common.base.Predicate;
import com.saturn.api.Option;
import com.saturn.api.OptionType;
import com.saturn.api.SingleValueStrategy;
import com.saturn.api.Timestamped;

public class Filters {

	public static Predicate<Timestamped<Option>> calls() {
		return new Predicate<Timestamped<Option>>() {

			@Override
			public boolean apply(Timestamped<Option> input) {
				return input.getObject().getType() == OptionType.CALL;
			}

		};
	}

	public static Predicate<Timestamped<Option>> puts() {
		return new Predicate<Timestamped<Option>>() {

			@Override
			public boolean apply(Timestamped<Option> input) {
				return input.getObject().getType() == OptionType.PUT;
			}

		};
	}

	public static Predicate<Timestamped<Option>> positiveBid() {
		return new Predicate<Timestamped<Option>>() {

			@Override
			public boolean apply(Timestamped<Option> input) {
				return input.getObject().getBid() > 0;
			}

		};
	}

	public static Predicate<Timestamped<Option>> positiveAsk() {
		return new Predicate<Timestamped<Option>>() {

			@Override
			public boolean apply(Timestamped<Option> input) {
				return input.getObject().getAsk() > 0;
			}

		};
	}

	public static Predicate<Timestamped<Option>> expiry(LocalDate minExpiry, LocalDate maxExpiry) {
		return new OptionExpiryPredicate(minExpiry, maxExpiry);
	}

	public static Predicate<Timestamped<Option>> expiry() {
		return new OptionExpiryPredicate();
	}

	public static Predicate<Timestamped<Option>> strike(float min, float max) {
		return new OptionStrikePredicate(min, max);
	}

	public static Predicate<Timestamped<Option>> strike() {
		return new OptionStrikePredicate();
	}

	public static Predicate<Timestamped<Option>> singleValueStrategy(SingleValueStrategy<Option> strategy,
			float minRoi, float maxRoi) {
		return new SingleValueStrategyPredicate(strategy, minRoi, maxRoi);
	}

	public static final Predicate<Timestamped<Option>> tickers(Iterable<String> tickerCollection) {
		return new TickersPredicate(tickerCollection);
	}

	private Filters() {
		// nope
	}
}
