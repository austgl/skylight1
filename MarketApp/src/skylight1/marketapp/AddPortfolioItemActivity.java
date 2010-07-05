package skylight1.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: Jul 4, 2010
 * Time: 6:48:54 PM
 */
public class AddPortfolioItemActivity extends Activity {  // This is intentionally not a GuiceActivity

    private static final String TAG = AddPortfolioItemActivity.class.getSimpleName();

//    @Inject
    private MarketDatabase marketDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_portfolio_item);

        findViewById(R.id.portfolio_item_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tickerStr = "";
                float avgPrice = 0;
                int numOfShares = 0;

                EditText tickerView = (EditText) findViewById(R.id.add_portfolio_item_ticker_text);
                EditText quantityView = (EditText) findViewById(R.id.add_portfolio_item_quantity_text);
                EditText avgPriceView = (EditText) findViewById(R.id.add_portfolio_item_avg_price_text);
                try {
                    tickerStr = tickerView.getText().toString();
                    avgPrice = Float.parseFloat(avgPriceView.getText().toString());
                    numOfShares = Integer.parseInt(quantityView.getText().toString());

                    // Clear for next entry
                    tickerView.setText("");
                    quantityView.setText("");
                    avgPriceView.setText("");
                    marketDatabase = new MarketDatabase(AddPortfolioItemActivity.this);
                    marketDatabase.open();
                    marketDatabase.insertPortfolioItem(tickerStr, numOfShares, avgPrice);
                    marketDatabase.cleanup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Adding Portfolio Item: " + tickerStr + "," + numOfShares + "," + avgPrice);

            }
        });
    }
}
