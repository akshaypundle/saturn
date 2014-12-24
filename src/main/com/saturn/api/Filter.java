package com.saturn.api;

public interface Filter<T> {
	boolean retain(T value);
}
