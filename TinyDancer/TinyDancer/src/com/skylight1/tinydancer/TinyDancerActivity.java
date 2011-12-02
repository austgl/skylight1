package com.skylight1.tinydancer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

public class TinyDancerActivity extends Activity {
	
	private ProgressBar  progressBar;
	static final String TAG = TinyDancerActivity.class.getName();
	Visualizer mvisualizer;
    private final class DataCaptureListener implements OnDataCaptureListener {
		private static final String TWO_FIFTY_SIX_ASTERISKS = "****************************************************************************************************************************************************************************************************************************************************************";

		public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
				int samplingRate) {
			for (int i : waveform) {
				
				
				int j = (int)i & 0xFF;
				progressBar.setProgress(j);
				try{
					Thread.sleep(1);
				}catch(InterruptedException e){e.printStackTrace();}
				//Log.i(TAG, "hey! " + TWO_FIFTY_SIX_ASTERISKS.substring(j));
			}
			
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
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();
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
}