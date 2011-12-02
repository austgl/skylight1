package com.skylight1.tinydancer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

public class TinyDancerActivity extends Activity {
	
	private ProgressBar  progressBar;
	static final String TAG = TinyDancerActivity.class.getName();
	Visualizer mvisualizer;
	private MediaPlayer mediaPlayer;
    private final class DataCaptureListener implements OnDataCaptureListener {
		private static final String TWO_FIFTY_SIX_ASTERISKS = "****************************************************************************************************************************************************************************************************************************************************************";

		private byte[] savedWaveform;
		private int offset;
		private int dataLength;

		public DataCaptureListener() {
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (savedWaveform != null && offset >= dataLength) {
						return;
					}
					int j = (int)savedWaveform[offset++] & 0xFF;
					progressBar.setProgress(j);
						//Log.i(TAG, "hey! " + TWO_FIFTY_SIX_ASTERISKS.substring(j));
				}
			}, 0, 1, TimeUnit.MILLISECONDS);
		}
		
		public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
				int samplingRate) {
			
			if (savedWaveform == null || savedWaveform.length < waveform.length) {
				savedWaveform = new byte[waveform.length];
			}
			System.arraycopy(waveform, 0, savedWaveform, 0, waveform.length);
			offset = 0;
			dataLength = waveform.length;
		}

		public void onFftDataCapture(Visualizer visualizer,byte[] fft,int samplingRate){
			Log.i(TAG, "yeah! " + fft.length);
		}
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar1);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        System.out.println("what the ?" + mediaPlayer.isPlaying());
        // int audioSessionId = mediaPlayer.getAudioSessionId();
        
                  mvisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                 System.out.println("audioSessionId" +mediaPlayer.getAudioSessionId());
                mvisualizer.setDataCaptureListener(new DataCaptureListener(), 1000, true, false);
                mvisualizer.setEnabled(true);
 //               visualizer.setEnabled(true);
//		final MediaController mediaController = (MediaController) findViewById(R.id.mediaController);
//		
//		MediaPlayerControl player = ;
//		mediaController.setMediaPlayer(player);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mediaPlayer.start();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mediaPlayer.stop();
    }
}