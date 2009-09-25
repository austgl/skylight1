package net.nycjava.skylight1;

import com.admob.android.ads.AdManager;
import com.admob.android.ads.AdView;

import net.nycjava.skylight1.R;
import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * reporting unsteady hand; report acknowledged; reporting slow hand; report acknowledged; go to welcome
 */
public class FailActivity extends SkylightActivity {

	@Dependency
	private LinearLayout view;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {

		dependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.failmsg, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.icon_2);
		view.addView(imageView);
		
		//Add AdMob banner
		AdView adView = new AdView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		adView.setLayoutParams(layoutParams);
		adView.setKeywords(getString(R.string.keywords));
		adView.setGravity(Gravity.BOTTOM);
		view.addView(adView);
		
		setContentView(view);

		MediaPlayer.create(getBaseContext(), R.raw.failed).start();
	}

	void nextLevel() {
		Intent intent = new Intent();
		intent.setClass(FailActivity.this, WelcomeActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			nextLevel();
			finish();
		}
		return true;
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			nextLevel();
			finish();
		}
		return true;		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			nextLevel();
			finish();			
			return true;
		}
		return false;
	}
}
