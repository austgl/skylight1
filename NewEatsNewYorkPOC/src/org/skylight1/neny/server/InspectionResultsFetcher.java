package org.skylight1.neny.server;

import static java.lang.String.format;
import static java.util.logging.Level.WARNING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.skylight1.neny.model.Address;
import org.skylight1.neny.model.Borough;
import org.skylight1.neny.model.Grade;
import org.skylight1.neny.model.Restaurant;

public class InspectionResultsFetcher {
	private static final Logger LOGGER = Logger.getLogger(InspectionResultsFetcher.class.getName());

	private final static String EXPECTED_HEADER_FOR_WEB_EXTRACT =
			"\"CAMIS\",\"DBA\",\"BORO\",\"BUILDING\",\"STREET\",\"ZIPCODE\",\"PHONE\",\"CUISINECODE\",\"INSPDATE\",\"ACTION\",\"VIOLCODE\",\"SCORE\",\"CURRENTGRADE\",\"GRADEDATE\",\"RECORDDATE\"";

	private final static Pattern PATTERN =
			Pattern.compile("\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\"");

	public Map<String, Restaurant> processFile(String aURLString) throws MalformedURLException, IOException {
		LOGGER.info(format("Starting to process URL %s", aURLString));

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final Map<String, Restaurant> result = new HashMap<>();
		final long startTime = System.currentTimeMillis();
		final URL url = new URL(aURLString);
		final URLConnection connection = url.openConnection();
		final InputStream inputStream = connection.getInputStream();
		try {
			final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().equals("WebExtract.txt")) {
					final BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream));

					// skip over the header
					final String header = br.readLine();
					if (!header.equals(EXPECTED_HEADER_FOR_WEB_EXTRACT)) {
						throw new RuntimeException(format("Received unexpected header for WebExtract.txt: %s", header));
					}

					String line;
					while ((line = br.readLine()) != null) {
						try {
							final Matcher matcher = PATTERN.matcher(line);
							if (!matcher.matches()) {
								throw new RuntimeException(format("Couldn't match WebExtract.txt record to pattern: %s", line));
							}

							final String camis = matcher.group(1);
							final Date gradeDate;
							final String gradeDateAtString = matcher.group(14);
							if (gradeDateAtString.isEmpty()) {
								gradeDate = null;
							} else {
								gradeDate = simpleDateFormat.parse(gradeDateAtString);
							}

							// if this restaurant is not in the result yet, or else is, but this has a more recent
							// grade, then store it
							if (!result.containsKey(camis) || isLaterThan(gradeDate, result.get(camis).getGradeDate())) {
								final String doingBusinessAs = matcher.group(2).trim();
								final Borough borough = Borough.findByCode(Integer.parseInt(matcher.group(3)));
								final String building = matcher.group(4).trim();
								final String street = matcher.group(5).trim();
								final String zipCode = matcher.group(6);
								final String phoneNumber = matcher.group(7);
								final String cuisine = matcher.group(8);
								final Grade currentGrade = Grade.findByCode(matcher.group(13));
								final Restaurant restaurant =
										new Restaurant(camis, doingBusinessAs, borough, new Address(building, street, zipCode), phoneNumber, cuisine, currentGrade, gradeDate);
								result.put(camis, restaurant);
							}
						} catch (Exception e) {
							LOGGER.log(WARNING, format("Unable to process record %s", line), e);
						}
					}
				}
			}
		} finally {
			inputStream.close();
		}

		final long endTime = System.currentTimeMillis();
		LOGGER.info(format("Finished processing URL %s in %,d ms", aURLString, (endTime - startTime)));

		return result;
	}

	private boolean isLaterThan(final Date aNewDate, final Date anOldDate) {
		// null date is never later than any other date, even a null one
		if (aNewDate == null) {
			return false;
		}

		// any non-null date is newer than a null date
		if (anOldDate == null) {
			return true;
		}

		return aNewDate.after(anOldDate);
	}
}
