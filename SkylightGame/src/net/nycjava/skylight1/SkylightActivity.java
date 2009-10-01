package net.nycjava.skylight1;

import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public abstract class SkylightActivity extends Activity {
	private static final int MENU_ITEM_0 = 0;
	private static final int MENU_ITEM_1 = 1;
	private static final int MENU_ITEM_2 = 2;

	public static final String DIFFICULTY_LEVEL = SkylightActivity.class.getPackage().getName() + ".difficultyLevel";

	public static final String DISPLAY_DEMO = SkylightActivity.class.getPackage().getName() + ".displayDemo";

	public static final String HIGH_SCORE_PREFERENCE_NAME = "highLevel";

	public static final int EASY_DIFFICULTY_LEVEL = 0;

	public static final int NORMAL_DIFFICULTY_LEVEL = 5;

	public static final int HARD_DIFFICULTY_LEVEL = 10;

	public static Uri URI_SKY;
	
	public static String ANDROID_ID;

	protected static final String SKYLIGHT_PREFS_FILE = "SkylightPrefsFile";

	private static final int DIALOG_SHOW_LEVEL_ID = 0;

	private static final int DIALOG_ABOUT_ID = 1;

	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		addDependencies(dependencyInjectingObjectFactory);

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);

		if(URI_SKY==null) {
			URI_SKY = Uri.parse(getString(R.string.instructions));
			ANDROID_ID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			if(ANDROID_ID==null)
				ANDROID_ID=String.format("EMULATOR-%d",System.currentTimeMillis());
			Log.println(Log.DEBUG, this.getLocalClassName(), "ANDROID_ID = "+ANDROID_ID);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ITEM_0, Menu.NONE, getString(R.string.instructions));
		menu.add(0, MENU_ITEM_1, Menu.NONE, getString(R.string.levelreached));
		menu.add(0, MENU_ITEM_2, Menu.NONE, getString(R.string.about));
		menu.getItem(MENU_ITEM_0).setIcon(android.R.drawable.ic_menu_help);
		menu.getItem(MENU_ITEM_1).setIcon(android.R.drawable.ic_menu_view);
		menu.getItem(MENU_ITEM_2).setIcon(android.R.drawable.ic_menu_info_details);
		return supRetVal;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			final Intent welcomeActivityIntent = new Intent();
			welcomeActivityIntent.putExtra(DISPLAY_DEMO, true);
			welcomeActivityIntent.setClass(SkylightActivity.this, WelcomeActivity.class);
			startActivity(welcomeActivityIntent);
			return true;
		case 1:
			showDialog(DIALOG_SHOW_LEVEL_ID);
			return true;
		case 2:
			showDialog(DIALOG_ABOUT_ID);
			return true;
		case 3:
			final Intent visitWebSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.websiteurl)));
			startActivity(visitWebSiteIntent);
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
		TextView text = null;
		switch (id) {
		case DIALOG_SHOW_LEVEL_ID:
			SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
			int oldHighScore = sharedPreferences.getInt(HIGH_SCORE_PREFERENCE_NAME, -1);

			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.level_dialog, (ViewGroup) findViewById(R.id.layout_root));

			dialog = new AlertDialog.Builder(this).setIcon(R.drawable.icon_small).setTitle(R.string.levelreached)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							removeDialog(DIALOG_SHOW_LEVEL_ID);
						}
					}).setView(layout).create();

			text = (TextView) layout.findViewById(R.id.highest_level);
			text.setText(String.format("%d", oldHighScore + 1));
			break;

		case DIALOG_ABOUT_ID:
			inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.about_dialog, (ViewGroup) findViewById(R.id.about_root));

			dialog = new AlertDialog.Builder(this).setIcon(R.drawable.icon_small).setTitle(R.string.about)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							removeDialog(DIALOG_ABOUT_ID);
						}
					}).setView(layout).create();

			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}
