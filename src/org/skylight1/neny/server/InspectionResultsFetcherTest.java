package org.skylight1.neny.server;

import static java.lang.String.format;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.skylight1.neny.model.Restaurant;

public class InspectionResultsFetcherTest {
	private static final Logger LOGGER = Logger.getLogger(InspectionResultsFetcherTest.class.getName());

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		final InspectionResultsFetcher fetcher = new InspectionResultsFetcher();

		// use the fetcher to retrieve the restaurants according to the old and new files
		// ideally the old would be from the database, and the new from the internet
		final Map<String, Restaurant> oldRestaurants = fetcher.processFile("file:///C:\\Users\\Sys\\Downloads\\dohmh_restaurant-inspections_002.zip");
		final Map<String, Restaurant> newRestaurants = fetcher.processFile("file:///C:\\Users\\Sys\\Downloads\\dohmh_restaurant-inspections_002 (1) (1).zip");
		// final Map<String, Restaurant> newRestaurants =
		// fetcher.processFile("http://nycopendata.socrata.com/download/4vkw-7nck/ZIP");

		// find out the difference between them, i.e., the restaurants graded for the first time
		final Map<String, Restaurant> addedRestaurants = new HashMap<String, Restaurant>(newRestaurants);
		addedRestaurants.keySet().removeAll(oldRestaurants.keySet());
		LOGGER.info(format("old size %d, new size %d, diff %d", oldRestaurants.size(), newRestaurants.size(), addedRestaurants.size()));
		LOGGER.info(addedRestaurants.toString());
	}
}
