package net.nycjava.skylight1;

import java.util.concurrent.Future;

import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.dependencyinjection.DependencyInjector;
import net.nycjava.skylight1.service.BalancedObjectObserver;
import net.nycjava.skylight1.service.BalancedObjectPublicationService;
import net.nycjava.skylight1.service.CompassService;
import net.nycjava.skylight1.service.CountdownObserver;
import net.nycjava.skylight1.service.CountdownPublicationService;
import net.nycjava.skylight1.service.RandomForceService;
import net.nycjava.skylight1.service.SensorAppliedForceAdapter;
import net.nycjava.skylight1.service.impl.BalancedObjectPublicationServiceImpl;
import net.nycjava.skylight1.service.impl.CompassServiceAndroidImp;
import net.nycjava.skylight1.service.impl.CountdownPublicationServiceImpl;
import net.nycjava.skylight1.service.impl.RandomForceServiceImpl;
import net.nycjava.skylight1.service.impl.SensorAppliedForceAdapterServiceAndroidImpl;
import android.content.Intent;
import android.content.SharedPreferences;
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

	@Dependency
	private CompassService compassService;

	private BalancedObjectObserver balanceObjObserver;

	private int width, height;

	private int difficultyLevel;

	private boolean previouslyPaused;

	private Future<Float> compassReadingFuture;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class,
				(SensorManager) getSystemService(SENSOR_SERVICE));

		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(BalancedObjectPublicationService.class,
				BalancedObjectPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(RandomForceService.class,
				RandomForceServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(SensorAppliedForceAdapter.class,
				SensorAppliedForceAdapterServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(CompassService.class,
				CompassServiceAndroidImp.class);

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

		compassReadingFuture = compassService.getCompassReading();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (previouslyPaused) {
			goToLoseActivity();
		}

		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		Log.i(SkillTestActivity.class.getName(), String.format("width=%d height=%d", width, height));

		balanceObjObserver = new BalancedObjectObserver() {
			public void balancedObjectNotification(float directionOfFallingInRadians, float anAngleOfLeanInRadians) {
			}

			public void fallenOverNotification() {
				goToLoseActivity();
			}
		};
		balanceObjPublicationService.addObserver(balanceObjObserver);

		countdownObserver = new CountdownObserver() {
			public void countdownNotification(int remainingTime) {
				if (remainingTime == 0) {
					// record the new high score
					SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
					int oldHighScore = sharedPreferences.getInt(HIGH_SCORE_PREFERENCE_NAME, -1);

					if (difficultyLevel > oldHighScore) {
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt(HIGH_SCORE_PREFERENCE_NAME, difficultyLevel);
						editor.commit();
					}
					// pass control to the success activity
					countdownPublicationService.stopCountdown();
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					addCompassReading(intent);
					intent.putExtra(DIFFICULTY_LEVEL, difficultyLevel);
					finish();
					startActivity(intent);
				}
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
		countdownPublicationService.setDuration(REMAINING_TIME);

		View skillTestView = (View) contentView.findViewById(R.id.skillTestView);
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(skillTestView);
		setContentView(contentView);

		sensorAppliedForceAdapter.start();
		randomForceService.start();
		countdownPublicationService.startCountdown();
		balanceObjPublicationService.startService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		previouslyPaused = true;

		countdownPublicationService.stopCountdown();
		countdownPublicationService.removeObserver(countdownObserver);
		balanceObjPublicationService.removeObserver(balanceObjObserver);
		randomForceService.stop();
		sensorAppliedForceAdapter.stop();
	}

	private void goToLoseActivity() {
		final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
		addCompassReading(intent);
		intent.putExtra(DIFFICULTY_LEVEL, difficultyLevel);
		balanceObjPublicationService.stopService();
		finish();
		startActivity(intent);
	}

	private void addCompassReading(final Intent anIntent) {
		float newReadings[];
		final float previousReadings[] = getIntent().getFloatArrayExtra(COMPASS_READINGS) != null ? getIntent()
				.getFloatArrayExtra(COMPASS_READINGS) : new float[0];
		try {
			final Float compassReading;
			if (compassReadingFuture.isDone()) {
				compassReading = compassReadingFuture.get();
				Log.i(SkillTestActivity.class.getName(), "compass reading is " + compassReading);
				newReadings = new float[previousReadings.length + 1];
				System.arraycopy(previousReadings, 0, newReadings, 0, previousReadings.length);
				newReadings[previousReadings.length] = compassReading;
			} else {
				Log.e(SkillTestActivity.class.getName(), "compass reading was not done!");
				newReadings = previousReadings;
			}
		} catch (Exception e) {
			Log.e(SkillTestActivity.class.getName(), "unable to retrieve compass reading ", e);
			newReadings = previousReadings;
		}
		anIntent.putExtra(COMPASS_READINGS, newReadings);
	}
}
