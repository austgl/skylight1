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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class SkillTestView extends View {
	private static final int SCREEN_MARGIN = 10;

	@Dependency
	private BalancedObjectPublicationService balancedObjectPublicationService;

	@Dependency
	private CountdownPublicationService countdownPublicationService;

	Paint lPaint = new Paint();

	private BalancedObjectObserver balancedObjectObserver;

	private CountdownObserver countdownObserver;

	private int remainingTime;

	private Bitmap glassBitmap;

	private int glassXOffset;

	private int glassYOffset;

	private int width, height;

	private int xpos, ypos;

	private final Typeface face;

	@Dependency
	private Integer difficultyLevel;

	private Bitmap levelGlassFullBitmap;

	private Bitmap levelGlassEmptyBitmap;

	public SkillTestView(Context c, AttributeSet anAttributeSet) {
		super(c, anAttributeSet);
		glassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.theglass);
		levelGlassFullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass);
		levelGlassEmptyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.level_glass_empty);
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
		} else if (difficultyLevel >= SkylightActivity.BUZZED_DIFFICULTY_LEVEL) {
			backgroundResourceId = R.drawable.wood;
		} else {
			backgroundResourceId = R.drawable.background_table;
		}
		setBackgroundResource(backgroundResourceId);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		width = getWidth();
		height = getHeight();
	}

	@Override
	protected void onDetachedFromWindow() {
		balancedObjectPublicationService.removeObserver(balancedObjectObserver);
		countdownPublicationService.removeObserver(countdownObserver);

		super.onDetachedFromWindow();
	}

	public void onDraw(Canvas canvas) {
		if (difficultyLevel < 17) {
			drawView(canvas);
		} else {
			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(-10, 0);
			drawView(canvas);
			canvas.restore();

			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(10, 0);
			drawView(canvas);
			canvas.restore();
		}
	}

	private void drawView(Canvas canvas) {
		xpos = width / 2 - glassBitmap.getWidth() / 2 + glassXOffset;
		ypos = height / 2 - glassBitmap.getHeight() / 2 + glassYOffset;

		// draw the level indicator
		drawCurrentLevel(canvas);

		// draw the timer 
		drawTimeRemaining(canvas);
		
		// draw the glass
		drawGlass(canvas);
	}

	private void drawTimeRemaining(Canvas aCanvas) {
		Paint arcPaint = new Paint();
		arcPaint.setAntiAlias(true);
		arcPaint.setStrokeWidth(2);
		int remainingTimeColor = Color.rgb(255 * (15 - remainingTime) / 15, 255 * remainingTime / 15, 0);
		arcPaint.setColor(remainingTimeColor);
		final RectF timeRemainingRect = new RectF(SCREEN_MARGIN, SCREEN_MARGIN, SCREEN_MARGIN
				+ levelGlassFullBitmap.getHeight(), SCREEN_MARGIN + +levelGlassFullBitmap.getHeight());
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);
		arcPaint.setColor(Color.BLACK);
		arcPaint.setStyle(Style.STROKE);
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		Rect timeRemainingTextBounds = new Rect();
		String timeRemainingString = String.valueOf(remainingTime);
		paint.getTextBounds(timeRemainingString, 0, timeRemainingString.length(), timeRemainingTextBounds);
		// Log.d(SkillTestView.class.getName(), String.format("rect = %s", timeRemainingRect));
		aCanvas.drawText(timeRemainingString, SCREEN_MARGIN + levelGlassFullBitmap.getHeight() / 2, SCREEN_MARGIN
				+ levelGlassFullBitmap.getHeight() / 2 - paint.ascent() / 4, paint);
	}

	private void drawCurrentLevel(Canvas aCanvas) {
		int glassNumber = 0;
		for (int previousLevels = 0; previousLevels < difficultyLevel; previousLevels++) {
			aCanvas.drawBitmap(levelGlassEmptyBitmap, getWidth() - levelGlassFullBitmap.getWidth() - SCREEN_MARGIN
					- glassNumber * 4, SCREEN_MARGIN, null);
			glassNumber++;
		}
		aCanvas.drawBitmap(levelGlassFullBitmap, getWidth() - levelGlassFullBitmap.getWidth() - SCREEN_MARGIN
				- glassNumber * 4, SCREEN_MARGIN, null);
	}

	private void drawGlass(Canvas aCanvas) {
		aCanvas.drawBitmap(glassBitmap, xpos, ypos, null);
	}
}
