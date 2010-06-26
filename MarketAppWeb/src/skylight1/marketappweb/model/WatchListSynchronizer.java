package skylight1.marketappweb.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

/**
 * 
 * @author Rob
 *
 */
public class WatchListSynchronizer {

	@SuppressWarnings({"unchecked"})
	public static Set<WatchList> doSynchronization(Set<WatchList> phoneLists, String user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Set<WatchList>  synchedLists;
		try {
			String query = "select from " + WatchList.class.getName() + " where user == '" + user + "'";
			List<WatchList> lists = (List<WatchList>) pm.newQuery(query).execute();
			Set<WatchList> cloudLists = new HashSet<WatchList>();
			//cloudLists.addAll(lists); <- can't do this - JDO doesn't like it when we delete them
			for(WatchList wl : lists) {
				cloudLists.add(cloneWatchList(wl));
			}
			for(WatchList wl : lists)
				pm.deletePersistent(wl);
			synchedLists = syncLists(phoneLists,cloudLists);
			for(WatchList wl : synchedLists)
				pm.makePersistent(wl);
		} finally {
			pm.close();
		}
		return synchedLists;
	}
	
	private static WatchList cloneWatchList(WatchList wl) {
		WatchList w = new WatchList(wl.getListName(),cloneItems(wl.getItems()),wl.getUser());
		w.setDeleted(wl.getDeleted());
		w.setLastEdited(wl.getLastEdited());
		return w;
	}
	
	private static SortedSet<WatchListItem> cloneItems(SortedSet<WatchListItem> items) {
		SortedSet<WatchListItem> clonedItems = new TreeSet<WatchListItem>();
		for(WatchListItem item : items) {
			WatchListItem i = new WatchListItem(item.getTicker());
			i.setDeleted(item.getDeleted());
			i.setLastEdited(item.getLastEdited());
			clonedItems.add(i);
		}
		return clonedItems;
	}
	
	private static Map<String,WatchList> mapLists(Set<WatchList> lists) {
		Map<String,WatchList> m = new HashMap<String,WatchList>();
		for(WatchList l : lists) {
			m.put(l.getListName(), l);
		}
		return m;
	}
	
	private static Set<WatchList> syncLists(Set<WatchList> l, Set<WatchList> r) {
		Set<WatchList> target = new HashSet<WatchList>();
		Map<String,WatchList> m = mapLists(r);
		for(WatchList i : l) {
			if(r.contains(i)) {
				WatchList j = m.get(i.getListName()); // awkward way to get the actual item from the right side list
				if(! i.getDeleted() && ! j.getDeleted())
					target.add(new WatchList(i.getListName(),syncItems(i.getItems(),j.getItems()), i.getUser()));
				else if(! i.getDeleted() && i.getLastEdited().after(j.getLastEdited()))
					addWatchList(i,target);
				else if(! j.getDeleted() && j.getLastEdited().after(i.getLastEdited()))
					addWatchList(j,target);
				r.remove(i);
			}
			else if(! i.getDeleted()) // not in the rhs list
				addWatchList(i,target);
		}
		// Now process the items that were in the right side list but not the left
		addLists(r, target);
		return target;
	}
	
	private static void addWatchList(WatchList wl, Set<WatchList> target) {
		SortedSet<WatchListItem> items = new TreeSet<WatchListItem>(); // created a new so we get current time for last edited and new primary key
		addItems(wl.getItems(),items);
		target.add(new WatchList(wl.getListName(),items, wl.getUser()));
	}

	private static void addLists(Set<WatchList> source, Set<WatchList> target) {
		for(WatchList wl : source) {
			if(! wl.getDeleted()) {
				addWatchList(wl, target);
			}
		}
	}

	private static Map<String,WatchListItem> mapItems(SortedSet<WatchListItem> items) {
		Map<String,WatchListItem> m = new HashMap<String,WatchListItem>();
		for(WatchListItem i : items) {
			m.put(i.getTicker(), i);
		}
		return m;
	}
	
	private static SortedSet<WatchListItem> syncItems(SortedSet<WatchListItem> l, SortedSet<WatchListItem> r) {
		SortedSet<WatchListItem> target = new TreeSet<WatchListItem>();
		Map<String,WatchListItem> m = mapItems(r);
		for(WatchListItem i : l) {
			boolean addIt = false;
			if(r.contains(i)) {
				WatchListItem j = m.get(i.getTicker()); // awkward way to get the actual item from the right side list
				if(! i.getDeleted() && ! j.getDeleted())
					addIt = true;
				else if(! i.getDeleted() && i.getLastEdited().after(j.getLastEdited()))
					addIt = true;
				else if(! j.getDeleted() && j.getLastEdited().after(i.getLastEdited()))
					addIt = true;
				r.remove(i);
			}
			else if(! i.getDeleted()) // not in the rhs list
				addIt = true;
			if(addIt)
				target.add(new WatchListItem(i.getTicker()));
		}
		// Now process the items that were in the right side list but not the left
		addItems(r, target);
		return target;
	}
	
	private static void addItems(SortedSet<WatchListItem> source, SortedSet<WatchListItem> target) {
		for(WatchListItem i : source) {
			if(! i.getDeleted())
				target.add(new WatchListItem(i.getTicker()));
		}
	}

}
