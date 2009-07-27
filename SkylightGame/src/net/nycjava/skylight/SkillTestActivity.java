package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.RandomForceService;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
import net.nycjava.skylight.service.impl.BalancedObjectPublicationServiceImpl;
import net.nycjava.skylight.service.impl.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.impl.RandomForceServiceImpl;
import net.nycjava.skylight.service.impl.SensorAppliedForceAdapterServiceAndroidImpl;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class SkillTestActivity extends SkylightActivity {
	public final static int REMAINING_TIME = 15; // TODO make configurable?

	public final static float MIN_DISTANCE = 0.1f; // TODO make configurable?

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	@Dependency
	private View contentView;

	private CountdownObserver countdownObserver;

	@Dependency
	private RandomForceService randomForceService;

	@Dependency
	private SensorAppliedForceAdapter sensorAppliedForceAdapter;

	@Dependency
	private BalancedObjectPublicationService balanceObjPublicationService;

	private BalancedObjectObserver balanceObjObserver;

	private int width, height;

	private int difficultyLevel;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {

		SensorManager aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class, aSensorManager);

		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(BalancedObjectPublicationService.class,
				BalancedObjectPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(RandomForceService.class,
				RandomForceServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(SensorAppliedForceAdapter.class,
				SensorAppliedForceAdapterServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationObject(View.class, (LinearLayout) getLayoutInflater()
				.inflate(R.layout.skilltest, null));

		aDependencyInjectingObjectFactory.registerImplementationObject(Integer.class, difficultyLevel);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		difficultyLevel = getIntent().getIntExtra(DIFFICULTY_LEVEL, 0);

		super.onCreate(savedInstanceState);

		randomForceService.setDifficultyLevel(difficultyLevel);
		balanceObjPublicationService.setDifficultyLevel(difficultyLevel);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		Log.i(SkillTestActivity.class.getName(), String.format("width=%d height=%d", width, height));

		balanceObjObserver = new BalancedObjectObserver() {
			public void balancedObjectNotification(float directionOfFallingInRadians, float anAngleOfLeanInRadians) {
			}

			public void fallenOverNotification() {
				final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
				intent.putExtra(DIFFICULTY_LEVEL, difficultyLevel);
				startActivity(intent);
				finish();
			}
		};
		balanceObjPublicationService.addObserver(balanceObjObserver);

		countdownObserver = new CountdownObserver() {
			public void countdownNotification(int remainingTime) {
				if (remainingTime == 0) {
					countdownPublicationService.stopCountdown();
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					intent.putExtra(DIFFICULTY_LEVEL, difficultyLevel);
					startActivity(intent);
					finish();
				}
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
		countdownPublicationService.setDuration(REMAINING_TIME);

		View skillTestView = (View) contentView.findViewById(R.id.skillTestView);
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(skillTestView);
		contentView.setBackgroundResource(R.drawable.background_table);
		setContentView(contentView);

		sensorAppliedForceAdapter.start();
		randomForceService.start();
		countdownPublicationService.startCountdown();
	}

	@Override
	protected void onPause() {
		super.onPause();

		countdownPublicationService.stopCountdown();
		countdownPublicationService.removeObserver(countdownObserver);
		balanceObjPublicationService.removeObserver(balanceObjObserver);
		randomForceService.stop();
		sensorAppliedForceAdapter.stop();
	}
}
