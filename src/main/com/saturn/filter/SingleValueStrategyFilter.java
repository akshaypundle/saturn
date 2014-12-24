package com.saturn.filter;

import org.apache.commons.lang3.Validate;

import com.saturn.api.Filter;
import com.saturn.api.Option;
import com.saturn.api.SingleValueStrategy;

public class SingleValueStrategyFilter implements Filter<Option> {
	private final SingleValueStrategy<Option> strategy;
	private final float minRoi;
	private final float maxRoi;

	SingleValueStrategyFilter(SingleValueStrategy<Option> strategy, float minRoi, float maxRoi) {
		this.strategy = Validate.notNull(strategy);
		this.minRoi = minRoi;
		this.maxRoi = maxRoi;

	}

	@Override
	public boolean retain(Option value) {
		final float roi = this.strategy.roi(value);
		return roi >= this.minRoi && roi <= this.maxRoi;
	}
}
