package net.nycjava.skylight.view;

import net.nycjava.skylight.R;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class SkillTestView extends View {
	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	Paint lPaint = new Paint();

	private BalancedObjectObserver balancedObjectObserver;

	private CountdownObserver countdownObserver;

	private int remainingTime;

	private Bitmap theGlass;

	private int glassXOffset;

	private int glassYOffset;

	private int xIncr, yIncr;

	private int width, height;

	private boolean firstDraw = true;

	private int xpos, ypos;

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		theGlass = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		glassXOffset = theGlass.getWidth() / 2;
		glassYOffset = theGlass.getHeight() / 2;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		balancedObjectObserver = new BalancedObjectObserver() {

			@Override
			public void balancedObjectNotification(float anX, float aY) {
				glassXOffset = (int) (Math.min(width / 2, height / 2) * anX);
				glassYOffset = (int) (Math.min(width / 2, height / 2) * aY);
				SkillTestView.this.postInvalidate();
			}

			@Override
			public void fallenOverNotification() {
			}
		};

		balancedObjectPublicationService.addObserver(balancedObjectObserver);

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
		balancedObjectPublicationService.removeObserver(balancedObjectObserver);
		countdownPublicationService.removeObserver(countdownObserver);

		super.onDetachedFromWindow();
	}

	public void onDraw(Canvas canvas) {

		if (firstDraw) {
			firstDraw = false;
			width = canvas.getWidth();
			height = canvas.getHeight();
		}

		xpos = width / 2 - theGlass.getWidth() / 2 + glassXOffset;
		ypos = height / 2 - theGlass.getHeight() / 2 + glassYOffset;
		canvas.drawBitmap(theGlass, xpos, ypos, null);

		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(400, 0);
		Paint paint = new Paint();
		paint.setColor(0xFF005500);
		paint.setStrokeWidth(2.0f);
		paint.setTextSize(24);
		Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
		paint.setTypeface(typeface);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawTextOnPath("  Time " + remainingTime, path, 0, 400, paint);
	}

	public void setXIncr(int xIncr) {
		this.xIncr = xIncr;
	}

	public int getXIncr() {
		return xIncr;
	}

	public void setYIncr(int yIncr) {
		this.yIncr = yIncr;
	}

	public int getYIncr() {
		return yIncr;
	}
}
