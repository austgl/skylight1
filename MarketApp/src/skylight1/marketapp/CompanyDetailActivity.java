package skylight1.marketapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class CompanyDetailActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_detail);
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		
		String parsevalue = "---";
		String id = String.valueOf(settings.getInt(PortfolioActivity.ITEM_ID, 1));
		String ticker = settings.getString(PortfolioActivity.TICKER, parsevalue); 
		String name = settings.getString(PortfolioActivity.NAME,parsevalue); 
		String nameticker = name+"(NasdaqGS:"+ticker+") ";		
		TextView strId = (TextView)findViewById(R.id.id); // havn't use, may be don't need it?		
		TextView name_ticker = (TextView)findViewById(R.id.name_tiker);
		name_ticker.setText(nameticker);
	 
		TextView price = (TextView) findViewById(R.id.price);
		price.setText(settings.getString(PortfolioActivity.PRICE, parsevalue));
		TextView askedsize = (TextView) findViewById(R.id.askSize);
		askedsize.setText(settings.getString(PortfolioActivity.ASKSIZE,parsevalue));
		
		TextView todayPercentChange = (TextView)findViewById(R.id.todaysPercentChange);
		todayPercentChange.setText(settings.getString(PortfolioActivity.TODAYPERCENTCHANGE,parsevalue));
		
		TextView todayPriceChange = (TextView)findViewById(R.id.todaysPriceChange);
		todayPriceChange.setText(settings.getString(PortfolioActivity.TODAYSPRICECHANGE, parsevalue));
		
		TextView volume = (TextView)findViewById(R.id.volume);
		volume.setText(settings.getString(PortfolioActivity.VOLUME,parsevalue));
		
		TextView stockexchange = (TextView)findViewById(R.id.exchange);
		stockexchange.setText(settings.getString(PortfolioActivity.EXCHANGE,parsevalue));
		
		TextView ebitda = (TextView) findViewById(R.id.ebitda);
		ebitda.setText(settings.getString(PortfolioActivity.EBITDA,parsevalue));
		
		TextView pegratio = (TextView)findViewById(R.id.pegRatio);
		pegratio.setText(settings.getString(PortfolioActivity.PEGRATIO,parsevalue));
		
		TextView movavg50 = (TextView)findViewById(R.id.mavg50);
		movavg50.setText(settings.getString(PortfolioActivity.MAVG50,parsevalue));
		
		TextView movavg200 = (TextView)findViewById(R.id.mavg200);
		movavg200.setText(settings.getString(PortfolioActivity.MAVG200, parsevalue));
		
		TextView peratio = (TextView)findViewById(R.id.peRatio);
		peratio.setText(settings.getString(PortfolioActivity.PERATIO,parsevalue));
		
		TextView bid = (TextView)findViewById(R.id.bidSize);
		bid.setText(settings.getString(PortfolioActivity.BIDSIZE,parsevalue));

	}

}
