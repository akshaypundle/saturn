package com.saturn.strategy;

import static com.saturn.feed.Feeds.deltaNeutral;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.saturn.Configuration;
import com.saturn.api.Option;
import com.saturn.api.Timestamped;
import com.saturn.filter.Filters;

public class Butterfly {

	private static Pattern IGNORE_LINE = Pattern.compile("\\s*#");
	private static Pattern BLANK_LINE = Pattern.compile("^\\s*$");
	private static Comparator<Option> OPTION_STRIKE_COMPARATOR = new Comparator<Option>() {

		@Override
		public int compare(Option o1, Option o2) {
			return Float.compare(o1.getStrike(), o2.getStrike());
		}
	};

	public static final Iterator<Object> createIterator() {
		final Predicate<Timestamped<Option>> filter = createButterflyFilter();
		final Iterator<Timestamped<Option>> butterflyCandidates = Iterators.filter(deltaNeutral(), filter);
		final Map<String, List<Option>> butterflyCandidateMap = Maps.newHashMap();
		while (butterflyCandidates.hasNext()) {
			final Option next = butterflyCandidates.next().getObject();
			final String symbolAndExpiry = next.getUnderlying().getSymbol() + next.getExpiry().toString();
			if (!butterflyCandidateMap.containsKey(symbolAndExpiry)) {
				butterflyCandidateMap.put(symbolAndExpiry, Lists.<Option> newArrayList());
			}
			butterflyCandidateMap.get(symbolAndExpiry).add(next);
		}

		final ArrayList<Object> butterflyList = Lists.newArrayList();
		for (final Entry<String, List<Option>> entry : butterflyCandidateMap.entrySet()) {
			butterflyList.addAll(toButterfly(entry.getValue()));
		}

		return butterflyList.iterator();
	}

	private static Predicate<Timestamped<Option>> createButterflyFilter() {
		// start with calls that expire in the next 11 days with positive bid
		// and ask, and strike price as configured via config file
		Predicate<Timestamped<Option>> filter = Predicates.<Timestamped<Option>> and(
				Filters.expiry(LocalDate.now(), LocalDate.now().plusDays(11)), Filters.calls());
		filter = Predicates.and(filter, Predicates.and(Filters.positiveBid(), Filters.positiveAsk()));
		filter = Predicates.and(filter, Filters.strike());

		// if a tickers file is configured, add it to the filters
		final String tickersFile = Configuration.getProperties().getProperty("saturn.butterfly.filter.tickersfile");
		if (tickersFile != null) {
			try {
				final List<String> tickerLines = FileUtils.readLines(new File(tickersFile));
				final List<String> tickerCollection = Lists.newArrayList();
				for (String ticker : tickerLines) {
					ticker = ticker.trim();
					if (!IGNORE_LINE.matcher(ticker).find() && !BLANK_LINE.matcher(ticker).matches()) {
						tickerCollection.add(ticker);
					}
				}
				if (!tickerCollection.isEmpty()) {
					filter = Predicates.and(filter, Filters.tickers(tickerCollection));
				}
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return filter;
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
		final List<Float> strikes = Lists.newArrayList(options.get(0).getStrike());
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
		final float midSetup = 2 * mid.getBid();
		for (int i = 0; i < midIndex; i++) {
			final int highIndex = midIndex + (midIndex - i);
			if (highIndex > options.size() - 1) {
				continue;
			}
			final Option low = options.get(i);
			final Option high = options.get(highIndex);

			final float cost = low.getAsk() + high.getAsk() - midSetup;
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
