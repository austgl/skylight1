package net.nycjava.skylight1;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;

import skylight1.util.Assets;

import com.admob.android.ads.AdManager;
import com.adwhirl.AdWhirlLayout;

import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * reporting success; report acknowledged; go to get ready
 */
public class SuccessActivity extends SkylightActivity {

	protected static final int DIFFICULTY_LEVEL_INCREMENT = 1;
	private static final String TAG = SuccessActivity.class.getName();
	private String highscores_server;
	private int globalBestLevel;

	@Dependency
	private RelativeLayout contentView;
	private MediaPlayer mp;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {
		dependencyInjectingObjectFactory.registerImplementationObject(RelativeLayout.class,
				(RelativeLayout) getLayoutInflater().inflate(R.layout.successmsg, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.success);
		LinearLayout linearLayout =  (LinearLayout)contentView.getChildAt(0);
		linearLayout.addView(imageView);
		contentView.requestLayout();
		
    	try{
    		//admob: don't show ads in emulator
            AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR
            //,"your_debugging_phone_id_here" // add phone id if debugging on phone
            });
            String adwhirl_id = Assets.getString("adwhirl_id",this);
            if(adwhirl_id!=null && adwhirl_id.length()>0) {
	            LinearLayout layout = (LinearLayout)contentView.findViewById(R.id.layout_ad);
	            AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, adwhirl_id);
	            layout.addView(adWhirlLayout);
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to create AdWhirlLayout", e);
        }

		setContentView(contentView);

		mp = MediaPlayer.create(getBaseContext(), R.raw.succeeded);
		if(mp!=null) {
			mp.start();
		}


		final int bestLevelCompleted = getIntent().getIntExtra(DIFFICULTY_LEVEL, 0);

		SharedPreferences sharedPreferences = getSharedPreferences(
				SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
		int globalBestLevelCompleted = sharedPreferences.getInt(
				GLOBAL_HIGH_SCORE_PREFERENCE_NAME, -1);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(HIGH_SCORE_PREFERENCE_NAME, bestLevelCompleted);
		editor.commit();

		if(globalBestLevelCompleted>0 && bestLevelCompleted > globalBestLevelCompleted) {
			
			globalBestLevel = bestLevelCompleted;

    		highscores_server = Assets.getString("highscores_server", this);
    		
    		Executors.defaultThreadFactory().newThread(new Runnable() {
    			@Override
    			public void run() {
    				try {
    					if(highscores_server.length()>0) {
    						final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
    						messageDigest.update(androidId.getBytes());
    						final String hashedPhoneId = Arrays.toString(messageDigest.digest()).replace(" ", "").replace("[",
    								"").replace("]", "");// could be nicer
    						final String locale = Locale.getDefault().toString();
    						final int azimuthVariance = 0;
    						final int signature = 0;
    						final URL statisticsURL = new URL(String.format(
    								"http://%s?id=%s&level=%d&azimuth=%d&locale=%s&sig=%d", highscores_server, hashedPhoneId,
    								bestLevelCompleted, azimuthVariance, locale, signature));
    						final HttpURLConnection httpURLConnection = (HttpURLConnection) statisticsURL.openConnection();
    						final InputStream inputStream = httpURLConnection.getInputStream();
    						final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    						final String globalBestLevelString = br.readLine();
    						globalBestLevel = Integer.parseInt(globalBestLevelString);
    					}
    					// save the global best level
    					SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
    					SharedPreferences.Editor editor = sharedPreferences.edit();
    					editor.putInt(GLOBAL_HIGH_SCORE_PREFERENCE_NAME, globalBestLevel);
    					editor.commit();
    					Log.i(TAG, String.format("Highest Level Reached: %d",
    							globalBestLevel));
    				} catch (Exception e) {
    					Log.e(TAG, "Failed to contact server", e);
    					return;
    				}
    			}
    		}).start();
		}
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
