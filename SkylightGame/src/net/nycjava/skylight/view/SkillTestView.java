package net.nycjava.skylight.view;

import net.nycjava.skylight.R;
import net.nycjava.skylight.SkylightActivity;
import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectObserver;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.CountdownObserver;
import net.nycjava.skylight.service.CountdownPublicationService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
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

	private int width, height;

	private boolean firstDraw = true;

	private int xpos, ypos;

	private final Typeface face;

	@Dependency
	private Integer difficultyLevel;

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		theGlass = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		face = Typeface.createFromAsset(getContext().getAssets(), "passthedrink.ttf");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		balancedObjectObserver = new BalancedObjectObserver() {

			@Override
			public void balancedObjectNotification(float anX, float aY) {
				glassXOffset = (int) (width / 2 * anX);
				glassYOffset = (int) (height / 2 * aY);
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

		// set the background
		final int backgroundResourceId;
		if (difficultyLevel >= SkylightActivity.SMASHED_DIFFICULTY_LEVEL) {
			backgroundResourceId = R.drawable.marble;
		} else {
			backgroundResourceId = R.drawable.background_table;
		}
		setBackgroundResource(backgroundResourceId);
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
			width = getWidth();
			height = getHeight();
		}

		xpos = width / 2 - theGlass.getWidth() / 2 + glassXOffset;
		ypos = height / 2 - theGlass.getHeight() / 2 + glassYOffset;
		// Log.i(SkillTestView.class.getName(), String.format("view=%dx%d; glassImage=%dx%d; renderPox=%dx%d; ", width,
		// height, theGlass.getWidth(), theGlass.getHeight(), xpos, ypos));
		canvas.drawBitmap(theGlass, xpos, ypos, null);

		Paint arcPaint = new Paint();
		int remainingTimeColor = Color.rgb(255 * (15 - remainingTime) / 15, 255 * remainingTime / 15, 0);
		arcPaint.setColor(remainingTimeColor);
		canvas.drawArc(new RectF(5, 5, 55, 55), -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true, arcPaint);
		arcPaint.setColor(Color.BLACK);
		arcPaint.setStyle(Style.STROKE);
		canvas.drawArc(new RectF(5, 5, 55, 55), -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true, arcPaint);
		
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setTextSize(20);
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText("Time " + remainingTime + " level=" + difficultyLevel, 10, 60, paint);
	}
}
