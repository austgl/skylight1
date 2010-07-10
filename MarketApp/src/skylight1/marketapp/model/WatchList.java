package skylight1.marketapp.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Rob
 * We may not want to keep this and WatchListItem but for now they illustrate what we'll need
 * for synchronizing with MarketAppWeb.
 *
 */
public class WatchList {

	private String listName;
    
	private SortedSet<WatchListItem> items;
    
	private boolean deleted;
	
	private Date lastEdited;
    
   public WatchList(String listName, SortedSet<WatchListItem> items) {
    	this.listName = listName;
    	this.items = new TreeSet<WatchListItem>();
    	this.items.addAll(items);
    	this.deleted = false;
    	this.lastEdited = new Date();
    }

	public String getListName() {
		return listName;
	}

	void setListName(String listName) {
		this.listName = listName;
	}

	public SortedSet<WatchListItem> getItems() {
		return items;
	}

	public boolean getDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		lastEdited = new Date(); // let's do this in case someone forgets
		this.deleted = deleted;
	}
	
	public Date getLastEdited() {
		return lastEdited;
	}
	
	public void setLastEdited(Date lastEdited) {
		this.lastEdited = lastEdited;
	}
	
}
