package skylight1.marketapp.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class EquityTimeSeriesTest2 {  // Press Ctrl-Shift-F10 here to run all tests in class

    @Before
    public void init() {


    }

    @Test
    public void twoPlusTwo() { // Press Ctrl-Shift-F10 here to run only twoPlusTwo test
        assertThat(2 + 2, is(4));
    }

    @Test
    public void seriesCreationTest2() {
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

        assertThat(aList.size(),is(9));

    }


    @Test
    public void seriesCreationTest() {

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


//        for (EquityTimeSeries ts : aList) {
//            ts.dumpData();
//        }
        assertThat(aList.size(),is(9));
    }
}