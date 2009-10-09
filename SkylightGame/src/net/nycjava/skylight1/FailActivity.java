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

/** AD BANNER DEPENDENCY - to be added by specific game's build script
 import com.admob.android.ads.AdView;
 */

/**
 * reporting unsteady hand; report acknowledged; reporting slow hand; report acknowledged; go to welcome
 */
public class FailActivity extends SkylightActivity {

	private static final String HIGH_SCORES_SERVER = "balancethebeer.appspot.com/stats";

	@Dependency
	private LinearLayout view;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory dependencyInjectingObjectFactory) {

		dependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.failmsg, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.icon_2);
		view.addView(imageView);

		/**
		 * AD BANNER DEPENDENCY - to be added by specific game's build script AdView adView = new AdView(this);
		 * LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		 * adView.setLayoutParams(layoutParams); adView.setKeywords(getString(R.string.keywords));
		 * adView.setGravity(Gravity.BOTTOM); view.addView(adView);
		 */

		setContentView(view);

		MediaPlayer.create(getBaseContext(), R.raw.failed).start();

		Executors.defaultThreadFactory().newThread(new Runnable() {
			@Override
			public void run() {
				final int failedLevel = getIntent().getIntExtra(DIFFICULTY_LEVEL, 0);
				try {
					final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
					messageDigest.update(androidId.getBytes());
					final String hashedPhoneId = Arrays.toString(messageDigest.digest()).replace(" ", "").replace("[",
							"").replace("]", "");// could be nicer
					final String locale = Locale.getDefault().toString();
					final int azimuthVariance = calculateAzimuth();
					final int signature = 0;
					final URL statisticsURL = new URL(String.format(
							"http://%s?id=%s&level=%d&azimuth=%d&locale=%s&sig=%d", HIGH_SCORES_SERVER, hashedPhoneId,
							failedLevel, azimuthVariance, locale, signature));
					final HttpURLConnection httpURLConnection = (HttpURLConnection) statisticsURL.openConnection();
					final InputStream inputStream = httpURLConnection.getInputStream();
					final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					final String globalBestLevelString = br.readLine();
					final int globalBestLevel = Integer.parseInt(globalBestLevelString);

					// save the global best level
					SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt(GLOBAL_HIGH_SCORE_PREFERENCE_NAME, globalBestLevel);
					editor.commit();

					Log.i(FailActivity.class.getName(), String.format("Highest Level Reached: %d",
							globalBestLevel));
				} catch (Exception e) {
					Log.e(FailActivity.class.getName(), "Failed to contact server for high scores", e);
					return;
				}
			}

			private int calculateAzimuth() {
				final float compassReadings[] = getIntent().getFloatArrayExtra(COMPASS_READINGS);

				// need at least two readings to get a variance
				if (compassReadings.length < 2) {
					Log.i(FailActivity.class.getName(), "returning az = 0");
					return 0;
				}

				// using two-pass algorithm from http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
				float mean = 0f;
				for (float compassReading : compassReadings) {
					mean += compassReading;
				}
				mean = mean / (float) compassReadings.length;
				float sumOfSquares = 0f;
				for (float compassReading : compassReadings) {
					final float distance = compassReading - mean;
					sumOfSquares += distance * distance;
				}
				final double variance = sumOfSquares / (float) (compassReadings.length - 1);

				final int standardDeviation = (int) Math.sqrt(variance);

				Log.i(FailActivity.class.getName(), format("az sd is %d", standardDeviation));
				return standardDeviation;
			}
		}).start();
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
