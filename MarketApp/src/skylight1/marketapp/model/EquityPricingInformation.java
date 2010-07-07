package skylight1.marketapp.model;

import android.util.Log;

import java.math.BigDecimal;
import java.util.Date;

public class EquityPricingInformation implements Comparable<EquityPricingInformation> {
    private static String TAG = "EquityPricingInformation";

	private final String ticker;

	private final String name;

	private final BigDecimal lastPrice;

	private final BigDecimal todayVolume;

	private final Date priceTime;

	private final BigDecimal priorDayClosePrice;

	private final BigDecimal priorDayVolume;

	private final Date priorDayClose;
    private float priceChange;

    public float getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(float percentChange) {
        this.percentChange = percentChange;
    }

    public float getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(float priceChange) {
        this.priceChange = priceChange;
    }

    private float percentChange;

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
                "lastPrice=" + lastPrice +
                ", ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", todayVolume=" + todayVolume +
                ", priceTime=" + priceTime +
                ", priorDayClosePrice=" + priorDayClosePrice +
                ", priorDayVolume=" + priorDayVolume +
                ", priorDayClose=" + priorDayClose +
                ", priceChange=" + priceChange +
                ", percentChange=" + percentChange +
                '}';
    }

    @Override
    public int compareTo(EquityPricingInformation equityPricingInformation) {
        return ticker.compareTo(equityPricingInformation.getTicker());
    }
}
