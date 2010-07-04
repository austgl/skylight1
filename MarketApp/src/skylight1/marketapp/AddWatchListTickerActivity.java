package skylight1.marketapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import skylight1.marketapp.model.PortDbAdapter;

import java.util.Arrays;
import java.util.HashSet;

public class AddWatchListTickerActivity extends GuiceActivity {
    private static final String TAG = AddWatchListTickerActivity.class.getSimpleName();

    @Inject
    private EquityPricingInformationFeed equityPricingInformationFeed;

    @Inject
    private MarketDatabase marketDatabase;

    @InjectView(R.id.add_watch_list_ticker_text)
    private EditText newTickerTextView;

    @InjectView(R.id.add_watch_list_ticker_add_button)
    private Button addButton;


    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
//
        setContentView(R.layout.add_watch_list_ticker);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PortDbAdapter mDbHelper = new PortDbAdapter(AddWatchListTickerActivity.this);
                mDbHelper.open();

                final String newTicker = newTickerTextView.getText().toString();
                Log.i(TAG, "New Ticker is: " + newTicker);
                marketDatabase.addWatchListTicker(newTicker);
                newTickerTextView.setText("");

                // mdm
                mDbHelper.createPort(newTicker);
                mDbHelper.close();
            }

        });
    }
}
