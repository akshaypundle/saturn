package com.saturn;

import static com.saturn.JsonWriter.newWriter;
import static com.saturn.feed.Feeds.combined;
import static com.saturn.feed.Feeds.deltaNeutral;
import static com.saturn.feed.Feeds.filtered;
import static com.saturn.feed.Feeds.yahoo;
import static com.saturn.filter.Filters.and;
import static com.saturn.filter.Filters.expiry;
import static com.saturn.filter.Filters.strike;

import org.apache.commons.io.IOUtils;

import com.saturn.api.Feed;
import com.saturn.api.Filter;
import com.saturn.api.Option;
import com.saturn.feed.Combiners;

public class App {

	public static void main(String[] args) {
		final Filter<Option> filter = and(strike(), expiry());
		final Feed<Option> yahooFeed = filtered(yahoo(), filter);
		final Feed<Option> deltaNeutralFeed = filtered(deltaNeutral(), filter);
		final Feed<Option> combinedFeed = combined(deltaNeutralFeed, yahooFeed, Combiners.<Option> latestTime());

		newWriter("/Users/apundle/repo/code/saturn/src/web/options.js", combinedFeed).write();

		IOUtils.closeQuietly(yahooFeed);
		IOUtils.closeQuietly(deltaNeutralFeed);
		IOUtils.closeQuietly(combinedFeed);
	}
}
