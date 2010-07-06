package skylight1.marketapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class CompanyDetailActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		String id = String.valueOf(settings.getInt(PortfolioActivity.ITEM_ID, 1));
		String ticker = settings.getString(PortfolioActivity.TICKER, "1");
		String numberOfShares = settings.getString(PortfolioActivity.NUMBER_OF_SHARES, "1");
		String currentPrice = settings.getString(PortfolioActivity.CURRENT_PRICE, "1");
		String avgPrice = settings.getString(PortfolioActivity.AVG_PRICE, "1");
		
		
		TextView strId = (TextView)findViewById(R.id.id);
		strId.setText(id);
		TextView tickerStr = (TextView)findViewById(R.id.ticker);
		tickerStr.setText(ticker);
		TextView numberSharesStr = (TextView)findViewById(R.id.numberOfShares);
		numberSharesStr.setText(numberOfShares);
		TextView priceStr = (TextView)findViewById(R.id.currentPrice);
		priceStr.setText(currentPrice);
		TextView avgPriceStr = (TextView)findViewById(R.id.avgPrice);
		avgPriceStr.setText(avgPrice);

	}

}
