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

	private final Typeface face;

	@Dependency
	private Integer difficultyLevel;

	private Bitmap levelGlassFullBitmap;

	private Bitmap levelGlassEmptyBitmap;
	
	long animationCycleStartTime = System.currentTimeMillis();

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
		if (difficultyLevel < 10) {
			drawView(canvas);
		} else {
			double animationCycle = (double) (System.currentTimeMillis() - animationCycleStartTime);
			double degreeOfDoubleVision = Math.cos(animationCycle / 8666d) * 10;
			double angleOfDoubleVision = animationCycle / 2534d;
			
			int x = (int) (Math.cos(angleOfDoubleVision) * degreeOfDoubleVision);
			int y = (int) (Math.sin(angleOfDoubleVision) * degreeOfDoubleVision);
			
			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(-x, -y);
			drawView(canvas);
			canvas.restore();

			canvas.saveLayerAlpha(new RectF(canvas.getClipBounds()), 128, Canvas.ALL_SAVE_FLAG);
			canvas.translate(x, y);
			drawView(canvas);
			canvas.restore();
		}
	}

	private void drawView(Canvas canvas) {
		// draw the glass
		drawGlass(canvas);

		// draw the level indicator
		drawCurrentLevel(canvas);

		// draw the timer 
		drawTimeRemaining(canvas);
	}

	private void drawTimeRemaining(Canvas aCanvas) {
		// draw the "pie"
		Paint arcPaint = new Paint();
		arcPaint.setAntiAlias(true);
		arcPaint.setStrokeWidth(2);
		int remainingTimeColor = Color.YELLOW;
		arcPaint.setColor(remainingTimeColor);
		final RectF timeRemainingRect = new RectF(SCREEN_MARGIN, SCREEN_MARGIN, SCREEN_MARGIN
				+ levelGlassFullBitmap.getHeight(), SCREEN_MARGIN + +levelGlassFullBitmap.getHeight());
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);
		arcPaint.setColor(Color.BLACK);
		arcPaint.setStyle(Style.STROKE);
		aCanvas.drawArc(timeRemainingRect, -90 + (15 - remainingTime) * 360 / 15, remainingTime * 360 / 15, true,
				arcPaint);

		// draw the number
		Paint paint = new Paint();
		paint.setTextSize(30);
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		Rect timeRemainingTextBounds = new Rect();
		String timeRemainingString = String.valueOf(remainingTime);
		paint.getTextBounds(timeRemainingString, 0, timeRemainingString.length(), timeRemainingTextBounds);
		// Log.d(SkillTestView.class.getName(), String.format("rect = %s", timeRemainingRect));
		final int x = SCREEN_MARGIN + levelGlassFullBitmap.getHeight() / 2;
		final int y = (int) (SCREEN_MARGIN + levelGlassFullBitmap.getHeight() / 2 - paint.ascent() / 4);
		paint.setColor(Color.BLACK);
		aCanvas.drawText(timeRemainingString, x - 1, y -1, paint);
		aCanvas.drawText(timeRemainingString, x - 1, y +1, paint);
		aCanvas.drawText(timeRemainingString, x + 1, y -1, paint);
		aCanvas.drawText(timeRemainingString, x + 1, y +1, paint);
		paint.setColor(Color.WHITE);
		aCanvas.drawText(timeRemainingString, x, y, paint);
	}

	private void drawCurrentLevel(Canvas aCanvas) {
		int glassNumber = 0;
		for (int previousLevels = 0; previousLevels < difficultyLevel; previousLevels++) {
			aCanvas.drawBitmap(levelGlassEmptyBitmap, getWidth() - levelGlassFullBitmap.getWidth() - SCREEN_MARGIN
					- glassNumber * 4, SCREEN_MARGIN, null);
			glassNumber++;
		}
		final int currentDifficultLevelGlassImageX = getWidth() - levelGlassFullBitmap.getWidth() - SCREEN_MARGIN
				- glassNumber * 4;
		aCanvas.drawBitmap(levelGlassFullBitmap, currentDifficultLevelGlassImageX, SCREEN_MARGIN, null);

		// draw the number
		Paint paint = new Paint();
		paint.setTextSize(30);
		paint.setTypeface(face);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		Rect timeRemainingTextBounds = new Rect();
		String levelString = String.valueOf(difficultyLevel + 1);
		paint.getTextBounds(levelString, 0, levelString.length(), timeRemainingTextBounds);
		final int x = currentDifficultLevelGlassImageX + levelGlassFullBitmap.getWidth() / 2;
		final int y = (int) (SCREEN_MARGIN + levelGlassFullBitmap.getHeight() / 2 - paint.ascent() / 4);
		paint.setColor(Color.BLACK);
		aCanvas.drawText(levelString, x - 1, y -1, paint);
		aCanvas.drawText(levelString, x - 1, y +1, paint);
		aCanvas.drawText(levelString, x + 1, y -1, paint);
		aCanvas.drawText(levelString, x + 1, y +1, paint);
		paint.setColor(Color.WHITE);
		aCanvas.drawText(levelString, x, y, paint);
	}

	private void drawGlass(Canvas aCanvas) {
		int xpos = width / 2 - glassBitmap.getWidth() / 2 + glassXOffset;
		int ypos = height / 2 - glassBitmap.getHeight() / 2 + glassYOffset;
		
		aCanvas.drawBitmap(glassBitmap, xpos, ypos, null);
	}
}
