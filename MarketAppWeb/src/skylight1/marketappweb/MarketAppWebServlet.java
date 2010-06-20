package skylight1.marketappweb;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import skylight1.marketappweb.model.*;

/**
 * 
 * @author Rob
 * 
 * This class was generated when creating the App Engine project. It's a handy way for me
 * to try out the JDO classes, but I imagine we will want our servlets (or whatever) in
 * a view directory.
 */
@SuppressWarnings({"unchecked","serial"})
public class MarketAppWebServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		// Let's make and persist a few items
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        try {
            // First clear out any from previous session:
            String query = "select from " + WatchListItem.class.getName();
            List<WatchList> lists = (List<WatchList>) pm.newQuery(query).execute();
            pm.deletePersistentAll(lists);
            WatchList wl = new WatchList("Tech Stocks","aardvark");
            wl.getItems().add(new WatchListItem("goog"));
            wl.getItems().add(new WatchListItem("msft"));
            wl.getItems().add(new WatchListItem("IBM"));
            pm.makePersistent(wl);

            wl = new WatchList("Energy Stocks","aardvark");
            wl.getItems().add(new WatchListItem("BP"));
            wl.getItems().add(new WatchListItem("XOM"));
            wl.getItems().add(new WatchListItem("goog"));
            pm.makePersistent(wl);
        } finally {
            pm.close();
        }
        // Now pretend we're in another part of the app and we want to retrieve them:
        pm = PMF.get().getPersistenceManager();
        try {
            String query = "select from " + WatchList.class.getName() + " where user == 'aardvark'" ;
            List<WatchList> lists = (List<WatchList>) pm.newQuery(query).execute();
            for(WatchList l : lists) {
            	resp.getWriter().println("\nItems in " + l.getListName());
            	for(WatchListItem wli : l.getItems())
            		resp.getWriter().println("\t" + wli.toString());
            }
            
            resp.getWriter().println("\nAll items:");
            query = "select from " + WatchListItem.class.getName();
            List<WatchListItem> items = (List<WatchListItem>) pm.newQuery(query).execute();
            for(WatchListItem wli : items)
            	resp.getWriter().println("\t" + wli.toString());
       } finally {
            pm.close();
        }
 	}
}
