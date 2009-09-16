package net.nycjava.skylight1.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.nycjava.skylight1.R;
import net.nycjava.skylight1.WelcomeActivity;
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
				if(listOfMovies.size()>1) {
					//TODO: delay for 2 seconds
					surfaceView.setBackgroundResource(R.drawable.welcome_background);
					// draw text on surfaceview: "How to Play:"
					//TODO: delay for another 2 seconds
				}
				loadNextMovie();
			}
		});

		surfaceView.setBackgroundResource(R.drawable.welcome_background);
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

	public void setListOfMovies(List<String> listOfMovies) {
		this.listOfMovies = listOfMovies;
	}

	public List<String> getListOfMovies() {
		return listOfMovies;
	}
}
