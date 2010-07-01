package skylight1.marketapp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by IntelliJ IDEA.
 * User: melling
 * Date: Jun 30, 2010
 * Time: 3:10:41 PM
 */
public class HelpActivity extends Activity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        WebView mWebView;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.loadUrl("file:///android_asset/help_watchlist.html");

    }
}
