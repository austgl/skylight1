package org.skylight1.neny;

import static com.google.appengine.api.memcache.jsr107cache.GCacheFactory.EXPIRATION_DELTA;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.skylight1.neny.model.Restaurant;
import org.skylight1.neny.server.RestaurantDAO;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class NewRestaurantsServlet extends HttpServlet {

	private static final int ONE_DAY = 24 * 60 * 60;

	private static final Logger LOGGER = Logger.getLogger(NewRestaurantsServlet.class.getName());

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			LOGGER.info("starting");

			resp.setContentType("application/json");
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -3);
			calendar.clear(Calendar.HOUR_OF_DAY);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);

			final Date cutoffDate = calendar.getTime();
			final List<Restaurant> listOfRestaurants;

			final CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			final Map<Object, Object> properties = new HashMap<Object, Object>();
			properties.put(EXPIRATION_DELTA, ONE_DAY);
			final Cache cache = cacheFactory.createCache(properties);

			if (cache.containsKey(cutoffDate)) {
				listOfRestaurants = (List<Restaurant>) cache.get(cutoffDate);
				LOGGER.info("found in cache; list length = " + listOfRestaurants.size());
			} else {
				listOfRestaurants = new RestaurantDAO().getListOfRestaurants(cutoffDate);
				cache.put(cutoffDate, listOfRestaurants);
				LOGGER.info("did not find in cache, so retrieved from database and stored in cache; list length = " + listOfRestaurants.size());
			}

			new Gson().toJson(listOfRestaurants, resp.getWriter());

			LOGGER.info("done");
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}