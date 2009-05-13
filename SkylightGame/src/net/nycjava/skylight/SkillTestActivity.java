package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.CountdownPublicationServiceImpl;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import net.nycjava.skylight.service.DestinationPublicationServiceImpl;
import net.nycjava.skylight.service.Position;
import net.nycjava.skylight.service.PositionPublicationService;
import net.nycjava.skylight.service.PositionPublicationServiceAndroidImpl;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
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

	// private SteadinessObserver steadinessObserver;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {

		SensorManager aSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		aDependencyInjectingObjectFactory.registerImplementationObject(SensorManager.class, aSensorManager);

		aDependencyInjectingObjectFactory.registerImplementationClass(PositionPublicationService.class,
				PositionPublicationServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationClass(DestinationPublicationService.class,
				DestinationPublicationServiceImpl.class);

		// destinationPublicationService.setSensorManager(aSensorManager);

		// aDependencyInjectingObjectFactory.registerImplementationClass(SteadinessPublicationService.class,
		// SteadinessPublicationServiceAndroidImpl.class);
		aDependencyInjectingObjectFactory.registerImplementationClass(CountdownPublicationService.class,
				CountdownPublicationServiceImpl.class);
		// aDependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		// aDependencyInjectingObjectFactory.registerImplementationClass(CameraObscurementPublicationService.class,
		// CameraObscurementPublicationServiceAndroidImpl.class);

		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.skilltest, null));
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

	private int rTime;

	public final int REMAINING_TIME = 30; // todo: make configurable?

	private float aDistance;

	public final float MIN_DISTANCE = 100; // todo: make configurable?

//	public final float MAX_ANGLE = 180;

	private Position destinationPosition = new Position(1f, 1f, 1f); // todo: make configurable?

	@Override
	protected void onResume() {
		super.onResume();

		countdownObserver = new CountdownObserver() {
			public void countdownNotification(int remainingTime) {
				rTime = remainingTime;
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
				aDistance = distance;

				 if(distance > MIN_DISTANCE) //|| anAngle > MAX_ANGLE )
				 {
					 final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
					 startActivity(intent);
					 finish();
				 }

				// if(distance >= 0)
				// Log.d("SkillTestActivity", "distance="+distance);

//				if (aDistance >= MIN_DISTANCE) {
//					countdownPublicationService.stopCountdown();
//					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
//					startActivity(intent);
//					finish();
//				}
			}
		};

		destinationPublicationService.addObserver(destinationObserver);
		
		TextView title = new TextView(this);
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		contentView.addView(title);
		CustomView cview = new CustomView(getApplicationContext());
		contentView.addView(cview);

		setContentView(contentView);
	}

	// @Override //TODO: not working
	// protected void onPause() {
	// countdownPublicationService.removeObserver(countdownObserver);
	// // cameraObscurementPublicationService.removeObserver(cameraObscurementObserver);
	// destinationPublicationService.removeObserver(destinationObserver);
	// // steadinessPublicationService.removeObserver(steadinessObserver);
	// super.onPause();
	// }

	CustomView vw = null;

	boolean running = true;

	boolean playSound = true;

	Bitmap ball = null;

	protected class CustomView extends View {
		Context ctx;

		Paint lPaint = new Paint();

		CustomView(Context c) {
			super(c);
			ctx = c;
		}

		public void onDraw(Canvas canvas) {

			Path path = new Path();
			path.moveTo(0, 0);
			path.lineTo(250, 0);
			path.moveTo(0, 50);
			path.lineTo(250, 50);
			Paint paint = new Paint();
			paint.setColor(0xFFFF0000);
			paint.setStrokeWidth(1.0f);
			paint.setTextSize(24);
			Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
			paint.setTypeface(typeface);
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawTextOnPath("Dist " + aDistance + "  Time " + rTime, path, 0, 250, paint);

			if (running) {
				invalidate();
			}

		}
	}

}
