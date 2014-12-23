package com.saturn.api.core;

import org.apache.commons.lang3.Validate;

import com.google.common.base.MoreObjects;

public class Instrument {
	private final float bid;
	private final float ask;
	private final String symbol;

	public Instrument(float bid, float ask, String symbol) {
		this.bid = bid;
		this.ask = ask;
		this.symbol = Validate.notNull(symbol);
	}

	public float getBid() {
		return this.bid;
	}

	public float getAsk() {
		return this.ask;
	}

	public String getSymbol() {
		return this.symbol;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("symbol", this.symbol) //
				.add("bid", this.bid) //
				.add("ask", this.ask) //
				.toString();
	}
}
