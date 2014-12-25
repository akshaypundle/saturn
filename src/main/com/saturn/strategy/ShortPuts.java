package com.saturn.strategy;

import com.saturn.api.Greeks;
import com.saturn.api.Option;
import com.saturn.api.OptionType;
import com.saturn.api.SingleValueStrategy;

public class ShortPuts implements SingleValueStrategy<Option> {

	@Override
	public float roi(Option value) {
		if (OptionType.PUT.equals(value.getType()) && value.getBid() > 0.05) {

			final float strike = value.getStrike();
			final float underlying = value.getUnderlying().getBid();
			if (underlying > 0) {
				if (strike <= underlying) {
					// out of the money puts
					return value.getBid() / underlying;
				} else {
					// in the money
					final Greeks greeks = value.getGreeks();
					final float delta;
					if (greeks != null) {
						delta = greeks.getDelta();
					} else {
						delta = 1 - (float) (0.5 * Math.exp(underlying - strike));
					}

					return ((underlying - strike) * delta + value.getBid()) / underlying;
				}
			}
		}
		return -100f;

	}
}
