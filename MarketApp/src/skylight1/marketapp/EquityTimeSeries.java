package skylight1.marketapp;

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

    public static List<EquityTimeSeries> dummyData() {
        List<EquityTimeSeries> aList = new ArrayList<EquityTimeSeries>(); 
        aList.add(new EquityTimeSeries("2010-06-01", 259.69, 265.94, 258.96, 260.83, 31249000));
        aList.add(new EquityTimeSeries("2010-06-02", 264.54, 264.80, 260.33, 263.95, 24550900));
        aList.add(new EquityTimeSeries("2010-06-03", 265.18, 265.55, 260.41, 263.12, 23200600));
        aList.add(new EquityTimeSeries("2010-06-04", 258.21, 261.90, 254.63, 255.96, 27020300));
        aList.add(new EquityTimeSeries("2010-06-07", 258.29, 259.15, 250.55, 250.94, 31650200));
        aList.add(new EquityTimeSeries("2010-06-08", 253.24, 253.80, 245.65, 249.33, 35711300));
        aList.add(new EquityTimeSeries("2010-06-09", 251.47, 251.90, 242.49, 243.20, 30478800));
        aList.add(new EquityTimeSeries("2010-06-10", 244.84, 250.98, 242.20, 250.51, 27655300));
        aList.add(new EquityTimeSeries("2010-06-11", 248.23, 253.86, 247.37, 253.51, 19454100));
        return aList;
    }

    public static void main(String[] args) {
        List<EquityTimeSeries> aList = new ArrayList<EquityTimeSeries>();


        aList.add(new EquityTimeSeries("2010-06-11", 248.23, 253.86, 247.37, 253.51, 19454100));
        aList.add(new EquityTimeSeries("2010-06-10", 244.84, 250.98, 242.20, 250.51, 27655300));
        aList.add(new EquityTimeSeries("2010-06-09", 251.47, 251.90, 242.49, 243.20, 30478800));
        aList.add(new EquityTimeSeries("2010-06-08", 253.24, 253.80, 245.65, 249.33, 35711300));
        aList.add(new EquityTimeSeries("2010-06-07", 258.29, 259.15, 250.55, 250.94, 31650200));
        aList.add(new EquityTimeSeries("2010-06-04", 258.21, 261.90, 254.63, 255.96, 27020300));
        aList.add(new EquityTimeSeries("2010-06-03", 265.18, 265.55, 260.41, 263.12, 23200600));
        aList.add(new EquityTimeSeries("2010-06-02", 264.54, 264.80, 260.33, 263.95, 24550900));
        aList.add(new EquityTimeSeries("2010-06-01", 259.69, 265.94, 258.96, 260.83, 31249000));


        for (EquityTimeSeries ts : aList) {
            ts.dumpData();
        }
    }

}
