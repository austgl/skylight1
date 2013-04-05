package org.skylight1.neny.server;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.skylight1.neny.model.Restaurant;

public class RestaurantDAO {

	public List<Restaurant> getListOfRestaurants(Date cutoffDate) {
		final EntityManager em = EMF.get().createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			final List<Restaurant> result = (List<Restaurant>) em 
					.createQuery(
							"SELECT r FROM Restaurant r JOIN FETCH r.borough JOIN FETCH "
									+ "r.address ORDER BY r.camis DESC")
					.setMaxResults(1000).getResultList();

			return result;
		} finally {
			em.close();
		}
	}
}
