package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.view.Preview;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * waiting for player to obscure camera; camera obscured; counting down start; camera unobscured; countdown complete
 */
public class GetReadyActivity extends SkylightActivity {
	@Dependency
	private View view;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		aDependencyInjectingObjectFactory.registerImplementationObject(View.class, getLayoutInflater().inflate(
				R.layout.getready, null));
		aDependencyInjectingObjectFactory.registerImplementationObject(Preview.class, new Preview(this));
	}

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