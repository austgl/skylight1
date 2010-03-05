package skylight1.nycevents;

import static skylight1.nycevents.Constants.CURRENT_TAB_PREF;
import static skylight1.nycevents.Constants.LAST_UPDATED_PREF;
import skylight1.util.LoggingExceptionHandler;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 * Show other activities in tabs for quick and easy access.
 *
 */
public class Tabs extends TabActivity {

	public static String ANDROIDLOGS_URL;

	private static final String LOG_TAG = Tabs.class.getName();

	// Constants for referring to tabs on the screen.
	private static final String PARKS_TAB = Constants.PARKS;

	private static final String ART_TAB = Constants.ART;

	private static final String MUSIC_TAB = Constants.MUSIC;

	// Receiver for intent to change to details tab.
	// private final BroadcastReceiver mShowDetailsReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(final Context context, final Intent intent) {
	// if ( LOG ) Log.d(LOG_TAG, "Received show details intent.");
	// getTabHost().setCurrentTabByTag(DETAIL_TAB);
	// }
	// };

	private String getVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "package name not found", e);
            return "";
        }
    }

	private boolean isDebug() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE ? true : false;
        } catch (PackageManager.NameNotFoundException e) {
        	Log.e(LOG_TAG, "package name not found", e);
        }
		return false;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Constants.debug = isDebug();

		if (Constants.debug) {
			Log.i(LOG_TAG, "onCreate()");
		}

		if(!isDebug()) {
			ANDROIDLOGS_URL = (String) getText(R.string.log_url);
		}
		LoggingExceptionHandler.setURL(ANDROIDLOGS_URL);
		Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler(
				this));

		// Setup tabs.
		final TabHost tabs = getTabHost();
		final Resources res = getResources();

		tabs.addTab(tabs.newTabSpec(PARKS_TAB).setIndicator(
				getText(R.string.tabs_list_indicator),
				res.getDrawable(R.drawable.nycpark)).setContent(
				new Intent(this, ParkLister.class)));
		tabs.addTab(tabs.newTabSpec(ART_TAB).setIndicator(
				getText(R.string.tabs_details_indicator),
				res.getDrawable(R.drawable.paint)).setContent(
				new Intent(this, ArtLister.class)));
		tabs.addTab(tabs.newTabSpec(MUSIC_TAB).setIndicator(
				getText(R.string.tabs_map_indicator),
				res.getDrawable(R.drawable.music)).setContent(
				new Intent(this, MusicLister.class)));

		// Restore tab user was last on.
		final SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		final String lastTabTag = settings.getString(CURRENT_TAB_PREF,
				PARKS_TAB);
		tabs.setCurrentTabByTag(lastTabTag);

		// Store tab switched to for restoring later if restarted.
		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(final String tabId) {
				Log.d(LOG_TAG, "tab changed to "+tabId);
				final Editor edit = settings.edit();
				edit.putString(CURRENT_TAB_PREF, tabId);
				edit.commit();
			}
		});

		final long lastUpdate = settings.getLong(LAST_UPDATED_PREF,0L);

		// only start if it hasn't been already started (by checking last update stamp):
		if(System.currentTimeMillis()-lastUpdate > AlarmManager.INTERVAL_HALF_DAY || lastUpdate==0L) {

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent();
			intent.setClass(this, DatabaseRefresher.class);

			long startTime = 0L;
			if(lastUpdate==0L) { // if this is the very first time, delay the start:
				startTime = System.currentTimeMillis() + 6 * 60 * 60 * 1000; // for 6 hours from now
			}

        	Log.i(LOG_TAG, String.format("starting alarm service in %d milliseconds", startTime));
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, //inexact saves battery
					startTime, AlarmManager.INTERVAL_HALF_DAY,
					PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) );
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		final int groupId = 0;
		final int aboutId = 0;
		menu.add(groupId, aboutId, Menu.NONE, getString(R.string.about))
				.setIcon(android.R.drawable.ic_menu_info_details);
		return supRetVal;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			showDialog(Constants.DIALOG_ABOUT_ID);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater inflater = null;
		View layout = null;
		switch (id) {
		case Constants.DIALOG_ABOUT_ID:
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.about_dialog,
					(ViewGroup) findViewById(R.id.about_root));
			dialog = new AlertDialog.Builder(this).setIcon(R.drawable.icon)
					.setMessage(getApplicationContext().getString(R.string.about_text,getVersionName()))
					.setTitle(R.string.about_title).setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(Constants.DIALOG_ABOUT_ID);
								}
							}).setView(layout).create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Constants.debug)
			Log.i(LOG_TAG, "onResume()");
		// registerReceiver(mShowDetailsReceiver , new
		// IntentFilter(DETAILS_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Constants.debug) {
			Log.i(LOG_TAG, "onPause()");
		}
		// unregisterReceiver(mShowDetailsReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		if (Constants.debug)
			Log.i(LOG_TAG, "onDestroy()");
	}

}
