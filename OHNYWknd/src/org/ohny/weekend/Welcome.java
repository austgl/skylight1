package org.ohny.weekend;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import skylight1.util.Assets;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Welcome extends Activity implements OnClickListener{
   
	static final String TAG = "OHNY Weekend";
	static final int ABOUT = 0;
	static final int GREET_DIALOG = 1;
	static final int ITS_OVER_DIALOG = 2;
	static final String INTENT_CATAGORY_TYPE = "android.intent.category.LAUNCHER";
	static final String MAP_COMPONENT_NAME = "com.google.android.apps.maps/com.google.android.maps.MapsActivity";

	// Google My Maps url/uri strings
//	static final String MANHATTAN1 = "http://maps.google.com/maps/ms?ie=UTF8&hl=en&msa=0&msid=101509637019979626548.00048f4bd84c82a876629&ll=40.736292,-73.993446&spn=0.068027,0.169086&z=13";
//	static final String MANHATTAN1 = "http://maps.google.com/maps/ms?ie=UTF8&hl=en&msa=0&ll=40.748037,-73.997211&spn=0.068015,0.169086&z=13&iwloc=00049173d61f6f468c6bf&msid=101509637019979626548.00049173d46d78e78cce8";
    static final String MANHATTAN1 = "http://maps.google.com/maps/ms?ie=UTF8&hl=en&msa=0&msid=116931521422408434103.000491f6895efdb759aa6&ll=40.741014,-73.988457&spn=0.122518,0.3368&z=12";
    static final String BROOKLYN = "";
    static final String BRONX = "";
    static final String QUEENS = "";
    static final String STATEN_ISLAND = "";
    static final String DONATE = "http://www.nycharities.org/donate/c_donate.asp?CharityCode=1114";
    
	Uri uriManhattan1;
	Uri uriBrooklyn;
	Uri uriBronx;
	Uri uriQueens;
	Uri uriStatenIsland;
	
	private GoogleAnalyticsTracker tracker;
	private String ga_id;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ga_id = Assets.getString("ga_id",this);
        if(ga_id.length()>0) {
        	//start tracker can be started with a dispatch interval (in seconds).
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.start(ga_id, 20, this);
        }
        
        setContentView(R.layout.main);
        
        // transform strings into URI format
        uriManhattan1 = Uri.parse(MANHATTAN1);
        
        Calendar endDate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        endDate.set(2010, Calendar.OCTOBER, 11);
        Date dueDate = endDate.getTime();
        Date today = new Date();
        // show dialog
        if(today.after(dueDate)) {
        	showDialog(ITS_OVER_DIALOG);
        } else {
        	showDialog(GREET_DIALOG);
        }
        
        // setup main screen buttons
        View m1 = findViewById(R.id.Button01);
        m1.setOnClickListener(this);  	
        View m2 = findViewById(R.id.Button02);
        m2.setOnClickListener(this);  	
        View m3 = findViewById(R.id.Button03);
        m3.setOnClickListener(this);  	
        View m4 = findViewById(R.id.Button04);
        m4.setOnClickListener(this);  	
        View m5 = findViewById(R.id.Button05);
        m5.setOnClickListener(this);
        
        View donateButton =  findViewById(R.id.DonateButton);	
        donateButton.setOnClickListener(new OnClickListener() {
	
	    @Override
	    public void onClick(View v) {
	    Intent i = new Intent(Intent.ACTION_VIEW);
	    i.setData(Uri.parse(DONATE));
		if(tracker!=null) {
			tracker.trackPageView("/donate");
		}
	    startActivity(i);
	    }
	
        });
    }

	@Override
	public void onClick(View v) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setComponent(ComponentName.unflattenFromString("com.google.android.apps.maps/com.google.android.maps.MapsActivity"));
		intent.addCategory(INTENT_CATAGORY_TYPE);
		
		switch(v.getId()) {
		case R.id.Button01:
			intent.setData(uriBronx);
			if(tracker!=null) {
				tracker.trackPageView("/bronx");
			}
			break;
		case R.id.Button02:
			intent.setData(uriQueens);
			if(tracker!=null) {
				tracker.trackPageView("/Queens");
			}
			break;
		case R.id.Button03:
			intent.setData(uriManhattan1);
			if(tracker!=null) {
				tracker.trackPageView("/manhattan1");
			}
			break;
		case R.id.Button04:
			intent.setData(uriBrooklyn);
			if(tracker!=null) {
				tracker.trackPageView("/brooklyn");
			}
			break;
		case R.id.Button05:
			intent.setData(uriStatenIsland);
			if(tracker!=null) {
				tracker.trackPageView("/statenisland");
			}
			break;
	}
		startActivity(intent);	
	}
    
    /* create menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
    	return true;
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case ABOUT:
        	final Intent intent_about = new Intent(Welcome.this, About.class);
    		if(tracker!=null) {
    			tracker.trackPageView("/about");
    		}
    		startActivity(intent_about);
            return true;
        }
        return false;
    }
    
    @Override
    public Dialog onCreateDialog(int anId) {
    	String buttonString = "OK";
    	AlertDialog.Builder messageDialog = new AlertDialog.Builder(this);
    	messageDialog.setTitle("OHNY Weekend");
    	messageDialog.setNegativeButton(buttonString, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
  
    	});
    	switch(anId) {
    	case (GREET_DIALOG): 
    		messageDialog.setMessage("Welcome to the OHNY Weekend 2010 Android App. Please check your specific site for Open House hours. Not all sites are available on both days.");
    	    return messageDialog.create();
    	case (ITS_OVER_DIALOG):
    		messageDialog.setMessage("OHNY Weekend 2010 is now over. Thanks for particpating!");
    		return messageDialog.create();
    	}	
    	return null;
    }
    
    @Override
    protected void onDestroy() {
      super.onDestroy();
      if(tracker!=null){
    	  tracker.stop();
      }
    }
}
