package org.skylight1.neny;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.skylight1.neny.model.Restaurant;
import org.skylight1.neny.server.EMF;
import org.skylight1.neny.server.InspectionResultsFetcher;

@SuppressWarnings("serial")
public class RefreshServlet extends HttpServlet {

	private static final int NUMBER_OF_MONTHS_THAT_ARE_NEW = -3;

	private static final Logger LOGGER = Logger.getLogger(RefreshServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			LOGGER.info("starting");
			resp.setContentType("text/plain");
			resp.getWriter().println("Processing...");

			// Get a Calendar instance
			Calendar cal = Calendar.getInstance();

			cal.roll(Calendar.MONTH, NUMBER_OF_MONTHS_THAT_ARE_NEW);

			final InspectionResultsFetcher fetcher = new InspectionResultsFetcher();

			// use the fetcher to retrieve the restaurants according to the old
			// and new files
			// ideally the old would be from the database, and the new from the
			// internet
			final Collection<Restaurant> restaurants = fetcher.processFile("http://nycopendata.socrata.com/download/4vkw-7nck/ZIP");

			final Collection<Restaurant> recentRestaurants = filterOutOldRestaurants(restaurants, cal.getTime());

			resp.getWriter().println(String.format("Collected %d restaurants, but only %d were 'new'", restaurants.size(), recentRestaurants.size()));
			resp.getWriter().println("...Done");

			final EntityManager em = EMF.get().createEntityManager();

			try {
				Iterator<Restaurant> iter = recentRestaurants.iterator();

				while (iter.hasNext()) {
					em.getTransaction().begin();
					final Restaurant restaurant = iter.next();
					try {
						em.persist(restaurant);
					} catch (EntityExistsException e) {
						final Restaurant existingRestaurant = em.find(Restaurant.class, restaurant.getCamis());
						restaurant.setDiscoveredDate(existingRestaurant.getDiscoveredDate());
						em.merge(restaurant);
					}
					em.getTransaction().commit();
				}

				LOGGER.info("just saved " + recentRestaurants.size());

			} finally {
				em.close();
			}

			LOGGER.info("done");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "failed", e);
		}
	}

	private Collection<Restaurant> filterOutOldRestaurants(Collection<Restaurant> aRestaurants, Date aCutoff) {
		final List<Restaurant> restaurants = new ArrayList<Restaurant>(aRestaurants);
		Collections.sort(restaurants, Collections.reverseOrder(new CamisComparator()));

		for (int i = 0; i < restaurants.size(); i++) {
			final Restaurant rest = restaurants.get(i);
			if (isOldDate(rest.getGradeDate(), aCutoff) || isOldDate(rest.getInspectionDate(), aCutoff)) {
				return restaurants.subList(0, i);
			}
		}

		return restaurants;
	}

	private boolean isOldDate(final Date aDate, final Date aCutoff) {
		return aDate != null && aDate.before(aCutoff);
	}
}
