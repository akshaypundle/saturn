package com.saturn;

import static com.saturn.JsonWriter.newWriter;
import static com.saturn.feed.Feeds.combined;
import static com.saturn.feed.Feeds.deltaNeutral;
import static com.saturn.feed.Feeds.yahoo;
import static com.saturn.filter.Filters.expiry;
import static com.saturn.filter.Filters.strike;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.saturn.api.Feed;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;
import com.saturn.feed.CombinedFeed;
import com.saturn.feed.Combiners;
import com.saturn.filter.Filters;
import com.saturn.strategy.CoveredCall;
import com.saturn.strategy.ShortPuts;

public class App {
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
					"coveredCallDownProtect", (option.getStrike() - underlying) / underlying);
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

	private static Comparator<Option> OPTION_STRIKE_COMPARATOR = new Comparator<Option>() {

		@Override
		public int compare(Option o1, Option o2) {
			return Float.compare(o1.getStrike(), o2.getStrike());
		}
	};

	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		final Feed<Option> yahoo = yahoo();
		final Feed<Option> deltaNeutral = deltaNeutral();

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

		newWriter("/Users/apundle/repo/code/saturn/src/web/options.js", Iterators.transform(rawOptions, TO_OPTIONS))
				.write();
		newWriter("/Users/apundle/repo/code/saturn/src/web/shortPuts.js", Iterators.transform(shortPuts, TO_SHORT_PUTS))
				.write();
		newWriter("/Users/apundle/repo/code/saturn/src/web/coveredCalls.js",
				Iterators.transform(coveredCalls, TO_COVERED_CALL)).write();

		filter = Predicates.<Timestamped<Option>> and(Filters.expiry(LocalDate.now(), LocalDate.now().plusDays(11)),
				Filters.calls());
		filter = Predicates.and(filter, Predicates.and(Filters.positiveBid(), Filters.positiveAsk()));

		// calculate butterflies
		// start with calls that expire in the next 11 days
		Iterator<Timestamped<Option>> butterflyCandidates = Iterators.filter(deltaNeutral(), filter);
		// limit to calls within 20%
		butterflyCandidates = Iterators.filter(butterflyCandidates, Filters.strike(0.80f, 1.20f));
		final Map<String, List<Option>> butterflyCandidateMap = Maps.newHashMap();
		while (butterflyCandidates.hasNext()) {
			final Option next = butterflyCandidates.next().getObject();
			final String symbol = next.getUnderlying().getSymbol();
			if (!butterflyCandidateMap.containsKey(symbol)) {
				butterflyCandidateMap.put(symbol, Lists.<Option> newArrayList());
			}
			butterflyCandidateMap.get(symbol).add(next);
		}

		final ArrayList<Object> butterflyList = Lists.newArrayList();
		for (final Entry<String, List<Option>> entry : butterflyCandidateMap.entrySet()) {
			butterflyList.addAll(toButterfly(entry.getValue()));
		}

		newWriter("/Users/apundle/repo/code/saturn/src/web/butterfly.js", butterflyList.iterator()).write();

		IOUtils.closeQuietly(yahoo);
		IOUtils.closeQuietly(deltaNeutral);
		IOUtils.closeQuietly(combinedFeed);
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
	}

	private static List<Map<String, Object>> toButterfly(List<Option> options) {
		if (options.size() < 3) {
			return Lists.newArrayList();
		}
		final List<Map<String, Object>> ret = Lists.newArrayList();

		Collections.sort(options, OPTION_STRIKE_COMPARATOR);
		final float underlyingPrice = options.get(0).getUnderlying().getBid();
		final float difference = options.get(1).getStrike() - options.get(0).getStrike();
		int lastSelectedIndex = 0; // 0 is always in
		final List<Option> prunedOptions = Lists.newArrayList(options.get(0));
		final List<Float> strikes = Lists.newArrayList();
		for (int i = 1; i < options.size(); i++) {
			final Option option = options.get(i);
			final float diff = option.getStrike() - options.get(lastSelectedIndex).getStrike();
			if (almostSame(diff, difference)) {
				prunedOptions.add(option);
				lastSelectedIndex = i;
				strikes.add(option.getStrike());
			}
		}

		int insertionPoint = Collections.binarySearch(strikes, underlyingPrice);

		if (insertionPoint > 0) { // exact match
			ret.addAll(toButterfly(prunedOptions, insertionPoint));
		} else {
			insertionPoint = -insertionPoint - 1;
			ret.addAll(toButterfly(prunedOptions, insertionPoint - 1));
			ret.addAll(toButterfly(prunedOptions, insertionPoint));
		}

		return ret;
	}

	private static List<Map<String, Object>> toButterfly(List<Option> options, int midIndex) {

		if ((midIndex >= options.size() - 1) || midIndex <= 0) {
			return Lists.newArrayList();
		}
		final List<Map<String, Object>> ret = Lists.newArrayList();
		final Option mid = options.get(midIndex);
		final float midSetup = 2 * mid.getAsk();
		for (int i = 0; i < midIndex; i++) {
			final int highIndex = midIndex + (midIndex - i);
			if (highIndex > options.size() - 1) {
				continue;
			}
			final Option low = options.get(i);
			final Option high = options.get(highIndex);

			final float cost = low.getBid() + high.getBid() - midSetup;
			final float spread = mid.getStrike() - low.getStrike();
			final float zero = (spread - (spread * cost) / (spread + cost)) / mid.getStrike();
			final Map<String, Object> map = Maps.newHashMap();
			map.put("option", mid);
			map.put("low", low.getStrike());
			map.put("high", high.getStrike());
			map.put("setup", cost);
			map.put("roi", spread - cost);
			if (!Float.isInfinite(zero) && !Float.isNaN(zero)) {
				map.put("zero", zero);
			}
			ret.add(map);

		}
		return ret;
	}

	private static boolean almostSame(float a, float b) {
		return Math.abs(a - b) < 0.0001;
	}
}
