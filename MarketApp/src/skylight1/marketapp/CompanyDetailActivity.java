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
//		SharedPreferences settings = PreferenceManager
//				.getDefaultSharedPreferences(getBaseContext());
		
		Bundle  tickerinfo = this.getIntent().getExtras(); 
		
		String parsevalue = "---";
		String id = String.valueOf(tickerinfo.getInt(PortfolioActivity.ITEM_ID));
				
		String ticker = tickerinfo.getString(PortfolioActivity.TICKER); 
		String name = tickerinfo.getString(PortfolioActivity.NAME); 
		String nameticker = name+"(NasdaqGS:"+ticker+") ";		
		TextView strId = (TextView)findViewById(R.id.id); // havn't use, may be don't need it?		
		
		TextView name_ticker = (TextView)findViewById(R.id.name_tiker);
		name_ticker.setText((nameticker!=null)?nameticker:parsevalue);
	 
		TextView price = (TextView) findViewById(R.id.price);
		price.setText((tickerinfo.getString(PortfolioActivity.PRICE)!=null)?tickerinfo.getString(PortfolioActivity.PRICE):parsevalue);
		
		TextView askedsize = (TextView) findViewById(R.id.askSize);
		askedsize.setText((tickerinfo.getString(PortfolioActivity.ASKSIZE)!=null)?tickerinfo.getString(PortfolioActivity.ASKSIZE):parsevalue);
		
		TextView todayPercentChange = (TextView)findViewById(R.id.todaysPercentChange);
		todayPercentChange.setText((tickerinfo.getString(PortfolioActivity.TODAYPERCENTCHANGE)!=null)?tickerinfo.getString(PortfolioActivity.TODAYPERCENTCHANGE):parsevalue);
		
		TextView todayPriceChange = (TextView)findViewById(R.id.todaysPriceChange);
		todayPriceChange.setText((tickerinfo.getString(PortfolioActivity.TODAYSPRICECHANGE)!=null)?tickerinfo.getString(PortfolioActivity.TODAYPERCENTCHANGE):parsevalue);
		
		TextView volume = (TextView)findViewById(R.id.volume);
		volume.setText((tickerinfo.getString(PortfolioActivity.VOLUME)!=null)?tickerinfo.getString(PortfolioActivity.VOLUME):parsevalue);
		
		TextView stockexchange = (TextView)findViewById(R.id.exchange);
		stockexchange.setText(tickerinfo.getString(PortfolioActivity.EXCHANGE)!=null?tickerinfo.getString(PortfolioActivity.EXCHANGE):parsevalue);
		
		TextView ebitda = (TextView) findViewById(R.id.ebitda);
		ebitda.setText(tickerinfo.getString(PortfolioActivity.EBITDA)!=null?tickerinfo.getString(PortfolioActivity.EBITDA):parsevalue);
		
		TextView pegratio = (TextView)findViewById(R.id.pegRatio);
		pegratio.setText(tickerinfo.getString(PortfolioActivity.PEGRATIO)!=null?tickerinfo.getString(PortfolioActivity.PEGRATIO):parsevalue);
		
		TextView movavg50 = (TextView)findViewById(R.id.mavg50);
		movavg50.setText(tickerinfo.getString(PortfolioActivity.MAVG50)!=null?tickerinfo.getString(PortfolioActivity.MAVG50):parsevalue);
		
		TextView movavg200 = (TextView)findViewById(R.id.mavg200);
		movavg200.setText(tickerinfo.getString(PortfolioActivity.MAVG200)!=null?tickerinfo.getString(PortfolioActivity.MAVG200):parsevalue);
		
		TextView peratio = (TextView)findViewById(R.id.peRatio);
		peratio.setText(tickerinfo.getString(PortfolioActivity.PERATIO)!=null?tickerinfo.getString(PortfolioActivity.PERATIO):parsevalue);
		
		TextView bid = (TextView)findViewById(R.id.bidSize);
		bid.setText(tickerinfo.getString(PortfolioActivity.BIDSIZE)!=null?tickerinfo.getString(PortfolioActivity.BIDSIZE):parsevalue);

	}

}
