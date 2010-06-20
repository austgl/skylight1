package skylight1.marketappweb.model;

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
public class WatchList {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
	private String listName;
    
    @Persistent
	private SortedSet<WatchListItem> items;
    
    @Persistent
    private String user; // this could change to a numeric value, depending on how we do authentication
    
    public WatchList(String listName, String user) {
    	this.listName = listName;
    	this.items = new TreeSet<WatchListItem>();
    	this.user = user;
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

}
