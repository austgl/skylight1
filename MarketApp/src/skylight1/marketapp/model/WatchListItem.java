package skylight1.marketapp.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Rob
 * We may not want to keep this and WatchList but for now they illustrate what we'll need
 * for synchronizing with MarketAppWeb.
 *
 */
public class WatchListItem implements Comparable<WatchListItem> {

    private String ticker;  
    
	private boolean deleted;
	
	private Date lastEdited;
    
    public WatchListItem(String ticker) {
    	this.ticker = ticker.toUpperCase();
    	this.deleted = false;
    	this.lastEdited = new Date();
    }
    
    /**
     * For now, let's make this read-only (no setter). I don't think it would make sense
     * to allow modification.
     */
	public String getTicker() {
		return ticker;
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
	public String toString() {
		return "Ticker: " + ticker + ", last edited (UTC): " +  
		 new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(lastEdited);
	}
	
	@Override
	public int compareTo(WatchListItem wli) {
		return ticker.compareToIgnoreCase(wli.getTicker());
	}

	@Override 
	public boolean equals(Object that) {
	    if ( this == that ) 
	    	return true;
	    if ( !(that instanceof WatchListItem) )
	    	return false;
	    return ticker.equals(((WatchListItem)that).getTicker()); 
	  }

	@Override
	public int hashCode() {
		return ticker.hashCode();
	}
	
}