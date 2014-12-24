package com.saturn.filter;

import com.saturn.api.Filter;

class AndFilter<T> implements Filter<T> {

	private final Filter<T>[] filters;
	private long trues, falses;

	@SafeVarargs
	AndFilter(Filter<T>... filters) {
		this.filters = filters;
		this.trues = 0;
		this.falses = 0;
	}

	@Override
	public boolean retain(T value) {
		for (final Filter<T> filter : this.filters) {
			if (!filter.retain(value)) {
				this.falses++;
				return false;
			}
		}
		this.trues++;
		return true;
	}
}
