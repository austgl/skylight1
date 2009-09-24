package net.nycjava.skylight1.tdc;

import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.tdc.R;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SkylightTDC extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create a dependency injecting object factory
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class,
				(SensorManager) getSystemService(SENSOR_SERVICE));

		// since activities are instantiated by the framework, use the dependency injector directly to inject any
		// dependencies this activity may have
		// new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(this);

		setContentView(R.layout.main);

		final Button cameraButton = (Button) findViewById(R.id.cameraButton);
		final Button sensorsButton = (Button) findViewById(R.id.sensorsButton);
		final Button quitButton = (Button) findViewById(R.id.quitButton);

		cameraButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Intent intent = new Intent(SkylightTDC.this, SkylightCameraTDC.class);
				startActivity(intent);
				finish();
			}
		});

		sensorsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				final Intent intent = new Intent(SkylightTDC.this, SkylightSensorsTDC.class);
				startActivity(intent);
				finish();
			}
		});

		quitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}