package skylight1.marketapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class CompanyDetails extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String id = String.valueOf(settings.getInt(PortfolioActivity.ITEM_ID, 1));
		String tiker = settings.getString(PortfolioActivity.TICKER, "1");
		String number_ofshares = settings.getString(PortfolioActivity.NUMBER_OF_SHARES, "1");
		String current_price = settings.getString(PortfolioActivity.CURRENT_PRICE, "1");
		String avg_price = settings.getString(PortfolioActivity.AVG_PRICE, "1");
		
		
		TextView strid = (TextView)findViewById(R.id.id);
		strid.setText(id);
		TextView strtiker = (TextView)findViewById(R.id.ticker);
		strtiker.setText(tiker);
		TextView strnumber = (TextView)findViewById(R.id.numberOfShares);
		strnumber.setText(number_ofshares);
		TextView strprice = (TextView)findViewById(R.id.currentPrice);
		strprice.setText(current_price);
		TextView stravgprice = (TextView)findViewById(R.id.avgPrice);		
		stravgprice.setText(avg_price);

	}

}
;