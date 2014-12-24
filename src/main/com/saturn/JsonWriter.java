package com.saturn;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saturn.api.Timestamped;

public class JsonWriter<T extends Timestamped<?>> {
	private static final JsonFactory FACTORY = new JsonFactory();

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final File dataFile;
	private final Iterator<T> feed;

	private JsonWriter(String filename, Iterator<T> feed) {
		this.dataFile = new File(filename);
		this.feed = Validate.notNull(feed);
		FACTORY.setCodec(MAPPER);
	}

	public void write() {
		try {
			final JsonGenerator generator = FACTORY.createGenerator(this.dataFile, JsonEncoding.UTF8);
			generator.writeStartArray();
			while (this.feed.hasNext()) {
				generator.writeObject(this.feed.next().getObject());
			}
			generator.writeEndArray();
			generator.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final <T extends Timestamped<?>> JsonWriter<T> newWriter(String filename, Iterator<T> feed) {
		return new JsonWriter<>(filename, feed);
	}
}
