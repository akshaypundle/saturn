package com.saturn.strategy;

import com.saturn.api.Option;
import com.saturn.api.OptionType;
import com.saturn.api.SingleValueStrategy;

public class CoveredCall implements SingleValueStrategy<Option> {

	@Override
	public float roi(Option value) {
		if (OptionType.CALL.equals(value.getType())) {

			final float strike = value.getStrike();
			final float underlying = value.getUnderlying().getBid();
			if (underlying > 0) {
				if (strike <= underlying) {
					// in the money
					return (strike + value.getBid() - underlying) / underlying;

				} else {
					// out of the money
					return value.getBid() / underlying;
				}
			}
		}
		return 0f;

	}
}
