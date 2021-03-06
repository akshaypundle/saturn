package com.saturn;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public final class Configuration {
	public static final String PROPERTIES_FILE = "saturn.properties";
	private static final Properties properties = loadProperties();

	public static Properties getProperties() {
		return properties;
	}

	private static final Properties loadProperties() {

		File curDir = new File(".");
		while (curDir != null) {
			final Properties properties = loadProperties(curDir);
			if (properties != null) {
				return properties;
			} else {
				curDir = curDir.getParentFile();
			}
		}
		throw new IllegalStateException("Cannot find saturn.properties");
	}

	private static final Properties loadProperties(File propertiesDir) {
		if (propertiesDir.exists() && propertiesDir.isDirectory()) {
			if (!new File(propertiesDir, PROPERTIES_FILE).exists()) {
				propertiesDir = new File(propertiesDir, "conf");
			}
			final File propertiesFile = new File(propertiesDir, PROPERTIES_FILE);
			if (propertiesFile.exists()) {
				try {
					final Properties properties = new Properties();
					properties.load(FileUtils.openInputStream(new File(propertiesDir, PROPERTIES_FILE)));
					return properties;
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	private Configuration() {
		// utils class
	}
}
