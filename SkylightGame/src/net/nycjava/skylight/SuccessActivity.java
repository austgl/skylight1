package net.nycjava.skylight;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;

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
		
		setContentView(contentView);

		MediaPlayer.create(getBaseContext(), R.raw.glassbinging).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			Intent intent = new Intent();
			intent.setClass(SuccessActivity.this, SkillTestActivity.class);
			intent.putExtra(DIFFICULTY_LEVEL, getIntent().getIntExtra(DIFFICULTY_LEVEL, 0)
					+ DIFFICULTY_LEVEL_INCREMENT);
			startActivity(intent);
			finish();
		}
		return true;
	}
	

}
