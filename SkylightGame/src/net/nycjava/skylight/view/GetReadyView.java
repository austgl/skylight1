package net.nycjava.skylight.view;

import net.nycjava.skylight.dependencyinjection.Dependency;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class GetReadyView extends FrameLayout {

	@Dependency
	private Preview preview;

	public GetReadyView(Context aContext, AttributeSet attrs) {
		super(aContext, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		addView(preview);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
