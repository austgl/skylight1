package net.nycjava.skylight.view;

import java.util.Random;

import net.nycjava.skylight.FailActivity;
import net.nycjava.skylight.R;
import net.nycjava.skylight.SkillTestActivity;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import net.nycjava.skylight.service.old.DestinationObserver;
import net.nycjava.skylight.service.old.DestinationPublicationService;
import android.content.Context;
import android.content.Intent;
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
	private int xIncr, yIncr;
	private int width, height;	
	private boolean firstDraw=true;
	private int xpos, ypos;
	private Context context;
	private Random rand = new Random(987654321);
	
	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		context = c;
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
		
		if(firstDraw) {
			firstDraw=false;
			width = canvas.getWidth();
			height = canvas.getHeight();
			xpos  = width / 2 - glassXOffset;
			ypos  = height / 2 - glassYOffset;
			xIncr = rand.nextInt(3)-1; 
			yIncr = rand.nextInt(3)-1;
			if(xIncr==0 && yIncr==0) {
				xIncr=-11; yIncr = rand.nextInt(3)-1;
			}
		} else {
			xpos +=xIncr;
			ypos +=yIncr;
			if(xpos+glassXOffset<0 || xpos+glassXOffset>width) {
				final Intent intent = new Intent(context, FailActivity.class);
				context.sendBroadcast(intent);
				//todo finish this activity and cleanup
			}
			else if(ypos+glassYOffset<0 || ypos+glassYOffset>height) {
				final Intent intent = new Intent(context, FailActivity.class);
				context.sendBroadcast(intent);
				//todo finish this activity and cleanup
			}
			postInvalidate();
		}

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

		canvas.drawBitmap(theGlass, xpos, ypos, null);
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
