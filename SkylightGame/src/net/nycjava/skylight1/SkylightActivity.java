package net.nycjava.skylight1;

import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.dependencyinjection.DependencyInjector;
import skylight1.util.Assets;
import skylight1.util.BuildInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class SkylightActivity extends Activity {
	private static final int MENU_ITEM_0 = 0;
	private static final int MENU_ITEM_1 = 1;

	public static final String DIFFICULTY_LEVEL = SkylightActivity.class
			.getPackage().getName() + ".difficultyLevel";

	public static final String COMPASS_READINGS = SkylightActivity.class
			.getPackage().getName() + ".compassReadings";

	public static final String DISPLAY_DEMO = SkylightActivity.class
			.getPackage().getName() + ".displayDemo";

	public static final String HIGH_SCORE_PREFERENCE_NAME = "highLevel";
	public static final String GLOBAL_HIGH_SCORE_PREFERENCE_NAME = "globalHighLevel";

	public static final int EASY_DIFFICULTY_LEVEL = 0;

	public static final int NORMAL_DIFFICULTY_LEVEL = 5;

	public static final int HARD_DIFFICULTY_LEVEL = 10;

	public static String androidId;
	public static boolean isDebug;

	protected static final String SKYLIGHT_PREFS_FILE = "SkylightPrefsFile";

	private static final int DIALOG_SHOW_LEVEL_ID = 0;

	private static final int DIALOG_ABOUT_ID = 1;

	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	protected GoogleAnalyticsTracker tracker;
	private String ga_id;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        ga_id = Assets.getString("ga_id",this);
        if(ga_id.length()>0) {
        	//start tracker can be started with a dispatch interval (in seconds).
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.setProductVersion("BTB", BuildInfo.getVersionName(this));
            tracker.start(ga_id, this);
        }

		// adjust media volume (After the glass crash instead of ring volume)
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		addDependencies(dependencyInjectingObjectFactory);

		// since activities are instantiated by the framework, use the
		// dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory)
				.injectDependenciesForClassHierarchy(this);

		androidId = BuildInfo.getAndroidID(this);
		if (androidId == null) {
			androidId = String
					.format("EMULATOR-%d", System.currentTimeMillis());
		}
		isDebug = BuildInfo.isDebuggable(this);
		if (isDebug) {
			Log.println(Log.DEBUG, this.getLocalClassName(), "androidId = "
					+ androidId);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM_0, Menu.NONE, getString(R.string.levelreached));
		menu.add(0, MENU_ITEM_1, Menu.NONE, getString(R.string.about));
		menu.getItem(MENU_ITEM_0).setIcon(android.R.drawable.ic_menu_view);
		menu.getItem(MENU_ITEM_1).setIcon(
				android.R.drawable.ic_menu_info_details);
		return supRetVal;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_0:
			showDialog(DIALOG_SHOW_LEVEL_ID);
    		if(tracker!=null) {
    			tracker.trackPageView("/highscores");
    		}
			return true;
		case MENU_ITEM_1:
			showDialog(DIALOG_ABOUT_ID);
    		if(tracker!=null) {
    			tracker.trackPageView("/about");
    		}
			return true;
		case 3:
			final Intent visitWebSiteIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(getString(R.string.websiteurl)));
			startActivity(visitWebSiteIntent);
    		if(tracker!=null) {
    			tracker.trackPageView("/web");
    		}
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
		case DIALOG_SHOW_LEVEL_ID:
			SharedPreferences sharedPreferences = getSharedPreferences(
					SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
			int bestLevelCompleted = sharedPreferences.getInt(
					HIGH_SCORE_PREFERENCE_NAME, -1);
			int globalBestLevelCompleted = sharedPreferences.getInt(
					GLOBAL_HIGH_SCORE_PREFERENCE_NAME, -1);

			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.level_dialog,
					(ViewGroup) findViewById(R.id.layout_root));

			dialog = new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon_small)
					.setTitle(R.string.levelreached)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(DIALOG_SHOW_LEVEL_ID);
								}
							}).setView(layout).create();

			final TextView highestLevelText = (TextView) layout
					.findViewById(R.id.highest_level);
			highestLevelText.setText(String
					.format("%d", bestLevelCompleted + 1));

			final TextView globalHighestLevelText = (TextView) layout
					.findViewById(R.id.global_highest_level);
			if (globalBestLevelCompleted > 0) {
				globalHighestLevelText.setText(String.format("%d",
						globalBestLevelCompleted));
			}
			break;

		case DIALOG_ABOUT_ID:
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.about_dialog,
					(ViewGroup) findViewById(R.id.about_root));

			dialog = new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon_small)
					.setTitle(R.string.about)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(DIALOG_ABOUT_ID);
								}
							}).setView(layout).create();

			break;

		default:
			dialog = null;
		}
		return dialog;
	}

    @Override
    protected void onPause() {
        super.onPause();
        if(tracker!=null){
    	    tracker.dispatch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tracker!=null){
    	    tracker.dispatch();
    	    tracker.stop();
        }
    }

	abstract protected void addDependencies(
			DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}
