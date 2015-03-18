package com.saturn.feed;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;
import com.saturn.api.Feed;
import com.saturn.api.Timestamped;

public class CombinedFeed<T> extends Feed<T> {

	private final LinkedHashMap<Object, Timestamped<T>> feedData;
	private final Iterator<Timestamped<T>> feedIterator;

	public CombinedFeed(Iterator<Timestamped<T>> feed1, Iterator<Timestamped<T>> feed2, Combiner<T> combiner) {
		Validate.notNull(feed1);
		Validate.notNull(feed2);
		Validate.notNull(combiner);
		this.feedData = Maps.newLinkedHashMap();

		while (feed1.hasNext()) {
			final Timestamped<T> next = feed1.next();
			if (next != null) {
				this.feedData.put(combiner.toKey(next), next);
			}
		}

		while (feed2.hasNext()) {
			final Timestamped<T> next = feed2.next();
			if (next != null) {
				final Object key = combiner.toKey(next);
				final Timestamped<T> existing = this.feedData.get(key);
				if (existing != null) {
					this.feedData.put(key, combiner.choose(next, existing));
				} else {
					this.feedData.put(key, next);
				}
			}
		}

		this.feedIterator = this.feedData.values().iterator();
	}

	@Override
	public boolean hasNext() {
		return this.feedIterator.hasNext();
	}

	@Override
	public Timestamped<T> next() {
		return this.feedIterator.next();
	}

	@Override
	public void close() throws IOException {
		this.feedData.clear();
	}

	public Map<Object, Timestamped<T>> getFeedData() {
		return Collections.unmodifiableMap(this.feedData);
	}
}
