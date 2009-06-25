package net.nycjava.skylight.view;

import net.nycjava.skylight.R;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.DestinationObserver;
import net.nycjava.skylight.service.DestinationPublicationService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	private Bitmap theGlass;
	private int glassXOffset;
	private int glassYOffset;
	
	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		  theGlass = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		  glassXOffset = theGlass.getWidth() / 2;
		  glassYOffset = theGlass.getHeight() / 2;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		destinationObserver = new DestinationObserver() {
			@Override
			public void destinationNotification(float anAngle, float aDistance) {
				distance = aDistance;

				postInvalidate();
			}
		};
		destinationPublicationService.addObserver(destinationObserver);

		countdownObserver = new CountdownObserver() {

			@Override
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
		
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;

/*
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
*/
		canvas.drawBitmap(theGlass, centerX - glassXOffset, centerY - glassYOffset, null);
	}
}
