package skylight1.marketapp.model;

import android.util.Log;

import java.math.BigDecimal;
import java.util.Date;

public class EquityPricingInformation {
    private static String TAG = "EquityPricingInformation";

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
        Log.i(TAG, "Creating Equity Ticker" + aTicker + ", Name=" + aName);
        
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

    @Override
    public String toString() {
        return "EquityPricingInformation{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", lastPrice=" + lastPrice +
                ", todayVolume=" + todayVolume +
                ", priceTime=" + priceTime +
                ", priorDayClosePrice=" + priorDayClosePrice +
                ", priorDayVolume=" + priorDayVolume +
                ", priorDayClose=" + priorDayClose +
                '}';
    }
}
