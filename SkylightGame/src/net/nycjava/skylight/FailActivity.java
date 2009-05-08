package net.nycjava.skylight;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.view.Preview;

/**
 * reporting unsteady hand; report acknowledged; reporting slow hand; report acknowledged; go to welcome
 */
public class FailActivity extends SkylightActivity {

	private View view;
	
	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		dependencyInjectingObjectFactory.registerImplementationObject(View.class, getLayoutInflater().inflate(
				R.layout.failMsg, null));
		dependencyInjectingObjectFactory.registerImplementationObject(Preview.class, new Preview(this));

	}

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(view);

		// Create our Preview view and set it as the content of our activity.
		// mPreview = new Preview(this);
		// setContentView(mPreview);
	}
}
