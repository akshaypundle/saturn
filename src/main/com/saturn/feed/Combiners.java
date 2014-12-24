package com.saturn.feed;

import com.saturn.api.Instrument;
import com.saturn.api.Timestamped;

public final class Combiners {

	public static <T extends Instrument> Combiner<T> latestTime() {
		return new LatestTime<T>();
	}

	private static class LatestTime<T extends Instrument> implements Combiner<T> {

		@Override
		public Object toKey(Timestamped<T> value) {
			return value.getObject().getSymbol();
		}

		@Override
		public Timestamped<T> choose(Timestamped<T> value1, Timestamped<T> value2) {
			return value1.getTimestamp() >= value2.getTimestamp() ? value1 : value2;
		}
	}

	private Combiners() {
		// nope
	}

}
