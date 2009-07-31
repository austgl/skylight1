package net.nycjava.skylight;

import java.io.IOException;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;

/**
 * waiting for player to obscure camera; camera obscured; counting down start; camera unobscured; countdown complete
 */
public class GetReadyActivity extends SkylightActivity {
	@Dependency
	private View view;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
		aDependencyInjectingObjectFactory.registerImplementationObject(View.class, getLayoutInflater().inflate(
				R.layout.getready, null));
	}

	private MediaPlayer mp;

	private SurfaceView mPreview;

	private SurfaceHolder holder;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.getready);

		mPreview = (SurfaceView) findViewById(R.id.getready);
		holder = mPreview.getHolder();
		holder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mp = new MediaPlayer();
				mp.setDisplay(mPreview.getHolder());

				mp.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.i(GetReadyActivity.class.getName(), "mp is prepared");
						mp.start();
					}});
				
				mp.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer arg0) {
						Log.i(GetReadyActivity.class.getName(), "complete");
						startSkillTestActivity();
					}
				});

				try {
					AssetFileDescriptor afd = getAssets().openFd("passthedrink.mp4");
					mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					mp.prepareAsync();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				mp.stop();
				mp.release();
				Log.i(GetReadyActivity.class.getName(), "surface destroyed");
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			startSkillTestActivity();
		}
		return true;
	}

	private void startSkillTestActivity() {
		final Intent intent = new Intent(GetReadyActivity.this, SkillTestActivity.class);
		intent.putExtra(DIFFICULTY_LEVEL, GetReadyActivity.this.getIntent().getIntExtra(DIFFICULTY_LEVEL, 0));
		startActivity(intent);
		finish();
	}

}