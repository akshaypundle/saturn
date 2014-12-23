package com.saturn.api.feed.deltaneutral;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.saturn.App;

class DownloadUtils {

	public static final void downloadDataFile(String feedDir) {
		if (feedDir == null) {
			throw new IllegalStateException("Feed directory not specified. Normally, this property is specified via "
					+ DeltaNeutralFeed.FEED_DIR_PROPERTY_KEY + " in " + App.PROPERTIES_FILE);
		}
		final FTPClient client = new FTPClient();
		final File feedDirectory = new File(feedDir);

		if (!feedDirectory.exists()) {
			feedDirectory.mkdir();
		}

		try {
			client.connect("eodftp.deltaneutral.com");
			client.login("akshaypundle", "password");
			client.enterLocalPassiveMode();
			client.changeWorkingDirectory("dbupdate");
			final FTPFile[] ftpFiles = client.listFiles();
			String fileToGet = null;
			for (final FTPFile ftpFile : ftpFiles) {
				final String name = ftpFile.getName();
				if (name.matches("options_.*zip")) {
					if (fileToGet == null || name.compareTo(fileToGet) > 0) {
						fileToGet = name;
					}
				}
			}
			if (fileToGet == null) {
				throw new IllegalStateException("No files found on deltaneutral site.");
			}

			client.setFileType(FTP.BINARY_FILE_TYPE);

			final File zip = new File(feedDirectory, fileToGet);
			// no need to download if it already exists
			if (zip.exists()) {
				unzip(zip, feedDirectory);
				return;
			}

			// if we're downloading a new zip, clean out the feed directory
			FileUtils.cleanDirectory(feedDirectory);

			// download the zip
			final FileOutputStream zipOutput = new FileOutputStream(zip);
			client.retrieveFile(fileToGet, zipOutput);
			IOUtils.closeQuietly(zipOutput);

			unzip(zip, feedDirectory);
			return;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (client.isConnected()) {
				try {
					client.logout();
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private static final void unzip(File zip, File directory) {

		try {
			// unzip the zip file
			final ZipFile zipFile = new ZipFile(zip);
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();

			// extract and save the csv files
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				final String name = entry.getName();
				final String baseName = FilenameUtils.getBaseName(name);
				final String extension = FilenameUtils.getExtension(name);
				final String nameWithoutDate = baseName.substring(0, baseName.length() - 9) + "." + extension;
				final File unzippedFile = new File(directory, nameWithoutDate);
				final FileOutputStream unzippedFileOutputStream = new FileOutputStream(unzippedFile);
				IOUtils.copy(zipFile.getInputStream(entry), unzippedFileOutputStream);
				IOUtils.closeQuietly(unzippedFileOutputStream);
			}

			IOUtils.closeQuietly(zipFile);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
