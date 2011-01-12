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

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: 1/11/11
 * Time: 11:03 PM
 */
public class CompanyDetailTest {

    @Before
    public void init() {


    }

    @Test
    public void twoPlusTwo() { // Press Ctrl-Shift-F10 here to run only twoPlusTwo test
        assertThat(2 + 2, is(4));
    }

    @Test
    public void companyDetailInitTest() {
        List<CompanyDetail> aList = new ArrayList<CompanyDetail>();
        aList.add(new CompanyDetail("AAPL"));
        aList.add(new CompanyDetail("GOOG"));
        aList.add(new CompanyDetail("ORCL"));

        assertThat(aList.size(),is(3));


    }
}
