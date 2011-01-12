package skylight1.marketapp;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: 1/11/11
 * Time: 11:27 PM
 */
public class PortfolioItemTest {

    @Before
    public void init() {


    }

    @Test
    public void twoPlusTwo() { // Press Ctrl-Shift-F10 here to run only twoPlusTwo test
        assertThat(2 + 2, is(4));
    }

    @Test
    public void portfolioItemInitTest() {

        PortfolioItem p = new PortfolioItem("AAPL", 330.12f,1000,220f);
        assertThat(p.getTicker(), is(equalTo("AAPL")));
    }
}
