package skylight1.toast;

import java.util.ArrayList;
import java.util.List;

import skylight1.toast.view.MediaPlayerHelper;
import skylight1.toast.view.MediaPlayerHelper.VideoStartListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import skylight1.toast.view.MediaPlayerHelper;
import skylight1.toast.view.TypeFaceTextView;
import skylight1.toast.view.MediaPlayerHelper.VideoStartListener;

public class ToastActivity extends Activity {

	public static final boolean LOG = true;

	private static final String LOG_TAG = ToastActivity.class.getSimpleName();

	private TiltDetector mTiltDetector;

	private SoundPlayer mSoundPlayer;

	private SensorManager mSensors;

	class HolderCallback implements Callback {

		View contentView;

		public HolderCallback(View view) {
			contentView = view;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			List<String> listOfMovies = new ArrayList<String>(2);
			listOfMovies.add("intro.mp4");
			listOfMovies.add("toast.mp4");

			MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper(ToastActivity.this, preview, listOfMovies
					.toArray(new String[listOfMovies.size()]));

			mediaPlayerHelper.setVideoStartListener(new VideoStartListener() {
				@Override
				public void videoStarted(int anIndex) {
					Log.i(ToastActivity.class.getName(), "just starting video " + anIndex);
					if (anIndex == 1 ) {
						contentView.post(new Runnable() {
							@Override
							public void run() {
								final TextView captionTextView = (TextView) findViewById(R.id.videoText);
								captionTextView.setVisibility(View.VISIBLE);
								Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
								fadeOutAnimation.setStartOffset(1000);
								fadeOutAnimation.setDuration(4000);
								fadeOutAnimation.setFillAfter(true);
								captionTextView.setAnimation(fadeOutAnimation);
							}
						});
					}
				}
			});

			mp = mediaPlayerHelper.createMediaListPlayer();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Log.i(ToastActivity.class.getName(), "surface changed");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}

	private SurfaceView preview;
	private SurfaceHolder holder;
	private MediaPlayer mp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create haptic feedback whenever a button is touched
		final OnTouchListener onTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View aView, MotionEvent anEvent) {
				if (anEvent.getAction() == MotionEvent.ACTION_DOWN) {
					aView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
							HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
				}
				return false;
			}
		};

		setContentView(R.layout.main);

		preview = (SurfaceView) findViewById(R.id.videoview);
		holder = preview.getHolder();
		holder.addCallback(new HolderCallback(preview.getRootView()));

        mSoundPlayer = new SoundPlayer(this);
    	mSensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
   		mTiltDetector = new TiltDetector(this);

    	//Use volume controls for stream we output on.
    	setVolumeControlStream(AudioManager.STREAM_MUSIC);

		System.gc();
	}

    public void onTilt() {
    	if ( LOG ) Log.i(LOG_TAG, "onTilt()");
    	mSoundPlayer.clink();
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

   	   	List<Sensor> orientaionSensors = mSensors.getSensorList(Sensor.TYPE_ORIENTATION);

    	if ( orientaionSensors.isEmpty() ) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Sorry, no orientation sensor was found. Toast needs one to run.")
        	       .setCancelable(false)
        	       .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                ToastActivity.this.finish();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        	return;
    	} else {
    		mSensors.registerListener(mTiltDetector, orientaionSensors.get(0),
    			SensorManager.SENSOR_DELAY_UI);
    	}
	}

	@Override
	protected void onPause() {
		Log.i(ToastActivity.class.getName(), "paused");
		super.onPause();

		mSensors.unregisterListener(mTiltDetector);

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