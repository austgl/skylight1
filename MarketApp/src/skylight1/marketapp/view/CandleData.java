package skylight1.marketapp.view;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class CandleStick.
 */
public class CandleData {
	
	/** The low. */
	private String date;
	
	/** The low. */
	private float low;
	
	/** The high. */
	private float high;
	
	/** The open. */
	private float open;
	
	/** The close. */
	private float close;
	
	/** The bull. */
	boolean bull;

	/**
	 * Instantiates a new candle data.
	 */
	public CandleData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new candle Data.
	 * 
	 * @param date the date
	 * @param low the low
	 * @param high the high
	 * @param open the open
	 * @param close the close
	 * @param bull the bull
	 */
	public CandleData(String date, float low, float high, float open,
			float close, boolean bull) {
		super();
		this.date = date;
		this.low = low;
		this.high = high;
		this.open = open;
		this.close = close;
		this.bull = bull;
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date the new date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Gets the low.
	 * 
	 * @return the low
	 */
	public float getLow() {
		return low;
	}

	/**
	 * Sets the low.
	 * 
	 * @param low the new low
	 */
	public void setLow(float low) {
		this.low = low;
	}

	/**
	 * Gets the high.
	 * 
	 * @return the high
	 */
	public float getHigh() {
		return high;
	}

	/**
	 * Sets the high.
	 * 
	 * @param high the new high
	 */
	public void setHigh(float high) {
		this.high = high;
	}

	/**
	 * Gets the open.
	 * 
	 * @return the open
	 */
	public float getOpen() {
		return open;
	}

	/**
	 * Sets the open.
	 * 
	 * @param open the new open
	 */
	public void setOpen(float open) {
		this.open = open;
	}

	/**
	 * Gets the close.
	 * 
	 * @return the close
	 */
	public float getClose() {
		return close;
	}

	/**
	 * Sets the close.
	 * 
	 * @param close the new close
	 */
	public void setClose(float close) {
		this.close = close;
	}

	/**
	 * Checks if is bull.
	 * 
	 * @return true, if is bull
	 */
	public boolean isBull() {
		return bull;
	}

	/**
	 * Sets the bull.
	 * 
	 * @param bull the new bull
	 */
	public void setBull(boolean bull) {
		this.bull = bull;
	}
	
	

}
