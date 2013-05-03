package org.skylight1.neny;

import java.util.Comparator;

import org.skylight1.neny.model.Restaurant;

final class CamisComparator implements Comparator<Restaurant> {
	@Override
	public int compare(Restaurant aRestaurant1, Restaurant aRestaurant2) {
		return aRestaurant1.getCamis().compareTo(aRestaurant2.getCamis());
	}
}