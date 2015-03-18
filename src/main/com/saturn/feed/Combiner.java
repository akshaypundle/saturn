package com.saturn.feed;

import com.saturn.api.Timestamped;

public interface Combiner<T> {

	Object toKey(Timestamped<T> value);

	Timestamped<T> choose(Timestamped<T> value1, Timestamped<T> value2);
}
