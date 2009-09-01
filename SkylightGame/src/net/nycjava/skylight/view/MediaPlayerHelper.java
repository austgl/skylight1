package net.nycjava.skylight.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.nycjava.skylight.R;
import net.nycjava.skylight.WelcomeActivity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.SurfaceView;

public class MediaPlayerHelper {
	private List<String> listOfMovies;

	private Context context;

	private MediaPlayer mediaPlayer;

	private SurfaceView surfaceView;

	public MediaPlayerHelper(Context aContext, SurfaceView aSurfaceView, String... aListOfResources) {
		context = aContext;
		surfaceView = aSurfaceView;
		listOfMovies = new ArrayList<String>(Arrays.asList(aListOfResources));
	}

	public MediaPlayer createMediaListPlayer() {
		mediaPlayer = new MediaPlayer();

		mediaPlayer.setDisplay(surfaceView.getHolder());

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.i(WelcomeActivity.class.getName(), "mp is prepared");

				// start the video
				mp.start();

				surfaceView.setBackgroundResource(0);
			}
		});

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i(WelcomeActivity.class.getName(), "mp is completed");
				loadNextMovie();
			}
		});

		// load the first movie
		loadNextMovie();

		return mediaPlayer;
	}

	private void loadNextMovie() {
		if (listOfMovies.isEmpty()) {
			return;
		}

		try {
			final String fileName = listOfMovies.remove(0);
			final AssetFileDescriptor afd = context.getAssets().openFd(fileName);
			surfaceView.setBackgroundResource(R.drawable.welcome_background);
			mediaPlayer.reset();
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
