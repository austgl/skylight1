package net.nycjava.skylight.view;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class SkillTestView extends View {
	@Dependency
	private DestinationPublicationService destinationPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	Paint lPaint = new Paint();

	private DestinationObserver destinationObserver;

	private CountdownObserver countdownObserver;

	private float distance;

	private int remainingTime;

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		destinationObserver = new DestinationObserver() {
			public void destinationNotification(float anAngle, float aDistance) {
				distance = aDistance;

				postInvalidate();
			}
		};
		destinationPublicationService.addObserver(destinationObserver);

		countdownObserver = new CountdownObserver() {

			public void countdownNotification(int aRemainingTime) {
				remainingTime = aRemainingTime;

				postInvalidate();
			}
		};
		countdownPublicationService.addObserver(countdownObserver);
	}

	@Override
	protected void onDetachedFromWindow() {
		destinationPublicationService.removeObserver(destinationObserver);
		countdownPublicationService.removeObserver(countdownObserver);

		super.onDetachedFromWindow();
	}

	public void onDraw(Canvas canvas) {

		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(250, 0);
		path.moveTo(0, 50);
		path.lineTo(250, 50);
		Paint paint = new Paint();
		paint.setColor(0xFFFF0000);
		paint.setStrokeWidth(1.0f);
		paint.setTextSize(24);
		Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
		paint.setTypeface(typeface);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawTextOnPath("Dist " + distance + "  Time " + remainingTime, path, 0, 250, paint);
	}
}
