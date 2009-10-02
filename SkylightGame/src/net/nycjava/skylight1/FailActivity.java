package net.nycjava.skylight1;

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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.admob.android.ads.AdView;

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

		// Add AdMob banner
		AdView adView = new AdView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		adView.setLayoutParams(layoutParams);
		adView.setKeywords(getString(R.string.keywords));
		adView.setGravity(Gravity.BOTTOM);
		view.addView(adView);

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
							"").replace("]", ""
									);// could be nicer
					final String locale = Locale.getDefault().toString();
					final int azimuthVariance = 0; //TODO:
					final int signature = 0;
					final URL statisticsURL = new URL(String.format(
							"http://%s?id=%s&level=%d&azimuth=%d&locale=%s&sig=%d", HIGH_SCORES_SERVER, hashedPhoneId,
							failedLevel, azimuthVariance, locale, signature));
					final HttpURLConnection httpURLConnection = (HttpURLConnection) statisticsURL.openConnection();
					final InputStream inputStream = httpURLConnection.getInputStream();
					final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					final String responseLine = br.readLine();
					final String bestLevels[] = responseLine.split(",");
					final int bestScoreEver = Integer.parseInt(bestLevels[0]);
					final int bestScoreToday = Integer.parseInt(bestLevels[1]);
					//TODO: store scores in preferences
					Log.i(FailActivity.class.getName(), String.format("\n\nHighest Level Reached:  ever: %d  today: %d\n\n",
							bestScoreEver, bestScoreToday));
				} catch (Exception e) {
					Log.e(FailActivity.class.getName(), "~~~failed to contact server for high scores~~~", e);
					return;
				}
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
