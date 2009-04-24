package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CameraObscurementPublicationService;
import net.nycjava.skylight.service.CameraObscurementPublicationServiceAndroidImp;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.DestinationPublicationService;
import net.nycjava.skylight.service.DestinationPublicationServiceImpl;
import net.nycjava.skylight.service.SteadinessPublicationService;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class SkillTestActivity extends SkylightActivity {
	@Dependency
	private DestinationPublicationService destinationPublicationService;

	@Dependency
	private SteadinessPublicationService steadinessPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	@Dependency
	private CameraObscurementPublicationService cameraObscurementPublicationService;

	@Dependency
	private LinearLayout contentView;

	private CountdownObserver countdownObserver;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationClass(DestinationPublicationService.class,
				DestinationPublicationServiceImpl.class);
		// aDependencyInjectingObjectFactory.registerImplementationClass(SteadinessPublicationService.class,
		// SteadinessPublicationServiceAndroidImpl.class);
		aDependencyInjectingObjectFactory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);
		aDependencyInjectingObjectFactory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImp.class);
		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.skilltest, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(contentView);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		final Intent intent = new Intent(SkillTestActivity.this, WelcomeActivity.class);
		startActivity(intent);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		countdownObserver = new CountdownObserver() {
			@Override
			public void countdownNotification(int remainingTime) {
				if (remainingTime == 0) {
					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					startActivity(intent);
				}
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
	}

	@Override
	protected void onPause() {
		countdownPublicationService.removeObserver(countdownObserver);
		super.onPause();
	}
}
