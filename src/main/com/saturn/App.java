package com.saturn;

import com.saturn.api.feed.deltaneutral.DeltaNeutralFeed;

public class App {
	public static final String PROPERTIES_FILE = "saturn.properties";

	public static void main(String[] args) {
		final DeltaNeutralFeed feed = new DeltaNeutralFeed(Configuration.loadProperties());
		System.out.println(feed.next());
		feed.close();
	}
}
