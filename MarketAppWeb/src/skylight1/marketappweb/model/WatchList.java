package skylight1.marketappweb.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * 
 * @author Rob
 *
 */
@PersistenceCapable
public class WatchList implements Comparable<WatchList> {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
	private String listName;
    
    @Persistent
	private SortedSet<WatchListItem> items;
    
    @Persistent
    private String user; // this could change to a numeric value, depending on how we do authentication
    
    @Persistent
	private boolean deleted;
	
    @Persistent
	private Date lastEdited;
    
   public WatchList(String listName, SortedSet<WatchListItem> items, String user) {
    	this.listName = listName;
    	this.items = new TreeSet<WatchListItem>();
    	this.items.addAll(items);
    	this.user = user;
    	this.deleted = false;
    	this.lastEdited = new Date();
    }

    public Key getKey() {
        return key;
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

	public String getUser() {
		return user;
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
	
	@Override
	public int compareTo(WatchList wl) {
		return listName.compareTo(wl.getListName()); // I think case sensitive might be desirable
	}

	@Override 
	public boolean equals(Object that) {
	    if ( this == that ) 
	    	return true;
	    if ( !(that instanceof WatchList) )
	    	return false;
	    return listName.equals(((WatchList)that).getListName()); 
	  }

	@Override
	public int hashCode() {
		return listName.hashCode();
	}
	
}
