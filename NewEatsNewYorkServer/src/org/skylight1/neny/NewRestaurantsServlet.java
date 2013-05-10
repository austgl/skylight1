package org.skylight1.neny;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.skylight1.neny.server.RestaurantDAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressWarnings("serial")
public class NewRestaurantsServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(NewRestaurantsServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			LOGGER.info("starting");

			resp.setContentType("application/json");
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -3);
			final Date cutoffDate = calendar.getTime();
			new Gson().toJson(new RestaurantDAO().getListOfRestaurants(cutoffDate), resp.getWriter());

			LOGGER.info("done");
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}