package skylight1.marketapp;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: May 20, 2010
 * Time: 8:28:03 PM
 */
public class PortfolioItem {
    public String getAveragePriceStr() {
        return averagePriceStr;
    }

    public String getNumberOfSharesStr() {
        return numberOfSharesStr;
    }

    public String getCurrentPriceStr() {
        return currentPriceStr;
    }

    private String averagePriceStr;
    private String numberOfSharesStr;
    private String currentPriceStr;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public float getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(float averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }

    public String getNumberOfSharesAsStr() {
        return String.valueOf(numberOfShares);  // Best way?
    }

    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    private String ticker;
    private float averagePrice;
    private int numberOfShares;
    private float currentPrice;

    // Tim, IntelliJ filled these in!!!

    public PortfolioItem(String ticker, float averagePrice, int numberOfShares, float currentPrice) {
        this.ticker = ticker;
        this.averagePrice = averagePrice;
        this.averagePriceStr = String.format("%8.2f", averagePrice);

        this.numberOfShares = numberOfShares;
        this.numberOfSharesStr =  String.format("%d", numberOfShares);


        this.currentPrice = currentPrice;
        this.currentPriceStr = String.format("%8.2f",currentPrice);//Put in setter
    }

    public float getProfit() {
        // numberOfShares * currentPrice  - numberOfShares * avgPrice
        return 100.01f;
    }


}
