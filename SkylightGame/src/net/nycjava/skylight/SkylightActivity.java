package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public abstract class SkylightActivity extends Activity {
	public static final String DIFFICULTY_LEVEL = SkylightActivity.class.getPackage().getName() + ".difficultyLevel";

	protected DependencyInjectingObjectFactory dependencyInjectingObjectFactory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove Title bar and Status bar from screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
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
             	Intent intent=getIntent();
               	String action=intent.getAction();
                	Intent intent1=new Intent(action,Uri.parse("http://nycjava.net"));
                
                	startActivity(intent1);
                    return true;
                
            case 1:
                finish();            	
            	return true;
            default:
                return false;
        }
    }
	abstract protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory);
}
