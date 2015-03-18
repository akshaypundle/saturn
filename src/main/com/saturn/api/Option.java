package com.saturn.api;

import javax.annotation.CheckForNull;

import org.apache.commons.lang3.Validate;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Option extends Instrument {

	@JsonSerialize(using = ToStringSerializer.class)
	private final LocalDate expiry;
	@CheckForNull
	private final Greeks greeks;
	private final float strike;
	private final OptionType type;
	private final Instrument underlying;

	public Option(float bid, float ask, String symbol, OptionType type, LocalDate expiry, float strike,
			Instrument underlying, @CheckForNull Greeks greeks) {
		super(bid, ask, symbol);
		this.type = Validate.notNull(type);
		this.expiry = Validate.notNull(expiry);
		this.strike = strike;
		this.underlying = Validate.notNull(underlying);
		this.greeks = greeks;
	}

	public LocalDate getExpiry() {
		return this.expiry;
	}

	@CheckForNull
	public Greeks getGreeks() {
		return this.greeks;
	}

	public float getStrike() {
		return this.strike;
	}

	public OptionType getType() {
		return this.type;
	}

	public Instrument getUnderlying() {
		return this.underlying;
	}

	@Override
	public String toString() {
		return toStringHelper().toString();
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper() //
				.add("underlying", this.underlying) //
				.add("expiry", this.expiry) //
				.add("type", this.type) //
				.add("strike", this.strike) //
				.add("greeks", this.greeks);
	}
}
