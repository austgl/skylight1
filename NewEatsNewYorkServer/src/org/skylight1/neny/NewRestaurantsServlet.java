package org.skylight1.neny;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.skylight1.neny.server.RestaurantDAO;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class NewRestaurantsServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(NewRestaurantsServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			LOGGER.info("starting");

			// TODO should use a date or duration from the client to limit the newness of restaurants returned
			resp.setContentType("application/json");
			new Gson().toJson(new RestaurantDAO().getListOfRestaurants(), resp.getWriter());

			LOGGER.info("done");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "failed", e);
		}
	}

}
