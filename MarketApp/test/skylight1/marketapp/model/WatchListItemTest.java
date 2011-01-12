package skylight1.marketapp.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: 1/11/11
 * Time: 11:38 PM
 */
public class WatchListItemTest {

    @Before
    public void init() {


    }

    @Test
    public void twoPlusTwo() { // Press Ctrl-Shift-F10 here to run only twoPlusTwo test
        assertThat(2 + 2, is(4));
    }

    @Test
    public void initTest() {
        WatchListItem watchListItem = new WatchListItem("AAPL");
        assertThat(watchListItem.getTicker(), is(equalTo("AAPL")));
    }
}
