package net.nycjava.skylight.view;

import net.nycjava.skylight.dependencyinjection.Dependency;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.VideoView;

public class GetReadyView extends FrameLayout {

//	@Dependency
//	private VideoView videoView;

	public GetReadyView(Context aContext, AttributeSet attrs) {
		super(aContext, attrs);
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		addView(videoView);
//
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//	}
//	public VideoView getVideoView() {
//		return videoView;
//	}
}
