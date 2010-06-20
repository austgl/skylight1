package skylight1.marketappweb.model;

import com.google.appengine.api.datastore.Key;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 
 * @author Rob
 *
 */
@PersistenceCapable
public class WatchListItem implements Comparable<WatchListItem> {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String ticker;  
    
    @Persistent
	private Boolean deleted;
	
    @Persistent
	private Date lastEdited;
    
    public WatchListItem(String ticker) {
    	this.ticker = ticker.toUpperCase();
    	this.deleted = false;
    	this.lastEdited = new Date();
    }
	
    public Key getKey() {
        return key;
    }
    
    /**
     * For now, let's make this read-only (no setter). I don't think it would make sense
     * to allow modification.
     */
	public String getTicker() {
		return ticker;
	}
	
	public Boolean getDeleted() {
		return deleted;
	}
	
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public Date getLastEdited() {
		return lastEdited;
	}
	
	public void setLastEdited(Date lastEdited) {
		this.lastEdited = lastEdited;
	}
	
	@Override
	public String toString() {
		return "Ticker: " + ticker + ", last edited (UTC): " +  
		 new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(lastEdited);
	}
	
	/**
	 * If we had only one watch list, the ticker would make a good primary key. But since 
	 * we'll want to allow the same stock in multiple watch lists, we'll need some other 
	 * way to keep a single entry for any given ticker within a watch list. So let's
	 * try this and keep an owned SortedSet of these in each watch list.
	 */
	@Override
	public int compareTo(WatchListItem wli) {
		return ticker.compareToIgnoreCase(wli.getTicker());
	}

}
