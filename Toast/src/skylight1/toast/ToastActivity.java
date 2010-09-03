package skylight1.toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;

public class ToastActivity extends Activity implements TiltDetector.TiltListener {

	public static final boolean LOG = false;

    private static final String LICENSING_BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvWkIEwKpdCv9mIMjUSImVCwLXVVwUiwHW8WqUwpaWB0ebs5eI/IeYSwjrQ0Uy6Qws6y/aw+BEoilE45I0eo5yENkGxJ1TvNSuU/JifAcX31wXcBPzOjz1Erd8/NbpV88/Mt07STZYZ6wjOTSXWF8NLgmwdbvefXDI1WBTKV5Hrca644fQ0WQYdMlrvvP1m5eWZ+kBTJa8YrySh8PDVtzW0DAQzoN5MWdIzN8kLJNkF/HVh4o0HUfUvRjjJe9pnvICzDAOnZTWRhyqW7Ww7kFj8CW8kxyH83d+P5SWY+2/X/7MJqZkG1ejCshVo0R5SiWsVJvpzQAJ4oeN9tINP7NswIDAQAB";
	
    private static final byte[] LICENSING_SALT = new byte[] {
        -46, 61, 30, -18, -113, -47, 84, -34, 21, 78, -92,
        -40, 76, -107, -36, -113, -11, 32, -64, 89
        };
    
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            Toast.makeText(ToastActivity.this, 
            		"Thank you for downloading from Google Market!", 
            		Toast.LENGTH_LONG);
        }

        public void dontAllow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            Toast.makeText(ToastActivity.this, "You pirate, you!", Toast.LENGTH_LONG);
        }

        public void applicationError(ApplicationErrorCode errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            Toast.makeText(ToastActivity.this, 
            		"Something's broken!" + errorCode, Toast.LENGTH_LONG);

        }
    }

    
    private String deviceSpecificId;

    private LicenseChecker mChecker;
    
	private static final String LOG_TAG = ToastActivity.class.getSimpleName();

	private TiltDetector mTiltDetector;

	private SoundPlayer mSoundPlayer;

	private String message;
	private ArrayList<String> messageList;
	private String[] splitList;


    private void fadeOutText() {
        final TextView captionTextView = (TextView) findViewById(R.id.videoText);
        Log.i(ToastActivity.class.getName(), "toast message = " + message);
        captionTextView.setText(message);
        captionTextView.setVisibility(View.VISIBLE);
        Animation fadeOutAnimation = new AlphaAnimation(0.0f, 1.0f);
//        fadeOutAnimation.setStartOffset(500);
        fadeOutAnimation.setDuration(3000);
//        fadeOutAnimation.setFillAfter(true);
        captionTextView.setAnimation(fadeOutAnimation);
    }


