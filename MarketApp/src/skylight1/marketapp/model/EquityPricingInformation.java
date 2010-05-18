package skylight1.marketapp.model;

import java.math.BigDecimal;
import java.util.Date;

public class EquityPricingInformation {
	private final String ticker;

	private final String name;

	private final BigDecimal lastPrice;

	private final BigDecimal todayVolume;

	private final Date priceTime;

	private final BigDecimal priorDayClosePrice;

	private final BigDecimal priorDayVolume;

	private final Date priorDayClose;

	public EquityPricingInformation(String aTicker, String aName, BigDecimal aLastPrice, BigDecimal aTodayVolume, Date aPriceTime,
			BigDecimal aPriorDayClosePrice, BigDecimal aPriorDayVolume, Date aPriorDayClose) {
		ticker = aTicker;
		name = aName;
		lastPrice = aLastPrice;
		todayVolume = aTodayVolume;
		priceTime = aPriceTime;
		priorDayClosePrice = aPriorDayClosePrice;
		priorDayVolume = aPriorDayVolume;
		priorDayClose = aPriorDayClose;
	}

	public String getTicker() {
		return ticker;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public Date getPriceTime() {
		return priceTime;
	}

	public BigDecimal getYesterdayClosePrice() {
		return priorDayClosePrice;
	}

}
