package net.nycjava.skylight1;

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

/** AD BANNER DEPENDENCY - to be added by specific game's build script
import com.admob.android.ads.AdView;
*/

/**
 * reporting success; report acknowledged; go to get ready
 */
public class SuccessActivity extends SkylightActivity {

	protected static final int DIFFICULTY_LEVEL_INCREMENT = 1;

	@Dependency
	private LinearLayout contentView;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.successmsg, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.icon);
		contentView.addView(imageView);
		
		/** AD BANNER DEPENDENCY - to be added by specific game's build script
		AdView adView = new AdView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		adView.setLayoutParams(layoutParams);
		adView.setKeywords("Android application");
		adView.setGravity(Gravity.BOTTOM);
		contentView.addView(adView);
		 */
		setContentView(contentView);

		MediaPlayer.create(getBaseContext(), R.raw.succeeded).start();
	}

	void nextLevel() {
		Intent intent = new Intent();
		intent.setClass(SuccessActivity.this, SkillTestActivity.class);
		intent.putExtra(DIFFICULTY_LEVEL, getIntent().getIntExtra(DIFFICULTY_LEVEL, 0)
				+ DIFFICULTY_LEVEL_INCREMENT);
		intent.putExtra(COMPASS_READINGS, getIntent().getFloatArrayExtra(COMPASS_READINGS));
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
