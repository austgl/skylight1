package skylight1.marketapp;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
                Intent intent = new Intent(MarketAppHomeActivity.this, AlertActivity.class);
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
                Intent intent = new Intent(MarketAppHomeActivity.this, PricingEngineActivity.class);
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
}