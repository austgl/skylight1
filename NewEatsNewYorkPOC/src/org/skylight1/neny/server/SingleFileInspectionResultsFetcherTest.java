package org.skylight1.neny.server;

import static java.lang.String.format;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.skylight1.neny.model.Restaurant;

public class SingleFileInspectionResultsFetcherTest {
	private static final Logger LOGGER = Logger.getLogger(SingleFileInspectionResultsFetcherTest.class.getName());

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		final SingleFileInspectionResultsFetcher fetcher = new SingleFileInspectionResultsFetcher();

		// use the fetcher to retrieve the restaurants according to the new file
		final Collection<Restaurant> newRestaurants =
		// fetcher.processFile("file:///C:\\Users\\Timothy\\Downloads\\dohmh_restaurant-inspections_002 (1).zip", new
		// Date());
				fetcher.processFile("file:///C:\\\\Users\\Sys\\Downloads\\dohmh_restaurant-inspections_12-08-31.zip", new Date(System.currentTimeMillis()
						- 1000L * 60L * 60L * 24L * 30L));
		// final Map<String, Restaurant> newRestaurants =
		// fetcher.processFile("http://nycopendata.socrata.com/download/4vkw-7nck/ZIP");

		LOGGER.info(format("new %d", newRestaurants.size()));

		for (final Restaurant r : newRestaurants) {
			LOGGER.info(r.toString());
		}
	}
}
