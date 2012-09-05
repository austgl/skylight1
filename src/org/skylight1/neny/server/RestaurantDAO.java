package org.skylight1.neny.server;

import java.util.List;

import javax.persistence.EntityManager;

import org.skylight1.neny.model.Restaurant;

public class RestaurantDAO {

	public List<Restaurant> getListOfRestaurants() {
		final EntityManager em = EMF.get().createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			final List<Restaurant> result = (List<Restaurant>) em.createQuery("SELECT r FROM Restaurant r").getResultList();
			// TODO fix this later to do an eager fetch
			for (final Restaurant doh : result) {
				System.out.println(doh);
			}
			return result;
		} finally {
			em.close();
		}
	}
}
