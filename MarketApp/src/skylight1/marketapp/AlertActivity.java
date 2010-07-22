package skylight1.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: May 20, 2010
 * Time: 7:20:30 PM
 */
public class AlertActivity extends Activity {

    private static final String TAG = AlertActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);



        findViewById(R.id.saveTickersButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addTicker("AAPL");
                Log.i(TAG, "Saving Watchlist");
            }
        });

        findViewById(R.id.loadTickersButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.i(TAG, "Loading Watchlist");
            }
        });
    }


    public void addTicker(String aTicker) {
        Log.i(TAG, "Adding ticker: " + aTicker);
    }

    public void removeTicker(String aTicker) {
//        mDbHelper.deletePort(aTicker);


    }
}
