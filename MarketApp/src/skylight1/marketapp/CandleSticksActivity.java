package skylight1.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import skylight1.marketapp.view.CandleDrawView;
public class CandleSticksActivity extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // Set full screen view
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        
	        String ticker = null;
	        Bundle  tickerinfo = null;
	        if(this.getIntent() != null){
	        	tickerinfo = this.getIntent().getExtras();
	        }
	        if(tickerinfo != null && 
	        		tickerinfo.getString(PortfolioActivity.TICKER) != null){	        	
	        	ticker = tickerinfo.getString(PortfolioActivity.TICKER);	        	
	        }
	        else{
	        	Log.e("CandleSticksActivity","CandleSticksActivity - ticker is null");	
	        	//set null ticker handling in CandleDrawView
	        	//till then ticker is "GOOG"
	        	ticker = "GOOG";
	  
	        }
	         	        
	        CandleDrawView customView = new CandleDrawView(this,ticker);
	        setContentView(customView);
	        customView.requestFocus();
	    }
}
