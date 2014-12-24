package com.saturn.feed;

import java.io.IOException;

import javax.annotation.CheckForNull;

import org.apache.commons.lang3.Validate;

import com.saturn.api.Feed;
import com.saturn.api.Filter;
import com.saturn.api.Timestamped;

public final class FilteredFeed<T> extends Feed<T> {
	private final Feed<T> feed;
	private final Filter<T> filter;

	public FilteredFeed(Feed<T> feed, Filter<T> filter) {
		this.feed = Validate.notNull(feed);
		this.filter = Validate.notNull(filter);
	}

	@Override
	public boolean hasNext() {
		return this.feed.hasNext();
	}

	@Override
	@CheckForNull
	public Timestamped<T> next() {
		Timestamped<T> value = null;
		while (value == null && this.feed.hasNext()) {
			value = this.feed.next();
			if (value != null && !this.filter.retain(value.getObject())) {
				value = null;
			}
		}
		return value;
	}

	@Override
	public void close() throws IOException {
		this.feed.close();
	}
}
