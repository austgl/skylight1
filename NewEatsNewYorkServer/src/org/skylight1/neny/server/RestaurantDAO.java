package org.skylight1.neny.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.skylight1.neny.model.Restaurant;

public class RestaurantDAO {

	public List<Restaurant> getListOfRestaurants(Date cutoffDate) {
		final EntityManager em = EMF.get().createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			final List<Restaurant> unfilteredListOfRestaurants =
					(List<Restaurant>) em.createQuery("SELECT r FROM Restaurant r JOIN FETCH r.borough JOIN FETCH " + "r.address ORDER BY r.discoveredDate DESC")
							.setMaxResults(1000).getResultList();

			final List<Restaurant> result = new ArrayList<Restaurant>();
			for (Restaurant restaurant : unfilteredListOfRestaurants) {
				if (restaurant.getDiscoveredDate().before(cutoffDate)) {
					break;
				}
				result.add(restaurant);
			}
			return result;
		} finally {
			em.close();
		}
	}
}