package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public abstract class SkylightActivity extends Activity {
	public static final String DIFFICULTY_LEVEL = SkylightActivity.class.getPackage().getName() + ".difficultyLevel";

	public static final String HIGH_SCORE_PREFERENCE_NAME = "highScore";

	public static final int SOBER_DIFFICULTY_LEVEL = 0;

	public static final int BUZZED_DIFFICULTY_LEVEL = 5;

	public static final int SMASHED_DIFFICULTY_LEVEL = 10;

	public static final Uri URI_SKY = Uri.parse("http://balancethebeer.com");

	protected static final String PASS_THE_DRINK_PREFS_FILE = "PassTheDrinkPrefsFile";

	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//
//		// remove Title bar and Status bar from screen
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		addDependencies(dependencyInjectingObjectFactory);

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, Menu.NONE, "about");
		menu.add(0, 1, Menu.NONE, "exit");
		return supRetVal;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:

			Intent intent = new Intent(Intent.ACTION_VIEW, URI_SKY);
			startActivity(intent);
			return true;

		case 1:
			finish();
			moveTaskToBack(true);
			return true;
		default:
			return false;
		}
	}

	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}
