package skylight1.marketapp;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import skylight1.marketapp.feed.YahooEquityPricingInformationFeed;
import skylight1.marketapp.model.WatchList;
import skylight1.marketapp.model.WatchListItem;
import skylight1.marketapp.Synchronizer;
//import skylight1.marketapp.CandleStickActivity;

public class MarketAppHomeActivity extends GuiceActivity {
    @InjectView(R.id.watchListButton)
    private Button watchListButton;

    @InjectView(R.id.alertButton)
    private Button alertButton;
    @InjectView(R.id.portfolioButton)
    private Button portfolioButton;
    @InjectView(R.id.pricingEngineButton)
    private Button pricingEngineButton;
    @InjectView(R.id.candleButton)
    private Button candleButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        watchListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View aV) {
                Intent intent = new Intent(MarketAppHomeActivity.this, WatchListActivity.class);
                startActivity(intent);
            }
        });


        alertButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View aV) {
                Intent intent = new Intent(MarketAppHomeActivity.this, CandleSticksActivity.class);
                startActivity(intent);
            }
        });

        portfolioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View aV) {
                Intent intent = new Intent(MarketAppHomeActivity.this, PortfolioActivity.class);
                startActivity(intent);
            }
        });
        pricingEngineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View aV) {
                Intent intent = new Intent(MarketAppHomeActivity.this, CompanyDetailActivity0.class);
                startActivity(intent);
            }
        });
        candleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View aV) {
                Intent intent = new Intent(MarketAppHomeActivity.this, CandleSticksActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_watch_ticker) {
            Intent intent = new Intent(MarketAppHomeActivity.this, AddWatchListTickerActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.sync_cloud) {
        	List<WatchList> l = new ArrayList<WatchList>();
        	String result = "";
        	if(Synchronizer.doSynchronization(Synchronizer.generateDummyWatchLists(), l)) {
        		for(WatchList wl : l) {
        			result += wl.getListName() + ":\n";
        			for(WatchListItem i : wl.getItems())
        				result += "\t" + i.toString() + "\n";
        		}
        	}
        	else
        		result = "Synchronization failed";
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
          Log.i("FOO", "Sync'ing with cloud");
        } else if (item.getItemId() == R.id.helpMenu) {
            Intent intent = new Intent(MarketAppHomeActivity.this, HelpActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.candlestick) {
            Intent intent = new Intent(MarketAppHomeActivity.this, CandleSticksActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.testMenu) {
            YahooEquityPricingInformationFeed y = new YahooEquityPricingInformationFeed();
            y.getCompanyDetail("AAPL");
        } else if (item.getItemId() == R.id.preferenceMenu) {
            Intent intent = new Intent(MarketAppHomeActivity.this, Preferences.class);
            startActivity(intent);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

}