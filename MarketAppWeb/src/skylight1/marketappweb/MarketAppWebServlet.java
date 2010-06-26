package skylight1.marketappweb;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import skylight1.marketappweb.model.*;

/**
 * 
 * @author Rob
 * 
 * This class was generated when creating the App Engine project. It's a handy way for me
 * to try out the JDO classes, but I imagine we will want our servlets (or whatever) in
 * a controller directory.
 */
@SuppressWarnings({"unchecked","serial"})
public class MarketAppWebServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		// First clear out any from previous session:
		clearAll();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			// Let's make and persist a few items
			WatchList wl = new WatchList("Tech Stocks",new TreeSet<WatchListItem>(),"aardvark");
			wl.getItems().add(new WatchListItem("goog"));
			wl.getItems().add(new WatchListItem("msft"));
			wl.getItems().add(new WatchListItem("IBM"));
			pm.makePersistent(wl);

			wl = new WatchList("Energy Stocks",new TreeSet<WatchListItem>(),"aardvark");
			wl.getItems().add(new WatchListItem("BP"));
			wl.getItems().add(new WatchListItem("XOM"));
			wl.getItems().add(new WatchListItem("goog"));
			pm.makePersistent(wl);

		} finally {
			pm.close();
		}
		showAll(resp);
		resp.getWriter().println("\nNow try to remove Exxon.");
		pm = PMF.get().getPersistenceManager();
		try {
			String query = "select from " + WatchList.class.getName() + " where user == 'aardvark' && listName == 'Energy Stocks'";
			List<WatchList> lists = (List<WatchList>) pm.newQuery(query).execute();
			for(WatchList l : lists) {
				for(WatchListItem wli : l.getItems())
					if(wli.getTicker().equals("XOM")) {
						l.getItems().remove(wli);
						break;
					}
			}
			// Can you delete a WatchList without explicitly emptying the WatchListItems?
			// Will the items be un-persisted?
			query = "select from " + WatchList.class.getName() + " where user == 'aardvark' && listName == 'Tech Stocks'" ;
			lists = (List<WatchList>) pm.newQuery(query).execute();
			for(WatchList l : lists) {
				pm.deletePersistent(l);
			}
			// Yes, it worked.
		} finally {
			pm.close();
		}
		showAll(resp);
		syncTests(resp);
		clearAll(); // We won't leave anything behind in case we make incompatible changes to the structure
	}
	
	private void clearAll() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			// First clear out any from previous session:
			String query = "select from " + WatchListItem.class.getName();
			List<WatchListItem> items = (List<WatchListItem>) pm.newQuery(query).execute();
			pm.deletePersistentAll(items);

			query = "select from " + WatchList.class.getName();
			List<WatchList> lists = (List<WatchList>) pm.newQuery(query).execute();
			pm.deletePersistentAll(lists);
		} finally {
			pm.close();
		}
	}

	private void showAll(HttpServletResponse resp) throws IOException {
		// Now pretend we're in another part of the app and we want to retrieve them:
		PersistenceManager pm = PMF.get().getPersistenceManager();
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
	private void syncTests(HttpServletResponse resp) throws IOException {
		resp.getWriter().println("\nNow test synchronization.");
		resp.getWriter().println("\nPhone has stuff but nothing here:");
		clearAll();
		Set<WatchList> phoneLists = new HashSet<WatchList>();
		WatchList wl = new WatchList("Tech Stocks",new TreeSet<WatchListItem>(),"aardvark");
		wl.getItems().add(new WatchListItem("goog"));
		WatchListItem msft = new WatchListItem("msft"); 
		wl.getItems().add(msft);
		wl.getItems().add(new WatchListItem("IBM"));
		phoneLists.add(wl);
		wl = new WatchList("Energy Stocks",new TreeSet<WatchListItem>(),"aardvark");
		wl.getItems().add(new WatchListItem("BP"));
		wl.getItems().add(new WatchListItem("XOM"));
		wl.getItems().add(new WatchListItem("goog"));
		phoneLists.add(wl);
		WatchListSynchronizer.doSynchronization(phoneLists, "aardvark");
		showAll(resp);
		resp.getWriter().println("\nOK, now we have stuff, let's delete Energy Stocks on the phone side:");
		wl.setDeleted(true);
		WatchListSynchronizer.doSynchronization(phoneLists, "aardvark");
		showAll(resp);
		resp.getWriter().println("\nNow let's delete Microsoft. Yay!");
		msft.setDeleted(true);
		WatchListSynchronizer.doSynchronization(phoneLists, "aardvark");
		showAll(resp);
		resp.getWriter().println("\nAnd put them back:");
		wl.setDeleted(false);
		msft.setDeleted(false);
		WatchListSynchronizer.doSynchronization(phoneLists, "aardvark");
		showAll(resp);
	}

}
