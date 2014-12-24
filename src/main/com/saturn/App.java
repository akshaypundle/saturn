package com.saturn;

import org.apache.commons.io.IOUtils;

import com.saturn.api.Feed;
import com.saturn.api.Filter;
import com.saturn.api.Option;
import com.saturn.feed.CombinedFeed;
import com.saturn.feed.Combiners;
import com.saturn.feed.FilteredFeed;
import com.saturn.feed.deltaneutral.DeltaNeutralFeed;
import com.saturn.feed.yahoo.YahooFeed;
import com.saturn.filter.Filters;

public class App {

	public static void main(String[] args) {
		final Filter<Option> filter = Filters.and(Filters.strike(), Filters.expiry());
		final Feed<Option> yahooFeed = new FilteredFeed<>(new YahooFeed(), filter);
		final Feed<Option> deltaNeutralFeed = new FilteredFeed<>(new DeltaNeutralFeed(), filter);
		final Feed<Option> combinedFeed = new CombinedFeed<Option>(yahooFeed, deltaNeutralFeed,
				Combiners.<Option> latestTime());

		IOUtils.closeQuietly(yahooFeed);
		IOUtils.closeQuietly(deltaNeutralFeed);
		IOUtils.closeQuietly(combinedFeed);
	}
}
