package skylight1.marketappweb.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 
 * @author Rob
 *
 */
@PersistenceCapable
public class WatchList {

	@PrimaryKey
	private String listName;
    
    @Persistent
	private SortedSet<WatchListItem> items;
    
    public WatchList(String listName) {
    	this.listName = listName;
    	this.items = new TreeSet<WatchListItem>();
    }

	public String getListName() {
		return listName;
	}

	public SortedSet<WatchListItem> getItems() {
		return items;
	}
}
