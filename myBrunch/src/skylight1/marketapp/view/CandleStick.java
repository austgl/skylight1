package skylight1.marketapp.view;

// TODO: Auto-generated Javadoc
/**
 * The Class CandleStick.
 */
public class CandleStick {
	
	/** The date. */
	private java.util.Date date;
	
	/** The top left x. */
	private int topLeftX;
	
	/** The top left y. */
	private int topLeftY;
	
	/** The bot right x. */
	private int botRightX;
	
	/** The bot right y. */
	private int botRightY;
	
	/** The color. */
	private int color;
	
	/**
	 * Instantiates a new candle stick.
	 */
	public CandleStick() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new candle stick.
	 * 
	 * @param topLeftX the top left x
	 * @param topLeftY the top left y
	 * @param botRightX the bot right x
	 * @param botRightY the bot right y
	 * @param color the color
	 * @param date the date
	 */
	public CandleStick(java.util.Date date, int topLeftX, int topLeftY, int botRightX,
			int botRightY, int color) {
		super();
		this.date = date;
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.botRightX = botRightX;
		this.botRightY = botRightY;
		this.color = color;
	}
	
	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public java.util.Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date the new date
	 */
	public void setDate(java.util.Date date) {
		this.date = date;
	}

	/**
	 * Gets the top left x.
	 * 
	 * @return the top left x
	 */
	public int getTopLeftX() {
		return topLeftX;
	}
	
	/**
	 * Sets the top left x.
	 * 
	 * @param topLeftX the new top left x
	 */
	public void setTopLeftX(int topLeftX) {
		this.topLeftX = topLeftX;
	}
	
	/**
	 * Gets the top left y.
	 * 
	 * @return the top left y
	 */
	public int getTopLeftY() {
		return topLeftY;
	}
	
	/**
	 * Sets the top left y.
	 * 
	 * @param topLeftY the new top left y
	 */
	public void setTopLeftY(int topLeftY) {
		this.topLeftY = topLeftY;
	}
	
	/**
	 * Gets the bot right x.
	 * 
	 * @return the bot right x
	 */
	public int getBotRightX() {
		return botRightX;
	}
	
	/**
	 * Sets the bot right x.
	 * 
	 * @param botRightX the new bot right x
	 */
	public void setBotRightX(int botRightX) {
		this.botRightX = botRightX;
	}
	
	/**
	 * Gets the bot right y.
	 * 
	 * @return the bot right y
	 */
	public int getBotRightY() {
		return botRightY;
	}
	
	/**
	 * Sets the bot right y.
	 * 
	 * @param botRightY the new bot right y
	 */
	public void setBotRightY(int botRightY) {
		this.botRightY = botRightY;
	}
	
	/**
	 * Gets the color.
	 * 
	 * @return the color
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Sets the color.
	 * 
	 * @param color the new color
	 */
	public void setColor(int color) {
		this.color = color;
	}
	
}
