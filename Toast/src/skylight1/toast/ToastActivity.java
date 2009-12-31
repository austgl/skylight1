package skylight1.toast;

import java.util.ArrayList;
import java.util.List;

import skylight1.toast.view.MediaPlayerHelper;
import skylight1.toast.view.TypeFaceTextView;
import skylight1.toast.view.MediaPlayerHelper.VideoStartListener;
import android.app.Activity;
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

public class ToastActivity extends Activity {

	private View contentView;

	private final class HolderCallback implements Callback {

		public HolderCallback(boolean aDemoOnly) {
//			demoOnly = aDemoOnly;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			List<String> listOfMovies = new ArrayList<String>(2);
			listOfMovies.add("intro.3gp");
//				listOfMovies.add("demo.mp4");

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
								final TextView captionTextView = (TextView) contentView.findViewById(R.id.videoText);
								//captionTextView.setText(getResources().getString(R.string.instructions)+":");
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

		preview = (SurfaceView) contentView.findViewById(R.id.videoview);
		holder = preview.getHolder();
		holder.addCallback(new HolderCallback(true));

		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();

//		final TextView captionTextView = (TextView) contentView.findViewById();
//		captionTextView.setBackgroundColor(Color.TRANSPARENT);
//		captionTextView.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
//		captionTextView.setDrawingCacheEnabled(false);
//		captionTextView.setVisibility(View.GONE);

		// if the resume is coming back from some pause, then resume the player
		if (mp != null) {
			mp.start();
		}
	}

	@Override
	protected void onPause() {
		Log.i(ToastActivity.class.getName(), "paused");
		super.onPause();

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
	}
}