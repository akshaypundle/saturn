package com.saturn.feed;

import com.saturn.api.Greeks;
import com.saturn.api.Instrument;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;

public final class Combiners {

	public static <T extends Instrument> Combiner<T> latestTime() {
		return new LatestTime<T>();
	}

	public static Combiner<Option> latestTimeCopyGreeks() {
		return new LatestTimeCombineGreeks();
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

	private static class LatestTimeCombineGreeks extends LatestTime<Option> {
		@Override
		public Timestamped<Option> choose(Timestamped<Option> value1, Timestamped<Option> value2) {
			Timestamped<Option> chosen = super.choose(value1, value2);
			Option chosenOption = chosen.getObject();

			if (chosenOption.getGreeks() == null) {
				Greeks greeks = null;
				if (value1.getObject().getGreeks() != null) {
					greeks = value1.getObject().getGreeks();
				} else if (value1.getObject().getGreeks() != null) {
					greeks = value2.getObject().getGreeks();
				}
				if (greeks != null) {
					chosenOption = new Option(chosenOption.getBid(), chosenOption.getAsk(), chosenOption.getSymbol(),
							chosenOption.getType(), chosenOption.getExpiry(), chosenOption.getStrike(),
							chosenOption.getUnderlying(), greeks);
					chosen = new Timestamped<Option>(chosenOption, chosen.getTimestamp());
				}
			}
			return chosen;
		}
	}

	private Combiners() {
		// nope
	}

}
