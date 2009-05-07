package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CameraObscurementObserver;
import net.nycjava.skylight.service.CameraObscurementPublicationService;
import net.nycjava.skylight.service.CameraObscurementPublicationServiceAndroidImpl;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import net.nycjava.skylight.service.DestinationPublicationServiceImpl;
import net.nycjava.skylight.service.PositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationServiceAndroidImpl;
import net.nycjava.skylight.service.SteadinessObserver;
import net.nycjava.skylight.service.SteadinessPublicationService;
import net.nycjava.skylight.service.SteadinessPublicationServiceAndroidImpl;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.SensorManager;
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

	private CameraObscurementObserver cameraObscurementObserver;

	private DestinationObserver destinationObserver;

	private SteadinessObserver steadinessObserver;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class,
				(SensorManager) getSystemService(SENSOR_SERVICE));

		aDependencyInjectingObjectFactory.registerImplementationClass (PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(DestinationPublicationService.class,
				DestinationPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(SteadinessPublicationService.class,
				SteadinessPublicationServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());

		aDependencyInjectingObjectFactory.registerImplementationClass(CameraObscurementPublicationService.class,
				CameraObscurementPublicationServiceAndroidImpl.class);

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
	
	private int rTime;
	public final int REMAINING_TIME = 10;
	private float aDistance;
	public final float MAX_DISTANCE = 40;
	public final float MAX_ANGLE = 180;
	@Override
	protected void onResume() {
		super.onResume();
		countdownObserver = new CountdownObserver() {
			public void countdownNotification(int remainingTime) {
				rTime = remainingTime;
				if (remainingTime == 0) {
					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					startActivity(intent);
				}
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
		countdownPublicationService.setDuration(REMAINING_TIME);
		countdownPublicationService.startCountdown();
		
		cameraObscurementObserver = new CameraObscurementObserver() {

			public void cameraObscurementNotification(CameraObscurementState cameraObscuredState) {
				
				if(cameraObscuredState == cameraObscuredState.unobscured )
				{
					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					startActivity(intent);
				}					
			}						
		};
		//cameraObscurementPublicationService.addObserver(cameraObscurementObserver);	
		
		destinationObserver = new DestinationObserver() {
						
			public void destinationNotification(float anAngle, float distance) {
				aDistance=distance;
				
				if(distance > MAX_DISTANCE || anAngle > MAX_ANGLE )
				{
					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					startActivity(intent);
				}	
				if(distance == MAX_DISTANCE)
				{
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					startActivity(intent);
				}
			}
		};
		destinationPublicationService.addObserver(destinationObserver);
		
		steadinessObserver = new SteadinessObserver() {
			
			public void steadinessNotification(float paremetersThatICantThinkOfRightNow) 
			{				
				final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
				startActivity(intent);
			}
			
			public void unsteadinessNotification() {
				final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
				startActivity(intent);
				
			}						
		};

		steadinessPublicationService.addObserver(steadinessObserver);
	}

	@Override
	protected void onPause() {
		countdownPublicationService.removeObserver(countdownObserver);
		super.onPause();
	}
}
