package skylight1.marketapp.model;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: 1/11/11
 * Time: 11:38 PM
 */
public class WatchListTest {

    @Before
    public void init() {


    }

    @Test
    public void twoPlusTwo() { // Press Ctrl-Shift-F10 here to run only twoPlusTwo test
        assertThat(2 + 2, is(4));
    }

    @Test
    public void initConstructor() {
        WatchListItem watchListItem1 = new WatchListItem("AAPL");
        WatchListItem watchListItem2 = new WatchListItem("ORCL");

        SortedSet<WatchListItem> techList = new TreeSet<WatchListItem>();
        techList.add(watchListItem1);
        techList.add(watchListItem2);
        WatchList watchList = new WatchList("technology", techList);
        watchList.getItems().add(watchListItem1);
        watchList.getItems().add(watchListItem2);

    }

}
