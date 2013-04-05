package org.skylight1.neny.server;

import static java.lang.String.format;
import static java.util.logging.Level.WARNING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

	private static final int GRP_CAMIS = 1;

	private static final int GRP_BUSINESS_NAME = 2;

	private static final int GRP_BOROUGH = 3;

	private static final int GRP_BUILDING = 4;

	private static final int GRP_STREET = 5;

	private static final int GRP_ZIPCODE = 6;

	private static final int GRP_PHONE = 7;

	private static final int GRP_CUISINE = 8;

	private static final int GRP_INSPDATE = 9;

	private static final int GRP_CURRENTGRADE = 13;

	private static final int GRP_GRADEDATE = 14;

	private static final Logger LOGGER = Logger.getLogger(InspectionResultsFetcher.class.getName());

	private final static String EXPECTED_HEADER_FOR_WEB_EXTRACT =
			"\"CAMIS\",\"DBA\",\"BORO\",\"BUILDING\",\"STREET\",\"ZIPCODE\",\"PHONE\",\"CUISINECODE\",\"INSPDATE\",\"ACTION\",\"VIOLCODE\",\"SCORE\",\"CURRENTGRADE\",\"GRADEDATE\",\"RECORDDATE\"";

	// regex pattern used to parse entries in the WebExtract.txt file
	private final static Pattern PATTERN =
			Pattern.compile("\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\"");

	// This SimpleDateFormat instance is used to interpret the Grade Date string
	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public Collection<Restaurant> processFile(String aURLString, Date aCutoffDate) throws MalformedURLException, IOException {

		LOGGER.info(format("Starting to process URL %s", aURLString));

		final long startTime = System.currentTimeMillis();

		final Set<String> oldRestaurants = new HashSet<String>();

		// This is the HashMap that will be returned from this method
		final Map<String, Restaurant> result = new HashMap<String, Restaurant>();

		// Keep track of when we've started
		final URL url = new URL(aURLString);
		final URLConnection connection = url.openConnection();

		// optionally set our timeouts, if this is a URL.
		if (connection instanceof HttpURLConnection) {
			LOGGER.info("setting timeout to zero");
			((HttpURLConnection) connection).setConnectTimeout(0);
			((HttpURLConnection) connection).setReadTimeout(0);
		}

		// Start reading from our source
		final InputStream inputStream = connection.getInputStream();
		try {

			// our input file is in ZIP format, so we'll be looking for a file
			// called
			// "WebExtract.txt" in this zip.
			final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			ZipEntry zipEntry;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {

				LOGGER.info(format("processing zip entry %s", zipEntry.getName()));

				// If it's "WebExtract.txt", we'll open a new BufferedReader to
				// read that file.

				if (zipEntry.getName().contains("WebExtract.txt")) {
					// if (zipEntry.getName().equals("WebExtract.txt")) {
					final BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream));

					// Check to make sure the header looks familiar.
					final String header = br.readLine();
					if (!header.equals(EXPECTED_HEADER_FOR_WEB_EXTRACT)) {
						throw new RuntimeException(format("Received unexpected header for WebExtract.txt: %s", header));
					}

					String line;

					// Iterate through the remaining lines in our WebExtract.txt
					// file
					while ((line = br.readLine()) != null) {

						// get outta here if our input line does not match our
						// expected pattern.
						try {
							final Matcher matcher = PATTERN.matcher(line);
							if (!matcher.matches()) {
								throw new RuntimeException(format("Couldn't match WebExtract.txt record to pattern: %s", line));
							}

							// Now we extract the inspectionDate and camis from
							// our input file:

							final String camis = matcher.group(GRP_CAMIS);

							// if the camis value is in our "oldRestaurants"
							// list, it means that
							// it's been around for a while - at least around
							// since before our "aCutoffDate"
							// value.

							if (!oldRestaurants.contains(camis)) {

								final String inspectionDateString = matcher.group(GRP_INSPDATE);

								final Date inspectionDate = convertStringToDate(inspectionDateString);

								if (inspectionDate.before(aCutoffDate)) {
									oldRestaurants.add(camis);
									result.keySet().remove(camis);
								} else {

									Date currentGradeDate = null;

									final String gradeDateAtString = matcher.group(GRP_GRADEDATE);
									if (gradeDateAtString.isEmpty()) {
										currentGradeDate = null;
									} else {
										currentGradeDate = simpleDateFormat.parse(gradeDateAtString);
									}

									final String doingBusinessAs = matcher.group(GRP_BUSINESS_NAME).trim();
									final Borough borough = Borough.findByCode(Integer.parseInt(matcher.group(GRP_BOROUGH)));
									final String building = matcher.group(GRP_BUILDING).trim();
									final String street = matcher.group(GRP_STREET).trim();
									final String zipCode = matcher.group(GRP_ZIPCODE);
									final String phoneNumber = matcher.group(GRP_PHONE);
									final String cuisine = matcher.group(GRP_CUISINE);
									final Grade currentGrade = Grade.findByCode(matcher.group(GRP_CURRENTGRADE));

									final Restaurant restaurant =
											new Restaurant(camis, doingBusinessAs, borough, new Address(building, street, zipCode), phoneNumber, cuisine, currentGrade, currentGradeDate, inspectionDate);

									final Restaurant existingRestaurant = result.get(camis);

									if (existingRestaurant == null || existingRestaurant.getInspectionDate() == null
											|| (inspectionDate != null && existingRestaurant.getInspectionDate().before(inspectionDate))) {
										result.put(camis, restaurant);
									}

								}

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

		return result.values();
	}

	private Date convertStringToDate(final String aDateString) throws ParseException {
		final Date resultDate;
		if (aDateString.isEmpty()) {
			resultDate = null;
		} else {
			resultDate = simpleDateFormat.parse(aDateString);
		}
		return resultDate;
	}

}
