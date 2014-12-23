package com.saturn.api.feed.deltaneutral;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.saturn.App;
import com.saturn.Configuration;
import com.saturn.api.core.Instrument;
import com.saturn.api.core.Option;
import com.saturn.api.core.OptionType;
import com.saturn.api.feed.Feed;
import com.saturn.api.feed.Timestamped;

public class DeltaNeutralFeed extends Feed<Option> {
	public static final String FEED_DIR_PROPERTY_KEY = "saturn.feed.deltaneutral.dir";

	private static final DateTimeFormatter EXPIRATION_DATE_PATTERN = DateTimeFormat.forPattern("MM/dd/yyyy");
	private static final DateTimeFormatter TIMESTAMP_DATE_PATTERN = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ss a");

	private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader("UnderlyingSymbol", "UnderlyingPrice",
			"Exchange", "OptionSymbol", "Blank", "Type", "Expiration", "DataDate", "Strike", "Last", "Bid", "Ask",
			"Volume", "OpenInterest", "IV", "Delta", "Gamma", "Theta", "Vega", "Alias");

	private final File dataFile;
	private CSVParser csvParser;
	private Iterator<CSVRecord> recordIterator;

	public DeltaNeutralFeed(Properties properties) {
		final String feedDir = properties.getProperty(FEED_DIR_PROPERTY_KEY);

		DownloadUtils.downloadDataFile(feedDir);
		this.dataFile = new File(feedDir, "options.csv");
		try {
			this.csvParser = new CSVParser(new FileReader(this.dataFile), CSV_FORMAT);
			this.recordIterator = this.csvParser.iterator();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	// main method for download / unzip the latest file
	public static void main(String[] args) {
		if (args.length == 1 && "--help".equals(args[0])) {
			System.out.println("Downloads the latest zip file from Delta neutral.");
			System.out.println("Download dir set in " + App.PROPERTIES_FILE + "via " + FEED_DIR_PROPERTY_KEY);
		}
		final String feedDir = Configuration.loadProperties().getProperty(FEED_DIR_PROPERTY_KEY);
		DownloadUtils.downloadDataFile(feedDir);
	}

	@Override
	public boolean hasNext() {
		return this.recordIterator.hasNext();
	}

	@Override
	public Timestamped<Option> next() {
		final CSVRecord record = this.recordIterator.next();
		final Instrument underlying = new Instrument(Float.valueOf(record.get("UnderlyingPrice")), //
				Float.valueOf(record.get("UnderlyingPrice")), //
				record.get("UnderlyingSymbol"));

		final Option option = new Option(Float.valueOf(record.get("Bid")), //
				Float.valueOf(record.get("Ask")),//
				record.get("OptionSymbol"), //
				OptionType.fromString(record.get("Type")), //
				EXPIRATION_DATE_PATTERN.parseLocalDate(record.get("Expiration")),
				Float.parseFloat(record.get("Strike")), underlying);

		final long timestamp = TIMESTAMP_DATE_PATTERN.parseDateTime(record.get("DataDate")).getMillis();
		return new Timestamped<Option>(option, timestamp);
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(this.csvParser);
	}
}
