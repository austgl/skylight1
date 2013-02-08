package org.skylight1.neny.filters;

import java.util.Date;

import org.skylight1.neny.model.Restaurant;

public interface RestaurantFilter {
	
	Date lastKnownRecord(Restaurant restaurant);

}
