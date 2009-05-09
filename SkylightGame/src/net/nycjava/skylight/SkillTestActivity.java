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
import net.nycjava.skylight.service.Position;
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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
	public final float MIN_DISTANCE = 20;
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
/*
		cameraObscurementObserver = new CameraObscurementObserver() {

			public void cameraObscurementNotification(CameraObscurementState cameraObscuredState) {

				if(cameraObscuredState == cameraObscuredState.unobscured )
				{
//					final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
//					startActivity(intent);
				}					
			}						
		};
		//cameraObscurementPublicationService.addObserver(cameraObscurementObserver);	
*/
		((DestinationPublicationServiceImpl) destinationPublicationService).setDestinationPosition(new Position(1f,1f,1f));
		
		destinationObserver = new DestinationObserver() {

			public void destinationNotification(float anAngle, float distance) {
				aDistance=distance;
				
				//if(distance > MIN_DISTANCE || anAngle > MAX_ANGLE )
				//{
				//	final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
				//	startActivity(intent);
				//}	
				
				if(distance >= MIN_DISTANCE)
				{
					final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
					startActivity(intent);
				}
			}
		};
		
		destinationPublicationService.addObserver(destinationObserver);
/*
		steadinessObserver = new SteadinessObserver() {

			public void steadinessNotification(float paremetersThatICantThinkOfRightNow) 
			{				
//				final Intent intent = new Intent(SkillTestActivity.this, SuccessActivity.class);
//				startActivity(intent);
			}

			public void unsteadinessNotification() {
//				final Intent intent = new Intent(SkillTestActivity.this, FailActivity.class);
//				startActivity(intent);

			}						
		};

		steadinessPublicationService.addObserver(steadinessObserver);
*/		
		TextView title = new TextView(this);
		title.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
									LayoutParams.WRAP_CONTENT));
		contentView.addView(title);
		CustomView cview = new CustomView(getApplicationContext());
		contentView.addView(cview);							
		setContentView(contentView);
		

	}

	@Override
	protected void onPause() {
		countdownPublicationService.removeObserver(countdownObserver);
		cameraObscurementPublicationService.removeObserver(cameraObscurementObserver);
		destinationPublicationService.removeObserver(destinationObserver);
		steadinessPublicationService.removeObserver(steadinessObserver);
		super.onPause();
	}

	CustomView vw = null;
	boolean running = true;
	boolean playSound = true;
	Bitmap ball = null;

	protected class CustomView extends View
	{
		Context ctx;
		Paint lPaint = new Paint();

		CustomView(Context c) 
		{
			super(c);
			ctx = c;
		}


		public void onDraw(Canvas canvas)
		{ 

			Path path = new Path ();
			path.moveTo (0, 0);
			path.lineTo (200, 0);
			path.moveTo (0, 50);
			path.lineTo (200, 50);
			Paint paint = new Paint ();
			paint.setColor (0xFFFF0000);
			paint.setStrokeWidth(1.0f);
			paint.setTextSize(24);
			Typeface typeface = Typeface.defaultFromStyle (Typeface.NORMAL);
			paint.setTypeface (typeface);
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.drawTextOnPath("distance:"+aDistance+"time:"+rTime, path, 0, 100, paint);

			if(running)
			{
				invalidate();
			}


		}
	}

}
