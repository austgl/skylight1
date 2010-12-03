package skylight1.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class HighScoreService {

	public static final String HIGH_SCORE_PREFERENCE_NAME = "highLevel";
	public static final String GLOBAL_HIGH_SCORE_PREFERENCE_NAME = "globalHighLevel";

	protected static final String SKYLIGHT_PREFS_FILE = "SkylightPrefsFile";

	private static final String TAG = HighScoreService.class.getName();
	
	public void recordScore(final int aScore, final Context aContext) {
		SharedPreferences sharedPreferences = aContext.getSharedPreferences(
				SKYLIGHT_PREFS_FILE, Context.MODE_PRIVATE);
		final int globalBestLevelCompleted = sharedPreferences.getInt(
				GLOBAL_HIGH_SCORE_PREFERENCE_NAME, -1);
		final int localBestLevelCompleted = sharedPreferences.getInt(
				HIGH_SCORE_PREFERENCE_NAME, -1);

		if(aScore>localBestLevelCompleted) {
    		SharedPreferences.Editor editor = sharedPreferences.edit();
    		editor.putInt(HIGH_SCORE_PREFERENCE_NAME, aScore);
    		editor.commit();
		}
		if(globalBestLevelCompleted>0 && aScore > globalBestLevelCompleted) {
			
    		final String highscores_server = Assets.getString("highscores_server", aContext);
    		
    		Executors.defaultThreadFactory().newThread(new Runnable() {
    			@Override
    			public void run() {
    				try {
    					int globalBestLevel = aScore;
    					if(highscores_server.length()>0) {
    						final String hashedPhoneId = new PhoneIdHasher().getHashedPhoneId(aContext);
    						final String locale = Locale.getDefault().toString();
    						final int azimuthVariance = 0;
    						final int signature = 0;
    						final URL statisticsURL = new URL(String.format(
    								"http://%s?id=%s&level=%d&azimuth=%d&locale=%s&sig=%d", highscores_server, hashedPhoneId,
    								aScore, azimuthVariance, locale, signature));
    						final HttpURLConnection httpURLConnection = (HttpURLConnection) statisticsURL.openConnection();
    						final InputStream inputStream = httpURLConnection.getInputStream();
    						final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    						final String globalBestLevelString = br.readLine();
    						globalBestLevel = Integer.parseInt(globalBestLevelString);
    					}
    					// save the global best level
    					SharedPreferences sharedPreferences = aContext.getSharedPreferences(SKYLIGHT_PREFS_FILE, Context.MODE_PRIVATE);
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
}
