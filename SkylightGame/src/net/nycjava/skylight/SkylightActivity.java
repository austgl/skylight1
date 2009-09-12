package net.nycjava.skylight;

import net.nycjava.skylight.R;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public abstract class SkylightActivity extends Activity {
	public static final String DIFFICULTY_LEVEL = SkylightActivity.class.getPackage().getName() + ".difficultyLevel";
	public static final String DISPLAY_DEMO = SkylightActivity.class.getPackage().getName() + ".displayDemo";

	public static final String HIGH_SCORE_PREFERENCE_NAME = "highScore";

	public static final int EASY_DIFFICULTY_LEVEL = 0;

	public static final int NORMAL_DIFFICULTY_LEVEL = 5;

	public static final int HARD_DIFFICULTY_LEVEL = 10;

	public static Uri URI_SKY;

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
		
		 URI_SKY= Uri.parse(getString(R.string.instructions));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, Menu.NONE, getString(R.string.instructions));
		menu.add(0, 1, Menu.NONE, getString(R.string.levelreached));
		menu.add(0, 2, Menu.NONE, getString(R.string.about));
		menu.add(0, 3, Menu.NONE, getString(R.string.website));
		menu.add(0, 4, Menu.NONE, getString(R.string.exit));
		menu.getItem(0).setIcon(android.R.drawable.ic_menu_help);
		menu.getItem(1).setIcon(android.R.drawable.ic_menu_info_details);
		menu.getItem(2).setIcon(android.R.drawable.ic_menu_info_details);
		menu.getItem(3).setIcon(android.R.drawable.ic_menu_agenda);
		menu.getItem(4).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return supRetVal;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(!(this instanceof SkillTestActivity)) {
				final Intent intent = new Intent();
				intent.putExtra(DISPLAY_DEMO, true);
				intent.setClass(SkylightActivity.this, WelcomeActivity.class);
				startActivity(intent);
			}
			return true;
		case 1:
			showDialog(DIALOG_SHOW_LEVEL_ID);
			return true;
		case 2:
			showDialog(DIALOG_ABOUT_ID);
			return true;
		case 3:
			final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.websiteurl)));
			startActivity(intent);
			return true;
		case 4:
			finish();
			moveTaskToBack(true);
			return true;
		default:
			return false;
		}
	}

    @Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_SHOW_LEVEL_ID:
	    	SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
		    int oldHighScore = sharedPreferences.getInt(HIGH_SCORE_PREFERENCE_NAME, 0);	   
	    	dialog = new AlertDialog.Builder(this)
            .setIcon(R.drawable.icon_small)
            .setTitle(R.string.levelreached)
            .setMessage(String.format("%s   %d\n", getString(R.string.levelmessage),oldHighScore+1))
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                   removeDialog(DIALOG_SHOW_LEVEL_ID);
                }
            })
            .create();
	    	/* custom:
			Context mContext = getApplicationContext();
			dialog = new Dialog(mContext);
			dialog.setContentView(R.layout.custom_dialog);
			dialog.setTitle(R.string.levelreached);
			TextView text = (TextView) dialog.findViewById(R.id.text);
			text.setText(R.string.levelmessage);
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.icon_small);
			...
			*/
	    	break;
	    case DIALOG_ABOUT_ID:
	    	dialog = new AlertDialog.Builder(this)
            .setIcon(R.drawable.icon_small)
            .setTitle(R.string.about)
            .setMessage(R.string.aboutmessage)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                   removeDialog(DIALOG_ABOUT_ID);
                }
            })
            .create();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}
