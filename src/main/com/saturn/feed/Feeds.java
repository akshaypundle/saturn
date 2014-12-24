package com.saturn.feed;

import com.saturn.api.Feed;
import com.saturn.api.Filter;
import com.saturn.api.Option;
import com.saturn.feed.deltaneutral.DeltaNeutralFeed;
import com.saturn.feed.yahoo.YahooFeed;

public final class Feeds {

	public static Feed<Option> deltaNeutral() {
		return new DeltaNeutralFeed();
	}

	public static Feed<Option> yahoo() {
		return new YahooFeed();
	}

	public static <T> Feed<T> filtered(Feed<T> feed, Filter<T> filter) {
		return new FilteredFeed<>(feed, filter);
	}

	public static <T> CombinedFeed<T> combined(Feed<T> feed1, Feed<T> feed2, Combiner<T> combiner) {
		return new CombinedFeed<>(feed1, feed2, combiner);
	}

	private Feeds() {
		// nope
	}
}
