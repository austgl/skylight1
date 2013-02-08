package org.skylight1.neny.filters;

import java.util.Date;

import org.skylight1.neny.model.Restaurant;

public class GooglePlacesRestaurantFilter implements RestaurantFilter {

	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";//parameters"
	private static final String MAPS_URL = "http://maps.googleapis.com/maps/api/geocode/json?"; //paramete"
	private String apiKey;
	public GooglePlacesRestaurantFilter(final String apiKey){
		
	}
	/**
	 * First make a call to Google Maps to get Geocode the address and 
	 * then make a call to Places API
	 */
	@Override
	public Date lastKnownRecord(Restaurant restaurant) {
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
