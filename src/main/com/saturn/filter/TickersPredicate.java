package com.saturn.filter;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;

class TickersPredicate implements Predicate<Timestamped<Option>> {

	private final ImmutableSet<String> tickers;

	public TickersPredicate(Iterable<String> tickerCollection) {
		final Builder<String> builder = ImmutableSet.<String> builder();
		for (final String ticker : tickerCollection) {
			builder.add(ticker);
		}
		this.tickers = builder.build();
	}

	@Override
	public boolean apply(Timestamped<Option> input) {
		final String ticker = input.getObject().getUnderlying().getSymbol();
		return this.tickers.contains(ticker);
	}
}
