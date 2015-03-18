package com.saturn.feed.yahoo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.CheckForNull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.saturn.Configuration;
import com.saturn.api.Feed;
import com.saturn.api.Instrument;
import com.saturn.api.Option;
import com.saturn.api.OptionType;
import com.saturn.api.Timestamped;

public class YahooFeed extends Feed<Option> {
	private static final String FEED_DIR_PROPERTY_KEY = "saturn.feed.yahoo.dir";
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	private final File[] csvFiles;
	private int nextFileIndex;

	@CheckForNull
	private CSVParser currentFileParser;
	@CheckForNull
	private Iterator<CSVRecord> currentRecordIterator;

	public YahooFeed() {
		final Properties properties = Configuration.getProperties();
		final File feedDir = new File(properties.getProperty(FEED_DIR_PROPERTY_KEY) + "/data");
		if (!feedDir.exists() && !feedDir.isDirectory()) {
			throw new IllegalStateException("feed directory not found: " + feedDir);
		}

		this.csvFiles = feedDir.listFiles();
		this.nextFileIndex = 0;
	}

	@Override
	public boolean hasNext() {
		if (currentFileHasNext()) {
			return true;
		} else if (hasNextFile()) {
			moveToNextFile();
			return currentFileHasNext();
		}
		return false;
	}

	@Override
	@CheckForNull
	public Timestamped<Option> next() {
		if (!hasNext()) {
			return null;
		}

		final CSVRecord record = this.currentRecordIterator.next();
		try {
			final Instrument underlying = new Instrument(Float.valueOf(record.get("Underlying_Price")), //
					Float.valueOf(record.get("Underlying_Price")), //
					record.get("Underlying"));

			final Option option = new Option(
					Float.valueOf(record.get("Bid")), //
					Float.valueOf(record.get("Ask")),//
					record.get("Symbol"), //
					OptionType.fromString(record.get("Type")), //
					TIME_PATTERN.parseLocalDate(record.get("Expiry")), Float.parseFloat(record.get("Strike")),
					underlying, null);

			final long timestamp = TIME_PATTERN.parseDateTime(record.get("Quote_Time")).getMillis();
			return new Timestamped<Option>(option, timestamp);
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		if (this.currentFileParser != null) {
			this.currentFileParser.close();
		}
		this.currentFileParser = null;
		this.currentRecordIterator = null;
		this.nextFileIndex = -1;
	}

	private boolean currentFileHasNext() {
		if (this.currentRecordIterator != null) {
			return this.currentRecordIterator.hasNext();
		}
		return false;
	}

	private boolean hasNextFile() {
		return this.nextFileIndex < this.csvFiles.length;
	}

	private void moveToNextFile() {
		if (this.currentFileParser != null) {
			IOUtils.closeQuietly(this.currentFileParser);
			this.currentFileParser = null;
			this.currentRecordIterator = null;
		}

		try {
			this.currentFileParser = new CSVParser(new FileReader(this.csvFiles[this.nextFileIndex++]),
					CSVFormat.DEFAULT.withHeader());
			this.currentRecordIterator = this.currentFileParser.iterator();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
