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

import com.sun.org.apache.bcel.internal.generic.INEG;

public class SingleFileInspectionResultsFetcher {
	private static final Logger LOGGER = Logger.getLogger(SingleFileInspectionResultsFetcher.class.getName());

	private final static String EXPECTED_HEADER_FOR_WEB_EXTRACT =
			"\"CAMIS\",\"DBA\",\"BORO\",\"BUILDING\",\"STREET\",\"ZIPCODE\",\"PHONE\",\"CUISINECODE\",\"INSPDATE\",\"ACTION\",\"VIOLCODE\",\"SCORE\",\"CURRENTGRADE\",\"GRADEDATE\",\"RECORDDATE\"";

	private final static Pattern PATTERN =
			Pattern.compile("\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\",\"((?:[^\"]|\"(?!,))*)\"");

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("resource")
	public Collection<Restaurant> processFile(String aURLString, Date aCutoffDate) throws MalformedURLException, IOException {
		LOGGER.info(format("Starting to process URL %s", aURLString));

		final long startTime = System.currentTimeMillis();

		final Set<String> oldRestaurants = new HashSet<String>();
		final Map<String, Restaurant> result = new HashMap<String, Restaurant>();

		final URL url = new URL(aURLString);
		final URLConnection connection = url.openConnection();
		final InputStream inputStream = connection.getInputStream();
		try {
			final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith("WebExtract.txt")) {
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

							// if the restaurant is already known to be one that was inspected a long time ago, then
							// skip it
							if (!oldRestaurants.contains(camis)) {

								// if it wasn't known before, but based on this record can be seen to have been
								// inspected a long time ago, then skip it and remove it if it was already in the result
								final Date inspectionDate = convertStringToDate(matcher.group(9));
								if (inspectionDate.before(aCutoffDate)) {
									oldRestaurants.add(camis);
									result.keySet().remove(camis);
								} else {
									final Date gradeDate = convertStringToDate(matcher.group(14));
									final String doingBusinessAs = matcher.group(2).trim();
									final Borough borough = Borough.findByCode(Integer.parseInt(matcher.group(3)));
									final String building = matcher.group(4).trim();
									final String street = matcher.group(5).trim();
									final String zipCode = matcher.group(6);
									final String phoneNumber = matcher.group(7);
									final String cuisine = matcher.group(8);
									final Grade currentGrade = Grade.findByCode(matcher.group(13));
									final Restaurant restaurant =
											new Restaurant(camis, doingBusinessAs, borough, new Address(building, street, zipCode), phoneNumber, cuisine, currentGrade, gradeDate, inspectionDate);
									final Restaurant existingRestaurant = result.get(camis);
									if (existingRestaurant == null || existingRestaurant.getInpectionDate() == null
											|| (inspectionDate != null && existingRestaurant.getInpectionDate().before(inspectionDate))) {
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
		final Date gradeDate;
		if (aDateString.isEmpty()) {
			gradeDate = null;
		} else {
			gradeDate = simpleDateFormat.parse(aDateString);
		}
		return gradeDate;
	}
}
