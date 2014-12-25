package com.saturn.feed;

import java.util.Iterator;

import com.saturn.api.Feed;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;
import com.saturn.feed.deltaneutral.DeltaNeutralFeed;
import com.saturn.feed.yahoo.YahooFeed;

public final class Feeds {

	public static Feed<Option> deltaNeutral() {
		return new DeltaNeutralFeed();
	}

	public static Feed<Option> yahoo() {
		return new YahooFeed();
	}

	public static <T> CombinedFeed<T> combined(Iterator<Timestamped<T>> feed1, Iterator<Timestamped<T>> feed2,
			Combiner<T> combiner) {
		return new CombinedFeed<>(feed1, feed2, combiner);
	}

	private Feeds() {
		// nope
	}
}