//	private SurfaceView preview;
//	private SurfaceHolder holder;
    private MediaPlayer mp;

    /**
     * Called when the activity is first created.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		deviceSpecificId = Secure.getString(getContentResolver(), "android_id");
		if ( null == deviceSpecificId ) {
			deviceSpecificId = "ALL_EMULATORS_CAN_SHARE_OH_NO";
		}
	    // Construct the LicenseChecker with a Policy.
	    mChecker = new LicenseChecker(
	        this, new ServerManagedPolicy(this,
	            new AESObfuscator(LICENSING_SALT, getPackageName(), deviceSpecificId)),
	        LICENSING_BASE64_PUBLIC_KEY
	        );

        mChecker.checkAccess(new MyLicenseCheckerCallback());
		
		message="Toasts go here";
    	// Load up toasts in array
    	loadToasts();
    	int pick = (int)(Math.random() * (double) splitList.length);
    	message = splitList[pick];

		setContentView(R.layout.main);

        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        ImageView imageView = (ImageView) findViewById(R.id.videoview);
        if (imageView != null) {
            imageView.setBackgroundResource(R.anim.toast_anim);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();

//             Start the animation (looped playback by default).
//            frameAnimation.start();
            AnimationRoutine animationRoutine = new AnimationRoutine();

            Timer t = new Timer(false);
            t.schedule(animationRoutine, 1000);
        } else {
            Log.e(LOG_TAG, "Error load Toast images for animation.");
        }

//		preview = (SurfaceView) findViewById(R.id.videoview);
		//Fake tilt and create haptic feedback whenever screen touched.
		final OnTouchListener onTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View aView, MotionEvent anEvent) {
				if (anEvent.getAction() == MotionEvent.ACTION_DOWN) {
					aView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
							HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

					onTiltStart();
					onTiltEnd();

					return true;
				}
				return false;
			}
		};
		imageView.setOnTouchListener(onTouchListener);

//		holder = preview.getHolder();
//		holder.addCallback(new HolderCallback(preview.getRootView()));

        mSoundPlayer = new SoundPlayer(this);
   		mTiltDetector = new TiltDetector(this);

    	//Use volume controls for stream we output on.
    	setVolumeControlStream(AudioManager.STREAM_MUSIC);

		System.gc();
	}



	class HolderCallback implements Callback {

		View contentView;

		public HolderCallback(View view) {
			contentView = view;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			List<String> listOfMovies = new ArrayList<String>(2);
			listOfMovies.add("toast.mp4");

/*			MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper(ToastActivity.this, preview, listOfMovies
					.toArray(new String[listOfMovies.size()]));

			mediaPlayerHelper.setVideoStartListener(new VideoStartListener() {
				@Override
				public void videoStarted(int anIndex) {
					Log.i(ToastActivity.class.getName(), "just starting video " + anIndex);
					if (anIndex == 0 ) {
						contentView.post(new Runnable() {
							@Override
							public void run() {
								fadeOutText();
							}
						});
					}
				}
			});

			mp = mediaPlayerHelper.createMediaListPlayer();
*/
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Log.i(ToastActivity.class.getName(), "surface changed");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}





    /**
     * Called to start frame animation.
     */
    private class AnimationRoutine extends TimerTask {
        AnimationRoutine() {
        }

        public void run() {
            ImageView img = (ImageView) findViewById(R.id.videoview);
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
            frameAnimation.start();
        }
    }


	/**
	 * Plays clink sound when tilted.
	 */
	@Override
    public void onTiltStart() {
    	if ( LOG ) Log.d(LOG_TAG, "onTiltStart()");
    	mSoundPlayer.clink();
    }

    /**
     * Fades out the previous message and shows a new one when a tilt ends.
     */
	@Override
	public void onTiltEnd() {
    	if ( LOG ) Log.d(LOG_TAG, "onTiltEnd()");

       	int pick = (int)(Math.random() * (double) splitList.length);
    	message = splitList[pick];

		if (mp != null) {
			mp.start();
		}

		fadeOutText();
	}

    public void loadToasts() {
    	try {
            InputStream is = getAssets().open("toasts.txt");

            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            if(messageList == null) {
            	messageList = new ArrayList<String>();
            }
            text = text.replaceAll("\r", "");
            splitList  = text.split("%\n");
            messageList = null;

    	} catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
    	}
    }

	@Override
	protected void onResume() {
		super.onResume();

		final TextView captionTextView = (TextView) findViewById(R.id.videoText);
		captionTextView.setBackgroundColor(Color.TRANSPARENT);
		captionTextView.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		captionTextView.setDrawingCacheEnabled(false);
		captionTextView.setVisibility(View.GONE);

		// if the resume is coming back from some pause, then resume the player
		if (mp != null) {
			mp.start();
		}

		mTiltDetector.setTiltListener(this);

    	if(messageList == null) {
    		loadToasts();
    	}
	}

	@Override
	protected void onPause() {
		Log.i(ToastActivity.class.getName(), "paused");
		super.onPause();

		mTiltDetector.setTiltListener(null);

		// if the media player has not already been disposed of (leaving this screen, as
		// compared to pausing to go to another application), then pause the video
		if (mp != null) {
			mp.pause();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.stop();
			mp=null;
		}

		mSoundPlayer.release();
		mSoundPlayer = null;
	}

}