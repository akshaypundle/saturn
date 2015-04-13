package com.saturn;

import static com.saturn.JsonWriter.newWriter;
import static com.saturn.feed.Feeds.combined;
import static com.saturn.feed.Feeds.deltaNeutral;
import static com.saturn.feed.Feeds.yahoo;
import static com.saturn.filter.Filters.expiry;
import static com.saturn.filter.Filters.strike;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.saturn.api.Feed;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;
import com.saturn.feed.CombinedFeed;
import com.saturn.feed.Combiners;
import com.saturn.filter.Filters;
import com.saturn.strategy.Butterfly;
import com.saturn.strategy.CoveredCall;
import com.saturn.strategy.ShortPuts;

public class App {
	public static final String OUTPUT_DIR_PROPERTY = "saturn.output.dir";

	private static CoveredCall COVERED_CALL_ROI = new CoveredCall();
	private static ShortPuts SHORT_PUT_ROI = new ShortPuts();
	private static Function<Timestamped<Option>, Option> TO_OPTIONS = new Function<Timestamped<Option>, Option>() {

		@Override
		public Option apply(Timestamped<Option> input) {
			return input.getObject();
		}

	};
	private static Function<Timestamped<Option>, Map<String, Object>> TO_COVERED_CALL = new Function<Timestamped<Option>, Map<String, Object>>() {

		@Override
		public Map<String, Object> apply(Timestamped<Option> input) {
			final Option option = input.getObject();
			final float underlying = option.getUnderlying().getAsk();
			return ImmutableMap.<String, Object> of(//
					"option", option, //
					"coveredCallRoi", COVERED_CALL_ROI.roi(option), //
					"coveredCallDownProtect", (underlying - option.getStrike()) / underlying);
		}

	};

	private static Function<Timestamped<Option>, Map<String, Object>> TO_SHORT_PUTS = new Function<Timestamped<Option>, Map<String, Object>>() {

		@Override
		public Map<String, Object> apply(Timestamped<Option> input) {
			final Option option = input.getObject();
			final float underlying = option.getUnderlying().getAsk();
			return ImmutableMap.<String, Object> of(//
					"option", option, //
					"shortPutRoi", SHORT_PUT_ROI.roi(option), //
					"shortPutDownProtect", (underlying - option.getStrike()) / option.getStrike());
		}

	};

	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		final Feed<Option> yahoo = yahoo();
		final Feed<Option> deltaNeutral = deltaNeutral();

		// figure out the output directory
		String outputDir = Configuration.getProperties().getProperty(OUTPUT_DIR_PROPERTY);
		if (StringUtils.isEmpty(outputDir)) {
			outputDir = ".";
		}
		outputDir = FilenameUtils.removeExtension(outputDir) + "/";

		Predicate<Timestamped<Option>> filter = Predicates.and(strike(), expiry());
		filter = Predicates.and(Predicates.notNull(), filter);

		final Iterator<Timestamped<Option>> yahooFiltered = Iterators.filter(yahoo, filter);
		final Iterator<Timestamped<Option>> deltaNeutralFiltered = Iterators.filter(deltaNeutral, filter);
		final CombinedFeed<Option> combinedFeed = combined(deltaNeutralFiltered, yahooFiltered,
				Combiners.latestTimeCopyGreeks());

		final Collection<Timestamped<Option>> combinedFeedData = combinedFeed.getFeedData().values();

		final Iterator<Timestamped<Option>> rawOptions = Iterators.filter(combinedFeedData.iterator(),
				Filters.expiry(LocalDate.now(), LocalDate.now().plusWeeks(2)));
		final Iterator<Timestamped<Option>> shortPuts = Iterators.filter(combinedFeedData.iterator(),
				Filters.singleValueStrategy(SHORT_PUT_ROI, 0, 100));
		final Iterator<Timestamped<Option>> coveredCalls = Iterators.filter(combinedFeedData.iterator(),
				Filters.singleValueStrategy(COVERED_CALL_ROI, 0, 100));

		newWriter(outputDir + "options.js", Iterators.transform(rawOptions, TO_OPTIONS)).write();
		newWriter(outputDir + "shortPuts.js", Iterators.transform(shortPuts, TO_SHORT_PUTS)).write();
		newWriter(outputDir + "coveredCalls.js", Iterators.transform(coveredCalls, TO_COVERED_CALL)).write();
		newWriter(outputDir + "butterfly.js", Butterfly.createIterator()).write();

		IOUtils.closeQuietly(yahoo);
		IOUtils.closeQuietly(deltaNeutral);
		IOUtils.closeQuietly(combinedFeed);
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
	}
}
