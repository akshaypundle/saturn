package com.saturn.filter;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Predicate;
import com.saturn.api.Option;
import com.saturn.api.SingleValueStrategy;
import com.saturn.api.Timestamped;

public class SingleValueStrategyPredicate implements Predicate<Timestamped<Option>> {
	private final SingleValueStrategy<Option> strategy;
	private final float minRoi;
	private final float maxRoi;

	SingleValueStrategyPredicate(SingleValueStrategy<Option> strategy, float minRoi, float maxRoi) {
		this.strategy = Validate.notNull(strategy);
		this.minRoi = minRoi;
		this.maxRoi = maxRoi;

	}

	@Override
	public boolean apply(Timestamped<Option> value) {
		final float roi = this.strategy.roi(value.getObject());
		return roi > this.minRoi && roi <= this.maxRoi;
	}
}
