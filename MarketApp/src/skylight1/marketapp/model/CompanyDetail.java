package skylight1.marketapp.model;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: Jul 6, 2010
 * Time: 3:52:26 PM
 */
public class CompanyDetail {

    String ticker;
    String name;

    public float getTodaysPriceChange() {
        return todaysPriceChange;
    }

    public void setTodaysPriceChange(float todaysPriceChange) {
        this.todaysPriceChange = todaysPriceChange;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    float price;
    float todaysPriceChange;

    public float getTodaysPercentChange() {
        return todaysPercentChange;
    }

    public void setTodaysPercentChange(float todaysPercentChange) {
        this.todaysPercentChange = todaysPercentChange;
    }

    float todaysPercentChange;

    long volume;
    String exchange;
    String ebita;
    String pegRatio;
    float mavg50;
    float mavg200;
    float peRatio;
    String bidSize;
    String askSize;

    public String getAskSize() {
        return askSize;
    }

    public void setAskSize(String askSize) {
        this.askSize = askSize;
    }

    public String getBidSize() {
        return bidSize;
    }

    public void setBidSize(String bidSize) {
        this.bidSize = bidSize;
    }

    public String getEbita() {
        return ebita;
    }

    public void setEbita(String ebita) {
        this.ebita = ebita;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public float getMavg200() {
        return mavg200;
    }

    public void setMavg200(float mavg200) {
        this.mavg200 = mavg200;
    }

    public float getMavg50() {
        return mavg50;
    }

    public void setMavg50(float mavg50) {
        this.mavg50 = mavg50;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(float peRatio) {
        this.peRatio = peRatio;
    }

    public String getPegRatio() {
        return pegRatio;
    }

    public void setPegRatio(String pegRatio) {
        this.pegRatio = pegRatio;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }


    public CompanyDetail(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public String toString() {
        return "CompanyDetail{" +
                "askSize='" + askSize + '\'' +
                ", ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", todaysPriceChange=" + todaysPriceChange +
                ", todaysPercentChange=" + todaysPercentChange +
                ", volume=" + volume +
                ", exchange='" + exchange + '\'' +
                ", ebita='" + ebita + '\'' +
                ", pegRatio='" + pegRatio + '\'' +
                ", mavg50=" + mavg50 +
                ", mavg200=" + mavg200 +
                ", peRatio=" + peRatio +
                ", bidSize='" + bidSize + '\'' +
                '}';
    }
}
