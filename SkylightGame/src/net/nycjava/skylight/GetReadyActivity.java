package net.nycjava.skylight;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.dependencyinjection.DependencyInjectingObjectFactory;
import net.nycjava.skylight.view.GetReadyView;
import net.nycjava.skylight.view.Preview;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * waiting for player to obscure camera; camera obscured; counting down start; camera unobscured; countdown complete
 */
public class GetReadyActivity extends SkylightActivity {
	@Dependency
	private View view;

//	@Dependency
//	private Camera camera;

	@Override
	protected void addDependencies(DependencyInjectingObjectFactory aDependencyInjectingObjectFactory) {
//		aDependencyInjectingObjectFactory.registerImplementationObject(Camera.class, Camera.open());
		aDependencyInjectingObjectFactory.registerImplementationObject(View.class, getLayoutInflater().inflate(
				R.layout.getready, null));
//		aDependencyInjectingObjectFactory.registerImplementationObject(VideoView.class, new VideoView(this));
	}

	private VideoView mVideoView;
//	private MediaController mc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.getready);
		mVideoView=(VideoView)findViewById(R.id.getready);
		mVideoView.setVideoPath("/sdcard/passthedrink.mp4");
//		mc=new MediaController(this);
//		mc.setMediaPlayer(mVideoView);
//		mVideoView.setMediaController(mc);
		mVideoView.requestFocus();	
		mVideoView.start();		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer arg0) {
				final Intent intent = new Intent(GetReadyActivity.this, SkillTestActivity.class);
				startActivity(intent);
				finish();								
			} 
		});
		
//		introVideo=(VideoView) findViewById(R.layout.getready); //((GetReadyView)view).getVideoView();//findViewById(R.layout.getready);
//		introVideo.setVideoPath("/sdcard/passthedrink.mp4");
//		introVideo.start();
/*
		 mVideoView = (VideoView) findViewById(R.id.getready); 
		 setContentView(mVideoView);//R.layout.videoview); 
		 MediaController nc = new MediaController(this); 
		 mVideoView.setMediaController(nc); 
		 mVideoView.requestFocus(); 

		 mVideoView.pause(); 
		 mVideoView.stopPlayback();
		 mVideoView.setVideoPath("/sdcard/passthedrink.mp4"); 
*/
		 
		 
		// Create our Preview view and set it as the content of our activity.
		// mPreview = new Preview(this);
		// setContentView(mPreview);
	}

	@Override
	protected void onDestroy() {
//		camera.release();
		super.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		final Intent intent = new Intent(GetReadyActivity.this, SkillTestActivity.class);
		startActivity(intent);
		finish();
		return true;
	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX() - CENTER_X;
//        float y = event.getY() - CENTER_Y;
//        boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
        		final Intent intent = new Intent(GetReadyActivity.this, SkillTestActivity.class);
        		startActivity(intent);
        		finish();
                break;
        }
        return true;
    }

}