package com.saturn.api;

import org.joda.time.LocalDateTime;

import com.google.common.base.MoreObjects;

public class Timestamped<T> {
	private final T object;
	private final long timestamp;

	public Timestamped(T object, long timestamp) {
		this.object = object;
		this.timestamp = timestamp;
	}

	public T getObject() {
		return this.object;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("object", this.object) //
				.add("timestamp", new LocalDateTime(this.timestamp)) //
				.toString();
	}
}
