package org.skylight1.neny;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.skylight1.neny.model.Restaurant;
import org.skylight1.neny.server.InspectionResultsFetcher;

@SuppressWarnings("serial")
public class RefreshServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(RefreshServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			LOGGER.info("starting");
			resp.setContentType("text/plain");
			resp.getWriter().println("Processing...");

			final InspectionResultsFetcher fetcher = new InspectionResultsFetcher();

			// use the fetcher to retrieve the restaurants according to the old and new files
			// ideally the old would be from the database, and the new from the internet
			final Map<String, Restaurant> restaurants = fetcher.processFile("http://nycopendata.socrata.com/download/4vkw-7nck/ZIP");

			resp.getWriter().println(String.format("Loaded %d restaurants", restaurants.size()));
			resp.getWriter().println("...Done");
			LOGGER.info("done");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "failed", e);
		}
	}
}
