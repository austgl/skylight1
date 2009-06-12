package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.dependencyinjection.DependencyInjector;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.BalancedObjectPublicationServiceImpl;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import net.nycjava.skylight.service.DestinationPublicationServiceMockImpl;
import net.nycjava.skylight.service.Position;
import net.nycjava.skylight.service.PositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationServiceAndroidImpl;
import net.nycjava.skylight.service.RandomForceService;
import net.nycjava.skylight.service.RandomForceServiceImpl;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
import net.nycjava.skylight.service.SensorAppliedForceAdapterServiceAndroidImpl;
import net.nycjava.skylight.service.TouchAppliedForceAdapter;
import net.nycjava.skylight.service.TouchAppliedForceAdapterServiceAndroidImpl;
import net.nycjava.skylight.view.Preview;
import net.nycjava.skylight.view.SkillTestAnimationView;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.System.out;

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

	@Dependency 
	private RandomForceService randomForceService;	
	
	@Dependency
	private BalancedObjectPublicationService balanceObjPublicationService;
	
	private BalancedObjectObserver balanceObjObserver;
	
	private boolean isEmulator=true;
	private int width, height;
	
	private TouchAppliedForceAdapter touchAppliedForceAdaptor;
	private SensorAppliedForceAdapter sensorAppliedForceAdaptor;
  
	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {

//		SensorManager aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class, aSensorManager);

//		aDependencyInjectingObjectFactory.registerImplementationClass(PositionPublicationService.class,
//				PositionPublicationServiceAndroidImpl.class);

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
		
	
		aDependencyInjectingObjectFactory.registerImplementationClass(RandomForceService.class, RandomForceServiceImpl.class);
		
		aDependencyInjectingObjectFactory.registerImplementationClass(BalancedObjectPublicationService.class,
																				BalancedObjectPublicationServiceImpl.class);
		aDependencyInjectingObjectFactory.registerImplementationClass(TouchAppliedForceAdapter.class,
																			TouchAppliedForceAdapterServiceAndroidImpl.class);
		aDependencyInjectingObjectFactory.registerImplementationClass(SensorAppliedForceAdapter.class,
																			SensorAppliedForceAdapterServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.skilltest, null));
//		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
//				(LinearLayout) getLayoutInflater().inflate(R.drawable.spill_animation, null));
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensors()>0 )
			isEmulator = false;
//		out.println("STA: isEmulator="+isEmulator);
		Log.i(SkillTestActivity.class.getName(), String.format("isEmulator=%b",isEmulator));		
	}

//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) { // TODO: remove this! this is for cheating only
//		final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
//		startActivity(intent);
//		return true;
//	}
	
	
	

	public final int REMAINING_TIME = 30; // todo: make configurable?

	public final float MIN_DISTANCE = 0.1f; // todo: make configurable?

	// public final float MAX_ANGLE = 180;

	private Position destinationPosition = new Position(1f, 1f, 1f); // todo: make configurable?


	@Override
	protected void onResume() {
		super.onResume();

		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		out.println("width="+width+" height="+height);
	 
		//touchAppliedForceAdaptor = new TouchAppliedForceAdaptor();
		//sensorAppliedForceAdaptor = new SensorAppliedForceAdaptor();
		//touchAppliedForceAdaptor.start();
		
//		sensorAppliedForceAdaptor.start();
	
/*		
		balanceObjObserver = new BalancedObjectObserver(){

			public void balancedObjectNotification(
					float directionOfFallingInRadians,
					float anAngleOfLeanInRadians) {
				
			}

			public void fallenOverNotification() {
				final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
				startActivity(intent);
				
			}
			
		};
		balanceObjPublicationService.addObserver(balanceObjObserver);
	
		
		
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

		
		 // cameraObscurementObserver = new CameraObscurementObserver() {
		 //
		 // public void cameraObscurementNotification(CameraObscurementState cameraObscuredState) {
		 // 
		 // if(cameraObscuredState == cameraObscuredState.unobscured ) { // final Intent intent = new
		 // Intent(SkillTestActivity.this, FailActivity.class); // startActivity(intent); } } };
		 // //cameraObscurementPublicationService.addObserver(cameraObscurementObserver);
		 //

		destinationPublicationService.setDestinationPosition(destinationPosition);

		destinationObserver = new DestinationObserver() {

			public void destinationNotification(float anAngle, float distance) {
//				if (distance < MIN_DISTANCE) // || anAngle > MAX_ANGLE )
//				{
//					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
//					startActivity(intent);
//					finish();
//				}

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
*/
		TextView title = new TextView(this);
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		contentView.addView(title);

//		View skillTestView = (View) contentView.findViewById(R.id.skillTestView);
//		new DependencyInjector(dependencyInjectingObjectFactory).injectDependenciesForClassHierarchy(skillTestView);

//		ImageView imageView = (ImageView)findViewById(R.id.skillTestBackgroundView);
//		imageView.setBackgroundResource(R.drawable.background_table);
		 // Get the background, which has been compiled to an AnimationDrawable object.
//		AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
		
//		SkillTestAnimationView skillTestAnimationView = (SkillTestAnimationView) contentView.findViewById(R.id.skillTestAnimationView);
//		new DependencyInjector(dependencyInjectingObjectFactory)
//				.injectDependenciesForClassHierarchy(skillTestAnimationView);
//		skillTestAnimationView.setFrameAnimation(frameAnimation);		
//		contentView.addView(skillTestAnimationView);
		
		setContentView(contentView);

	}

	// @Override //TODO: not working
	protected void onPause() {
		countdownPublicationService.removeObserver(countdownObserver);
		// cameraObscurementPublicationService.removeObserver(cameraObscurementObserver);
//		destinationPublicationService.removeObserver(destinationObserver);
//		 steadinessPublicationService.removeObserver(steadinessObserver);
		
		//touchAppliedForceAdaptor.stop();
//		sensorAppliedForceAdaptor.stop();
		
		
		super.onPause();
	}
}
