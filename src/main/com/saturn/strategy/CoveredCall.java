package com.saturn.strategy;

import com.saturn.api.Greeks;
import com.saturn.api.Option;
import com.saturn.api.OptionType;
import com.saturn.api.SingleValueStrategy;

public class CoveredCall implements SingleValueStrategy<Option> {

	@Override
	public float roi(Option value) {
		if (OptionType.CALL.equals(value.getType()) && value.getBid() > 0.05) {

			final float strike = value.getStrike();
			final float underlying = value.getUnderlying().getBid();
			if (underlying > 0) {
				if (strike <= underlying) {
					// in the money
					return (strike + value.getBid() - underlying) / underlying;
				} else {
					// out of the money
					final Greeks greeks = value.getGreeks();
					final float delta;
					if (greeks != null) {
						delta = greeks.getDelta();
					} else {
						delta = (float) (0.5 * Math.exp(underlying - strike));
					}

					return value.getBid() / underlying;
				}
			}
		}
		return -100f;

	}
}
