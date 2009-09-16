package net.nycjava.skylight1;

import net.nycjava.skylight1.R;
import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight1.view.MediaPlayerHelper;
import net.nycjava.skylight1.view.TypeFaceTextView;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends SkylightActivity {
	private static final int BUTTON_FLASH_PERIOD = 700;

	private final class HolderCallback implements Callback {
		private boolean demoOnly;

		public HolderCallback(boolean aDemoOnly) {
			demoOnly = aDemoOnly;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper(WelcomeActivity.this, preview, "intro.mp4",
					"demo.mp4");
			if (demoOnly) {
				mediaPlayerHelper.getListOfMovies().remove(0);
			} else {
				SharedPreferences sharedPreferences = getSharedPreferences(SKYLIGHT_PREFS_FILE, MODE_PRIVATE);
				if (sharedPreferences.getInt(HIGH_SCORE_PREFERENCE_NAME, 0) > 0)
					mediaPlayerHelper.getListOfMovies().remove(1);
			}
			mp = mediaPlayerHelper.createMediaListPlayer();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.release();
			Log.i(WelcomeActivity.class.getName(), "surface destroyed");
		}
	}

	public class HighlightTextFocusChangeListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			((TextView) arg0).setTextColor(arg1 ? focusedColor : unfocusedColor);
		}
	}

	private final class DifficultyClickListener implements OnClickListener {
		final private int difficulty;

		public DifficultyClickListener(int aDifficulty) {
			super();
			difficulty = aDifficulty;
		}

		@Override
		public void onClick(View aView) {
			// stop the animation immediately
			contentView.setAnimation(null);
			buttonsAnimation = null;

			// mark the button as pressed
			final int unfocusedColor = WelcomeActivity.this.getResources().getColor(R.color.button_font_color);
			final int focusedColor = WelcomeActivity.this.getResources().getColor(R.color.button_font_color_focused);
			((TypeFaceTextView) contentView.findViewById(R.id.easy)).setTextColor(unfocusedColor);
			((TypeFaceTextView) contentView.findViewById(R.id.normal)).setTextColor(unfocusedColor);
			((TypeFaceTextView) contentView.findViewById(R.id.hard)).setTextColor(unfocusedColor);
			((TextView) aView).setTextColor(focusedColor);

			// stop the media player
			if (mp != null) {
				mp.pause();
			}

			final Intent intent = new Intent(WelcomeActivity.this, SkillTestActivity.class);
			intent.putExtra(SkylightActivity.DIFFICULTY_LEVEL, difficulty);
			startActivity(intent);
		}
	}

	@Dependency
	private LinearLayout contentView;

	private SurfaceView preview;

	private SurfaceHolder holder;

	private Animation buttonsAnimation;

	private TypeFaceTextView animatingButtons[];

	private int animatingButtonIndex;

	private MediaPlayer mp;

	private int unfocusedColor;

	private int focusedColor;

	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(LinearLayout.class,
				(LinearLayout) getLayoutInflater().inflate(R.layout.welcome, null));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TypeFaceTextView easyButton = (TypeFaceTextView) contentView.findViewById(R.id.easy);
		final TypeFaceTextView normalButton = (TypeFaceTextView) contentView.findViewById(R.id.normal);
		final TypeFaceTextView hardButton = (TypeFaceTextView) contentView.findViewById(R.id.hard);

		easyButton.setOnClickListener(new DifficultyClickListener(EASY_DIFFICULTY_LEVEL));
		normalButton.setOnClickListener(new DifficultyClickListener(NORMAL_DIFFICULTY_LEVEL));
		hardButton.setOnClickListener(new DifficultyClickListener(HARD_DIFFICULTY_LEVEL));

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
		easyButton.setOnTouchListener(onTouchListener);
		normalButton.setOnTouchListener(onTouchListener);
		hardButton.setOnTouchListener(onTouchListener);

		easyButton.setOnFocusChangeListener(new HighlightTextFocusChangeListener());
		normalButton.setOnFocusChangeListener(new HighlightTextFocusChangeListener());
		hardButton.setOnFocusChangeListener(new HighlightTextFocusChangeListener());

		boolean demoOnly = getIntent().getBooleanExtra(DISPLAY_DEMO, false);

		TextView videoText = (TextView) contentView.findViewById(R.id.videoText);
		videoText.setText(demoOnly ? getResources().getString(R.string.instructions) : null);

		preview = (SurfaceView) contentView.findViewById(R.id.videoview);
		holder = preview.getHolder();
		holder.addCallback(new HolderCallback(demoOnly));

		unfocusedColor = getResources().getColor(R.color.button_font_color);
		focusedColor = getResources().getColor(R.color.button_font_color_focused);

		setContentView(contentView);

		// create data structures to help with the button animation
		animatingButtons = new TypeFaceTextView[] { easyButton, normalButton, hardButton };

		// encourage a garbage collection, to minimize the change that the skill
		// test activity stutters from GCs
		System.gc();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		buttonsAnimation = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				final TypeFaceTextView animatingButton = animatingButtons[animatingButtonIndex];
				final int unFocusedAnimatedColour = Color.rgb(255, (int) (255 * interpolatedTime),
						(int) (255 * interpolatedTime));
				final int focussedAnimatedColour = Color.rgb((int) (255 * interpolatedTime),
						(int) (255 * interpolatedTime), 255);

				for (final TypeFaceTextView button : animatingButtons) {
					if (button == animatingButton) {
						button.setTextColor(button.isFocused() ? focussedAnimatedColour : unFocusedAnimatedColour);
					} else {
						button.setTextColor(button.isFocused() ? focusedColor : unfocusedColor);
					}
				}
			}
		};
		buttonsAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				animatingButtonIndex++;
				animatingButtonIndex = animatingButtonIndex % 3;
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		buttonsAnimation.setDuration(BUTTON_FLASH_PERIOD);
		buttonsAnimation.setRepeatCount(Animation.INFINITE);
		buttonsAnimation.setInterpolator(new CycleInterpolator(0.5f));
		animatingButtons[0].setAnimation(buttonsAnimation);

		buttonsAnimation.start();

		Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
		fadeOutAnimation.setStartTime(3000);
		fadeOutAnimation.setDuration(4000);
		fadeOutAnimation.setFillAfter(true);
		contentView.findViewById(R.id.videoText).setAnimation(fadeOutAnimation);
	}
}
