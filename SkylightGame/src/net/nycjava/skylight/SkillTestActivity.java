package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import net.nycjava.skylight.service.DestinationPublicationServiceMockImpl;
import net.nycjava.skylight.service.Position;
import net.nycjava.skylight.service.PositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationServiceAndroidImpl;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SkillTestActivity extends SkylightActivity {
	// @Dependency
	// private PositionPublicationService positionPublicationService;
	@Dependency
	private DestinationPublicationService destinationPublicationService;

	// @Dependency
	// private SteadinessPublicationService steadinessPublicationService;
	@Dependency
	private CountdownPublicationService countdownPublicationService;

	// @Dependency
	// private CameraObscurementPublicationService cameraObscurementPublicationService;
	@Dependency
	private LinearLayout contentView;

	private CountdownObserver countdownObserver;

	// private CameraObscurementObserver cameraObscurementObserver;
	// private PositionObserver positionObserver;
	private DestinationObserver destinationObserver;

	/* TODO - adjust this code when BalanceObjObserver,TouchAppliedForceAdaptor,SensorAppliedForceAdaptor are  implemented
	@Dependency 
	private RandomForceService randomForceService;	
	
	@Dependency
	private BalancedObjectPublicationService balanceObjPublicationService;
	
	private BalanceObjObserver balanceObjObserver;
	
	private TouchAppliedForceAdaptor touchAppliedForceAdaptor;
	private SensorAppliedForceAdaptor sensorAppliedForceAdaptor;
  */
	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {

		SensorManager aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class, aSensorManager);

		aDependencyInjectingObjectFactory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(DestinationPublicationService.class,
		// DestinationPublicationServiceImpl.class);
				DestinationPublicationServiceMockImpl.class);

		// destinationPublicationService.setSensorManager(aSensorManager);

		// aDependencyInjectingObjectFactory.registerImplementationClass(SteadinessPublicationService.class,
		// SteadinessPublicationServiceAndroidImpl.class);
		aDependencyInjectingObjectFactory.registerSingletonImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);
		// aDependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		// aDependencyInjectingObjectFactory.registerImplementationClass(CameraObscurementPublicationService.class,
		// CameraObscurementPublicationServiceAndroidImpl.class);
		
		/*  TODO - adjust this code adjust this code when BalanceObjService,TouchAppliedForceAdaptor,SensorAppliedForceAdaptor are  implemented
		
		aDependencyInjectingObjectFactory.registerImplementationObject(RandomForceService.class, RandomForceServiceImpl.class);
		
		aDependencyInjectingObjectFactory.registerImplementationObject(BalancedObjectPublicationService.class,
																				BalancedObjectPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.skilltest, null));
		
		 */
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) { // TODO: remove this! this is for cheating only
		final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
		startActivity(intent);
		return true;
	}

	public final int REMAINING_TIME = 30; // todo: make configurable?

	public final float MIN_DISTANCE = 0.1f; // todo: make configurable?

	// public final float MAX_ANGLE = 180;

	private Position destinationPosition = new Position(1f, 1f, 1f); // todo: make configurable?

	@Override
	protected void onResume() {
		super.onResume();
		
		/*- TODO - adjust this code when BalanceObjService,TouchAppliedForceAdaptor,SensorAppliedForceAdaptor are  implemented
		  
		touchAppliedForceAdaptor = new TouchAppliedForceAdaptor();
		sensorAppliedForceAdaptor = new SensorAppliedForceAdaptor();
		touchAppliedForceAdaptor.start();
		sensorAppliedForceAdaptor.start();
		
		balanceObjObserver = new BalanceObjObserver(){
			public void  balanceObjNotification() { // TODO - add params
	
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					startActivity(intent);

			}
			
		};
		balancedObjPublicationService.addObserver(countdownObserver);
		balancedObjPublicationService.start();
		*/
		
		countdownObserver = new CountdownObserver() {
			public void countdownNotification(int remainingTime) {
				if (remainingTime == 0) {
					countdownPublicationService.stopCountdown();
					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
		countdownPublicationService.setDuration(REMAINING_TIME);
		countdownPublicationService.startCountdown();

		/*
		 * cameraObscurementObserver = new CameraObscurementObserver() {
		 * 
		 * public void cameraObscurementNotification(CameraObscurementState cameraObscuredState) {
		 * 
		 * if(cameraObscuredState == cameraObscuredState.unobscured ) { // final Intent intent = new
		 * Intent(SkillTestActivity.this, FailActivity.class); // startActivity(intent); } } };
		 * //cameraObscurementPublicationService.addObserver(cameraObscurementObserver);
		 */

		destinationPublicationService.setDestinationPosition(destinationPosition);

		destinationObserver = new DestinationObserver() {

			public void destinationNotification(float anAngle, float distance) {
				if (distance < MIN_DISTANCE) // || anAngle > MAX_ANGLE )
				{
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					startActivity(intent);
					finish();
				}

				// if(distance >= 0)
				// Log.d("SkillTestActivity", "distance="+distance);

				// if (aDistance >= MIN_DISTANCE) {
				// countdownPublicationService.stopCountdown();
				// final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
				// startActivity(intent);
				// finish();
				// }
			}
		};

		destinationPublicationService.addObserver(destinationObserver);

		TextView title = new TextView(this);
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		contentView.addView(title);

		View skillTestView = (View) contentView.findViewById(R.id.skillTestView);
		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(skillTestView);

		setContentView(contentView);
	}

	// @Override //TODO: not working
	protected void onPause() {
		countdownPublicationService.removeObserver(countdownObserver);
		// cameraObscurementPublicationService.removeObserver(cameraObscurementObserver);
		destinationPublicationService.removeObserver(destinationObserver);
		// steadinessPublicationService.removeObserver(steadinessObserver);
		
		/* TODO adjust when TouchAppliedForceAdaptor,SensorAppliedForceAdaptor are  implementedd
		touchAppliedForceAdaptor.stop();
		sensorAppliedForceAdaptor.stop();
		
		*/
		super.onPause();
	}
}
