package com.saturn.api.feed;

public interface Filter<T> {
	boolean retain(T value);
}
