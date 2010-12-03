package net.nycjava.skylight1;

import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import skylight1.util.Assets;
import skylight1.util.HighScoreService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.admob.android.ads.AdManager;
import com.adwhirl.AdWhirlLayout;

/**
 * reporting unsteady hand; report acknowledged; reporting slow hand; report acknowledged; go to welcome
 */
public class FailActivity extends SkylightActivity {

	private static final String TAG = FailActivity.class.getName();
	
	@Dependency
	private RelativeLayout view;
	private MediaPlayer mp;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {

		dependencyInjectingObjectFactory.registerImplementationObject(RelativeLayout.class,
				(RelativeLayout) getLayoutInflater().inflate(R.layout.failmsg, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.failed);
		LinearLayout linearLayout =  (LinearLayout)view.getChildAt(0);
		linearLayout.addView(imageView);
		view.requestLayout();
		
    	try{
    		//admob: don't show ads in emulator
            AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR
            //,"your_debugging_phone_id_here" // add phone id if debugging on phone
            });
            String adwhirl_id = Assets.getString("adwhirl_id",this);
            if(adwhirl_id!=null && adwhirl_id.length()>0) {
	            LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_ad);
	            AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, adwhirl_id);
	            layout.addView(adWhirlLayout);
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to create AdWhirlLayout", e);
        }

		setContentView(view);

		mp = MediaPlayer.create(getBaseContext(), R.raw.failed);
		if(mp!=null) {
			mp.start();
		}

		final int failedLevel = getIntent().getIntExtra(DIFFICULTY_LEVEL, 0);
		new HighScoreService().recordScore(failedLevel, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mp!=null) {
			mp.stop();
			mp.release();
			mp = null;
			System.gc();
		}
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
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			nextLevel();
			finish();
			return true;
		}
		return false;
	}
}
