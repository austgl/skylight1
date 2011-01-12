package skylight1.marketapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: Jun 21, 2010
 * Time: 11:03:04 AM
 */
public class EquityTimeSeries {

    public Date getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    private Date date;
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;

//    public EquityTimeSeries(Date date, double open, double high, double low, double close, int volume) {
//        this.date = date;
//        this.open = open;
//        this.high = high;
//        this.low = low;
//        this.close = close;
//        this.volume = volume;
//    }

    /*
     * Constructor that dates the Date as a string.
     */

    public EquityTimeSeries(String date, double open, double high, double low, double close, int volume) {
        this.date = extractDate(date);
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public void dumpData() {
        System.out.println("->" + date + ":" + open + "," + high + "," + low + "," + close + "," + volume);
    }


    public Date extractDate(String aDateStr) {

        String pattern = "yyyy-MM-dd";
        Date date = null;

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {

            date = format.parse(aDateStr);
//            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
