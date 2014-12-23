package com.saturn.api.core;

import org.joda.time.LocalDate;

import com.google.common.base.MoreObjects;

public class Option extends Instrument {

	private final OptionType type;
	private final LocalDate expiry;
	private final float strike;
	private final Instrument underlying;

	public Option(float bid, float ask, String symbol, OptionType type, LocalDate expiry, float strike,
			Instrument underlying) {
		super(bid, ask, symbol);
		this.type = type;
		this.expiry = expiry;
		this.strike = strike;
		this.underlying = underlying;
	}

	public OptionType getType() {
		return this.type;
	}

	public LocalDate getExpiry() {
		return this.expiry;
	}

	public float getStrike() {
		return this.strike;
	}

	public Instrument getUnderlying() {
		return this.underlying;
	}

	@Override
	public String toString() {

		return MoreObjects.toStringHelper(this) //
				.add("underlying", this.underlying) //
				.add("expiry", this.expiry) //
				.add("type", this.type) //
				.add("strike", this.strike) //
				.toString();
	}
}
